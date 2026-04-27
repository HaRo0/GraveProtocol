package net.haro0.hytale.graveprotocol.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class TowerUpgradeUi extends InteractiveCustomUIPage<TowerUpgradeUi.BindingData> {

    private static final int MAX_UPGRADE_OPTIONS = 6;

    private final Ref<ChunkStore> tRef;

    public TowerUpgradeUi(@Nonnull PlayerRef playerRef, @Nonnull Ref<ChunkStore> tRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC);
        this.tRef = tRef;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/TowerUpgrade.ui");

        var tower = getTowerComponent();
        if (tower == null || !tower.hasTower()) {
            uiCommandBuilder.set("#Title.Text", "No tower built");
            return;
        }

        uiCommandBuilder.set("#Title.Text", "Upgrade " + tower.getTowerModel());

        var upgradePaths = tower.getUpgrades().keySet().stream().sorted(Comparator.naturalOrder()).toList();
        for (int i = 0; i < MAX_UPGRADE_OPTIONS; i++) {
            var buttonId = "#Upgrade" + (i + 1);
            if (i >= upgradePaths.size()) {
                uiCommandBuilder.set(buttonId + ".Visible", false);
                uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    buttonId,
                    EventData.of("Action", "Unavailable").append("TowerId", "").append("UpgradePath", "")
                );
                continue;
            }

            var upgradePath = upgradePaths.get(i);
            var branch = tower.getUpgrades().get(upgradePath);
            var nextLevel = tower.getCurrentUpgradeLevel(upgradePath) + 1;
            var canBuy = nextLevel < branch.length;
            var text = canBuy
                ? upgradePath + " (" + (nextLevel + 1) + "/" + branch.length + ")"
                : upgradePath + " (MAX)";

            uiCommandBuilder.set(buttonId + ".Text", text);
            uiCommandBuilder.set(buttonId + ".Visible", true);
            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                buttonId,
                EventData.of("Action", canBuy ? "BuyUpgrade" : "Maxed")
                    .append("TowerId", tower.getTowerModel())
                    .append("UpgradePath", upgradePath)
            );
        }

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#Cancel",
            EventData.of("Action", "Close").append("TowerId", "").append("UpgradePath", "")
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BindingData data) {
        if ("Close".equals(data.action)) {
            close();
            return;
        }

        var player = store.getComponent(ref, Player.getComponentType());
        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }

        var tower = getTowerComponent();
        if (tower == null || !tower.hasTower()) {
            playerRef.sendMessage(Message.raw("No tower exists at this position anymore."));
            close();
            return;
        }

        if (!"BuyUpgrade".equals(data.action) || data.upgradePath == null || data.upgradePath.isBlank()) {
            playerRef.sendMessage(Message.raw("That upgrade option is unavailable."));
            return;
        }

        if (!tower.buyUpgrade(data.upgradePath)) {
            playerRef.sendMessage(Message.raw("Upgrade path is already maxed or invalid."));
            return;
        }

        playerRef.sendMessage(Message.raw("Purchased upgrade: " + data.upgradePath));
        close();
    }

    private TowerComponent getTowerComponent() {

        return tRef.getStore().getComponent(tRef,TowerComponent.getComponentType());
    }

    public static class BindingData {

        public static final BuilderCodec<BindingData> CODEC = BuilderCodec.builder(BindingData.class, BindingData::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, value) -> entry.action = value, entry -> entry.action).add()
            .append(new KeyedCodec<>("TowerId", Codec.STRING), (entry, value) -> entry.towerId = value, entry -> entry.towerId).add()
            .append(new KeyedCodec<>("UpgradePath", Codec.STRING), (entry, value) -> entry.upgradePath = value, entry -> entry.upgradePath).add()
            .build();

        private String action;
        private String towerId;
        private String upgradePath;
    }
}

