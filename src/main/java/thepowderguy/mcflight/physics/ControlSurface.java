package thepowderguy.mcflight.physics;

import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

//** NOTE: implement this class when needed.
public abstract class ControlSurface {

	public final Vec3 position;
	protected Vec3 prevtransformposition = new Vec3();
	protected Vec3 transformposition = new Vec3();
	public Vec3 normal_position;
	public final double wingArea;
	protected double angleOfAttack;
	Vec3 prevForce = new Vec3();
	Vec3 force = new Vec3();
	protected Vec3 normal;
	protected Vec3 rotationaxis;

	public abstract double getDragFromAlpha(double d);
	public abstract double getLiftFromAlpha(double d);
	public abstract boolean isStalled();
	public abstract ControlSurface getInstance();
	
	public ControlSurface(double x, double y, double z, double scale, double area, Vec3 normalin, Vec3 rotationaxisin) {
		position = new Vec3(x*scale, y*scale, z*scale);
		wingArea = area;
		normal_position = new Vec3();
		normal = normalin;
		rotationaxis = rotationaxisin;
	}
	
	public void updateNormal(Mat3 transform, double rotation) {
		Vec3 axis = transform.transform(rotationaxis);
		Vec3 tmpNorm = transform.transform(normal);
		normal_position.set(Vec3.AxisAngleRotation(axis, tmpNorm, rotation));
	}

	public void updateNormal(Mat3 transform) {
		normal_position.set(transform.transform(normal));
	}
	
	public Vec3 getNormal() {
		return normal_position;
	}
	
	public Vec3 getInterpolatedPosition(float partialTicks) {
		return Vec3.interpolate(prevtransformposition, transformposition, partialTicks);
	}
	
	public double getAngleOfAttack() {
		return this.angleOfAttack;
	}
	
	public void setAngleOfAttack(double angle) {
		angleOfAttack = angle;
	}
	
	public Vec3 getForce(float partialTicks) {
		return Vec3.interpolate(prevForce, force, partialTicks);
	}
	
	public void setForce(Vec3 inforce) {
		prevForce = force;
		force = inforce;
	}
	
	public void setPosition(Vec3 pos) {
		prevtransformposition = transformposition;
		transformposition = pos;
	}
}
