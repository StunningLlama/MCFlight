package thepowderguy.mcflight.common.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.math.ControlSurface;
import thepowderguy.mcflight.math.Mat3;
import thepowderguy.mcflight.math.Vec3;

public class EntityBiplane extends EntityAirplane {
	
	private static int SurfaceElevatorControl = 0;
	private static int SurfaceElevator = 1;
	private static int SurfaceRudderControl = 2;
	private static int SurfaceRudder = 3;
	private static int SurfaceAlieronLeft = 4;
	private static int SurfaceAlieronRight = 5;
	
	static {
		engineSound = "mcflight:airplane.biplane.engine";
		collisionPoints = new Vec3[] {
					new Vec3(5.5, -16, 7.5),
					new Vec3(-5.5, -16, 7.5),
					new Vec3(0.0, -9.0, -49.0),
					new Vec3(0.0, 0.0, 22.0),
					new Vec3(-50.0, -5.0, 0.0),
					new Vec3(50.0, -5.0, 0.0)
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
				new ControlSurface(0.0, 0.0, -50.5, EntityBiplane.scale, 2.0),
				new ControlSurface(0.0, 0.0, -44.0, EntityBiplane.scale, 1.0),
				new ControlSurface(0.0, 6.0, -50.5, EntityBiplane.scale, 2.0),
				new ControlSurface(0.0, 6.0, -44.0, EntityBiplane.scale, 1.0),
				new ControlSurface(-23, -7.0, -11.0, EntityBiplane.scale, 1.5),
				new ControlSurface(23, -7.0, -11.0, EntityBiplane.scale, 1.5),
		};
	}

	@Override
	protected void updateControlSurfaceNormals(Mat3 transform, double forceElevators, double forceRudder, double forceAlierons){
		controlSurfaces[SurfaceElevatorControl].updateNormal(Vec3.AxisAngleRotation(vside, vup, forceElevators));
		controlSurfaces[SurfaceElevator].updateNormal(vup);
		controlSurfaces[SurfaceRudderControl].updateNormal(Vec3.AxisAngleRotation(vup, vside, forceRudder));
		controlSurfaces[SurfaceRudder].updateNormal(vside);
		controlSurfaces[SurfaceAlieronLeft].updateNormal(Vec3.AxisAngleRotation(vside, vup, forceAlierons));
		controlSurfaces[SurfaceAlieronRight].updateNormal(Vec3.AxisAngleRotation(vside, vup, -forceAlierons));
	}
}
