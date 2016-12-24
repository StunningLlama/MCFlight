package thepowderguy.mcflight.client;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import thepowderguy.mcflight.common.entity.EntityAirplane;
import thepowderguy.mcflight.math.Vec3;

public class CustomRenderPlayer extends RenderPlayer {

	public CustomRenderPlayer(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}
	

    public CustomRenderPlayer(RenderManager rendermanager, boolean b) {
		super(rendermanager, b);
	}


	protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
		//System.out.println("This is being called...");
    	if (entityLiving.getRidingEntity() instanceof EntityAirplane) {
    		EntityAirplane entity = (EntityAirplane)entityLiving.getRidingEntity();
    		Vec3 angles = entity.getInterpolatedRotation(partialTicks);
    		GlStateManager.rotate(180f+(float)angles.x, 0.0F, 1.0F, 0.0F);
    		GlStateManager.rotate((float)-angles.y, 1.0F, 0.0F, 0.0F);
    		GlStateManager.rotate((float)-angles.z, 0.0F, 0.0F, 1.0F);
    	} else {
    		super.applyRotations(entityLiving, p_77043_2_, p_77043_3_, partialTicks);
    	}
    }
}
