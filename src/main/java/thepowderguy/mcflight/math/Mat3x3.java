package thepowderguy.mcflight.math;

public class Mat3x3 {
	double[][] values;
	public Mat3x3(double[][] in) {
		values = in;
	}
	
	public static Vector mul(Mat3x3 a, Vector b) {
		return new Vector(a.values[0][0]*b.x+a.values[0][1]*b.y+a.values[0][2]*b.z,
				a.values[1][0]*b.x+a.values[1][1]*b.y+a.values[1][2]*b.z,
				a.values[2][0]*b.x+a.values[2][1]*b.y+a.values[2][2]*b.z
				);
	}

	public static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	public static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
	}
	
	public static Mat3x3 mul(Mat3x3 a, Mat3x3 b) {
		return new Mat3x3(new double[][] {
				{a.values[0][0] * b.values[0][0] + a.values[0][1] * b.values[1][0] + a.values[0][2] * b.values[2][0],
				 a.values[0][0] * b.values[0][1] + a.values[0][1] * b.values[1][1] + a.values[0][2] * b.values[2][1],
				 a.values[0][0] * b.values[0][2] + a.values[0][1] * b.values[1][2] + a.values[0][2] * b.values[2][2]},
				{a.values[1][0] * b.values[0][0] + a.values[1][1] * b.values[1][0] + a.values[1][2] * b.values[2][0],
				 a.values[1][0] * b.values[0][1] + a.values[1][1] * b.values[1][1] + a.values[1][2] * b.values[2][1],
				 a.values[1][0] * b.values[0][2] + a.values[1][1] * b.values[1][2] + a.values[1][2] * b.values[2][2]},
				{a.values[2][0] * b.values[0][0] + a.values[2][1] * b.values[1][0] + a.values[2][2] * b.values[2][0],
				 a.values[2][0] * b.values[0][1] + a.values[2][1] * b.values[1][1] + a.values[2][2] * b.values[2][1],
				 a.values[2][0] * b.values[0][2] + a.values[2][1] * b.values[1][2] + a.values[2][2] * b.values[2][2]}}
				);
	}
	
	public Vector transform(Vector in) {
		return Mat3x3.mul(this, in);
	}
	
	public static Vector transform(double yaw, double pitch, double roll, Vector in) {
		return Mat3x3.mul(getTransformMatrix(yaw, pitch, roll), in);
	}
	
	public static Mat3x3 getTransformMatrix(double yaw, double pitch, double roll) {
		Mat3x3 rRoll = new Mat3x3(new double[][]{
			{dCos(roll),	-dSin(roll),	0.0	},
			{dSin(roll),	dCos(roll),		0.0	},
			{0.0,			0.0,			1.0	}});
		Mat3x3 rPitch = new Mat3x3(new double[][]{
			{1.0,	0.0,			0.0				},
			{0.0,	dCos(pitch),	-dSin(pitch)	},
			{0.0,	dSin(pitch),	dCos(pitch)		}});
		Mat3x3 rYaw = new Mat3x3(new double[][]{
			{dCos(yaw),		0.0,	dSin(yaw)	},
			{0.0,			1.0,	0.0			},
			{-dSin(yaw),	0.0,	dCos(yaw)	}});
		return (Mat3x3.mul(Mat3x3.mul(rYaw, rPitch), rRoll)); //yaw x pitch x roll
	}

	public static Vector getangles(Mat3x3 in) {
		double pdeg = -Math.asin(in.values[1][2]);
		double pitch = Math.toDegrees(pdeg);
		double yaw = Math.toDegrees(Math.atan2(in.values[0][2], in.values[2][2]));
		double roll = Math.toDegrees(Math.atan2(in.values[1][0], in.values[1][1]));
		return new Vector(yaw, pitch, roll);
	}
	public static Vector getangles(Vector x, Vector y, Vector z) {
		double pdeg = -Math.asin(z.y);
		double pitch = Math.toDegrees(pdeg);
		double yaw = Math.toDegrees(Math.atan2(z.x, z.z));
		double roll = Math.toDegrees(Math.atan2(x.y, y.y));
		return new Vector(yaw, pitch, roll);
	}
}