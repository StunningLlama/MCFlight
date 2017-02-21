package thepowderguy.mcflight.common.entity.biplane;

import java.util.ArrayList;

import net.minecraft.world.World;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplane;
import thepowderguy.mcflight.physics.AerofoilBasic;
import thepowderguy.mcflight.physics.AerofoilCtrlSuface;
import thepowderguy.mcflight.physics.AerofoilWing;
import thepowderguy.mcflight.physics.CollisionPoint;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.physics.VolumeUnit;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

public class EntityBiplane extends EntityAirplane {
	
	private static int SurfaceElevatorControl1;
	private static int SurfaceElevatorControl2;
	private static int SurfaceRudderControl;
	private static int SurfaceAlieronLeft;
	private static int SurfaceAlieronRight;
	
	static {
		engineSound = "mcflight:airplane.biplane.engine";
		scale = RenderBiplane.scale/16.0;
		airplaneItem = Mcflight.item_airplane_biplane;
		fuelCapacity = 100.0f;
		defaultAirfoilSections = new ArrayList<ControlSurface>();
		for (double a = -8; a <= 12.1; a += 20)
			for (double b = -5.25; b <= 9.1; b += 9.5)
				for (double c = -50+100.0/18.0; c <= 50.1; c += 100.0/9.0)
					defaultAirfoilSections.add(new AerofoilWing			(c,	a,	b,	EntityBiplane.scale,	2.0/36,	new Vec3(0, 1, 0),	null)             );
		//defaultAirfoilSections.add(new AerofoilWing			(10,	0.0,	0.0,	EntityBiplane.scale,	2.0,	new Vec3(0, 1, 0),	null)             );
		SurfaceElevatorControl1 = defaultAirfoilSections.size();
		defaultAirfoilSections.add(new AerofoilCtrlSuface	(9.0,	0.0,	-50.5,	EntityBiplane.scale,	0.125,	new Vec3(0, 1, 0),	new Vec3(1, 0, 0)));
		defaultAirfoilSections.add(new AerofoilBasic		(9.0,	0.0,	-44.0,	EntityBiplane.scale,	0.1,	new Vec3(0, 1, 0),	null)             );
		SurfaceElevatorControl2 = defaultAirfoilSections.size();
		defaultAirfoilSections.add(new AerofoilCtrlSuface	(-9.0,	0.0,	-51.5,	EntityBiplane.scale,	0.125,	new Vec3(0, 1, 0),	new Vec3(1, 0, 0)));
		defaultAirfoilSections.add(new AerofoilBasic		(-9.0,	0.0,	-43.5,	EntityBiplane.scale,	0.1,	new Vec3(0, 1, 0),	null)             );
		SurfaceRudderControl = defaultAirfoilSections.size();
		defaultAirfoilSections.add(new AerofoilCtrlSuface	(0.0,	6.0,	-50.5,	EntityBiplane.scale,	0.25,	new Vec3(1, 0, 0),	new Vec3(0, 1, 0)));
		defaultAirfoilSections.add(new AerofoilBasic		(0.0,	6.0,	-44.0,	EntityBiplane.scale,	0.2,	new Vec3(1, 0, 0),	null)             );
		SurfaceAlieronLeft = defaultAirfoilSections.size();
		defaultAirfoilSections.add(new AerofoilCtrlSuface	(-23,	-7.0,	-12.0,	EntityBiplane.scale,	0.2,	new Vec3(0, 1, 0),	new Vec3(1, 0, 0)));
		SurfaceAlieronRight = defaultAirfoilSections.size();
		defaultAirfoilSections.add(new AerofoilCtrlSuface	(23,	-7.0,	-12.0,	EntityBiplane.scale,	0.2,	new Vec3(0, 1, 0),	new Vec3(1, 0, 0)));

		defaultCollisionPoints = new ArrayList<CollisionPoint>();
		defaultCollisionPoints.add(new CollisionPoint	(5.5,	-16,	7.5,	0.4,	3.0, 1.0)); //wheel
		defaultCollisionPoints.add(new CollisionPoint	(-5.5,	-16,	7.5,	0.4,	3.0, 1.0)); //wheel
		defaultCollisionPoints.add(new CollisionPoint	(0.0,	-9.0,	-49.0,	0.4,	1.0, 1.0)); //tail wheel
		defaultCollisionPoints.add(new CollisionPoint	(0.0,	-2.5,	21.0,	1,		1.0, 0.5)); //engine
		defaultCollisionPoints.add(new CollisionPoint	(-50.0,	-8,		9.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(-50.0,	-8,		-10.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(50.0,	-8,		9.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(50.0,	-8,		-10.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(-50.0,	12,		9.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(-50.0,	12,		-10.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(50.0,	12,		9.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(50.0,	12,		-10.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(0.0,	9.0,	-49.0,	0.4,	1.0, 0.5)); //tail wheel
		defaultCollisionPoints.add(new CollisionPoint	(0.0,	12,		 0,		0.4,	1.0, 0.5)); //tail wheel
		defaultCollisionPoints.add(new CollisionPoint	(0.0,	-8,		-10.0,	1,		1.0, 0.5)); //wing
		defaultCollisionPoints.add(new CollisionPoint	(0.0,	-8,		18.0,	1,		1.0, 0.5)); //engine
		
		defaultVolumeUnits = new ArrayList<VolumeUnit>();
		for (int x = -6; x <= 6; x += 4)
			for (int y = -8; y <= 8; y += 4)
				for (int z = -30; z <= 20; z += 4)
					defaultVolumeUnits.add(new VolumeUnit(x*scale, y*scale, z*scale));
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
	}

	@Override
	protected void updateControlSurfaceNormals(Mat3 transform, double forceElevators, double forceRudder, double forceAlierons){
		for (ControlSurface cs: airfoilSections) {
			if (!(cs instanceof AerofoilCtrlSuface))
				cs.updateNormal(transform);
		}
		airfoilSections[SurfaceElevatorControl1].updateNormal(transform, forceElevators);
		airfoilSections[SurfaceElevatorControl2].updateNormal(transform, forceElevators);
		airfoilSections[SurfaceRudderControl].updateNormal(transform, forceRudder);
		airfoilSections[SurfaceAlieronLeft].updateNormal(transform, forceAlierons);
		airfoilSections[SurfaceAlieronRight].updateNormal(transform, -forceAlierons);
	}

	@Override
	protected boolean canSteer() {
		return collisionPoints[2].isCollided;
	}

	@Override
	protected ControlSurface getWing() {
		return null;
	//	return airfoilSections[SurfaceWingL];
	}
	
}
