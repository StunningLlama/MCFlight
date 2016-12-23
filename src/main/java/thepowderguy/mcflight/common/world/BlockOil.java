package thepowderguy.mcflight.common.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockOil extends BlockFluidClassic {

	
	public BlockOil(Fluid fluid, Material material) {
		super(fluid, material);
	//	this.
	}
	
	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 300;
	}
	
	@Override
    public boolean displaceIfPossible(World world, BlockPos pos)
    {
        if (world.isAirBlock(pos))
        {
            return true;
        }

        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == this)
        {
            return false;
        }

        if (displacements.containsKey(block))
        {
            if (displacements.get(block))
            {
                if (state.getBlock() != Blocks.SNOW_LAYER) //Forge: Vanilla has a 'bug' where snowballs don't drop like every other block. So special case because ewww...
                    block.dropBlockAsItem(world, pos, state, 0);
                return true;
            }
            return false;
        }

        Material material = state.getMaterial();
        if (material.blocksMovement() || material == Material.PORTAL || material == Material.FIRE)
        {
            return false;
        }

        int density = getDensity(world, pos);
        if (density == Integer.MAX_VALUE)
        {
            block.dropBlockAsItem(world, pos, state, 0);
            return true;
        }

        if (this.density > density)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
	//@Override
	//public int getRenderType() {
	//	return 1;
	//}
	
}
