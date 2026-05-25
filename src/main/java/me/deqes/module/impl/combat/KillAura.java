package me.deqes.module.impl.combat;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import lombok.Setter;
import me.deqes.event.EventTick;
import me.deqes.module.Category;
import me.deqes.module.Module;
import me.deqes.util.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;

public class KillAura extends Module implements Wrapper {

    // Режимы работы
    public enum Mode {
        SINGLE("Single"),    // Атака одной цели
        SWITCH("Switch"),    // Переключение между целями
        MULTI("Multi");      // Атака всех целей в радиусе

        private final String name;
        Mode(String name) { this.name = name; }
        public String getName() { return name; }
    }

    // Getters и Setters для настроек
    @Setter
    @Getter
    private Mode currentMode = Mode.SINGLE;
    private Entity currentTarget = null;
    private int switchTimer = 0;
    private int attackCooldown = 0;
    private int ticks = 0;

    // Настройки
    @Getter
    private float range = 4.2f;
    private int attackDelay = 3; // тиков между атаками
    @Getter
    private boolean throughWalls = false;
    private boolean checkHurtTime = true;
    @Getter
    private boolean playersOnly = true;
    @Getter
    private boolean mobs = false;
    @Getter
    @Setter
    private boolean animals = false;

    public KillAura() {
        super("KillAura", Category.COMBAT, 0);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        currentTarget = null;
        attackCooldown = 0;
        switchTimer = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        currentTarget = null;
    }

    @Subscribe
    public void onTick(EventTick e) {
        if (mc.player == null || mc.world == null) return;

        ticks++;

        // Обновляем кулдаун атаки
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        // Получаем список целей
        List<Entity> targets = getTargets();

        if (targets.isEmpty()) {
            currentTarget = null;
            return;
        }

        // В зависимости от режима
        switch (currentMode) {
            case SINGLE:
                handleSingleMode(targets);
                break;
            case SWITCH:
                handleSwitchMode(targets);
                break;
            case MULTI:
                handleMultiMode(targets);
                break;
        }
    }

    private void handleSingleMode(List<Entity> targets) {
        // Выбираем ближайшую цель
        Entity target = targets.stream()
                .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                .orElse(null);

        if (target != null && canAttack(target)) {
            attack(target);
            currentTarget = target;
        }
    }

    private void handleSwitchMode(List<Entity> targets) {
        // Переключаем цель каждые 20 тиков
        if (switchTimer <= 0 || currentTarget == null || !targets.contains(currentTarget)) {
            currentTarget = targets.get(ticks % targets.size());
            switchTimer = 20;
        } else {
            switchTimer--;
        }

        if (currentTarget != null && canAttack(currentTarget)) {
            attack(currentTarget);
        }
    }

    private void handleMultiMode(List<Entity> targets) {
        // Атакуем все цели в радиусе
        for (Entity target : targets) {
            if (canAttack(target)) {
                attack(target);
                // Небольшая задержка между атаками разных целей
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ignored) {}
            }
        }
        attackCooldown = attackDelay;
    }

    private List<Entity> getTargets() {
        Box box = mc.player.getBoundingBox().expand(range);

        return mc.world.getEntitiesByClass(
                Entity.class,
                box,
                entity -> {
                    if (entity == mc.player) return false;
                    if (!(entity instanceof LivingEntity)) return false;
                    if (!isValidTarget(entity)) return false;
                    if (!throughWalls && !mc.player.canSee(entity)) return false;

                    LivingEntity living = (LivingEntity) entity;
                    if (checkHurtTime && living.hurtTime > 0) return false;
                    if (living.isDead()) return false;
                    if (living.getHealth() <= 0) return false;

                    return true;
                }
        );
    }

    private boolean isValidTarget(Entity entity) {
        if (playersOnly && !(entity instanceof PlayerEntity)) {
            if (mobs && entity.getType().getName().getString().toLowerCase().contains("zombie")) return true;
            if (animals && !(entity instanceof PlayerEntity) && !(entity.getType().getName().getString().toLowerCase().contains("zombie"))) return true;
            return false;
        }
        return true;
    }

    private boolean canAttack(Entity target) {
        if (target == null) return false;
        if (mc.player.distanceTo(target) > range) return false;

        // Проверка на друзей (можно расширить)
        if (target instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) target;
            if (player.isCreative() || player.isSpectator()) return false;
        }

        return true;
    }

    private void attack(Entity target) {
        if (mc.interactionManager == null) return;

        // Поворачиваемся к цели (опционально)
        lookAtTarget(target);

        // Атакуем
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(Hand.MAIN_HAND);

        attackCooldown = attackDelay;
    }

    private void lookAtTarget(Entity target) {
        Vec3d targetPos = target.getBoundingBox().getCenter();
        Vec3d playerPos = mc.player.getEyePos();

        double dx = targetPos.x - playerPos.x;
        double dy = targetPos.y - playerPos.y;
        double dz = targetPos.z - playerPos.z;

        double distance = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, distance));

        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }

    public void setRange(float range) { this.range = Math.max(1, Math.min(6, range)); }

    public int getAttackDelay() { return attackDelay; }
    public void setAttackDelay(int delay) { this.attackDelay = Math.max(1, Math.min(10, delay)); }

    public void setThroughWalls(boolean throughWalls) { this.throughWalls = throughWalls; }

    public void setPlayersOnly(boolean playersOnly) { this.playersOnly = playersOnly; }

    public void setMobs(boolean mobs) { this.mobs = mobs; }

}