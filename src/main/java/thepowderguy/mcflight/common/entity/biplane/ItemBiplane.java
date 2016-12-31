package thepowderguy.mcflight.common.entity.biplane;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thepowderguy.mcflight.common.entity.biplane.EntityBiplane;

public class ItemBiplane extends Item {

	public ItemBiplane() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
    public EnumActionResult onItemUse(EntityPlayer p, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		if (p.world.isRemote || p.getHeldItemMainhand() == null)
			return EnumActionResult.PASS;
		if (p.getHeldItemMainhand().getItem() instanceof ItemBiplane) {
			ItemStack i = p.getHeldItemMainhand();
			EntityBiplane plane = new EntityBiplane(p.getEntityWorld(), pos.getX(), pos.getY()+2.5, pos.getZ(), EntityBiplane.fuelCapacity);
			plane.rotationYaw = -p.rotationYaw;
			p.getEntityWorld().spawnEntity(plane);
			plane.readEntityFromItemStack(p.getHeldItemMainhand());
			i.shrink(1);
			return EnumActionResult.SUCCESS;
			//i.setCount(p.getHeldItemMainhand().getCount() - 1);
			//p.setHeldItem(EnumHand.MAIN_HAND, i);
		}
		return EnumActionResult.PASS;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced)
    {
		NBTTagCompound tagCompound = stack.getSubCompound("Color");
		if (tagCompound == null) return;
		double fuel = tagCompound.getDouble("Fuel");
		EnumDyeColor col1 = EnumDyeColor.byMetadata(tagCompound.getInteger("ColFuselage"));
		EnumDyeColor col2 = EnumDyeColor.byMetadata(tagCompound.getInteger("ColWing"));
    	tooltip.add("Fuel: " + (int)Math.round(fuel) + "%");
    	tooltip.add("Fuselage Color: " + col1.getName());
    	tooltip.add("Wing Color: " + col2.getName());
    }
}
