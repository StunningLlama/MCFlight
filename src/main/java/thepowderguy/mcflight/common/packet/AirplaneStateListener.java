package thepowderguy.mcflight.common.packet;

import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplane;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AirplaneStateListener implements IMessageHandler<AirplaneStatePacket, IMessage> {

	@Override
	public IMessage onMessage(AirplaneStatePacket message, MessageContext ctx) {
		switch (message.state) {
		case 1:
			if (ctx.getServerHandler().playerEntity.getRidingEntity() instanceof EntityAirplane) {
				((EntityAirplane)ctx.getServerHandler().playerEntity.getRidingEntity()).explode();
			}
			break;
		case 2:
		case 3:
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			Entity entity = ctx.getServerHandler().playerEntity.world.getEntityByID(message.value);
			if (entity != null && entity instanceof EntityAirplane) {
				if (EntityAirplane.isPaint(player.getHeldItemMainhand())) {
					EnumDyeColor col = EnumDyeColor.byDyeDamage(player.getHeldItemMainhand().getItemDamage());
					if (message.state == 2)
						((EntityAirplane)entity).setFuselageColor(col);
					else if (message.state == 3)
						((EntityAirplane)entity).setWingColor(col);
				}
			}
			break;
		default:
			break;
		}
		return null;
	}
}