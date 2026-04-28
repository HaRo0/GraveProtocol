package net.haro0.hytale.graveprotocol.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
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
import net.haro0.hytale.graveprotocol.codecs.components.tower.TowerComponent;
import net.haro0.hytale.graveprotocol.utils.BlockUtils;
import net.haro0.hytale.graveprotocol.utils.LevelStartService;

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

        var playerData = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        var lynnRef = LevelStartService.findLynn(store);
        var lynnComponent = lynnRef != null ? store.getComponent(lynnRef, LynnComponent.getComponentType()) : null;

        var balance = lynnComponent != null ? lynnComponent.getMaterial() : 0;
        uiCommandBuilder.set("#Balance.Text", "Temporary Currency: " + balance);

        // Only show unlocked towers
        var unlockedIds = java.util.Arrays.stream(towerIds)
            .filter(id -> playerData != null && playerData.isTowerUnlocked(id))
            .toArray(String[]::new);

        for (int i = 0; i < MAX_TOWER_OPTIONS; i++) {
            var buttonId = "#Tower" + (i + 1);
            if (i < unlockedIds.length) {
                var towerId = unlockedIds[i];
                var tower = Tower.getAssetMap().getAsset(towerId);
                var price = tower != null ? tower.getPrice() : 0;
                uiCommandBuilder.set(buttonId + ".Text", towerId + " - " + price + " coins");
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

        var towerAsset = Tower.getAssetMap().getAsset(data.towerId);
        if (towerAsset == null) {
            playerRef.sendMessage(Message.raw("Unknown tower: " + data.towerId));
            return;
        }

        var playerData = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        if (playerData == null || !playerData.isTowerUnlocked(data.towerId)) {
            playerRef.sendMessage(Message.raw("You have not unlocked that tower yet."));
            return;
        }

        var lynnRef = LevelStartService.findLynn(store);
        var lynnComponent = lynnRef != null ? store.getComponent(lynnRef, LynnComponent.getComponentType()) : null;
        if (lynnComponent == null) {
            playerRef.sendMessage(Message.raw("No active level – you cannot place towers right now."));
            return;
        }

        var price = towerAsset.getPrice();
        if (!lynnComponent.spendMaterial(price)) {
            playerRef.sendMessage(Message.raw("Not enough temporary currency! Need " + price + ", have " + lynnComponent.getMaterial() + "."));
            return;
        }

        var world = player.getWorld();
        if (world == null) {
            // Refund
            lynnComponent.addMaterial(price);
            return;
        }

        world.execute(() -> {
            var rotation = world.getBlockRotationIndex(blockPos.x, blockPos.y, blockPos.z);
            int index = BlockType.getAssetMap().getIndex(towerAsset.getTowerModel());
            var type = BlockType.getAssetMap().getAsset(index);
            world.getChunk(ChunkUtil.indexChunkFromBlock(blockPos.x, blockPos.z))
                .setBlock(blockPos.x, blockPos.y, blockPos.z,index,type ,rotation,0,0);
            var tRef = BlockUtils.getBlockEntity(world, blockPos);
            var cStore = tRef.getStore();
            var towerComponent = cStore.ensureAndGetComponent(tRef, TowerComponent.getComponentType());
            towerComponent.applyTower(towerAsset);
            playerRef.sendMessage(Message.raw("Built tower " + data.towerId + " for " + price + " temporary currency."));
            close();
            store.getExternalData().getWorld().execute(() -> TowerDefenseHudUi.refreshFor(player));
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

