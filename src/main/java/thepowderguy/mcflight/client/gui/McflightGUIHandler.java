package thepowderguy.mcflight.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import thepowderguy.mcflight.common.entity.EntityAirplane;

public class McflightGUIHandler implements IGuiHandler{
		@Override
		public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			if (ID == 0 && world.getEntityByID(x) instanceof EntityAirplane) {
				EntityAirplane entity = (EntityAirplane) world.getEntityByID(x);
				return new ContainerAirplane(player.inventory, entity.inv, entity, player);
			}
			return null;
		}

		@Override
		public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
			if (ID == 0 && world.getEntityByID(x) instanceof EntityAirplane) {
				EntityAirplane entity = (EntityAirplane) world.getEntityByID(x);
				return new GuiAirplane (player.inventory, entity.inv, entity);
			} else if (ID == 1) {
				return new GuiPaintSelect(x);
			}
			return null;
		}
}
