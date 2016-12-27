package thepowderguy.mcflight.physics;

import thepowderguy.mcflight.util.Vec3;

public class CollisionPoint {
	
	private final Vec3 position;
	private Vec3 prevtransformposition = new Vec3();
	private Vec3 transformposition = new Vec3();
	public boolean isCollided;
	private Vec3 force = new Vec3();
	public final double friction;
	public final double brakeMul;
	
	public CollisionPoint(double x, double y, double z, double fric, double brakes) {
		position = new Vec3(x, y, z);
		friction = fric;
		brakeMul = brakes;
	}
	
	public void update(Vec3 newpos) {
		this.prevtransformposition = this.transformposition;
		this.transformposition = newpos;
	}
	
	public void setForce(Vec3 fc) {
		force = fc;
	}
	
	public Vec3 getForce() {
		return force;
	}

	public Vec3 getPosition() {
		return position;
	}

	public Vec3 getInterpolatedPosition(float partialTicks) {
		return Vec3.interpolate(this.prevtransformposition, this.transformposition, partialTicks);
	}
}
