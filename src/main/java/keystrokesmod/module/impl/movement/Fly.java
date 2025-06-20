package keystrokesmod.module.impl.movement;

import keystrokesmod.module.Module;
import keystrokesmod.module.impl.movement.fly.*;
import keystrokesmod.module.setting.impl.ButtonSetting;
import keystrokesmod.module.setting.impl.ModeValue;
import keystrokesmod.utility.render.RenderUtils;
import keystrokesmod.eventbus.annotations.EventListener;
import keystrokesmod.event.render.Render2DEvent;

public class Fly extends Module {
    public final ModeValue mode;
    private final ButtonSetting showBPS;
    private final ButtonSetting stopAtEnd;

    public Fly() {
        super("Fly", category.movement);
        this.registerSetting(mode = new ModeValue("Mode", this)
                .add(new Vanilla1Fly("Vanilla1", this))
                .add(new Vanilla2Fly("Vanilla2", this))
                .add(new AirWalkFly("AirWalk", this))
                .add(new AirPlaceFly("AirPlace", this))
                .add(new VulcanFly("Vulcan", this))
                .add(new MatrixFly("Matrix", this))
                .add(new MatrixBowFly("MatrixBow", this))
                .add(new MatrixTNTFly("MatrixTNT", this))
                .add(new CustomFly("Custom", this))
                .add(new GrimACFly("GrimAC", this))
                .add(new SelfBowFly("SelfBow", this))
        );
        this.registerSetting(showBPS = new ButtonSetting("Show BPS", false));
        this.registerSetting(stopAtEnd = new ButtonSetting("Stop at end", false));
    }

    @Override
    public String getInfo() {
        return mode.getSubModeValues().get((int) mode.getInput()).getPrettyName();
    }

    public void onEnable() {
        mode.enable();
    }

    public void onDisable() {
        mode.disable();

        if (stopAtEnd.isToggled()) {
            mc.thePlayer.motionZ = 0;
            mc.thePlayer.motionY = 0;
            mc.thePlayer.motionX = 0;
        }
    }

    @EventListener
    public void onRenderTick(Render2DEvent e) {
        RenderUtils.renderBPS(showBPS.isToggled());
    }
}
