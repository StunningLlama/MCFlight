package thepowderguy.mcflight.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thepowderguy.mcflight.client.MCFlightClientProxy;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

public class EntityAirplaneCamera extends Entity {
	
	float rotationRoll;
	float prevRotationRoll;
	
	//Cockpit view
	//Third person level
	//Tail view
	//Wing view?
	
	static CameraView view_cockpit = new CameraView("airplane.view.cockpit", false);
	static CameraView view_tail = new CameraView("airplane.view.tail", true);
	static CameraView view_behind = new CameraView("airplane.view.behind", true);
	static CameraView view_follow = new CameraView("airplane.view.follow", true);
	static CameraView view_wing_L = new CameraView("airplane.view.wingl", true);
	static CameraView view_wing_R = new CameraView("airplane.view.wingr", true);
	static CameraView view_passenger = new CameraView("airplane.view.passenger", true);
	
	public static float min_zoom = 1.0f;
	public static float max_zoom = 10.0f;
	
	public static CameraView[] views = new CameraView[] {
		view_cockpit  ,
		view_tail     ,
		view_behind	  ,
		view_follow   ,
		view_wing_L   ,
		view_wing_R   ,
		view_passenger
	};
	
	EntityAirplane ent;
	
	public EntityAirplaneCamera(World worldIn, EntityAirplane plane) {
		super(worldIn);
		ent = plane;
		posX = plane.posX;
		posY = plane.posY;
		posZ = plane.posZ;
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
this.setPosition(posX, posY, posZ);
		this.rotationYaw = 0;
		this.rotationPitch = 0;
		this.rotationRoll = 0;
		this.prevRotationYaw = rotationYaw;
		this.prevRotationPitch = rotationPitch;
		this.prevRotationRoll = rotationRoll;
		this.spawnEntity(this, worldIn);
		Minecraft.getMinecraft().world.weatherEffects.add(this);
		// TODO Auto-generated constructor stub
	}


