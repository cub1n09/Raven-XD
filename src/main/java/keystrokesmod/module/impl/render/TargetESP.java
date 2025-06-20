package keystrokesmod.module.impl.render;

import keystrokesmod.event.render.Render3DEvent;
import keystrokesmod.eventbus.annotations.EventListener;
import keystrokesmod.module.Module;
import keystrokesmod.module.impl.combat.KillAura;
import keystrokesmod.module.impl.render.targetvisual.ITargetVisual;
import keystrokesmod.module.impl.render.targetvisual.targetesp.*;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.utility.Utils;
import net.minecraft.entity.EntityLivingBase;
import keystrokesmod.event.network.AttackEntityEvent;
import org.jetbrains.annotations.Nullable;

public class TargetESP extends Module {
    private static @Nullable EntityLivingBase target = null;
    private final ModeValue mode;
    private final ButtonSetting onlyKillAura;
    private long lastTargetTime = -1;

    public TargetESP() {
        super("TargetESP", category.render);
        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new RavenTargetESP("Raven", this))
                .add(new JelloTargetESP("Jello", this))
                .add(new VapeTargetESP("Vape", this))
                .add(new LiquidBounceESP("LiquidBounce", this))
                .add(new RingESP("Ring", this))
        );
        this.registerSetting(onlyKillAura = new ButtonSetting("Only killAura", true));
    }

    @Override
    public void onEnable() {
        mode.enable();
    }

    @Override
    public void onDisable() {
        mode.disable();

        target = null;
        lastTargetTime = -1;
    }

    @Override
    public void onUpdate() {
        if (!Utils.nullCheck()) {
            target = null;
            return;
        }


        if (KillAura.target != null) {
            target = KillAura.target;
            lastTargetTime = System.currentTimeMillis();
        }

        if (target != null && lastTargetTime != -1 && (target.isDead || System.currentTimeMillis() - lastTargetTime > 5000 || target.getDistanceSqToEntity(mc.thePlayer) > 20)) {
            target = null;
            lastTargetTime = -1;
        }

        if (onlyKillAura.isToggled()) return;

        // manual target
        if (target != null) {
            if (!Utils.inFov(180, target) || target.getDistanceSqToEntity(mc.thePlayer) > 36) {
                target = null;
            }
        }
    }

    @EventListener
    public void onAttack(AttackEntityEvent event) {
        if (onlyKillAura.isToggled()) return;

        if (event.getTarget() instanceof EntityLivingBase) {
            target = (EntityLivingBase) event.getTarget();
        }
    }

    @EventListener
    public void onRender3D(Render3DEvent event) {
        if (target != null)
            ((ITargetVisual) mode.getSubModeValues().get((int) mode.getInput())).render(target);
    }
}
