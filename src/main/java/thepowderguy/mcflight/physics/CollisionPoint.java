package thepowderguy.mcflight.physics;

import thepowderguy.mcflight.util.Vec3;

public class CollisionPoint {
	
	private final Vec3 position;
	public boolean isCollided;
	public final double friction;
	
	public CollisionPoint(double x, double y, double z, double fric) {
		position = new Vec3(x, y, z);
		friction = fric;
	}
	
	public Vec3 getPosition() {
		return position;
	}
}
