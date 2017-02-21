package thepowderguy.mcflight.common.entity;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import thepowderguy.mcflight.client.InterfaceKeyHandler;
import thepowderguy.mcflight.client.RenderAirplaneInterface;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.item.AircraftPaint;
import thepowderguy.mcflight.common.item.Kerosene;
import thepowderguy.mcflight.common.packet.AirplaneStatePacket;
import thepowderguy.mcflight.common.packet.AirplaneUpdatePacket;
import thepowderguy.mcflight.physics.CollisionPoint;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.physics.VolumeUnit;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

public abstract class EntityAirplane extends Entity {
	
	public static InterfaceKeyHandler input;
	public InventoryBasic inv;
//	public double drag;
	public static boolean useMouseInput = true;
	private EntityAirplaneCamera cam;
	Minecraft minecraft;
	
	public EntityAirplane(World worldIn) {
		super(worldIn);
		init(EntityAirplane.fuelCapacity);
	}
	
	private double yoffset = -1.0;
	
	@Override
    public void setPosition(double x, double y, double z)
    {
        this.posX = x;
        this.posY = y;
        this.posZ = z;
        float f = this.width / 2.0F;
        float f1 = this.height;
        this.setEntityBoundingBox(new AxisAlignedBB(x - (double)f, y + yoffset, z - (double)f, x + (double)f, y + (double)f1  + yoffset, z + (double)f));
    }

	public EntityAirplane(World worldIn, double x, double y, double z) {
		this(worldIn, x, y, z, EntityAirplane.fuelCapacity);
	}

	public EntityAirplane(World worldIn, double x, double y, double z, float fuel_i) {
		super(worldIn);
		this.setPosition(x, y, z);
		this.motionX = this.motionY = this.motionZ = this.prevPosX = this.prevPosY = this.prevPosZ = 0;
		init(fuel_i);
	}
	
	
	protected void init(float fuelcap) {
		if (serverSide()) {
			sfuel = fuelcap;
			this.setFuel(sfuel);
		}
		this.setFuselageColor(EnumDyeColor.SILVER);
		this.setWingColor(EnumDyeColor.SILVER);
		this.setSize(2.0f, 2.0f);
		minecraft = Minecraft.getMinecraft();
		
		inv = new InventoryBasic("ASDF", false, 17);
		if (clientSide())
			cam = new EntityAirplaneCamera(world, this);
		
		this.airfoilSections = new ControlSurface[defaultAirfoilSections.size()];
		this.collisionPoints = new CollisionPoint[defaultCollisionPoints.size()];
		this.volumeUnits = new VolumeUnit[defaultVolumeUnits.size()];
		for (int i = 0; i < defaultAirfoilSections.size(); i++) {
			this.airfoilSections[i] = this.defaultAirfoilSections.get(i).getInstance();
		}
		for (int i = 0; i < defaultCollisionPoints.size(); i++) {
			this.collisionPoints[i] = this.defaultCollisionPoints.get(i).getInstance();
		}
		for (int i = 0; i < defaultVolumeUnits.size(); i++) {
			this.volumeUnits[i] = this.defaultVolumeUnits.get(i).getInstance();
		}
	}

