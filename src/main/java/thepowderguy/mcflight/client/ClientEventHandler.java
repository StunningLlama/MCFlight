package thepowderguy.mcflight.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import thepowderguy.mcflight.client.util.CameraDistanceEvent;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplane;
import thepowderguy.mcflight.common.entity.EntityAirplaneCamera;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

public class ClientEventHandler {

	public static double camroll = 0.0;
	public boolean prevRiding = false;
	Minecraft mc = Minecraft.getMinecraft();

	public ClientEventHandler() {
	}

	@SubscribeEvent
	public void cam(EntityViewRenderEvent.CameraSetup event) {// mc.gameSettings.thirdPersonView
																// == 0 &&
		if ((mc.player.getRidingEntity() instanceof EntityAirplane)) {
/*			EntityAirplane entity = (EntityAirplane) mc.player.getRidingEntity();
			Vec3 rot = entity.getInterpolatedRotation((float) event.getRenderPartialTicks());
			Mat3 basetransform = Mat3.getTransformMatrix(rot.x, rot.y, rot.z);
			Mat3 looktransform = Mat3.getTransformMatrix(EntityAirplane.viewYawOffset, EntityAirplane.viewPitchOffset,
					0);
			Mat3 transform = Mat3.mul(basetransform, looktransform);
			Vec3 out = Mat3.getangles(transform);*/
			EntityAirplaneCamera cam = null;
			if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityAirplaneCamera)
				cam = (EntityAirplaneCamera) Minecraft.getMinecraft().getRenderViewEntity();
			Vec3 out = cam.getInterpolatedRotations((float)event.getRenderPartialTicks());
			event.setYaw(180 - (float) out.x);
			event.setPitch((float) out.y);
			event.setRoll((float) out.z);
		} else {
			event.setRoll((float) camroll);
		}
	}

	@SubscribeEvent
	public void onCameraDistanceUpdate(CameraDistanceEvent event) {
		if (event.getPlayer().getRidingEntity() instanceof EntityAirplane)
			event.setDist(EntityAirplaneCamera.views[Mcflight.keyhandler.camera_mode].zoom);
	}

	@SubscribeEvent
	public void onRenderTickActual(RenderTickEvent event) {
		if (event.phase != TickEvent.Phase.START)
			return;

		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4))
			camroll -= 0.5;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6))
			camroll += 0.5;

		if (isPlayerRidingAirplane() && !Mcflight.keyhandler.look_around.isKeyDown()) {
			mc.mouseHelper.mouseXYChange();
			int dx = mc.mouseHelper.deltaX;
			int dy = mc.mouseHelper.deltaY;
			if (dx != 0 || dy != 0) {
				EntityAirplane.useMouseInput = true;
			}
			EntityAirplane.mouseX += dx;
			EntityAirplane.mouseY += dy;
			EntityAirplane.mouseY = EntityAirplane.clamp(-280, EntityAirplane.mouseY, 280);
			EntityAirplane.mouseX = EntityAirplane.clamp(-400, EntityAirplane.mouseX, 400);
		}

		if (mc.player != null) {
			if (isPlayerRidingAirplane()) {
				if (mc.inGameHasFocus) {
					mc.inGameHasFocus = false;
					mc.mouseHelper.grabMouseCursor();
				}
			} else {
				if (prevRiding) {
					if (mc.player.isDead) {
						if (mc.inGameHasFocus) {
							mc.inGameHasFocus = false;
							mc.mouseHelper.ungrabMouseCursor();
						}
					} else {
						mc.setIngameFocus();
					}
				}
			}
			prevRiding = mc.player.getRidingEntity() instanceof EntityAirplane;
		}

		if (mc.player != null) {
			if (isPlayerRidingAirplane()) {
				if (!(mc.getRenderViewEntity() instanceof EntityAirplaneCamera)) {
					mc.setRenderViewEntity(((EntityAirplane)mc.player.getRidingEntity()).getCamera());
				}
			} else if (mc.getRenderViewEntity() instanceof EntityAirplaneCamera) {
				mc.setRenderViewEntity(mc.player);
			}
		}
	}

	@SubscribeEvent
	public void onFovUpdate(FOVUpdateEvent event) {
		if (event.getEntity().getRidingEntity() instanceof EntityAirplane) {
			event.setNewfov(
					event.getFov() + 0.1f * (float) ((EntityAirplane) event.getEntity().getRidingEntity()).velocity);
		}
	}

	private boolean isPlayerRidingAirplane() {
		return mc.player != null
				&& mc.player.getRidingEntity() instanceof EntityAirplane;
	}
}
