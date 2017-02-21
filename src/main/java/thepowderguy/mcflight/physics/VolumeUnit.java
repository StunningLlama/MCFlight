package thepowderguy.mcflight.physics;

import thepowderguy.mcflight.util.Vec3;

public class VolumeUnit {

	private final Vec3 position;
	private Vec3 prevtransformposition = new Vec3();
	private Vec3 transformposition = new Vec3();
	Vec3 prevForce = new Vec3();
	Vec3 force = new Vec3();
	

	public VolumeUnit(double x, double y, double z) {
		position = new Vec3(x, y, z);
	}

	public void update(Vec3 newpos) {
		this.prevtransformposition = this.transformposition;
		this.transformposition = newpos;
	}

	public Vec3 getPosition() {
		return position;
	}

	public Vec3 getInterpolatedPosition(float partialTicks) {
		return Vec3.interpolate(this.prevtransformposition, this.transformposition, partialTicks);
	}
	
	public VolumeUnit getInstance() {
		return new VolumeUnit(position.x, position.y, position.z);
	}
	

	public Vec3 getForce(float partialTicks) {
		return Vec3.interpolate(prevForce, force, partialTicks);
	}
	
	public void setForce(Vec3 inforce) {
		prevForce = force;
		force = inforce;
	}
}