	public boolean canBeCollidedWith()
	{
		return !this.isDead;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tagCompound) {
		NBTTagList rot = tagCompound.getTagList("Rotation", 5);
		rotationRoll = rot.getFloatAt(2);
		NBTTagList motionrot = tagCompound.getTagList("MotionRotation", 6);
		angVelX = motionrot.getDoubleAt(0);
		angVelY = motionrot.getDoubleAt(1);
		angVelZ = motionrot.getDoubleAt(2);
		throttle = tagCompound.getDouble("Engine");
		sfuel = tagCompound.getFloat("Fuel");
		this.setFuel(sfuel);
		this.setFuselageColor(EnumDyeColor.byMetadata(tagCompound.getInteger("ColFuselage")));
		this.setWingColor(EnumDyeColor.byMetadata(tagCompound.getInteger("ColWing")));

        NBTTagList nbttaglist = tagCompound.getTagList("Items", 10);
        
        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < 15)
            {
                this.inv.setInventorySlotContents(j, new ItemStack(nbttagcompound));
            }
        }
	}
	
	public void readEntityFromItemStack(ItemStack item) {
		NBTTagCompound tagCompound = item.getSubCompound("Color");
		if (tagCompound == null) return;
		sfuel = tagCompound.getFloat("Fuel");
		this.setFuel(sfuel);
		this.setFuselageColor(EnumDyeColor.byMetadata(tagCompound.getInteger("ColFuselage")));
		this.setWingColor(EnumDyeColor.byMetadata(tagCompound.getInteger("ColWing")));
	}

	public void writeEntityToItemStack(ItemStack item) {
		NBTTagCompound tagCompound = item.getOrCreateSubCompound("Color");
		tagCompound.setFloat("Fuel", this.getFuel());
		tagCompound.setInteger("ColFuselage", this.getFuselageColor().getMetadata());
		tagCompound.setInteger("ColWing", this.getWingColor().getMetadata());
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		tagCompound.setTag("Rotation", this.newFloatNBTList(rotationYaw, rotationPitch, rotationRoll));
		tagCompound.setTag("MotionRotation", this.newDoubleNBTList(angVelX, angVelY, angVelZ));
		tagCompound.setDouble("Engine", throttle);
		tagCompound.setFloat("Fuel", this.getFuel());
		
		tagCompound.setInteger("ColFuselage", this.getFuselageColor().getMetadata());
		tagCompound.setInteger("ColWing", this.getWingColor().getMetadata());

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < 15; ++i)
        {
            ItemStack itemstack = this.inv.getStackInSlot(i);

            if (!itemstack.isEmpty())
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte)i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        tagCompound.setTag("Items", nbttaglist);
	}

	public AxisAlignedBB getCollisionBox(Entity entityIn)
	{
		return entityIn.getEntityBoundingBox();
	}

	public AxisAlignedBB getBoundingBox()
	{
		return this.getEntityBoundingBox();
	}

	private long tick = 0;

	public float sfuel = 0;
	
	public float rotationRoll = 0;
	public float prevRotationRoll = 0;
	public double angVelX = 0.0;
	public double angVelY = 0.0;
	public double angVelZ = 0.0;
	public double throttle = 0;
	public double velocity = 0.0;
	public boolean collidedX = false;
	public boolean collidedY = false;
	public boolean collidedZ = false;
	public double mass = 800.0;
	//weight is 800kg
	
	// 1u = 800kg*1m/t^2
	// 1u = 320000 kg*m/s^2
	// u = x KN
	// u = x 1000*kg*m/s^2
	// 320000 kg*m/s^2 = x *1000*kg*m/s^2
	// 1u=320kn
	// f kn = w kg * a m/t^2
	// f 1000*kg*m/s^2 = w kg * a m/t^2
	// f 1000 = w * a * 400
	// a = 10f / 4w
	// 1 m/tick = 20 degrees/tick
	// 320 m/tick = x d/t
	
	public Vec3 vfwd;
	public Vec3 vup;
	public Vec3 vside;
	public Vec3 inddrag_vec = new Vec3();
	public Vec3 drag_vec = new Vec3();
	public Vec3 thrust_vec = new Vec3();
	public Vec3 gravity_vec = new Vec3();
	
	public Vec3 mo = new Vec3();
	
	//units are kN
	public static double gravity_const = 0.01;
	public static double drag_const = 1.28;//0.025;
	public static double lift_const = 11.2;//0.12;
	public static double thrust_const = 3.2;
	public static double conversionconst = 2.5;
	
	public static double friction_const = 0.001;
	public static double dragMul_forward = 1.0;
	public static double dragMul_sideways = 1.0;
	public static double controlSensitivity = 20.0;
	public static double torqueMultiplier = 0.0625;
	public static double rotationSpeedDecay = 0.95;
	public static double elevTrim = -0.25;
	
	public static double mouseX = 0;
	public static double mouseY = 0;
	
	public double prevMotionX = 0;
	public double prevMotionY = 0;
	public double prevMotionZ = 0;
	public boolean stall = false;
	public double damage = 0.0;

	double prevForceElevator = 0.0;
	double forceElevator = 0.0;
	double prevForceAlierons = 0.0;
	double forceAlierons = 0.0;
	double prevForceRudder = 0.0;
	double forceRudder = 0.0;
	public double prevProppos = 0.0;
	public double propPos = 0.0;
	double propVel = 0.0;
	public boolean vectorsInitialized = false;
	public boolean ismoving = false;
	
	public Vec3 frictionmotiondiff;
	public Vec3 totalnormalforce;

	protected static String engineSound;
	public String text = "";
	protected static double scale;
	protected static Item airplaneItem;
	protected static List<CollisionPoint> defaultCollisionPoints;
	protected static List<ControlSurface> defaultAirfoilSections;
	protected static List<VolumeUnit> defaultVolumeUnits;
	protected CollisionPoint[] collisionPoints;
	protected ControlSurface[] airfoilSections;
	protected VolumeUnit[] volumeUnits;
	
	public static float fuelCapacity;
	
	/* Ideally this should be 1.0 to make the simulation completely accurate, but because minecraft's chunk loading is so slow right now, the ratio
	*  of the airplane's size to it's maximum velocity is too high, therefore turning the airplane will affect the velocity of its components a
	*  disporportionate amount. Thus we have to reduce the impact that angular rotation has on the velocity of lifting surfaces on the airplane,
	*  to do this we add a scalar value to the angular velocity.
	*/
	public static double angularVelocitySizeModifier = 0.5;
	
