package net.haro0.hytale.graveprotocol.ui;

import com.hypixel.hytale.builtin.hytalegenerator.props.ManualProp;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import net.haro0.hytale.graveprotocol.codecs.assets.Tower;
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;
import net.haro0.hytale.graveprotocol.utils.BlockUtils;

import javax.annotation.Nonnull;
import java.util.Comparator;

public class TowerPurchaseUi extends InteractiveCustomUIPage<TowerPurchaseUi.BindingData> {

    private static final int MAX_TOWER_OPTIONS = 6;

    private final Vector3i blockPos;
    private final String[] towerIds;

    public TowerPurchaseUi(@Nonnull PlayerRef playerRef, @Nonnull Vector3i blockPos) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC);
        this.blockPos = new Vector3i(blockPos.x, blockPos.y, blockPos.z);
        this.towerIds = Tower.getAssetMap().getAssetMap().values().stream()
            .map(Tower::getId)
            .sorted(Comparator.naturalOrder())
            .toArray(String[]::new);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/TowerPurchase.ui");

        for (int i = 0; i < MAX_TOWER_OPTIONS; i++) {
            var buttonId = "#Tower" + (i + 1);
            if (i < towerIds.length) {
                var towerId = towerIds[i];
                uiCommandBuilder.set(buttonId + ".Text", towerId);
                uiCommandBuilder.set(buttonId + ".Visible", true);
                uiEventBuilder.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    buttonId,
                    EventData.of("Action", "BuildTower").append("TowerId", towerId).append("UpgradePath", "")
                );
                continue;
            }

            uiCommandBuilder.set(buttonId + ".Visible", false);
            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                buttonId,
                EventData.of("Action", "Unavailable").append("TowerId", "").append("UpgradePath", "")
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

        if (!"BuildTower".equals(data.action) || data.towerId == null || data.towerId.isBlank()) {
            playerRef.sendMessage(Message.raw("That tower option is unavailable."));
            return;
        }

        var world = player.getWorld();
        if (world == null) {
            return;
        }

        var towerAsset = Tower.getAssetMap().getAsset(data.towerId);
        if (towerAsset == null) {
            playerRef.sendMessage(Message.raw("Unknown tower: " + data.towerId));
            return;
        }

        world.execute(() -> {
            world.setBlock(blockPos.x, blockPos.y, blockPos.z, towerAsset.getTowerModel());
            var tRef = BlockUtils.getBlockEntity(world,blockPos);
            var cStore = tRef.getStore();
            var towerComponent = cStore.ensureAndGetComponent(tRef, TowerComponent.getComponentType());
            towerComponent.applyTower(towerAsset);
            playerRef.sendMessage(Message.raw("Built tower " + data.towerId + "."));
            close();

        });
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


