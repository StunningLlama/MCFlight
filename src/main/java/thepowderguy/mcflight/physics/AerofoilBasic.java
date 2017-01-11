package thepowderguy.mcflight.physics;

import thepowderguy.mcflight.util.Vec3;

public class AerofoilBasic extends ControlSurface {

	public AerofoilBasic(double x, double y, double z, double scale, double area, Vec3 normal, Vec3 rotationaxis) {
		super(x, y, z, scale, area, normal, rotationaxis);
	}


	@Override
	public double getLiftFromAlpha(double a) {
		return dSin(a)*dCos(a)*2.0;
	}
	

	public double getDragFromAlpha(double a) {
		return dSin(a)*dSin(a)*2.0;
	}
	

	public static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	public static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
	}

	@Override
	public boolean isStalled() {
		return angleOfAttack < -45.0 || angleOfAttack > 45.0;
	}

	@Override
	public ControlSurface getInstance() {
		return new AerofoilBasic(this.position.x, position.y, position.z, 1, wingArea, Vec3.copy(normal), Vec3.copy(rotationaxis));
	}
}
