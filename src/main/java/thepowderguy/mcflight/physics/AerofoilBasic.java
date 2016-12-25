package thepowderguy.mcflight.physics;

public class AerofoilBasic extends ControlSurface {

	public AerofoilBasic(double x, double y, double z, double scale, double area) {
		super(x, y, z, scale, area);
	}

	@Override
	public double getLiftFromAlpha(double a) {
		return dSin(a/2.0)*dCos(a/2.0)*2.0;
	}
	

	public double getDragFromAlpha(double a) {
		return dSin(a/2.0)*dSin(a/2.0)*2.0;
	}
	

	public static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	public static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
	}
}
