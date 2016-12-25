package thepowderguy.mcflight.common.entity;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import thepowderguy.mcflight.client.RenderAirplaneInterface;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.item.AircraftPaint;
import thepowderguy.mcflight.common.item.Kerosene;
import thepowderguy.mcflight.common.packet.AirplaneStatePacket;
import thepowderguy.mcflight.common.packet.AirplaneUpdatePacket;
import thepowderguy.mcflight.common.world.Oil;
import thepowderguy.mcflight.physics.CollisionPoint;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

public abstract class EntityAirplane extends Entity {
		
	public InventoryBasic inv;
//	public double drag;
	public static int KEYBIND_THRUST_UP = Keyboard.KEY_I;
	public static int KEYBIND_THRUST_DOWN = Keyboard.KEY_K;
	public static int KEYBIND_AILERON_LEFT = Keyboard.KEY_LEFT;
	public static int KEYBIND_AILERON_RIGHT = Keyboard.KEY_RIGHT;
	public static int KEYBIND_RUDDER_LEFT = Keyboard.KEY_J;
	public static int KEYBIND_RUDDER_RIGHT = Keyboard.KEY_L;
	public static int KEYBIND_ELEVATOR_UP = Keyboard.KEY_DOWN;
	public static int KEYBIND_ELEVATOR_DOWN = Keyboard.KEY_UP;
	public static int KEYBIND_BRAKE = Keyboard.KEY_Z;
	public static int KEYBIND_LOOK = Keyboard.KEY_SPACE;
	public static boolean useMouseInput = true;
	private EntityAirplaneCamera cam;
	Minecraft minecraft;
	
	public EntityAirplane(World worldIn) {
		super(worldIn);
		init(EntityAirplane.fuelCapacity);
	}
	
	private double yoffset = 0.0;
	
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
		cam = new EntityAirplaneCamera(world, this);
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
		engine = tagCompound.getDouble("Engine");
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
		tagCompound.setDouble("Engine", engine);
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
	public double engine = 0;
	public double velocity = 0.0;
	public boolean collidedX = false;
	public boolean collidedY = false;
	public boolean collidedZ = false;
	public double weight = 1.0;

	public Vec3 vfwd;
	public Vec3 vup;
	public Vec3 vside;
	public Vec3 lift_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 inddrag_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 drag_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 thrust_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 gravity_vec = new Vec3(0.0, -gravity_const, 0.0);
	
	public static double gravity_const = 0.025;
	public static double drag_const = 0.01;//0.025;
	public static double friction_const = 1.0;
	public static double dragMul_forward = -1.0;
	public static double dragMul_sideways = -1.0;
	public static double dragMul_vertical = -1.0;
	public static double lift_const = 0.04;//0.12;
	public static double camber = 0;
	public static double thrust_const = 0.01;
	public static double controlSensitivity = 75.0;
	public static double torqueMultiplier = 20.0;
	public static double rotationSpeedDecay = 0.8;
	
	public static double mouseX = 0;
	public static double mouseY = 0;
	public static double viewPitchOffset = 0.0;
	public static double viewYawOffset = 0.0;
	
	public double prevMotionX = 0;
	public double prevMotionY = 0;
	public double prevMotionZ = 0;
	Vec3[] points;
	public boolean stall = false;
	public double damage = 0.0;

	double prevForceElevator = 0.0;
	double forceElevator = 0.0;
	double prevForceAlierons = 0.0;
	double forceAlierons = 0.0;
	double prevForceRudder = 0.0;
	double forceRudder = 0.0;
	double prevProppos = 0.0;
	double propPos = 0.0;
	double propVel = 0.0;

	protected static String engineSound;
	public String text = "";
	protected static CollisionPoint[] collisionPoints;
	protected static double scale;
	protected static Item airplaneItem;
	protected ControlSurface[] controlSurfaces;
	public static float fuelCapacity;
	
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
	// * A bit more realistic control surfaces
	
	protected double thrusttovelfunc(double x) {
		return 0.1*Math.log(100.0*x+1.0)/Math.log(100.0+1.0);
	}
	