	public boolean spawnEntity(Entity entityIn, World w)
	{
		// do not drop any items while restoring blocksnapshots. Prevents dupes
		if (!w.isRemote && (entityIn == null || (entityIn instanceof net.minecraft.entity.item.EntityItem && w.restoringBlockSnapshots))) return false;

		int i = MathHelper.floor(entityIn.posX / 16.0D);
		int j = MathHelper.floor(entityIn.posZ / 16.0D);

		if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityJoinWorldEvent(entityIn, w))) return false;

		w.getChunkFromChunkCoords(i, j).addEntity(entityIn);
		w.loadedEntityList.add(entityIn);
		w.onEntityAdded(entityIn);
		return true;
	}


	@Override
	protected void entityInit() {
		this.setSize(0.1f, 0.1f);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onUpdate() {
        this.world.theProfiler.startSection("entityBaseTick");

        if (this.isRiding() && this.getRidingEntity().isDead)
        {
            this.dismountRidingEntity();
        }

        if (this.rideCooldown > 0)
        {
            --this.rideCooldown;
        }

        this.prevDistanceWalkedModified = this.distanceWalkedModified;

        this.spawnRunningParticles();
        this.handleWaterMovement();

        if (this.world.isRemote)
        {
            this.extinguish();
        }

        if (this.posY < -64.0D)
        {
            this.kill();
        }

        this.firstUpdate = false;
        this.world.theProfiler.endSection();
	}

    public float getEyeHeight()
    {
    	return 0f;
    }
    public boolean hasPlayer() {
    	return ent.getControllingPassenger() != null;
    }
    public AbstractClientPlayer getPlayer() {
    	return (AbstractClientPlayer)ent.getControllingPassenger();
    }

    public Vec3 getInterpolatedRotations(float partialTicks) {

    	return new Vec3(
    			prevRotationYaw+(rotationYaw-prevRotationYaw)*partialTicks,
    			prevRotationPitch+(rotationPitch-prevRotationPitch)*partialTicks,
    			prevRotationRoll+(rotationRoll-prevRotationRoll)*partialTicks);

    }


	public static float clamp(float min, float val, float max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}
	
    public void updatePositions(boolean isPlayerRiding, Mat3 transform) {

		//super.onUpdate();

		CameraView view = EntityAirplaneCamera.views[MCFlightClientProxy.keyhandler.camera_mode];
		view.prevZoom = view.zoom;
		if (MCFlightClientProxy.keyhandler.zoom_in.isKeyDown()) {
			System.out.println("Zoom in!");
			view.zoom -= 0.2;
			view.zoom = clamp(EntityAirplaneCamera.min_zoom, view.zoom, EntityAirplaneCamera.max_zoom);
		}
		if (MCFlightClientProxy.keyhandler.zoom_out.isKeyDown()) {
			System.out.println("Zoom Out!");
			view.zoom += 0.2;
			view.zoom = clamp(EntityAirplaneCamera.min_zoom, view.zoom, EntityAirplaneCamera.max_zoom);
		}
		
		this.lastTickPosX = this.posX;
		this.lastTickPosY = this.posY;
		this.lastTickPosZ = this.posZ;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if (world.isRemote)
		{
			EntityAirplane entity = ent;

			if (isPlayerRiding) {
				Minecraft mc = Minecraft.getMinecraft();
				CameraView currentview = views[MCFlightClientProxy.keyhandler.camera_mode];
				if (MCFlightClientProxy.keyhandler.look_around.isKeyDown()) {
					mc.mouseHelper.mouseXYChange();
					int dx = mc.mouseHelper.deltaX;
					int dy = mc.mouseHelper.deltaY;
					currentview.viewYawOffset += dx / -20.0;
					currentview.viewPitchOffset += dy / -20.0;
					currentview.viewYawOffset = EntityAirplane.clamp(-180, currentview.viewYawOffset, 180);
					currentview.viewPitchOffset = EntityAirplane.clamp(-90, currentview.viewPitchOffset, 90);
				}
				int view_id = MCFlightClientProxy.keyhandler.camera_mode;
				Mat3 basetransform = null;
				Mat3 looktransform = Mat3.getTransformMatrix(currentview.viewYawOffset, currentview.viewPitchOffset, 0);
				GameSettings settings = Minecraft.getMinecraft().gameSettings;
				Vec3 rot = new Vec3(entity.rotationYaw, entity.rotationPitch, entity.rotationRoll);
				Vec3 entPos = new Vec3(entity.posX, entity.posY , entity.posZ);
				Vec3 camPos;
				switch(view_id) {
				case 0:
					basetransform = Mat3.getTransformMatrix(rot.x, rot.y, rot.z);
					camPos = Vec3.add(entPos, Vec3.mul(entity.vup, 0.66));
					settings.thirdPersonView = 0;
					break;
				case 1:
					basetransform = Mat3.getTransformMatrix(rot.x, rot.y, rot.z);
					camPos = entPos;
					settings.thirdPersonView = 1;
					break;
				case 2:
					basetransform = Mat3.getTransformMatrix(rot.x, rot.y, 0f);
					camPos = entPos;
					settings.thirdPersonView = 1;
					break;
				case 3:
					basetransform = Mat3.getTransformMatrix(rot.x, 0f, 0f);
					camPos = entPos;
					settings.thirdPersonView = 1;
					break;
				case 4:
					Vec3 pos = transform.transform(new Vec3(3.0, 0.0, 0.0));
					basetransform = getTransformFromPos(pos, entity.vfwd);
					camPos = Vec3.add(entPos, pos);
					settings.thirdPersonView = 0;
					break;
				case 5:
					Vec3 pos1 = transform.transform(new Vec3(-3.0, 0.0, 0.0));
					basetransform = getTransformFromPos(pos1, Vec3.mul(entity.vfwd, -1.0));
					camPos = Vec3.add(entPos, pos1);
					settings.thirdPersonView = 0;
					break;
				case 6:
					basetransform = Mat3.getTransformMatrix(rot.x, rot.y, rot.z);
					camPos = Vec3.add(entPos, Vec3.mul(entity.vup, 0.66), Vec3.mul(entity.vfwd, -1.2));
					settings.thirdPersonView = 0;
					break;
				default:
					throw new RuntimeException("WTF!");
				}
				Mat3 comtransform = Mat3.mul(basetransform, looktransform);
				Vec3 out = Mat3.getangles(comtransform);
				this.setPosition(camPos.x, camPos.y, camPos.z);
				AbstractClientPlayer player = this.getPlayer();
				player.prevRotationYawHead = player.rotationYawHead;
				player.prevRotationYaw = player.rotationYawHead;
				player.prevRotationPitch = player.rotationPitch;
				player.rotationYawHead = (float) -currentview.viewYawOffset;
				player.rotationYaw = (float) -currentview.viewYawOffset;
				player.rotationPitch = (float) currentview.viewPitchOffset;
				/*player.prevRotationYawHead = 0;
				player.prevRotationYaw = 0;
				player.prevRotationPitch = 0;
				player.rotationYawHead = 0;
				player.rotationYaw = 0;
				player.rotationPitch = 0;
				player.renderYawOffset = 0;
				player.prevRenderYawOffset = 0;*/
				//player.turn(yaw, pitch);
				this.prevRotationYaw = rotationYaw;
				this.prevRotationPitch = rotationPitch;
				this.prevRotationRoll = rotationRoll;
				this.rotationYaw = (float)out.x;
				this.rotationPitch = (float)out.y;
				this.rotationRoll = (float)out.z;
				
				if (rotationYaw-prevRotationYaw > 180)
					prevRotationYaw += 360;
				if (rotationYaw-prevRotationYaw < -180)
					prevRotationYaw += -360;
				if (rotationRoll-prevRotationRoll > 180)
					prevRotationRoll += 360;
				if (rotationRoll-prevRotationRoll < -180)
					prevRotationRoll += -360;
			}
		}
    }
    
    private static Mat3 getTransformFromPos(Vec3 pos, Vec3 horizontal) {
    	Vec3 fwd = pos.unitvector().mul(-1.0);
    	Vec3 side = horizontal.unitvector();
    	Vec3 up = Vec3.cross(fwd, side).unitvector();
    	Vec3 angles = Mat3.getangles(side, up, fwd);
    	return Mat3.getTransformMatrix(angles.x, angles.y, angles.z);
    }
}

