package net.haro0.hytale.graveprotocol.ui;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import net.haro0.hytale.graveprotocol.codecs.components.npcs.LynnComponent;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;
import net.haro0.hytale.graveprotocol.utils.LevelUtils;
import net.haro0.hytale.graveprotocol.utils.PrestigeUtils;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TowerDefenseHudUi extends CustomUIHud {

    private static final ConcurrentHashMap<UUID, TowerDefenseHudUi> ACTIVE_HUDS = new ConcurrentHashMap<>();

    public TowerDefenseHudUi(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder commandBuilder) {
        commandBuilder.append("HUD/TowerDefenseHUD.ui");

        var ref = getPlayerRef().getReference();
        if (ref == null || !ref.isValid()) return;

        var store = ref.getStore();

        var data = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        if (data == null) return;

        var prestige = PrestigeUtils.getPrestige(data);
        var allPrestiges = PrestigeUtils.getAllPrestiges();
        var level = LevelUtils.getPlayerLevel(ref, store);
        var levels = LevelUtils.getPrestigeLevels(prestige);

        var lynnRef = LevelStartService.findLynn(store);
        var lynnComponent = lynnRef != null ? store.getComponent(lynnRef, LynnComponent.getComponentType()) : null;
        var lynnStatMap = lynnRef != null ? store.getComponent(lynnRef, EntityStatMap.getComponentType()) : null;

        int waveIndex = lynnComponent != null ? lynnComponent.getWaveIndex() + 1 : 1;
        int totalWaves = level != null ? level.getWaves().length : 0;

        int levelDisplay = data.getLevelIndex() + 1;
        int totalLevels = levels.length;

        int prestigeDisplay = data.getPrestigeIndex() + 1;
        int totalPrestiges = allPrestiges.length;

        int material = lynnComponent != null ? lynnComponent.getMaterial() : 0;
        int currency = data.getCurrency();

        float lynnHp = lynnStatMap != null ? lynnStatMap.get(DefaultEntityStatTypes.getHealth()).get() : 0;
        float lynnMaxHp = lynnStatMap != null ? lynnStatMap.get(DefaultEntityStatTypes.getHealth()).getMax() : 0;

        commandBuilder.set("#WaveLabel.Text", "Wave: " + waveIndex + "/" + totalWaves);
        commandBuilder.set("#LevelLabel.Text", "Level: " + levelDisplay + "/" + totalLevels);
        commandBuilder.set("#PrestigeLabel.Text", "Prestige: " + prestigeDisplay + "/" + totalPrestiges);
        commandBuilder.set("#MaterialLabel.Text", "Material: " + material);
        commandBuilder.set("#CurrencyLabel.Text", "Currency: " + currency);
        commandBuilder.set("#LynnHpLabel.Text", "Lynn: " + (int) lynnHp + "/" + (int) lynnMaxHp);
    }

    public static void openFor(@Nonnull PlayerRef playerRef, @Nonnull Player player) {

        var hud = player.getHudManager().getCustomHud();
        if(hud instanceof TowerDefenseHudUi){
            hud.show();
            return;
        }
        hud = new TowerDefenseHudUi(playerRef);
        player.getHudManager().setCustomHud(playerRef, hud);
    }

    public static void refreshFor(@Nonnull Player player) {
        var hud = player.getHudManager().getCustomHud();
        if(hud instanceof TowerDefenseHudUi){
            hud.show();
        }
    }

    public static void closeFor(@Nonnull Player player){
        if(!(player.getHudManager().getCustomHud() instanceof TowerDefenseHudUi)) return;

        player.getHudManager().setCustomHud(player.getPlayerRef(), null);
    }
}


