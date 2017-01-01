package thepowderguy.mcflight.client;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

public class RenderAirplaneRider extends RenderEntity {

	public RenderAirplaneRider(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		return;
	}

}
