package thepowderguy.mcflight.client.util;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CameraDistanceEvent extends Event {
	private double maxDist;
	private final float partialTicks;
	private final EntityPlayerSP player;
	
	public CameraDistanceEvent(double maxDistance, EntityPlayerSP p, float pTicks) {
		maxDist = maxDistance;
		player = p;
		partialTicks = pTicks;
	}
	
	public void setDist(double dist) {
		maxDist = dist;
	}
	
	public double getDist() {
		return maxDist;
	}
	
	public EntityPlayerSP getPlayer() {
		return player;
	}
	
	public float getPartialTicks() {
		return this.partialTicks;
	}
}
