package thepowderguy.mcflight.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import thepowderguy.mcflight.common.entity.EntityBiplane;

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
			p.getHeldItemMainhand().setCount(p.getHeldItemMainhand().getCount() - 1);
			EntityBiplane plane = new EntityBiplane(p.getEntityWorld(), pos.getX(), pos.getY()+1.5, pos.getZ(), EntityBiplane.fuelCapacity);
			plane.rotationYaw = -p.rotationYaw;
			p.getEntityWorld().spawnEntity(plane);
		}
		return EnumActionResult.PASS;
    }
}
