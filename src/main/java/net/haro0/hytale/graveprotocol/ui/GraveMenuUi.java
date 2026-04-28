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
import net.haro0.hytale.graveprotocol.utils.LevelStartService;

import javax.annotation.Nonnull;

public class GraveMenuUi extends InteractiveCustomUIPage<GraveMenuUi.BindingData> {

    public GraveMenuUi(@Nonnull PlayerRef playerRef) {

        super(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder uiCommandBuilder, @Nonnull UIEventBuilder uiEventBuilder, @Nonnull Store<EntityStore> store) {

        uiCommandBuilder.append("Pages/GraveMenu.ui");

        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Shop", EventData.of("Action", "Shop"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#Prestige", EventData.of("Action", "Prestige"));
        uiEventBuilder.addEventBinding(CustomUIEventBindingType.Activating, "#StartLevel", EventData.of("Action", "StartLevel"));
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull BindingData data) {

        var player = store.getComponent(ref, Player.getComponentType());
        if (player == null) {
            return;
        }

        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        switch (data.action) {
            case "Shop" -> {
                player.getPageManager().openCustomPage(ref, store, new ShopUi(playerRef));
            }
            case "Prestige" -> playerRef.sendMessage(Message.raw("Prestige menu is not implemented yet."));
            case "StartLevel" -> {
                LevelStartService.startLevel(ref, store);
                close();
            }
            default -> {
            }
        }
    }

    public static class BindingData {

        public static final BuilderCodec<BindingData> CODEC = BuilderCodec.builder(BindingData.class, BindingData::new)
            .append(new KeyedCodec<>("Action", Codec.STRING), (entry, s) -> entry.action = s, entry -> entry.action).add()
            .build();

        private String action;
    }
}

