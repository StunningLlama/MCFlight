package thepowderguy.mcflight.math;

import net.minecraft.util.math.Vec3d;

public class Vec3 {
	
	public double x;
	public double y;
	public double z;
	
	public Vec3(double xi, double yi, double zi) {
		x = xi;
		y = yi;
		z = zi;
	}

	public Vec3(Vec3 v) {
		x = v.x;
		y = v.y;
		z = v.z;
	}

	public Vec3() {
		this.x = 0.0;
		this.y = 0.0;
		this.z = 0.0;
	}
	
	public void set(Vec3 a) {
		x = a.x;
		y = a.y;
		z = a.z;
	}
	
	public static Vec3 cross(Vec3 a, Vec3 b) {
		return new Vec3(a.y*b.z - a.z*b.y, a.z* b.x - a.x*b.z, a.x*b.y - a.y*b.x);
	}

	public static double dot(Vec3 a, Vec3 b) {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	
	public static double mag(Vec3 a) {
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
	
	public static double angle(Vec3 a, Vec3 b) {
		double dotprod = Vec3.dot(a, b);
		return zeroIfNaN(Math.toDegrees(Math.acos(dotprod/(a.mag() * b.mag()))));
	}
	
	public static Vec3 add(Vec3 a, Vec3 b) {
		return new Vec3(a.x+b.x, a.y+b.y, a.z+b.z);
	}

	public static Vec3 sub(Vec3 a, Vec3 b) {
		return new Vec3(a.x-b.x, a.y-b.y, a.z-b.z);
	}
	
	public static Vec3 add(Vec3 a, Vec3 b, Vec3 c) {
		return new Vec3(a.x+b.x+c.x, a.y+b.y+c.y, a.z+b.z+c.z);
	}
	
	public void add(Vec3 a) {
		x += a.x;
		y += a.y;
		z += a.z;
	}
	
	public void add(double x, double y, double z) {
		x += x;
		y += y;
		z += z;
	}

	public Vec3 mul(double a) {
		x *= a;
		y *= a;
		z *= a;
		return this;
	}
	
	public static Vec3 addn(Vec3 ... vectors) {
		double xi = 0;
		double yi = 0;
		double zi = 0;
		for (Vec3 v: vectors) {
			xi += v.x;
			yi += v.y;
			zi += v.z;
		}
		return new Vec3(xi, yi, zi);
	}

	public static Vec3 proj(Vec3 x, Vec3 a, Vec3 b) {
		Vec3 norm = Vec3.cross(a, b);
		norm = Vec3.unitvector(norm);
		return Vec3.sub(x, Vec3.mul(x, Vec3.dot(x, norm)));
	}
	
	public static Vec3 mul(Vec3 a, double b) {
		return new Vec3(a.x*b, a.y*b, a.z*b);
	}

	public static Vec3 unitvector(Vec3 a) {
		if (a.mag() == 0)
			return new Vec3(0.0, 0.0, 0.0);
		return Vec3.mul(a, 1.0/a.mag());
	}

	private static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	private static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
	}

	public static double cosTheta(Vec3 a, Vec3 b) {
		return zeroIfNaN(Vec3.dot(a, b)/(a.mag()*b.mag()));
	}

	public static double sinTheta(Vec3 a, Vec3 b) {
		return Vec3.cross(a, b).mag()/(a.mag()*b.mag());
	}
	
	public static Vec3 AxisAngleRotation(Vec3 axis, Vec3 point, double theta) {
		return Vec3.add(Vec3.mul(point, dCos(theta)), Vec3.mul(axis, Vec3.dot(axis, point)*(1.0-dCos(theta))), Vec3.mul(Vec3.cross(axis, point), dSin(theta)));
	}
	
	public static double distsq(Vec3 a, Vec3 b) {
		return (a.x-b.x)*(a.x-b.x) + (a.y-b.y)*(a.y-b.y) + (a.z-b.z)*(a.z-b.z);
	}

	public static double dist(Vec3 a, Vec3 b) {
		return Math.sqrt(Vec3.distsq(a, b));
	}
	public Vec3d toVec3d() {
		return new Vec3d(x, y, z);
	}
	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}
}
