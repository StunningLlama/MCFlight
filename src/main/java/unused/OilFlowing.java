package unused;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;

public class OilFlowing extends BlockDynamicLiquid {

	public OilFlowing(Material materialIn) {
		super(materialIn);
		this.setUnlocalizedName("oil_flowing");
		// TODO Auto-generated constructor stub
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.LIQUID;
	}
}
