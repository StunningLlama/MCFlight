package thepowderguy.mcflight.common.entity;

import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import thepowderguy.mcflight.client.RenderAirplaneInterface;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.packet.AirplaneStatePacket;
import thepowderguy.mcflight.common.packet.AirplaneUpdatePacket;
import thepowderguy.mcflight.math.Mat3;
import thepowderguy.mcflight.math.Vec3;
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
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityAirplane extends Entity {
	
	public static double[] lift = {0.0, 0.06492615177838702, 0.12984941173838446, 0.1947553207880445, 0.25960628528830265, 0.32433000977941967, 0.38880792970742317, 0.45286364415054925, 0.5162513485456844, 0.5786442674148072, 0.6396230870914297, 0.6986643884470395, 0.7551290796175415, 0.8082508287296992, 0.8571244966275771, 0.9006945695989816, 0.9377435921019033, 0.9668805994909583, 0.9865295507438308, 0.9949177611877131, 0.9900643352257495, 0.9697685990634762, 0.931598533435264, 0.8728792063307597, 0.7906812057213275, 0.6897758258053173, 0.6501610341965861, 0.633599839734877, 0.6295064882803101, 0.6364344334189236, 0.6495190528271049, 0.6622106946333051, 0.6740955347138786, 0.6851590932219072, 0.6953878909155601, 0.7047694655804739, 0.713292387213041, 0.7209462719461079, 0.7277217947001187, 0.7336107005442869, 0.7386058147539587, 0.7427010515519081, 0.74589142152292, 0.7481730376926238, 0.7495431202631728, 0.75, 0.749543120265523, 0.7481730376973214, 0.7458914215299592, 0.7427010515612804, 0.7386058147656527, 0.7336107005582883, 0.7277217947164103, 0.7209462719646701, 0.713292387233851, 0.7047694656035066, 0.6953878909407871, 0.6851590932492979, 0.6740955347433997, 0.6622106946649206, 0.6495190528607763, 0.6360360721415066, 0.621778179442223, 0.6067627458089186, 0.5910080652345242, 0.5745333323704964, 0.5573586191410906, 0.5395048502888138, 0.5209937778808491, 0.5018479548075121, 0.48209070730502857, 0.46174610653610804, 0.44083893926294065, 0.4193946776483449, 0.3974394482218616, 0.37500000004860135, 0.35210367213962995, 0.32877836014359374, 0.30505248236016935, 0.2809549451167431, 0.25651510755050333, 0.23176274583885437, 0.2067280169217311, 0.18144142176001293, 0.1559337681748013, 0.1302361333128352, 0.10437982578377492, 0.07839634751548394, 0.05231735537378239, 0.026174622593432346, 0.0};
	
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
	Minecraft minecraft;
	
	public EntityAirplane(World worldIn) {
		super(worldIn);
		inv = new InventoryBasic("ASDF", false, 17);
		this.setSize(1.0f, 1.0f);
		this.fuel = EntityBiplane.fuelCapacity;
		minecraft = Minecraft.getMinecraft();
	}

	public EntityAirplane(World worldIn, double x, double y, double z) {
		this(worldIn, x, y, z, EntityBiplane.fuelCapacity);
	}
	
	public EntityAirplane(World worldIn, double x, double y, double z, double fuel_i) {
		super(worldIn);
		this.setPosition(x, y, z);
		this.motionX = this.motionY = this.motionZ = this.prevPosX = this.prevPosY = this.prevPosZ = 0;
		this.setSize(1.0f, 1.0f);
		this.fuel = fuel_i;
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
		fuel = tagCompound.getDouble("Fuel");


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

	@Override
	public void writeEntityToNBT(NBTTagCompound tagCompound) {
		tagCompound.setTag("Rotation", this.newFloatNBTList(rotationYaw, rotationPitch, rotationRoll));
		tagCompound.setTag("MotionRotation", this.newDoubleNBTList(angVelX, angVelY, angVelZ));
		tagCompound.setDouble("Engine", engine);
		tagCompound.setDouble("Fuel", fuel);
		
		

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

	public float rotationRoll = 0;
	public float prevRotationRoll = 0;
	public double angVelX = 0.0;
	public double angVelY = 0.0;
	public double angVelZ = 0.0;
	public double engine = 0;
	public double velocity = 0.0;
	public boolean isOnGround = false;
	public double fuel = 0;
	public double weight = 1.0;

	public Vec3 vfwd;
	public Vec3 vup;
	public Vec3 vside;
	public Vec3 lift_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 inddrag_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 drag_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 thrust_vec = new Vec3(0.0, 0.0, 0.0);
	public Vec3 gravity_vec = new Vec3(0.0, -gravity_const, 0.0);
	
	public static double gravity_const = 0.035;
	public static double drag_const = 0.015;//0.025;
	public static double dragMul_forward = -1.0;
	public static double dragMul_sideways = -1.0;
	public static double dragMul_vertical = -1.0;
	public static double lift_const = 0.2;//0.12;
	public static double camber = 0;
	public static double thrust_const = 0.02;
	public static double controlSensitivity = 2.0;
	public static double rotationSpeedDecay = 0.8;
	public static double fuelCapacity = 100.0;
	
	public static double mouseX = 0;
	public static double mouseY = 0;
	public static double viewPitchOffset = 0.0;
	public static double viewYawOffset = 0.0;
	
	public double prevMotionX = 0;
	public double prevMotionY = 0;
	public double prevMotionZ = 0;
	
	public boolean stall = false;
	public double damage = 0.0;
	
	public static String engineSound = "mcflight:airplane.biplane.engine";
	
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
	// * implement refueling and gui changes
	// * Make the model look better and add rotating things
	// * Make the sounds better
	// * Add collision detect
	// * Realistic torquing?
	
	
	public void onUpdate()
	{
		super.onUpdate();
		prevRotationRoll = rotationRoll;
		tick++;
		//System.out.println((Math.pow(0.5, engine/5)));
		if (!world.isRemote) {
			if (this.engine > 0.0 && (tick % Math.round(10.0*(Math.pow(0.5, engine/5.0)))) == 0)
				this.playSound(Mcflight.sound_engine, 0.4f, (float)engine/3 + 1);
			if (this.getControllingPassenger() instanceof EntityPlayer)
				return;
			else
				engine = 0;
		}

		if ((tick%3)==0 && world.isRemote) {
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
		double bodyDragFwd = velocity_sq * drag_const * dragMul_forward * Vec3.cosTheta(vel, vfwd) * (world.isRemote && Keyboard.isKeyDown(KEYBIND_BRAKE) ? 2.0 : 1.0);
		double bodyDragUp = velocity_sq * drag_const * dragMul_vertical * Vec3.cosTheta(vel, vup);
		double bodyDragSideways = velocity_sq * drag_const * dragMul_sideways *Vec3.cosTheta(vel, vside);
		drag_vec = new Vec3 (
				bodyDragFwd*vfwd.x + bodyDragUp*vup.x + bodyDragSideways * vside.x,
				bodyDragFwd*vfwd.y + bodyDragUp*vup.y + bodyDragSideways * vside.y,
				bodyDragFwd*vfwd.z + bodyDragUp*vup.z + bodyDragSideways * vside.z);
		drag_vec.mul(1.0/weight);
		tmpMotion.add(drag_vec);
		
		//Lift
		Vec3 out = Vec3.unitvector(Vec3.cross(vel, Vec3.cross(vup, vel)));
		double angleOfAttack = Vec3.angle(vup, vel) - 90.0;
		double lift = getLiftFromAlpha(angleOfAttack+camber) * lift_const * velocity_sq * air;
		double inducedDrag = getDragFromAlpha(angleOfAttack+camber) * lift_const * velocity_sq * air;
		
		lift_vec = Vec3.mul(out, lift / weight);
		tmpMotion.add(lift_vec);

		inddrag_vec = Vec3.unitvector(vel).mul(-1.0 * inducedDrag / weight);
		tmpMotion.add(inddrag_vec);
		
		tmpMotion.add(gravity_vec);
		
		motionX = tmpMotion.x;
		motionY = tmpMotion.y;
		motionZ = tmpMotion.z;
		
		velocity = Math.sqrt(motionX*motionX+motionY*motionY+motionZ*motionZ);
		
		
		//Gravity

		
		//Control Surfaces
		fuel -= engine/100.0;

		double forceElevator = 0.0;
		double forceAlierons = 0.0;
		double forceRudder = 0.0;

		if (world.isRemote && minecraft.player.getRidingEntity() == this) {
			
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

		if (fuel <= 0) {
			fuel = 0;
			engine = 0;
		}
		//TODO forceRudder on ground
		Vec3 angVelocity = new Vec3(angVelX, angVelY, angVelZ);
		angVelocity = Vec3.addn(angVelocity,
				Vec3.mul(vfwd, forceAlierons*1.0*velocity_sq*air*controlSensitivity/weight),
				Vec3.mul(vup, forceRudder*1.0*velocity_sq*air*controlSensitivity/weight
						+ Vec3.dot(vel, vside)*0.5/weight
						+ (onGround? (forceRudder*6.0*velocity):0)),
				Vec3.mul(vside, forceElevator*1.0*velocity_sq*air*controlSensitivity/weight
						- Vec3.dot(vel, vup)*0.5/weight));
		//angVelocity = Vector.addn(angVelocity, Vector.mul(vfwd, forceAlierons*1.0*controlSensitivity), Vector.mul(vup, forceRudder*1.0*controlSensitivity), Vector.mul(vwing, forceElevator*1.0*controlSensitivity));
		//enable to test control surfaces (constant)
		angVelX = angVelocity.x*rotationSpeedDecay * (onGround?0.8:1.0);
		angVelY = angVelocity.y*rotationSpeedDecay;
		angVelZ = angVelocity.z*rotationSpeedDecay;
		
		
		//Integration
		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		
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
		
		//Collision detection TODO make better collision detect
		if (isWheelInsideBlock(posX, posY-0.2, posZ, this.getEntityWorld())) {
			if (motionY < 0)
			{
				if (motionY < -0.5) {
					AirplaneStatePacket pack = new AirplaneStatePacket(1);
					Mcflight.network2.sendToServer(pack);
				}
				posY -= motionY;
				motionY = 0;
			}
			this.isOnGround = true;
			this.onGround = true;
			this.isCollidedVertically = true;
		} else {
			this.isOnGround = false;
			this.onGround = false;
			this.isCollidedVertically = false;
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

			
			double friction = 0.01;
			if (world.isRemote && Keyboard.isKeyDown(KEYBIND_BRAKE)) {
				friction *= 3.0;
			}
			
			double mag = Math.sqrt(motionX*motionX+motionZ*motionZ);
			if (mag > friction) {
				motionX *= (mag-friction)/mag;
				motionZ *= (mag-friction)/mag;
			} else {
				motionX = 0.0;
				motionZ = 0.0;
			}
		}
		
		if (world.isRemote && minecraft.player.getRidingEntity() == this) {
			double accel = Math.sqrt(
					(motionX-prevMotionX)*(motionX-prevMotionX)+
					(motionY-prevMotionY+gravity_const)*(motionY-prevMotionY+gravity_const)+
					(motionZ-prevMotionZ)*(motionZ-prevMotionZ));
			//Vector fdelta = new Vector(motionX-prevMotionX, motionY-prevMotionY, motionZ-prevMotionZ);
			RenderAirplaneInterface.instance.setDebugVars(velocity, angleOfAttack, mag_lift, mag_drag, mag_inddrag, mag_thrust, air, angVelocity.mag(), this);
			RenderAirplaneInterface.instance.setVars(velocity, accel, this.posY, rotationPitch, rotationYaw, rotationRoll);
		}
		
		
		//TODO!!
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
			rotationYaw+(rotationYaw-prevRotationYaw)*partialTicks,
			rotationPitch+(rotationPitch-prevRotationPitch)*partialTicks,
			rotationRoll+(rotationRoll-prevRotationRoll)*partialTicks);
	}

    public void onDeath(DamageSource cause)
    {
        if (world.getGameRules().getBoolean("doEntityDrops"))
        {
           	this.dropItemWithOffset(Mcflight.item_airplane_biplane, 1, 0.0f);
           	for (int i = 0; i < inv.getSizeInventory(); i++) {
           		ItemStack item = inv.getStackInSlot(i);
           		if (!item.isEmpty())
           			this.entityDropItem(item, 0.0f);
           	}
        }
    }
   
	@Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        if (this.isEntityInvulnerable(source))
        {
            return false;
        }
        else if (!world.isRemote && !this.isDead)
        {
            if (source instanceof EntityDamageSourceIndirect && source.getEntity() != null && this.isPassenger(source.getEntity()))
            {
                return false;
            }
            else
            {
                
                boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
                
                damage += amount;
                
                if (flag || amount > 4.0)
                {
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
	
	public double getLiftFromAlpha(double a) {
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

	public double getDragFromAlpha(double a) {
		return dSin(a/2.0)*dSin(a/2.0)*2.0;
	}
	
	public double getAirDensity(double x) {
		return 1/(1+Math.pow(1.05, x-256));
	}
	
	public boolean isWheelInsideBlock(double x, double y, double z, World w) {
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = w.getBlockState(pos);
		if (state.getBlock().isAir(state, w, pos))		
			return false;
		return true;
	}
	
	//Trig functions

	public static double dSin(double a) {
		return Math.sin(Math.toRadians(a));
	}

	public static double dCos(double a) {
		return Math.cos(Math.toRadians(a));
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
	
	/*@Override
	public boolean interactFirst(EntityPlayer playerIn) {
		if (this.getControllingPassenger() != null && this.getControllingPassenger() instanceof EntityPlayer && this.getControllingPassenger() != playerIn) {
			return true;
		} else {
			if (!world.isRemote) {
				playerIn.startRiding(this);
				mouseX = 0;
				mouseY = 0;
			}
			return true;
		}
	}*/


	@Override
	public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
	{
		if (!world.isRemote)
		{
			if(player.isSneaking()) {
				player.openGui(Mcflight.instance, 0, world, this.getEntityId(), 0, 0);
				return true;
			} else {
				player.startRiding(this);
				return true;
			}
		}
		return false;
    }

	/*@Override
	public boolean processInitialInteract(EntityPlayer player, @Nullable ItemStack stack, EnumHand hand)
    {
        if (!world.isRemote && !player.isSneaking())
        {
            player.startRiding(this);
        }

        return true;
    }*/

	@Override
	public void updatePassenger(Entity passenger) {
		if (passenger != null) {
			passenger.setPosition(this.posX, this.posY + 0.5, this.posZ);
			passenger.rotationYaw = -rotationYaw;
			passenger.rotationPitch = rotationPitch;
		}
	}
	
	@Override
	public double getMountedYOffset() {
		return  0.5;
	}

    public void setDead()
    {
    	super.setDead();
 /*   	if (world.isRemote) {
    		if (this.getPassengers().size() > 0 && this.getControllingPassenger().isDead)
    			Minecraft.getMinecraft().setIngameNotInFocus();
    		else
    			Minecraft.getMinecraft().setIngameFocus();
    	}*/
    }
    
    @Override
    public void dismountRidingEntity() {
		System.out.println("AAA" + world.isRemote);
    	if (world.isRemote) {
    		if (Minecraft.getMinecraft().player.isDead)
    			//Minecraft.getMinecraft().setIngameNotInFocus();
    			Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
    		else {
    			Minecraft.getMinecraft().setIngameFocus();
    		}
    	}
    	super.dismountRidingEntity();
    }

	protected void entityInit()
	{
	}
}

