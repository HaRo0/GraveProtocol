package net.haro0.hytale.graveprotocol.ui;

import com.hypixel.hytale.builtin.instances.InstancesPlugin;
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
import net.haro0.hytale.graveprotocol.codecs.components.player.GPPlayerDataComponent;
import net.haro0.hytale.graveprotocol.utils.InstanceUtils;
import net.haro0.hytale.graveprotocol.utils.LevelUtils;
import net.haro0.hytale.graveprotocol.utils.PrestigeUtils;

import javax.annotation.Nonnull;

public class PrestigeUi extends InteractiveCustomUIPage<PrestigeUi.BindingData> {

    public PrestigeUi(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {
        uiCommandBuilder.append("Pages/Prestige.ui");

        var data = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        if (data == null) {
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmPrestige",
                EventData.of("Action", "Close"));
            uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Cancel",
                EventData.of("Action", "Close"));
            return;
        }

        var allPrestiges = PrestigeUtils.getAllPrestiges();
        var prestige = PrestigeUtils.getPrestige(data);
        var levels = LevelUtils.getPrestigeLevels(prestige);

        int prestigeDisplay = data.getPrestigeIndex() + 1;
        int totalPrestiges = allPrestiges.length;
        int levelsComplete = Math.min(data.getLevelIndex(), levels.length);
        int totalLevels = levels.length;
        boolean canPrestige = levelsComplete >= totalLevels;
        boolean isMaxPrestige = data.getPrestigeIndex() >= totalPrestiges - 1;

        uiCommandBuilder.set("#CurrentPrestige.Text",
            "Current Prestige: " + prestigeDisplay + " / " + totalPrestiges + " (" + prestige.getId() + ")");
        uiCommandBuilder.set("#LevelProgress.Text",
            "Levels Completed: " + levelsComplete + " / " + totalLevels);

        if (isMaxPrestige && canPrestige) {
            uiCommandBuilder.set("#StatusLabel.Text", "Maximum prestige reached. All levels complete!");
            uiCommandBuilder.set("#ConfirmPrestige.Visible", false);
        } else if (canPrestige) {
            uiCommandBuilder.set("#StatusLabel.Text", "Ready to prestige! All levels complete.");
            uiCommandBuilder.set("#ConfirmPrestige.Visible", true);
        } else {
            uiCommandBuilder.set("#StatusLabel.Text",
                "Complete all " + totalLevels + " levels to prestige.");
            uiCommandBuilder.set("#ConfirmPrestige.Visible", false);
        }

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#ConfirmPrestige",
            EventData.of("Action", "Prestige"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Cancel",
            EventData.of("Action", "Close"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BindingData data) {
        if ("Close".equals(data.action)) {
            close();
            return;
        }

        if (!"Prestige".equals(data.action)) {
            return;
        }

        var playerData = store.getComponent(ref, GPPlayerDataComponent.getComponentType());
        if (playerData == null) {
            close();
            return;
        }

        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        var prestige = PrestigeUtils.getPrestige(playerData);
        var levels = LevelUtils.getPrestigeLevels(prestige);
        var allPrestiges = PrestigeUtils.getAllPrestiges();

        if (playerData.getLevelIndex() < levels.length) {
            if (playerRef != null) {
                playerRef.sendMessage(Message.raw("You must complete all levels before prestiging!"));
            }
            return;
        }

        if (playerData.getPrestigeIndex() >= allPrestiges.length - 1) {
            if (playerRef != null) {
                playerRef.sendMessage(Message.raw("You have already reached maximum prestige!"));
            }
            close();
            return;
        }

        playerData.setPrestigeIndex(playerData.getPrestigeIndex() + 1);
        playerData.setLevelIndex(0);

        var player = store.getComponent(ref, Player.getComponentType());
        if (player != null) {
            store.getExternalData().getWorld().execute(() -> TowerDefenseHudUi.refreshFor(player));
        }

        if (playerRef != null) {
            var newPrestige = PrestigeUtils.getPrestige(playerData);
            playerRef.sendMessage(Message.raw("Prestiged! Now on prestige " + (playerData.getPrestigeIndex() + 1)
                + ": " + newPrestige.getId()));
        }

        store.getExternalData().getWorld().execute(() -> InstancesPlugin.exitInstance(ref,store));

        close();
    }

    public static class BindingData {

        public static final BuilderCodec<BindingData> CODEC = BuilderCodec.builder(BindingData.class, BindingData::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (e, v) -> e.action = v, e -> e.action).add()
            .build();

        private String action;
    }
}

