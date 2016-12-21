package thepowderguy.mcflight.common.item;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thepowderguy.mcflight.common.entity.AirplaneColors;

public class AircraftPaint extends Item {
	public AircraftPaint() {
		super();
	}
	public static int getCustColor(ItemStack i) {
		return AirplaneColors.getHexRgb(EnumDyeColor.byDyeDamage(i.getMetadata()));
	}
}