//	public EnumDyeColor FuselageColor;
//	public EnumDyeColor WingColor;
    private static final DataParameter<Byte> FUSELAGE_COLOR = EntityDataManager.<Byte>createKey(EntityAirplane.class, DataSerializers.BYTE);
    private static final DataParameter<Byte> WING_COLOR = EntityDataManager.<Byte>createKey(EntityAirplane.class, DataSerializers.BYTE);
    private static final DataParameter<Float> FUEL_AMOUNT = EntityDataManager.<Float>createKey(EntityAirplane.class, DataSerializers.FLOAT);

    public EnumDyeColor getFuselageColor()
    {
        return EnumDyeColor.byMetadata(((Byte)this.dataManager.get(FUSELAGE_COLOR)).byteValue());
    }
    
    public EnumDyeColor getWingColor()
    {
        return EnumDyeColor.byMetadata(((Byte)this.dataManager.get(WING_COLOR)).byteValue());
    }

    public void setFuselageColor(EnumDyeColor color)
    {
        this.dataManager.set(FUSELAGE_COLOR, Byte.valueOf((byte) color.getMetadata()));
    }
    
    public void setWingColor(EnumDyeColor color)
    {
        this.dataManager.set(WING_COLOR, Byte.valueOf((byte) color.getMetadata()));
    }
    
    public void setFuel(float fuel) {
    	this.dataManager.set(FUEL_AMOUNT, Float.valueOf(fuel));
    }
    
    public float getFuel() {
    	return this.dataManager.get(FUEL_AMOUNT).floatValue();
    }
    
    protected void entityInit()
    {
        this.dataManager.register(FUSELAGE_COLOR, Byte.valueOf((byte)0));
        this.dataManager.register(WING_COLOR, Byte.valueOf((byte)0));
        this.dataManager.register(FUEL_AMOUNT, 0f);
    }
    
	
	//packet update
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotationDirect(double x, double y, double z,
			float yaw, float pitch, int somethinglol, boolean p_180426_10_)
	{
		if (this.getControllingPassenger() instanceof EntityPlayer) {
			return;
		} else {
			super.setPositionAndRotationDirect(x, y, z, yaw, pitch, somethinglol, p_180426_10_);
		}
	}

	//packet update
	@Override
	public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
	{
		if (this.getControllingPassenger() instanceof EntityPlayer) {
			return;
		} else {
			super.setPositionAndRotation(x, y, z, yaw, pitch);
		}
	}
   
	//TODO
	// * implement refueling and gui changes DONE!
	// * Make the model look better and add rotating things DONE!
	// * Make the sounds better DONE!
	// * Add collision detect DONE!
	// * Color customization DONE!
	// * A bit more realistic control surfaces DONE!
	// * Realistic friction DONE!
	// * More camera types DONE!
	// * Floating planes
	// * 
	
	// Bugs:
	// It is hard to get off airplanes sometimes
	
	protected double thrusttovelfunc(double x) {
		return 0.1*Math.log(100.0*x+1.0)/Math.log(100.0+1.0);
	}
	
	protected abstract void updateControlSurfaceNormals(Mat3 transform, double forceElevators, double forceRudder, double forceAlierons);
	
	protected abstract boolean canSteer();
	
	protected abstract ControlSurface getWing();
	
	private void dosomestuff() {
		
	}
	
	public void onUpdate()
	{
		super.onUpdate();
	//	if (world.isRemote)
	//		this.cam.onUpdate();
		prevRotationRoll = rotationRoll;

		prevMotionX = motionX;
		prevMotionY = motionY;
		prevMotionZ = motionZ;
		
		prevProppos = propPos;
		propVel += throttle*1.0;
		propVel -= Math.signum(propVel) * (0.005 + Math.abs(propVel*0.03) + propVel*propVel*0.02);
		if (propVel < 0.005)
			propVel = 0;
		propPos += propVel;
		//propPos += thrusttovelfunc(engine/thrust_const);
		tick++;
		//System.out.println((Math.pow(0.5, engine/5)));
		Vec3 posvec = new Vec3(posX, posY, posZ);
		
		if (serverSide()) {

			sfuel -= throttle/100.0;
			if (sfuel < 0)
				sfuel = 0;
			if (tick%20 == 0)
				this.setFuel(sfuel);
			
			if (this.throttle > 0.0 && (tick % Math.round(10.0*(Math.pow(0.5, throttle/5.0)))) == 0)
				this.playSound(Mcflight.sound_engine, 0.4f, (float)throttle/3 + 1);
			if (this.getControllingPassenger() instanceof EntityPlayer)
				return;
			else
				throttle = 0;
		}

		if ((tick%3) == 0 && clientSide() && this.getControllingPassenger() == Minecraft.getMinecraft().player) {
			AirplaneUpdatePacket packet = new AirplaneUpdatePacket(posX, posY, posZ, throttle, rotationPitch, rotationYaw, rotationRoll);
			Mcflight.network.sendToServer(packet);
		}

		double air = this.getAirDensity(this.posY);
		
		Vec3 tmpMotion = new Vec3(motionX, motionY, motionZ);
		
		double velocity_sq = motionX*motionX + motionY*motionY + motionZ*motionZ;
		velocity = Math.sqrt(velocity_sq);
		Mat3 transform = Mat3.getTransformMatrix(rotationYaw, rotationPitch, rotationRoll);
		vfwd = transform.transform(new Vec3(0.0, 0.0, 1.0));
		vside = transform.transform(new Vec3(1.0, 0.0, 0.0));
		vup = transform.transform(new Vec3(0.0, 1.0, 0.0));
		Vec3 vel = new Vec3(motionX, motionY, motionZ);
		
		//Thrust
		thrust_vec = Vec3.mul(vfwd, throttle * thrust_const * air);
		tmpMotion.add(Vec3.mul(thrust_vec, conversionconst / mass));

		//Drag
		//bodyDragUp is basically the same thing as lift induced drag
		double bodyDragFwd = -1.0 * velocity_sq * drag_const * dragMul_forward * Vec3.cosTheta(vel, vfwd) * (clientSide() && input.brake.isKeyDown() ? 1.5 : 1.0);
		double bodyDragSideways = velocity_sq * drag_const * dragMul_sideways * Vec3.sinTheta(vel, vfwd);
		Vec3 dirvec = Vec3.cross(vfwd, Vec3.cross(vfwd, vel)).unitvector();
		//double bodyDragSideways = velocity_sq * drag_const * dragMul_sideways *Vec3.cosTheta(vel, vside);
		drag_vec = new Vec3 (
				bodyDragFwd*vfwd.x + dirvec.x * bodyDragSideways,
				bodyDragFwd*vfwd.y + dirvec.y * bodyDragSideways,
				bodyDragFwd*vfwd.z + dirvec.z * bodyDragSideways);
		tmpMotion.add(Vec3.mul(drag_vec, conversionconst / mass));
		
		//Control Surfaces

		this.updateControls();
		
		this.updateControlSurfaceNormals(transform, -forceElevator*controlSensitivity, -forceRudder*controlSensitivity, forceAlierons*controlSensitivity);

		Vec3 angVelocity = new Vec3(angVelX, angVelY, angVelZ);
		Vec3 angVelDiff = new Vec3();
		double mag_lift = 0f;
		double mag_inddrag = 0f;
		double angleOfAttackWing = 0f;

		if (Keyboard.isKeyDown(Keyboard.KEY_C))
			angVelocity = new Vec3(0.0, 5.0, 0.0);

		gravity_vec = new Vec3(0.0, -gravity_const*mass, 0.0);
		tmpMotion.add(Vec3.mul(gravity_vec, conversionconst / mass));
		
		for (ControlSurface cs: this.airfoilSections) {

			Vec3 tpos = transform.transform(cs.position);
			Vec3 distaxis = Vec3.sub(tpos, Vec3.proj(tpos, angVelocity));
			Vec3 cvel = Vec3.add(vel, Vec3.cross(Vec3.mul(angVelocity, Math.PI/180.0), distaxis).mul(angularVelocitySizeModifier));
			if (angVelocity.magsq() == 0)
				cvel.set(vel);
			
			double coefficient = lift_const * cvel.magsq() * air;
			Vec3 out = Vec3.unitvector(Vec3.cross(cvel, Vec3.cross(cs.normal_position, cvel)));
			double angleOfAttack = Vec3.angle(cs.getNormal(), cvel) - 90.0;
			cs.setAngleOfAttack(angleOfAttack);
			
			double lift = cs.getLiftFromAlpha(angleOfAttack) * coefficient * cs.wingArea;
			Vec3 lift_vec = Vec3.mul(out, lift);
			
			double inducedDrag = cs.getDragFromAlpha(angleOfAttack) * coefficient * cs.wingArea;
			Vec3 inddrag_vec = Vec3.unitvector(cvel).mul(-1.0 * inducedDrag);
			
			Vec3 force = Vec3.add(lift_vec, inddrag_vec);
			boolean inwater = this.isInWater(Vec3.add(posvec, tpos));
			float waterdrag = (inwater? 10.0f:1.0f);
			tmpMotion.add(Vec3.mul(force, waterdrag * conversionconst / mass));
			cs.setForce(force);
			cs.setPosition(tpos);
			angVelDiff.add(Vec3.cross(tpos, force).mul(this.torqueMultiplier));
			
			if (this.getWing() == cs) {
				mag_lift = lift;
				mag_inddrag = inducedDrag;
				angleOfAttackWing = angleOfAttack;
				stall = cs.isStalled();
				//mo.set(cvel);
			}
		}

		for (VolumeUnit vu: this.volumeUnits) {
			Vec3 tpos = transform.transform(vu.getPosition());
			vu.update(tpos);
			Vec3 force = new Vec3(0.0, 0.0002, 0.0);
			boolean inwater = this.isInWater(Vec3.add(posvec, tpos));
			if (inwater) {
				Vec3 distaxis = Vec3.sub(tpos, Vec3.proj(tpos, angVelocity));
				Vec3 cvel = Vec3.add(vel, Vec3.cross(Vec3.mul(angVelocity, Math.PI/180.0), distaxis).mul(angularVelocitySizeModifier));
				if (angVelocity.magsq() == 0)
					cvel.set(vel);
				force.add(Vec3.mul(cvel, -0.001));
				tmpMotion.add(force);
				angVelDiff.add(Vec3.cross(tpos, force).mul(this.torqueMultiplier));
				vu.setForce(force);
			} else {
				vu.setForce(new Vec3(0, 0, 0));
			}
		}

		angVelocity.add(angVelDiff);
		//Gravity
		
		velocity = Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ);
		
		if (this.getFuel() <= 0) {
			throttle = 0;
		}

		if (canSteer()) {
			angVelocity.add(Vec3.mul(vup, forceRudder * velocity * 1.0));
		}
		
		
		Vec3[] points = new Vec3[collisionPoints.length];
	
		for (int i = 0; i < points.length; i++) {
			Vec3 coord = transform.transform(collisionPoints[i].getPosition());
			coord.mul(scale);
			points[i] = coord;
			collisionPoints[i].update(coord);
		}
		
		Vec3 motion = new Vec3(motionX, motionY, motionZ);
		this.onGround = false;
		this.isCollidedVertically = false;
		this.collidedX = false;
		this.collidedY = false;
		this.collidedZ = false;
		Vec3 motiondiff = new Vec3();
		frictionmotiondiff = new Vec3();
		Vec3 angmotiondiff = new Vec3();
		totalnormalforce = new Vec3();

		boolean brakedown = (clientSide() && input.brake.isKeyDown());
		for (int i = 0; i < points.length; i++) {
			Vec3 collisionVec = (getCollisionVector(posX+points[i].x, posY+points[i].y, posZ+points[i].z, this.getEntityWorld()));
			if (collisionVec != null) {
				this.onGround = true;
				collisionPoints[i].isCollided = true;
				Vec3 normal = Vec3.mul(collisionVec, 128.0*collisionPoints[i].normalCoeff);
				angmotiondiff.add(Vec3.cross(points[i], normal).mul(this.torqueMultiplier));
				motiondiff.add(Vec3.mul(normal, conversionconst / mass));
				
				double brake_multiplier = brakedown? collisionPoints[i].brakeMul: 1.0;
				totalnormalforce.add(collisionVec);
				Vec3 fcDirection = Vec3.cross(collisionVec, Vec3.cross(collisionVec, motion)).unitvector();
				double fcMag = brake_multiplier*friction_const*collisionPoints[i].friction*(2.0*normal.mag()/0.025);
				Vec3 friction = new Vec3();
				friction = fcDirection.mul(fcMag);
				frictionmotiondiff.add(friction);
				collisionPoints[i].setForce(Vec3.add(fcDirection, normal));
			} else {
				collisionPoints[i].isCollided = false;
				collisionPoints[i].setForce(new Vec3());
			}
		}

		this.isCollidedVertically = this.collidedY;
		this.isCollidedHorizontally = this.collidedX || this.collidedZ;
		
		//System.out.println(motiondiff.mag());
		if (motiondiff.mag() > 0.2)
			motiondiff = Vec3.mul(motiondiff, 0.2/motiondiff.mag());

		
		tmpMotion.add(motiondiff);

		//Integration
		motionX = tmpMotion.x;
		motionY = tmpMotion.y;
		motionZ = tmpMotion.z;
		
		frictionmotiondiff.mul(conversionconst / mass);
		motion = new Vec3(motionX, motionY, motionZ);
		ismoving = !(frictionmotiondiff.mag() > motion.mag() || motion.mag() < 0.0005);
		Vec3 newMotion = Vec3.add(motion, frictionmotiondiff);
		//Vec3 fcNormalDirection = Vec3.cross(totalnormalforce, Vec3.cross(totalnormalforce, motion));
		//if (Vec3.dot(fcNormalDirection, newMotion) > 0.0)
		if (!ismoving)
		//	newMotion.set(Vec3.proj(newMotion, Vec3.cross(motion, totalnormalforce), totalnormalforce));
			newMotion.set(Vec3.proj(motion, totalnormalforce));
		motionX = newMotion.x;
		motionY = newMotion.y;
		motionZ = newMotion.z;
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		angVelocity.add(angmotiondiff);
		if (this.onGround) { //collision damping
			Vec3 para = Vec3.proj(angVelocity, angmotiondiff);
			Vec3 perp = Vec3.sub(angVelocity, para);
			angVelocity.set(Vec3.add(para.mul(0.85), perp));
		}
		
		double am = 1.0;
	//	if (isOnGround)
	//		am = 0.5;
		angVelX = angVelocity.x*am*rotationSpeedDecay * (onGround?0.8:1.0);
		angVelY = angVelocity.y*am*rotationSpeedDecay;
		angVelZ = angVelocity.z*am*rotationSpeedDecay;
		
		
		double mag_drag = drag_vec.mag();
		double mag_thrust = thrust_vec.mag();
		double mag_grav = gravity_vec.mag();
		
		// 1. Convert pitch/yaw/roll axis to vectors
		// 2. Use axis angle rotation to rotate vectors (these are now the values of a transformation matrix)
		// 3. Convert transformation matrix back to euler angles
		double rotationVelocity = angVelocity.mag();
		Vec3 angles = new Vec3(rotationYaw, rotationPitch, rotationRoll);
		if (rotationVelocity > 0.0) {
			Vec3 axisRot = Vec3.mul(angVelocity, 1.0/rotationVelocity);
			Vec3 vfwd_new = Vec3.AxisAngleRotation(axisRot, vfwd, rotationVelocity);
			Vec3 vup_new = Vec3.AxisAngleRotation(axisRot, vup, rotationVelocity);
			Vec3 vwing_new = Vec3.AxisAngleRotation(axisRot, vside, rotationVelocity);
			angles = Mat3.getangles(vwing_new, vup_new, vfwd_new);
		}
		rotationYaw = (float) angles.x;
		rotationPitch = (float) angles.y;
		rotationRoll = (float) angles.z;


		text = (collidedX + " " + collidedY + " " + collidedZ);
		if (collidedX) {
			if (motionX < -0.5) {
				AirplaneStatePacket pack = new AirplaneStatePacket(1, 0);
				Mcflight.network2.sendToServer(pack);
			}
			motionX *= 0.8;
		}
		if (collidedY) {
			if (motionY < -0.5) {
				AirplaneStatePacket pack = new AirplaneStatePacket(1, 0);
				Mcflight.network2.sendToServer(pack);
			}
			motionY *= 0.8;
		}
		if (collidedZ) {
			if (motionZ < -0.5) {
				AirplaneStatePacket pack = new AirplaneStatePacket(1, 0);
				Mcflight.network2.sendToServer(pack);
			}
			motionZ *= 0.8;
		}


		//Turning on ground
		if (this.onGround)
		{
			double tmpXX = dSin(this.rotationYaw);
			double tmpZZ = dCos(this.rotationYaw);
			double dotp = zeroIfNaN(motionX*tmpXX + motionZ*tmpZZ);
			
			motionX = tmpXX*Math.abs(dotp);
			motionZ = tmpZZ*Math.abs(dotp);
		}

		if (clientSide() && minecraft.player.getRidingEntity() == this) {
			double accel = Math.sqrt(
					(motionX-prevMotionX)*(motionX-prevMotionX)+
					(motionY-prevMotionY+gravity_const)*(motionY-prevMotionY+gravity_const)+
					(motionZ-prevMotionZ)*(motionZ-prevMotionZ));
			//Vector fdelta = new Vector(motionX-prevMotionX, motionY-prevMotionY, motionZ-prevMotionZ);
			RenderAirplaneInterface.instance.setDebugVars(velocity, ismoving?angleOfAttackWing:0.0, mag_lift, mag_drag, mag_inddrag, mag_thrust, mag_grav, air, angVelocity.mag(), this);
			RenderAirplaneInterface.instance.setVars(velocity, accel, this.posY, rotationPitch, rotationYaw, rotationRoll, throttle);
		}


		//velYaw *= (isOnGround? 0.85: 1.0);
		//Rotation modular arithmetic
		if (clientSide())
			cam.updatePositions(this.getControllingPassenger() == Minecraft.getMinecraft().player, transform);


		if (rotationYaw-prevRotationYaw > 180)
			prevRotationYaw += 360;
		if (rotationYaw-prevRotationYaw < -180)
			prevRotationYaw += -360;
		if (rotationRoll-prevRotationRoll > 180)
			prevRotationRoll += 360;
		if (rotationRoll-prevRotationRoll < -180)
			prevRotationRoll += -360;
		
		vectorsInitialized = true;
	}
	
	public boolean isInWater(Vec3 pos) {
		BlockPos bp = new BlockPos(pos.x, pos.y, pos.z);
		IBlockState bs = world.getBlockState(bp);
		Material m = bs.getMaterial();
		if (m != Material.WATER && m != Material.LAVA)
			return false;
		float lvl = BlockLiquid.getLiquidHeightPercent(bs.getValue(BlockLiquid.LEVEL));
		if ((pos.y - Math.floor(pos.y)) < (1.0-lvl))
			return true;
		return false;
	}
	
	public static double[] lift = {0, 0.0865682024, 0.173132549, 0.2596737611, 0.3461417137, 0.432440013, 0.5184105729, 0.6038181922, 0.6883351314, 0.7715256899, 0.8528307828, 0.9315525179, 1.0068387728, 1.0776677716, 1.1428326622, 1.2009260928, 1.2503247895, 1.2891741327, 1.3153727343, 1.3265570149, 1.3200857803, 1.2930247988, 1.2421313779, 1.1638389418, 1.0542416076, 0.9197011011, 0.8668813789, 0.8447997863, 0.8393419844, 0.8485792446, 0.8660254038, 0.8829475928, 0.8987940463, 0.9135454576, 0.9271838546, 0.9396926208, 0.9510565163, 0.9612616959, 0.9702957263, 0.9781476007, 0.984807753, 0.9902680687, 0.9945218954, 0.9975640503, 0.999390827, 1, 0.999390827, 0.9975640503, 0.9945218954, 0.9902680687, 0.984807753, 0.9781476007, 0.9702957263, 0.961261696, 0.9510565163, 0.9396926208, 0.9271838546, 0.9135454577, 0.8987940463, 0.8829475929, 0.8660254038, 0.8480480962, 0.8290375726, 0.8090169944, 0.7880107536, 0.7660444432, 0.7431448255, 0.7193398004, 0.6946583705, 0.6691306064, 0.6427876097, 0.6156614754, 0.5877852524, 0.5591929035, 0.5299192643, 0.5000000001, 0.4694715629, 0.4383711469, 0.4067366431, 0.3746065935, 0.3420201434, 0.3090169945, 0.2756373559, 0.2419218957, 0.2079116909, 0.1736481778, 0.139173101, 0.1045284634, 0.0697564738, 0.0348994968, 0};
	public static double getLiftFromAlpha(double a) {
		if (!Double.isFinite(a))
			return 0;
		double x = Math.abs(a);
		int lower = (int)Math.floor(x);
		if (lower >= 90 || lower < 0)
			return 0.0;
		double phigher = x-Math.floor(x);
		double plower = 1.0-phigher;
		return (lift[lower]*plower + lift[lower+1]*phigher)*Math.signum(a);
	}

	public static double getDragFromAlpha(double a) {
		return dSin(a)*dSin(a)*2.0;
	}
	
	static private void test(double v) {
		//v = 0;
		Vec3 vel = new Vec3(0, 0, v);
		double velocity_sq = v*v;
		double angle = 0;
		

		double coefficient = lift_const * velocity_sq;
		
		double interval = 1.0;
		double lift = -100;
		while (Math.abs(lift-gravity_const) > 0.0000001 && angle <= 90) {
			lift = getLiftFromAlpha(angle) * coefficient * 4.0;
			if (lift > gravity_const) {
				angle = angle - interval;
				interval = interval*0.1;
			} else {
				angle = angle + interval;
			}
		}
		double inducedDrag = 0;
		if (angle < 89.5)
		{
			inducedDrag = getDragFromAlpha(angle) * coefficient * 4.0;
		}
		
	
		Vec3 _vfwd = new Vec3(0, dCos(angle), dSin(angle));
		double bodyDragFwd = velocity_sq * drag_const * dragMul_forward * Vec3.cosTheta(vel, _vfwd);
		double bodyDragSideways = velocity_sq * drag_const * dragMul_sideways * Vec3.sinTheta(vel, _vfwd) * -1.0;
		Vec3 dirvec = Vec3.cross(_vfwd, Vec3.cross(_vfwd, vel)).unitvector();
		//double bodyDragSideways = velocity_sq * drag_const * dragMul_sideways *Vec3.cosTheta(vel, vside);
		Vec3 _drag_vec = new Vec3 (
				bodyDragFwd*_vfwd.x + dirvec.x * bodyDragSideways,
				bodyDragFwd*_vfwd.y + dirvec.y * bodyDragSideways,
				bodyDragFwd*_vfwd.z + dirvec.z * bodyDragSideways);
		double paraDrag = _drag_vec.mag();
		System.out.println(String.format("%f\t%f\t%f", v*72.0, inducedDrag, paraDrag));
	}
	public static void print() {
		//for (int i = 0; i < 144; i++)
		//	test(i/72.0);
		for (int i = 0; i <= 90; i++) {
			System.out.println(String.format("%f\t%f", EntityAirplane.getLiftFromAlpha(i), EntityAirplane.getDragFromAlpha(i)));
		}
	}
	public void updateControls() {

		prevForceElevator = forceElevator;
		prevForceAlierons = forceAlierons;
		prevForceRudder = forceRudder;
		forceElevator = 0.0 + elevTrim;
		forceAlierons = 0.0;
		forceRudder = 0.0;
		
		if (clientSide() && minecraft.player.getRidingEntity() == this) {
			
			if (input.throttle_up.isKeyDown())
				throttle = clamp(0, throttle + 0.025, 1);
			
			else if (input.throttle_down.isKeyDown())
				throttle = clamp(0, throttle - 0.025, 1);

			if (input.rudder_left.isKeyDown())
				forceRudder = 1.0;
			
			else if (input.rudder_right.isKeyDown())
				forceRudder = -1.0;
			
			if (input.elevator_down.isKeyDown() || input.elevator_up.isKeyDown() || input.alieron_ccw.isKeyDown() || input.alieron_cw.isKeyDown())
				useMouseInput = false;
			
			if  (useMouseInput) {

				forceElevator = 12.5*((double)mouseY)/3000.0  + elevTrim;
				forceAlierons = 12.5*((double)mouseX)/4000.0;

			} else {
				
				if (input.elevator_down.isKeyDown())
					forceElevator = -1.0 + elevTrim;
				
				else if (input.elevator_up.isKeyDown())
					forceElevator = 1.0 + elevTrim;
				
				if (input.alieron_ccw.isKeyDown())
					forceAlierons = 1.0;
				
				else if (input.alieron_cw.isKeyDown())
					forceAlierons = -1.0;
				
			}
		}
		
	}
	
	@Nullable
	public Entity getControllingPassenger()
	{
		List<Entity> list = this.getPassengers();
		return list.isEmpty() ? null : (Entity)list.get(0);
	}

	public void explode() {
		boolean flag = world.getGameRules().getBoolean("mobGriefing");
		world.newExplosion(this, this.posX, this.posY, this.posZ, 0.0f, flag, flag);
		this.entityDropItem(new ItemStack(Blocks.PLANKS, 3, 0), 0.0f);
		this.entityDropItem(new ItemStack(Items.STICK, 3, 0), 0.0f);
		this.entityDropItem(new ItemStack(Blocks.PLANKS, 3, 0), 0.0f);
		this.entityDropItem(new ItemStack(Items.STICK, 3, 0), 0.0f);
		this.kill();
	}

	public Vec3 getInterpolatedRotation(float partialTicks) {
		return new Vec3(
				prevRotationYaw+(rotationYaw-prevRotationYaw)*partialTicks,
				prevRotationPitch+(rotationPitch-prevRotationPitch)*partialTicks,
				prevRotationRoll+(rotationRoll-prevRotationRoll)*partialTicks);
	}


	public Vec3 getInterpolatedControlSurfaces(double partialTicks) {
		return new Vec3(
				prevForceRudder+(forceRudder-prevForceRudder)*partialTicks,
				prevForceElevator+(forceElevator-prevForceElevator)*partialTicks,
				prevForceAlierons+(forceAlierons-prevForceAlierons)*partialTicks).mul(controlSensitivity);
	}
	
	public Vec3 getMotionVector(float partialTicks) {
		return new Vec3(
				prevMotionX+(motionX-prevMotionX)*partialTicks,
				prevMotionY+(motionY-prevMotionY)*partialTicks,
				prevMotionZ+(motionZ-prevMotionZ)*partialTicks);
	}
	
	@Override
	public void setDead()
    {
    	super.setDead();
        if (serverSide() && world.getGameRules().getBoolean("doEntityDrops"))
        {
           	for (int i = 0; i < inv.getSizeInventory(); i++) {
           		ItemStack item = inv.getStackInSlot(i);
           		if (!item.isEmpty())
           			this.entityDropItem(item, 0.0f);
           	}
        }
        if (clientSide())
        	this.cam.setDead();
    }
   
	@Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (serverSide() && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null && this.isPassenger(source.getEntity()))
            {
                return false;
            }
            else
            {
                
                boolean flag = source.getEntity() instanceof EntityPlayer;// && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
                
                damage += amount;
                
                if (flag || amount > 4.0)
                {
                	ItemStack planeitem = new ItemStack(airplaneItem);
                	this.writeEntityToItemStack(planeitem);
                   	this.entityDropItem(planeitem, 0.0f);
                    this.setDead();
                }

                return true;
            }
        }
        else
        {
            return true;
        }
    }
	//public double getDrag(double vel, double drag) {
	//	return sign(vel)*drag*vel*vel;
	//}
	
	protected double getAirDensity(double x) {
		return 1/(1+Math.pow(1.05, x-256));
	}
	
	
	protected Vec3 getCollisionVector(double x, double y, double z, World w) {
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = w.getBlockState(pos);
		AxisAlignedBB box = state.getBlock().getCollisionBoundingBox(state, w, pos);
		if (box == null || !box.isVecInside(new Vec3d(Mod1(x), Mod1(y), Mod1(z))))
			return null;
		double avgx = avg(box.minX, box.maxX);
		double avgy = avg(box.minY, box.maxY);
		double avgz = avg(box.minZ, box.maxZ);
		double mx = Mod1(x);
		double my = Mod1(y);
		double mz = Mod1(z);
		//goes through every surrounding block
		Vec3[] p1 = new Vec3[] {
				new Vec3(mx, box.maxY, mz),
				new Vec3(box.minX, my, mz),
				new Vec3(box.maxX, my, mz),
				new Vec3(mx, my, box.minZ),
				new Vec3(mx, my, box.maxZ),
				new Vec3(mx, box.minY, mz),
		};
		Vec3[] p2 = new Vec3[] {
				new Vec3(box.minX, my, box.minZ),
				new Vec3(box.maxX, my, box.minZ),
				new Vec3(box.minX, my, box.maxZ),
				new Vec3(box.maxX, my, box.maxZ),

				new Vec3(mx, box.minY, box.minZ),
				new Vec3(mx, box.maxY, box.minZ),
				new Vec3(mx, box.minY, box.maxZ),
				new Vec3(mx, box.maxY, box.maxZ),

				new Vec3(box.minX, box.minY, mz),
				new Vec3(box.maxX, box.minY, mz),
				new Vec3(box.minX, box.maxY, mz),
				new Vec3(box.maxX, box.maxY, mz),
		};
		Vec3[] p3 = new Vec3[] {
				new Vec3(box.minX, box.minY, box.minZ),
				new Vec3(box.maxX, box.minY, box.minZ),
				new Vec3(box.minX, box.maxY, box.minZ),
				new Vec3(box.maxX, box.maxY, box.minZ),
				new Vec3(box.minX, box.minY, box.maxZ),
				new Vec3(box.maxX, box.minY, box.maxZ),
				new Vec3(box.minX, box.maxY, box.maxZ),
				new Vec3(box.maxX, box.maxY, box.maxZ)
		};
		final Vec3 modposvec = new Vec3(mx, my, mz);
		Vec3 floorposvec = new Vec3(Math.floor(x), Math.floor(y), Math.floor(z));
		Vec3 center = new Vec3(avgx, avgy, avgz);
		Vec3 out = checkCollisionSet(p1, modposvec, floorposvec, w);
		if (out == null) {
			out = checkCollisionSet(p2, modposvec, floorposvec, w);
			if (out == null) {
				out = checkCollisionSet(p3, modposvec, floorposvec, w);
				if (out == null) {
					return null;
				}
			}
		}
		if (Math.abs(out.x) > 0.001)
			collidedX = true;
		if (Math.abs(out.y) > 0.001)
			collidedY = true;
		if (Math.abs(out.z) > 0.001)
			collidedZ = true;
			
		return out;
	}
	
	private Vec3 checkCollisionSet(Vec3[] points, final Vec3 modposvec, Vec3 floorposvec, World w) {
		Arrays.sort(points, new Comparator<Vec3>() {
			@Override
			public int compare(Vec3 a, Vec3 b) {
				return Double.compare(Vec3.distsq(modposvec, a), Vec3.distsq(modposvec, b));
			}
		});
		for (int closest = 0; closest < points.length; closest++) {
			if (!isVecInsideBox(Vec3.add(floorposvec, modposvec, Vec3.sub(points[closest], modposvec).mul(1.1)), w)) {
				Vec3 out = Vec3.sub(points[closest], modposvec);
				return out;
			}
		}
		return null;
	}
	
	protected boolean isVecInsideBox(Vec3 p, World w) {
		BlockPos pos = new BlockPos(p.x, p.y, p.z);
		IBlockState state = w.getBlockState(pos);
		AxisAlignedBB box = state.getBlock().getCollisionBoundingBox(state, w, pos);

		if (box != null && box.isVecInside(new Vec3d(Mod1(p.x), Mod1(p.y), Mod1(p.z))))
			return true;
		return false;
	}
	//Trig functions

	public static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	public static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
	}
	
	public static double Mod1(double x) {
		return x - Math.floor(x);
	}
	
	public static int sign(double a) {
		if (a < 0) return -1;
		if (a > 0) return 1;
		return 0;
	}
	
	public static double clamp(double min, double val, double max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}
	
	public static int clamp(int min, int val, int max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}

	public static double zeroIfNaN(double a) {
		if (Double.isFinite(a))
			return a;
		return 0;
	}
	
	public static double avg(double a, double b) {
		return ((a+b)/2.0);
	}
	
	/*@Override
	public boolean interactFirst(EntityPlayer playerIn) {
		if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger() != playerIn) {
			return true;
		} else {
			if (serverSide()) {
				playerIn.startRiding(this);
				mouseX = 0;
				mouseY = 0;
			}
			return true;
		}
	}*/

	public static boolean isPaint(ItemStack i) {
		return (i != null && i.getItem() instanceof AircraftPaint);
	}
	
	boolean reloadChunks = true;
	
	@Override
	protected void addPassenger(Entity passenger)
	{
		super.addPassenger(passenger);
		if (passenger == Minecraft.getMinecraft().player) {
		//	Minecraft.getMinecraft().setRenderViewEntity(this.cam);
			if (reloadChunks) {
				try {
					RenderGlobal r = Minecraft.getMinecraft().renderGlobal;
					Minecraft mc = Minecraft.getMinecraft();

					Field f = RenderGlobal.class.getDeclaredField("viewFrustum");
					f.setAccessible(true);
					Field fac;
					fac = RenderGlobal.class.getDeclaredField("renderChunkFactory");
					fac.setAccessible(true);
					IRenderChunkFactory factory = (IRenderChunkFactory) fac.get(r);
					ViewFrustum frustum = new ViewFrustum(mc.world, mc.gameSettings.renderDistanceChunks, r, factory);
					f.set(r, frustum);
					if (mc.world != null)
					{
						Entity entity = mc.getRenderViewEntity();

						if (entity != null)
						{
							frustum.updateChunkPositions(entity.posX, entity.posZ);
						}
					}

				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
	
	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if (hand != EnumHand.MAIN_HAND)
			return false;
		if (serverSide())
		{
			ItemStack i = player.getHeldItem(hand);
			if (isPaint(i))
				return true;
			if (i != null && i.getItem() instanceof Kerosene) {
					sfuel += 20.0;
					if (sfuel >= 100)
						sfuel = 100;
					this.setFuel(sfuel);
					//i = new ItemStack(Items.BUCKET);
					i.shrink(1);
					ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(Items.BUCKET));
					return true;
			}
			if(player.isSneaking()) {
				player.openGui(Mcflight.instance, 0, world, this.getEntityId(), 0, 0);
				return true;
			} else {
				player.startRiding(this);
				return true;
			}
		} else {
			if (isPaint(player.getHeldItem(hand))) {
				player.openGui(Mcflight.instance, 1, world, this.getEntityId(), 0, 0);
				return true;
				
			}
		}
		return false;
    }

	/*@Override
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand)
    {
        if (serverSide() && !player.isSneaking())
        {
            player.startRiding(this);
        }

        return true;
    }*/

	@Override
	public void updatePassenger(Entity passenger) {
		if (passenger != null) {
			Vec3 riderPos = Vec3.mul(this.vup, -1.0);
			passenger.setPosition(this.posX + riderPos.x, this.posY + riderPos.y, this.posZ+riderPos.z);
			//passenger.rotationYaw = -rotationYaw;
			//passenger.rotationPitch = rotationPitch;
			if (passenger instanceof EntityPlayer) {
			//	((EntityPlayer)passenger).rota
			}
		}
	}
	
	@Override
	public double getMountedYOffset() {
		return  0.5;
	}

    
    @Override
    public void dismountRidingEntity() {
		System.out.println("AAA" + clientSide());
    	if (clientSide()) {
    		if (Minecraft.getMinecraft().player.isDead)
    			//Minecraft.getMinecraft().setIngameNotInFocus();
    			Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
    		else {
    			Minecraft.getMinecraft().setIngameFocus();
    		}
    	}
    	super.dismountRidingEntity();
    }
    
    private boolean serverSide() {
    	return !world.isRemote;
    }
   
    private boolean clientSide() {
    	return world.isRemote;
    }

	public Entity getCamera() {
		return this.cam;
	}
}

