package thepowderguy.mcflight.common.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EntityBiplane extends EntityAirplane {

	public EntityBiplane(World worldIn) {
		super(worldIn);
	}
	
	public EntityBiplane(World worldIn, double x, double y, double z, double fuel)
	{
		super(worldIn, x, y, z, fuel);
	}
    
}
