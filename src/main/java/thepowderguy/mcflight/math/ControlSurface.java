package thepowderguy.mcflight.math;

public class ControlSurface {
	private Vec3 position;
	private Vec3 transformposition;
	private Vec3 normal = new Vec3();
	private double wingArea;
	public ControlSurface(double x, double y, double z, double scale) {
		position = new Vec3(x*scale, y*scale, z*scale);
	}
	public void updateNormal(Vec3 n) {
		normal.set(n);
	}
	public Vec3 getPosition() {
		return this.transformposition;
	}
	public void applyAerodynamicForces(Mat3 transform, Vec3 motion, Vec3 angularVelocity, Vec3 vel, double coefficient, double ctrlModifier) {
		double dragMagnitude = Vec3.cosTheta(vel, normal) * coefficient;
		Vec3 force = Vec3.mul(normal, dragMagnitude);
		motion.add(force);
		transformposition = transform.transform(position);
		angularVelocity.add(Vec3.cross(transformposition, force).mul(ctrlModifier));
	}
}