	protected abstract void updateControlSurfaceNormals(Mat3 transform, double forceElevators, double forceRudder, double forceAlierons);
	
	protected abstract boolean canSteer();
	
	protected abstract ControlSurface getWing();
	
	public void onUpdate()
	{
		super.onUpdate();
	//	if (world.isRemote)
	//		this.cam.onUpdate();
		prevRotationRoll = rotationRoll;
		prevProppos = propPos;
		propVel += engine*1.0;
		propVel -= Math.signum(propVel) * (0.005 + Math.abs(propVel*0.03) + propVel*propVel*0.02);
		if (propVel < 0.005)
			propVel = 0;
		propPos += propVel;
		//propPos += thrusttovelfunc(engine/thrust_const);
		tick++;
		//System.out.println((Math.pow(0.5, engine/5)));
		if (serverSide()) {

			sfuel -= engine/100.0;
			if (sfuel < 0)
				sfuel = 0;
			if (tick%20 == 0)
				this.setFuel(sfuel);
			
			if (this.engine > 0.0 && (tick % Math.round(10.0*(Math.pow(0.5, engine/5.0)))) == 0)
				this.playSound(Mcflight.sound_engine, 0.4f, (float)engine/3 + 1);
			if (this.getControllingPassenger() instanceof EntityPlayer)
				return;
			else
				engine = 0;
		}

		if ((tick%3)==0 && clientSide()) {
			AirplaneUpdatePacket packet = new AirplaneUpdatePacket(posX, posY, posZ, engine, rotationPitch, rotationYaw, rotationRoll);
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
		thrust_vec = Vec3.mul(vfwd, engine * thrust_const / weight);
		tmpMotion.add(thrust_vec);

		//Drag
		//bodyDragUp is basically the same thing as lift induced drag
		double bodyDragFwd = velocity_sq * drag_const * dragMul_forward * Vec3.cosTheta(vel, vfwd) * (clientSide() && Keyboard.isKeyDown(KEYBIND_BRAKE) ? 2.0 : 1.0);
		double bodyDragUp = velocity_sq * drag_const * dragMul_vertical * Vec3.cosTheta(vel, vup);
		double bodyDragSideways = velocity_sq * drag_const * dragMul_sideways *Vec3.cosTheta(vel, vside);
		drag_vec = new Vec3 (
				bodyDragFwd*vfwd.x + bodyDragUp*vup.x + bodyDragSideways * vside.x,
				bodyDragFwd*vfwd.y + bodyDragUp*vup.y + bodyDragSideways * vside.y,
				bodyDragFwd*vfwd.z + bodyDragUp*vup.z + bodyDragSideways * vside.z);
		drag_vec.mul(1.0/weight);
		tmpMotion.add(drag_vec);
		
		//Lift
		/*Vec3 out = Vec3.unitvector(Vec3.cross(vel, Vec3.cross(vup, vel)));
		double angleOfAttack = Vec3.angle(vup, vel) - 90.0;
		double lift = getLiftFromAlpha(angleOfAttack+camber) * lift_const * velocity_sq * air;
		double inducedDrag = getDragFromAlpha(angleOfAttack+camber) * lift_const * velocity_sq * air;
		
		lift_vec = Vec3.mul(out, lift / weight);
		tmpMotion.add(lift_vec);

		inddrag_vec = Vec3.unitvector(vel).mul(-1.0 * inducedDrag / weight);
		tmpMotion.add(inddrag_vec);*/

		//Control Surfaces

		prevForceElevator = forceElevator;
		prevForceAlierons = forceAlierons;
		prevForceRudder = forceRudder;
		forceElevator = 0.0;
		forceAlierons = 0.0;
		forceRudder = 0.0;
		
		if (clientSide() && minecraft.player.getRidingEntity() == this) {
			
			if (Keyboard.isKeyDown(KEYBIND_THRUST_UP))
				engine = clamp(0, engine + 0.025, 1);
			
			else if (Keyboard.isKeyDown(KEYBIND_THRUST_DOWN))
				engine = clamp(0, engine - 0.025, 1);

			if (Keyboard.isKeyDown(KEYBIND_RUDDER_LEFT))
				forceRudder = 0.2;
			
			else if (Keyboard.isKeyDown(KEYBIND_RUDDER_RIGHT))
				forceRudder = -0.2;

			if  (useMouseInput) {

				forceElevator = 2.5*((double)mouseY)/3000.0;
				forceAlierons = 2.5*((double)mouseX)/4000.0;

			} else {
				
				if (Keyboard.isKeyDown(KEYBIND_ELEVATOR_DOWN))
					forceElevator = 0.2;
				
				else if (Keyboard.isKeyDown(KEYBIND_ELEVATOR_UP))
					forceElevator = -0.2;
				
				if (Keyboard.isKeyDown(KEYBIND_AILERON_LEFT))
					forceAlierons = -0.2;
				
				else if (Keyboard.isKeyDown(KEYBIND_AILERON_RIGHT))
					forceAlierons = 0.2;
				
			}
		}
		
		this.updateControlSurfaceNormals(transform, -forceElevator*controlSensitivity, -forceRudder*controlSensitivity, forceAlierons*controlSensitivity);

		Vec3 angVelocity = new Vec3(angVelX, angVelY, angVelZ);

		double angleOfAttack = getWing().getAngleOfAttack();
		
		for (ControlSurface cs: this.controlSurfaces) {
			cs.applyAerodynamicForces(transform, tmpMotion, angVelocity, vel, lift_const * velocity_sq * air / weight, this.torqueMultiplier);
		}
		
		//Gravity
		tmpMotion.add(gravity_vec);
		
		motionX = tmpMotion.x;
		motionY = tmpMotion.y;
		motionZ = tmpMotion.z;
		
		velocity = Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ);
		
		if (this.getFuel() <= 0) {
			engine = 0;
		}

		
		if (canSteer()) {
			angVelocity.add(Vec3.mul(vup, forceRudder * velocity * 6.0));
		}
		
		//Integration
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		
		this.onGround = false;
		this.isCollidedVertically = false;
		this.collidedX = false;
		this.collidedY = false;
		this.collidedZ = false;
		points = new Vec3[collisionPoints.length];
	
		for (int i = 0; i < points.length; i++) {
			Vec3 coord = transform.transform(collisionPoints[i].getPosition());
			coord.mul(scale);
			points[i] = coord;
		}
		
		//Force X Position
		//boolean[] ongroundarr = {false, false, false};
		Vec3 motiondiff = new Vec3();
		Vec3 angmotiondiff = new Vec3();
		for (int i = 0; i < points.length; i++) {
			Vec3 force = (getCollisionVector(posX+points[i].x, posY+points[i].y, posZ+points[i].z, this.getEntityWorld()));
			if (force != null) {
				this.onGround = true;
				collisionPoints[i].isCollided = true;
				angmotiondiff.add(Vec3.cross(points[i], force).mul(6));
				motiondiff.add(Vec3.mul(force, 0.4));
			} else {
				collisionPoints[i].isCollided = false;
			}
		}

		this.isCollidedVertically = this.collidedY;
		this.isCollidedHorizontally = this.collidedX || this.collidedZ;
		
		//System.out.println(motiondiff.mag());
		if (motiondiff.mag() > 0.2)
			motiondiff = Vec3.mul(motiondiff, 0.2/motiondiff.mag());
		
		motionX += motiondiff.x;
		motionY += motiondiff.y;
		motionZ += motiondiff.z;
		angVelocity.add(angmotiondiff);
		double am = 1.0;
	//	if (isOnGround)
	//		am = 0.5;
		angVelX = angVelocity.x*am*rotationSpeedDecay * (onGround?0.8:1.0);
		angVelY = angVelocity.y*am*rotationSpeedDecay;
		angVelZ = angVelocity.z*am*rotationSpeedDecay;
		
		
		double mag_lift = lift_vec.mag();
		double mag_drag = drag_vec.mag();
		double mag_inddrag = inddrag_vec.mag();
		double mag_thrust = thrust_vec.mag();

		if (mag_lift > 1.0) System.out.println("LIFT IS " + mag_lift);
		if (mag_drag > 1.0) System.out.println("DRAG IS " + mag_drag);
		if (mag_inddrag > 1.0) System.out.println("IND DRAG IS " + mag_inddrag);
		if (mag_thrust > 1.0) System.out.println("THRUST IS " + mag_thrust);

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

		if (Math.abs(angleOfAttack) > 21.0) {
			stall = true;
		} else {
			stall = false;
		}


		//Turning on ground
		if (this.onGround)
		{
			double tmpXX = dSin(this.rotationYaw);
			double tmpZZ = dCos(this.rotationYaw);
			double dotp = zeroIfNaN(motionX*tmpXX + motionZ*tmpZZ);
			//double magdiff = motionX*(1-Math.abs(dotp));
			motionX = tmpXX*Math.abs(dotp);
			motionZ = tmpZZ*Math.abs(dotp);
			if (motionY < 0) this.motionY *= 0;


			double friction = 0;
			for (CollisionPoint point: this.collisionPoints)
				if (point.isCollided)
					friction += point.friction;
			
			if (clientSide() && Keyboard.isKeyDown(KEYBIND_BRAKE)) {
				friction *= 1.5;
			}
			
			friction *= friction_const;
			
			double mag = Math.sqrt(motionX*motionX+motionZ*motionZ);
			if (mag > friction) {
				motionX *= (mag-friction)/mag;
				motionZ *= (mag-friction)/mag;
			} else {
				motionX = 0.0;
				motionZ = 0.0;
			}
		}

		if (clientSide() && minecraft.player.getRidingEntity() == this) {
			double accel = Math.sqrt(
					(motionX-prevMotionX)*(motionX-prevMotionX)+
					(motionY-prevMotionY+gravity_const)*(motionY-prevMotionY+gravity_const)+
					(motionZ-prevMotionZ)*(motionZ-prevMotionZ));
			//Vector fdelta = new Vector(motionX-prevMotionX, motionY-prevMotionY, motionZ-prevMotionZ);
			RenderAirplaneInterface.instance.setDebugVars(velocity, angleOfAttack, mag_lift, mag_drag, mag_inddrag, mag_thrust, air, angVelocity.mag(), this);
			RenderAirplaneInterface.instance.setVars(velocity, accel, this.posY, rotationPitch, rotationYaw, rotationRoll);
		}


		//velYaw *= (isOnGround? 0.85: 1.0);
		//Rotation modular arithmetic
		if (rotationYaw < -180.0) rotationYaw += 360;
		if (rotationYaw > 180.0) rotationYaw -= 360;
		if (rotationRoll < -180.0) rotationRoll += 360;
		if (rotationRoll > 180.0) rotationRoll -= 360;

		if (rotationPitch < -90.0) {
			rotationPitch = -180 - rotationPitch;
			rotationYaw = -rotationYaw;
			rotationRoll = -rotationRoll;
		}
		if (rotationPitch > 90.0) {
			rotationPitch = 180 - rotationPitch;
			rotationYaw = -rotationYaw;
			rotationRoll = -rotationRoll;
		}

		prevMotionX = motionX;
		prevMotionY = motionY;
		prevMotionZ = motionZ;
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
			//Vec3 v = Vec3.add(floorposvec, center, Vec3.sub(sides[j], center).mul(1.1));
		//	System.out.println(Vec3.distsq(points[closest], modposvec));
			if (!isVecInsideBox(Vec3.add(floorposvec, modposvec, Vec3.sub(points[closest], modposvec).mul(1.1)), w)) {
				Vec3 out = Vec3.sub(points[closest], modposvec);
				return out;
				//return new Vec3(0.0, 1.0-Mod1(y), 0.0);
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
			if (i != null)
				System.out.println("Hello! " + i.getItem().getClass().getName());
			else
				System.out.println("MEOW");
			Thread.currentThread().dumpStack();
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
			passenger.rotationYaw = -rotationYaw;
			passenger.rotationPitch = rotationPitch;
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

