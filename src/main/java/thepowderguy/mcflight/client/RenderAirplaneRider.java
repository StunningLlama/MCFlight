package thepowderguy.mcflight.client;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import thepowderguy.mcflight.common.entity.EntityAirplaneCamera;

public class RenderAirplaneRider extends RenderEntity {

	public RenderAirplaneRider(RenderManager renderManagerIn) {
		super(renderManagerIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
	//	super.doRender(entity, x, entityYaw, z, entityYaw, partialTicks);
		return;
	}

}
