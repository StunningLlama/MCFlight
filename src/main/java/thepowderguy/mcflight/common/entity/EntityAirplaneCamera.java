package thepowderguy.mcflight.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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
	
	static CameraView view_cockpit = new CameraView();
	static CameraView view_tail = new CameraView();
	static CameraView view_follow = new CameraView();
	static CameraView view_wing_L = new CameraView();
	static CameraView view_wing_R = new CameraView();
	static CameraView view_passenger = new CameraView();
	
	public static float min_zoom = 1.0f;
	public static float max_zoom = 10.0f;
	
	public static CameraView[] views = new CameraView[] {
		view_cockpit  ,
		view_tail     ,
		view_follow   ,
		view_wing_L   ,
		view_wing_R   ,
		view_passenger
	};
	
	EntityAirplane ent;
	
	public EntityAirplaneCamera(World worldIn, EntityAirplane plane) {
		super(worldIn);
		ent = plane;
		Minecraft.getMinecraft().world.weatherEffects.add(this);
		// TODO Auto-generated constructor stub
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

    
    public void updatePositions(boolean isPlayerRiding, Mat3 transform) {

		//super.onUpdate();
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
				if (Mcflight.keyhandler.look_around.isKeyDown()) {
					mc.mouseHelper.mouseXYChange();
					int dx = mc.mouseHelper.deltaX;
					int dy = mc.mouseHelper.deltaY;
					views[Mcflight.keyhandler.camera_mode].viewYawOffset += dx / -20.0;
					views[Mcflight.keyhandler.camera_mode].viewPitchOffset += dy / -20.0;
					views[Mcflight.keyhandler.camera_mode].viewYawOffset = EntityAirplane.clamp(-180, views[Mcflight.keyhandler.camera_mode].viewYawOffset, 180);
					views[Mcflight.keyhandler.camera_mode].viewPitchOffset = EntityAirplane.clamp(-90, views[Mcflight.keyhandler.camera_mode].viewPitchOffset, 90);
				}
				int view_id = Mcflight.keyhandler.camera_mode;
				Mat3 basetransform = null;
				Mat3 looktransform = Mat3.getTransformMatrix(views[Mcflight.keyhandler.camera_mode].viewYawOffset, views[Mcflight.keyhandler.camera_mode].viewPitchOffset, 0);
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
					basetransform = Mat3.getTransformMatrix(rot.x, 0f, 0f);
					camPos = entPos;
					settings.thirdPersonView = 1;
					break;
				case 3:
					Vec3 pos = transform.transform(new Vec3(3.0, 0.0, 0.0));
					basetransform = getTransformFromPos(pos, entity.vfwd);
					camPos = Vec3.add(entPos, pos);
					settings.thirdPersonView = 0;
					break;
				case 4:
					Vec3 pos1 = transform.transform(new Vec3(-3.0, 0.0, 0.0));
					basetransform = getTransformFromPos(pos1, Vec3.mul(entity.vfwd, -1.0));
					camPos = Vec3.add(entPos, pos1);
					settings.thirdPersonView = 0;
					break;
				case 5:
					basetransform = Mat3.getTransformMatrix(rot.x, rot.y, rot.z);
					camPos = Vec3.add(entPos, Vec3.mul(entity.vup, 0.66), Vec3.mul(entity.vfwd, -2.0));
					settings.thirdPersonView = 0;
					break;
				default:
					throw new RuntimeException("WTF!");
				}
				Mat3 comtransform = Mat3.mul(basetransform, looktransform);
				Vec3 out = Mat3.getangles(comtransform);
				this.setPosition(camPos.x, camPos.y, camPos.z);
				this.prevRotationYaw = rotationYaw;
				this.prevRotationPitch = rotationPitch;
				this.prevRotationRoll = rotationRoll;
				this.rotationYaw = (float)out.x;
				this.rotationPitch = (float)out.y;
				this.rotationRoll = (float)out.z;
				
				

				if (rotationYaw < -180.0) {
					rotationYaw += 360;
					prevRotationYaw += 360;
				}
				if (rotationYaw > 180.0) {
					rotationYaw -= 360;
					prevRotationYaw -= 360;
				}
				if (rotationRoll < -180.0) {
					rotationRoll += 360;
					prevRotationRoll += 360;
				}
				if (rotationRoll > 180.0) {
					rotationRoll -= 360;
					prevRotationRoll -= 360;
				}

				if (rotationPitch < -90.0) {
					rotationPitch = -180 - rotationPitch;
					rotationYaw = -rotationYaw;
					rotationRoll = -rotationRoll;
					prevRotationPitch = -180 - prevRotationPitch;
					prevRotationYaw = -prevRotationYaw;
					prevRotationRoll = -prevRotationRoll;
				}
				if (rotationPitch > 90.0) {
					rotationPitch = 180 - rotationPitch;
					rotationYaw = -rotationYaw;
					rotationRoll = -rotationRoll;
					prevRotationPitch = 180 - prevRotationPitch;
					prevRotationYaw = -prevRotationYaw;
					prevRotationRoll = -prevRotationRoll;
				}
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

