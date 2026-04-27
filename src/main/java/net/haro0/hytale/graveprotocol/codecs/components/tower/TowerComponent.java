package net.haro0.hytale.graveprotocol.codecs.components.tower;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentRegistryProxy;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import lombok.Getter;
import lombok.SneakyThrows;
import net.haro0.hytale.graveprotocol.codecs.assets.Tower;
import net.haro0.hytale.graveprotocol.codecs.components.player.GPInstanceComponent;
import net.haro0.hytale.graveprotocol.codecs.data.Attacker;
import net.haro0.hytale.graveprotocol.codecs.data.DamageType;
import net.haro0.hytale.graveprotocol.codecs.data.tower.TowerUpgrade;
import net.haro0.hytale.graveprotocol.systems.TowerAttackSystem;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class TowerComponent extends Tower implements Component<ChunkStore> {

    public static final BuilderCodec<TowerComponent> CODEC = BuilderCodec.builder(TowerComponent.class, TowerComponent::new, Tower.CODEC)
        .append(new KeyedCodec<>("UpgradeProgress", new MapCodec<>(Codec.INTEGER,HashMap::new)), (t,u) -> t.upgradeProgress = u, t -> t.upgradeProgress)
        .add()
        .append(new KeyedCodec<>("TicksWaited", Codec.INTEGER), (t, v) -> t.ticksWaited = v, t -> t.ticksWaited)
        .add()
        .build();

    @Getter
    private static ComponentType<ChunkStore, TowerComponent> componentType;
    public static void register(ComponentRegistryProxy<ChunkStore> registry) {

        componentType = registry.registerComponent(TowerComponent.class, "GPTowerComponent", CODEC);
    }

    private Map<String, Integer> upgradeProgress = new HashMap<>();
    @Getter
    private int ticksWaited = 0;

    private Map<String, Integer> cachedUpgrades = null;
    private Attacker cachedAttackData;
    private int ticksToWait;
    private float cachedAttackRange;
    private float cachedProjectileSpeed;

    public void waitTick(){
        ticksWaited++;
    }

    public void resetWaitedTicks(){
        ticksWaited = 0;
    }

    public float getTicksToWait(){
        if(!isCacheValid()) reloadCache();
        return ticksToWait;
    }

    public Attacker getAttackData(){
        if(!isCacheValid()) reloadCache();
        return cachedAttackData;
    }

    public float getAttackRange(){
        if(!isCacheValid()) reloadCache();
        return cachedAttackRange;
    }

    public float getProjectileSpeed(){
        if(!isCacheValid()) reloadCache();
        return cachedProjectileSpeed;
    }

    private void reloadCache(){

        cachedUpgrades = new HashMap<>();
        var attackData = new Attacker();
        attackData.setAttackDamage(this.attackData.getAttackDamage());
        attackData.setAttackTypes(new HashSet<>(this.attackData.getAttackTypes()));
        var attackSpeed = this.attackSpeed;
        var attackRange = this.attackRange;
        var projectileSpeed = this.projectileSpeed;

        for(var key : upgradeProgress.keySet()) {
            var n = upgradeProgress.get(key);
            cachedUpgrades.put(key, n);

            var upgrades = this.upgrades.get(key);
            for (var i = 0; i <= n;i++) {
                attackData.setAttackDamage(attackData.getAttackDamage() + upgrades[i].getAttackData().getAttackDamage());
                attackSpeed += upgrades[i].getAttackSpeed();
                attackRange += upgrades[i].getAttackRange();
                projectileSpeed += upgrades[i].getProjectileSpeed();
            }
        }
        cachedAttackData = attackData;
        ticksToWait = (int)Math.ceil(TowerAttackSystem.TICKS_PER_SECOND / attackSpeed);
        cachedAttackRange = attackRange;
        cachedProjectileSpeed = projectileSpeed;
    }

    private boolean isCacheValid(){
        if(cachedUpgrades == null) return false;
        for(var key : upgradeProgress.keySet()) {
            if(upgradeProgress.get(key) != cachedUpgrades.get(key)) return false;
        }
        return true;
    }

    @NullableDecl
    @Override
    public TowerComponent clone() {
        return (TowerComponent) super.clone();
    }
}