package thepowderguy.mcflight.common.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.physics.AerofoilBasic;
import thepowderguy.mcflight.physics.AerofoilCtrlSuface;
import thepowderguy.mcflight.physics.AerofoilWing;
import thepowderguy.mcflight.physics.CollisionPoint;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

public class EntityBiplane extends EntityAirplane {
	
	private static int SurfaceWing = 0;
	private static int SurfaceElevatorControl = 1;
	private static int SurfaceElevator = 2;
	private static int SurfaceRudderControl = 3;
	private static int SurfaceRudder = 4;
	private static int SurfaceAlieronLeft = 5;
	private static int SurfaceAlieronRight = 6;
	
	static {
		engineSound = "mcflight:airplane.biplane.engine";
		collisionPoints = new CollisionPoint[] {
					new CollisionPoint(5.5, -16, 7.5, 0.0004),
					new CollisionPoint(-5.5, -16, 7.5, 0.0004),
					new CollisionPoint(0.0, -9.0, -49.0, 0.0004),
					new CollisionPoint(0.0, 0.0, 22.0, 0.001),
					new CollisionPoint(-50.0, -5.0, 0.0, 0.001),
					new CollisionPoint(50.0, -5.0, 0.0, 0.001)
			};
		scale = RenderBiplane.scale/16.0;
		airplaneItem = Mcflight.item_airplane_biplane;
		fuelCapacity = 100.0f;
	}
	
	public EntityBiplane(World worldIn) {
		super(worldIn);
	}
	
	public EntityBiplane(World worldIn, double x, double y, double z, float fuel)
	{
		super(worldIn, x, y, z, fuel);
	}
    
	@Override
	public void init(float fuel) {
		super.init(fuel);
		controlSurfaces = new ControlSurface[] {
				new AerofoilWing(0.0, 0.0, -3.0, EntityBiplane.scale, 4.0),
				new AerofoilCtrlSuface(0.0, 0.0, -50.5, EntityBiplane.scale, 0.25),
				new AerofoilBasic(0.0, 0.0, -20.0, EntityBiplane.scale, 0.2),
				new AerofoilCtrlSuface(0.0, 6.0, -50.5, EntityBiplane.scale, 0.25),
				new AerofoilBasic(0.0, 6.0, -44.0, EntityBiplane.scale, 0.2),
				new AerofoilCtrlSuface(-23, -7.0, -11.0, EntityBiplane.scale, 0.2),
				new AerofoilCtrlSuface(23, -7.0, -11.0, EntityBiplane.scale, 0.2),
		};
	}

	@Override
	protected void updateControlSurfaceNormals(Mat3 transform, double forceElevators, double forceRudder, double forceAlierons){
		controlSurfaces[SurfaceWing].updateNormal(vup);
		controlSurfaces[SurfaceElevatorControl].updateNormal(Vec3.AxisAngleRotation(vside, vup, forceElevators));
		controlSurfaces[SurfaceElevator].updateNormal(vup);
		controlSurfaces[SurfaceRudderControl].updateNormal(Vec3.AxisAngleRotation(vup, vside, forceRudder));
		controlSurfaces[SurfaceRudder].updateNormal(vside);
		controlSurfaces[SurfaceAlieronLeft].updateNormal(Vec3.AxisAngleRotation(vside, vup, forceAlierons));
		controlSurfaces[SurfaceAlieronRight].updateNormal(Vec3.AxisAngleRotation(vside, vup, -forceAlierons));
	}

	@Override
	protected boolean canSteer() {
		return collisionPoints[2].isCollided;
	}

	@Override
	protected ControlSurface getWing() {
		return controlSurfaces[SurfaceWing];
	}
	
}
