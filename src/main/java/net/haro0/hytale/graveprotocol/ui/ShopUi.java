package net.haro0.hytale.graveprotocol.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.assets.Tower;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class ShopUi extends InteractiveCustomUIPage<ShopUi.BindingData> {

    private final String[] towerIds;

    public ShopUi(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC);
        this.towerIds = Tower.getAssetMap().getAssetMap().values().stream()
            .map(Tower::getId)
            .sorted(Comparator.naturalOrder())
            .toArray(String[]::new);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Shop.ui");

        var playerData = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        var balance = playerData != null ? playerData.getCurrency() : 0;
        uiCommandBuilder.set("#Balance.Text", "Currency: " + balance);

        uiCommandBuilder.clear("#ItemList");
        for (int i = 0; i < towerIds.length; i++) {
            var towerId = towerIds[i];
            var tower = Tower.getAssetMap().getAsset(towerId);
            if (tower == null) continue;

            uiCommandBuilder.append("#ItemList", "Pages/ListButton.ui");
            var alreadyUnlocked = playerData != null && playerData.isTowerUnlocked(towerId);
            var cost = tower.getShopUnlockCost();
            var label = alreadyUnlocked
                ? towerId + " [Unlocked]"
                : towerId + " - " + cost + " Currency";
            uiCommandBuilder.set("#ItemList[" + i + "].Text", label);
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ItemList[" + i + "]",
                EventData.of("Action", alreadyUnlocked ? "AlreadyUnlocked" : "BuyTower").append("TowerId", towerId));
        }

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Cancel",
            EventData.of("Action", "Close").append("TowerId", ""));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BindingData data) {
        if ("Close".equals(data.action)) {
            close();
            return;
        }

        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        if ("AlreadyUnlocked".equals(data.action)) {
            playerRef.sendMessage(Message.raw("You already own that tower."));
            return;
        }

        if (!"BuyTower".equals(data.action) || data.towerId == null || data.towerId.isBlank()) {
            playerRef.sendMessage(Message.raw("That option is unavailable."));
            return;
        }

        var tower = Tower.getAssetMap().getAsset(data.towerId);
        if (tower == null) {
            playerRef.sendMessage(Message.raw("Unknown tower: " + data.towerId));
            return;
        }

        var playerData = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        if (playerData == null) {
            playerRef.sendMessage(Message.raw("Could not load your player data."));
            return;
        }

        if (playerData.isTowerUnlocked(data.towerId)) {
            playerRef.sendMessage(Message.raw("You already unlocked " + data.towerId + "."));
            return;
        }

        var cost = tower.getShopUnlockCost();
        if (!playerData.spendCurrency(cost)) {
            playerRef.sendMessage(Message.raw("Not enough Currency! Need " + cost + ", have " + playerData.getCurrency() + "."));
            return;
        }

        playerData.unlockTower(data.towerId);
        playerRef.sendMessage(Message.raw("Unlocked tower " + data.towerId + "! (" + cost + " Currency spent)"));

        var player = store.getComponent(ref, Player.getComponentType());
        store.getExternalData().getWorld().execute(() -> TowerDefenseHudUi.refreshFor(player));
        close();
    }

    public static class BindingData {

        public static final BuilderCodec<BindingData> CODEC = BuilderCodec.builder(BindingData.class, BindingData::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (e, v) -> e.action = v, e -> e.action).add()
            .append(new KeyedCodec<>("TowerId", Codec.STRING), (e, v) -> e.towerId = v, e -> e.towerId).add()
            .build();

        private String action;
        private String towerId;
    }
}

