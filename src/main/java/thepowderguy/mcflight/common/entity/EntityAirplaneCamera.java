package thepowderguy.mcflight.common.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thepowderguy.mcflight.util.Vec3;

public class EntityAirplaneCamera extends Entity {
	
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
			if (entity.vup == null)
				return;
			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
				Vec3 camPos = Vec3.mul(entity.vup, 0.66);
				this.setPosition(entity.posX + camPos.x, entity.posY + camPos.y, entity.posZ+camPos.z);
			} else {
				Vec3 camPos = Vec3.mul(entity.vup, 0);
				this.setPosition(entity.posX + camPos.x, entity.posY + camPos.y , entity.posZ+camPos.z);
				//System.out.println(this.posX  + " " + this.posY + " " + this.posZ);
			}
		}
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
}
