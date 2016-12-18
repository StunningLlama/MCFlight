package thepowderguy.mcflight.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class CameraDistanceEvent extends Event {
	private double maxDist;
	private final EntityPlayerSP player;
	
	public CameraDistanceEvent(double maxDistance, EntityPlayerSP p) {
		maxDist = maxDistance;
		player = p;
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
}
