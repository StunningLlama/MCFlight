package thepowderguy.mcflight.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thepowderguy.mcflight.common.entity.AirplaneColors;

public class AircraftPaint extends Item {
	public AircraftPaint() {
		super();
	}
	
	public static int getCustColor(ItemStack i) {
		return AirplaneColors.getHexRgb(EnumDyeColor.byDyeDamage(i.getMetadata()));
	}
	

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems)
    {
        for (int i = 0; i < 16; i++)
        {
            ItemStack itemstack = new ItemStack(itemIn, 1, i);
            subItems.add(itemstack);
        }
    }

}
