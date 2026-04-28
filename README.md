# GraveProtocol

> A **Tower Defense** game-mode plugin for [Hytale](https://hytale.com), built by **HaRo0**.

---

## Overview

**GraveProtocol** turns a Hytale server world into a fully-featured tower defense experience. Players interact with **Lynn**, a custom NPC guide, to access the shop, prestige menu, and wave-based level progression. During a run they buy and upgrade **towers** to intercept waves of enemies before they reach their target — all driven by a data-driven asset pipeline that makes adding new content as simple as dropping a file into the right folder.

---

## Features

### 🗼 Tower Defense Gameplay
- Place towers on designated blocks by interacting with them.
- Towers automatically detect enemies within their **attack range** and fire each tick cycle.
- Two built-in attack archetypes:
  - **AOE Attack** — deals splash damage to all enemies in range.
  - **Instant Damage (Magic) Attack** — strikes a single target for precise, high damage.
- Towers can be **purchased** (if no tower exists on that block) or **upgraded** (if one already does) through a contextual in-game UI.

### 🧍 Lynn NPC
- An interactive NPC named **Lynn** placed in the hub world.
- Use (right-click) Lynn to open the **Lynn Menu**, your central control panel.

### 📋 Lynn Menu
The Grave Menu provides three actions:

| Action | Description |
|---|---|
| **Shop** | Browse and unlock towers using earned currency. |
| **Prestige** | Advance to the next prestige tier once all levels are cleared. |
| **Start Level** | Launch the next wave level for your current prestige. |

### 💰 Shop
- Lists every available tower with its unlock cost.
- Deducts currency from your **player data** on purchase.
- Already-owned towers are shown as *[Unlocked]* — no accidental double-purchases.

### 🏆 Prestige System
- Multiple **prestige tiers**, each with their own set of levels.
- Complete every level in your current prestige to unlock the **Confirm Prestige** button.
- Prestiging resets your level index and advances you to the next tier, then exits the active instance.
- A live UI shows: *Current Prestige X / N*, *Levels Completed X / N*, and a contextual status message.

### 🌊 Level & Wave System
- Levels and waves are defined entirely in **asset files** (`GraveProtocol/Levels`, `GraveProtocol/Waves`).
- Enemies (`GraveProtocol/Enemies`) are referenced by their model asset IDs and spawned per wave.
- Adding new levels or waves requires no code changes — drop a new asset file and reload.

### 🎮 In-Game HUD
- A persistent **Tower Defense HUD** displays live run information.
- Automatically refreshes when player data changes (e.g. after a purchase or prestige).

### ⚙️ Data-Driven Asset Pipeline
| Asset Folder | Type | Description |
|---|---|---|
| `GraveProtocol/Levels` | `Level` | Level definitions linked to a prestige. |
| `GraveProtocol/Waves` | `Wave` | Wave definitions with enemy lists and timing. |
| `GraveProtocol/Enemies` | `Enemy` | Enemy archetypes (model, stats). |
| `GraveProtocol/Towers` | `Tower` | Tower definitions (attack type, range, cost, upgrades). |
| `GraveProtocol/Prestiges` | `Prestige` | Prestige tiers and their instance targets. |

---

## Installation

1. Download the latest `GraveProtocol-x.x.x.jar`
2. Drop it into your Hytale server's `mods/` folder. 
3. Start (or restart) your server. The asset pack is bundled inside the jar and loaded automatically.

---

## Architecture (For Developers)

GraveProtocol is built on Hytale's **ECS (Entity Component System)** runtime.

### Components
| Component | Purpose |
|---|---|
| `GPPlayerDataComponent` | Stores per-player currency, unlocked towers, prestige & level index. |
| `GPInstanceComponent` | Tracks which tower-defense instance a player is currently in. |
| `LynnComponent` | Marks an entity as a Lynn NPC. |
| `LynnAttackerComponent` | Marks an entity as an enemy that towers should target. |
| `TowerComponent` | Block-level component storing the tower placed on that block. |

### Systems
| System | Purpose |
|---|---|
| `TowerAttackSystem` | Tick-driven; queries all `TowerComponent` blocks, finds enemies in range, and delegates to the configured attack type. |
| `LynnDamageSystem` | Handles incoming damage events for Lynn Attacker entities. |
| `LynnDeathSystem` | Handles Lynn NPC death/removal. |
| `LynnAttackerDeathSystem` | Awards currency and cleans up attacker entities on death. |

### Interactions
| Interaction ID | Trigger | Effect                                          |
|---|---|-------------------------------------------------|
| `OpenLynnMenu` | Use on Lynn NPC | Opens `LynnMenuUi`.                             |
| `OpenTowerMenu` | Use on tower/empty block | Opens `TowerPurchaseUi` or `TowerUpgradeUi`.    |
| `EnterTowerDefenseInstance` | Use on entry block | Teleports player into a tower-defense instance. |

---

## Credit

The current map was made by [xSpring](https://www.curseforge.com/members/xspring/projects) check it out [here](https://www.curseforge.com/hytale/worlds/hub-lobby-floating-island).

---

## License

See [LICENSE](LICENSE) for details.

---

*Made with ❤️ by HaRo0*
