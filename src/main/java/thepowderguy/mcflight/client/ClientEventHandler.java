package thepowderguy.mcflight.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import thepowderguy.mcflight.common.entity.EntityAirplane;
import thepowderguy.mcflight.math.Mat3;
import thepowderguy.mcflight.math.Vec3;

public class ClientEventHandler {

	public static double camroll = 0.0;
	public boolean prevRiding = false;
	
	public ClientEventHandler() {}
	
	@SubscribeEvent
	public void cam(EntityViewRenderEvent.CameraSetup event) {//Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && 
		if ((Minecraft.getMinecraft().player.getRidingEntity() instanceof EntityAirplane)) {
			EntityAirplane entity = (EntityAirplane) Minecraft.getMinecraft().player.getRidingEntity();
			Vec3 rot = entity.getInterpolatedRotation((float)event.getRenderPartialTicks());
			Mat3 basetransform = Mat3.getTransformMatrix(rot.x, rot.y, rot.z);
			Mat3 looktransform = Mat3.getTransformMatrix(EntityAirplane.viewYawOffset, EntityAirplane.viewPitchOffset, 0);
			Mat3 transform = Mat3.mul(basetransform, looktransform);
			Vec3 out = Mat3.getangles(transform);
			event.setYaw(180-(float)out.x);
			event.setPitch((float)out.y);
			event.setRoll((float)out.z);
		} else {
			event.setRoll((float)camroll);
		}
	}

	@SubscribeEvent
	public void onCameraDistanceUpdate(CameraDistanceEvent event) {
		if (event.getPlayer().getRidingEntity() instanceof EntityAirplane)
			event.setDist(7.0);
	}
	@SubscribeEvent
	public void onRenderTickActual(RenderTickEvent event)
	{
		if (event.phase != TickEvent.Phase.START)
			return;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD4)) camroll -= 0.5;
		if (Keyboard.isKeyDown(Keyboard.KEY_NUMPAD6)) camroll += 0.5;

		if (EntityAirplane.useMouseInput && isPlayerRidingAirplane()) {
			Minecraft.getMinecraft().mouseHelper.mouseXYChange();
			int dx = Minecraft.getMinecraft().mouseHelper.deltaX;
			int dy = Minecraft.getMinecraft().mouseHelper.deltaY;
			if (Keyboard.isKeyDown(EntityAirplane.KEYBIND_LOOK)) {
				EntityAirplane.viewYawOffset += dx/-20.0;
				EntityAirplane.viewPitchOffset += dy/-20.0;
				EntityAirplane.viewYawOffset = EntityAirplane.clamp(-180, EntityAirplane.viewYawOffset, 180);
				EntityAirplane.viewPitchOffset = EntityAirplane.clamp(-90, EntityAirplane.viewPitchOffset, 90);
			} else {

				EntityAirplane.mouseX += dx;
				EntityAirplane.mouseY += dy;
				EntityAirplane.mouseY = EntityAirplane.clamp(-280, EntityAirplane.mouseY, 280);
				EntityAirplane.mouseX = EntityAirplane.clamp(-400, EntityAirplane.mouseX, 400);
			}

		}

		if (Minecraft.getMinecraft().player != null) {
			if (isPlayerRidingAirplane()){
				if (Minecraft.getMinecraft().inGameHasFocus) {
					Minecraft.getMinecraft().inGameHasFocus = false;
					Minecraft.getMinecraft().mouseHelper.grabMouseCursor();
				}
			} else {
				if (prevRiding) {
					if (Minecraft.getMinecraft().player.isDead) {
						if (Minecraft.getMinecraft().inGameHasFocus) {
							Minecraft.getMinecraft().inGameHasFocus = false;
							Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
						}
					} else {
						Minecraft.getMinecraft().setIngameFocus();
					}
				}
			}
			prevRiding = Minecraft.getMinecraft().player.getRidingEntity() instanceof EntityAirplane;
		}
	}

	@SubscribeEvent
	public void onFovUpdate(FOVUpdateEvent event)
	{
		if (event.getEntity().getRidingEntity() instanceof EntityAirplane)
		{
			event.setNewfov(event.getFov() + 0.1f*(float)((EntityAirplane)event.getEntity().getRidingEntity()).velocity);
		}
	}

	private boolean isPlayerRidingAirplane() {
		return Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getRidingEntity() instanceof EntityAirplane;
	}
}
