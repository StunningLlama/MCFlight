package thepowderguy.mcflight.math;

public class Vector {
	
	public double x;
	public double y;
	public double z;
	
	public Vector(double xi, double yi, double zi) {
		x = xi;
		y = yi;
		z = zi;
	}

	public Vector(Vector v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vector() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}
	
	public static Vector cross(Vector a, Vector b) {
		return new Vector(a.y*b.z - a.z*b.y, a.z* b.x - a.x*b.z, a.x*b.y - a.y*b.x);
	}

	public static double dot(Vector a, Vector b) {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	
	public static double mag(Vector a) {
		return Math.sqrt(a.x*a.x + a.y*a.y + a.z*a.z);
	}
	
	public double mag() {
		return Math.sqrt(x*x + y*y + z*z);
	}

	public static double zeroIfNaN(double a) {
		if (Double.isFinite(a))
			return a;
		return 0;
	}
	
	public static double angle(Vector a, Vector b) {
		double dotprod = Vector.dot(a, b);
		return zeroIfNaN(Math.toDegrees(Math.acos(dotprod/(a.mag() * b.mag()))));
	}
	
	public static Vector add(Vector a, Vector b) {
		return new Vector(a.x+b.x, a.y+b.y, a.z+b.z);
	}
	public static Vector add(Vector a, Vector b, Vector c) {
		return new Vector(a.x+b.x+c.x, a.y+b.y+c.y, a.z+b.z+c.z);
	}
	
	public void add(Vector a) {
		x += a.x;
		y += a.y;
		z += a.z;
	}

	public Vector mul(double a) {
		x *= a;
		y *= a;
		z *= a;
		return this;
	}
	
	public static Vector addn(Vector ... vectors) {
		double xi = 0;
		double yi = 0;
		double zi = 0;
		for (Vector v: vectors) {
			xi += v.x;
			yi += v.y;
			zi += v.z;
		}
		return new Vector(xi, yi, zi);
	}

	public static Vector mul(Vector a, double b) {
		return new Vector(a.x*b, a.y*b, a.z*b);
	}

	public static Vector unitvector(Vector a) {
		if (a.mag() == 0)
			return new Vector(0.0, 0.0, 0.0);
		return Vector.mul(a, 1.0/a.mag());
	}

	private static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	private static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
	}

	public static double cosTheta(Vector a, Vector b) {
		return Vector.dot(a, b)/(a.mag()*b.mag());
	}

	public static double sinTheta(Vector a, Vector b) {
		return Vector.cross(a, b).mag()/(a.mag()*b.mag());
	}
	
	public static Vector AxisAngleRotation(Vector axis, Vector point, double theta) {
		return Vector.add(Vector.mul(point, dCos(theta)), Vector.mul(axis, Vector.dot(axis, point)*(1.0-dCos(theta))), Vector.mul(Vector.cross(axis, point), dSin(theta)));
	}
	
}
