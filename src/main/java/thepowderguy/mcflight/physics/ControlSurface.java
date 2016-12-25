package thepowderguy.mcflight.physics;

import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

//** NOTE: implement this class when needed.
public abstract class ControlSurface {

	protected final Vec3 position;
	protected Vec3 transformposition;
	protected Vec3 normal;
	protected final double wingArea;
	protected double angleOfAttack;
	Vec3 force;

	public abstract double getDragFromAlpha(double d);
	public abstract double getLiftFromAlpha(double d);
	
	public void applyAerodynamicForces(Mat3 transform, Vec3 motion, Vec3 angularVelocity, Vec3 vel, double coefficient, double ctrlModifier) {
		double camber = 0f;
		Vec3 out = Vec3.unitvector(Vec3.cross(vel, Vec3.cross(normal, vel)));
		angleOfAttack = Vec3.angle(normal, vel) - 90.0;
		double lift = getLiftFromAlpha(angleOfAttack+camber) * coefficient * wingArea;
		double inducedDrag = getDragFromAlpha(angleOfAttack+camber) * coefficient * wingArea;
		Vec3 lift_vec = Vec3.mul(out, lift);
		Vec3 inddrag_vec = Vec3.unitvector(vel).mul(-1.0 * inducedDrag);
		force = Vec3.add(lift_vec, inddrag_vec);
		
		motion.add(force);
		transformposition = transform.transform(position);
		angularVelocity.add(Vec3.cross(transformposition, force).mul(ctrlModifier));
	}
	
	/*
	public void applyAerodynamicForces(Mat3 transform, Vec3 motion, Vec3 angularVelocity, Vec3 vel, double coefficient, double ctrlModifier) {
		double dragMagnitude = Vec3.cosTheta(vel, normal) * coefficient * wingArea;
		Vec3 force = Vec3.mul(normal, dragMagnitude);
		motion.add(force);
		transformposition = transform.transform(position);
		angularVelocity.add(Vec3.cross(transformposition, force).mul(ctrlModifier));
	}
	*/
	
	public ControlSurface(double x, double y, double z, double scale, double area) {
		position = new Vec3(x*scale, y*scale, z*scale);
		wingArea = area;
		normal = new Vec3();
	}
	
	public void updateNormal(Vec3 n) {
		normal.set(n);
	}
	
	public Vec3 getPosition() {
		return this.transformposition;
	}
	
	public double getAngleOfAttack() {
		return this.angleOfAttack;
	}
	
	public Vec3 getForce() {
		return new Vec3(force);
	}
}
