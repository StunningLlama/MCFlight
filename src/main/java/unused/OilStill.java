package unused;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;

public class OilStill extends BlockStaticLiquid {

	public OilStill(Material materialIn) {
		super(materialIn);
		this.setUnlocalizedName("oil_still");
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.LIQUID;
	}
}
