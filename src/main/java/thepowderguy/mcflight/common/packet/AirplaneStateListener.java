package thepowderguy.mcflight.common.packet;

import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplane;
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
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			RayTraceResult trace = player.rayTrace(5.0, 0.0f);
			if (trace.entityHit != null && trace.entityHit instanceof EntityAirplane) {
				if (player.getHeldItem(player.getActiveHand()).getItem() == Mcflight.item_paint) {
					EnumDyeColor col = EnumDyeColor.byMetadata(player.getHeldItem(player.getActiveHand()).getItemDamage());
					if (message.value == 0)
						((EntityAirplane)trace.entityHit).FuselageColor = col;
					else if (message.value == 1)
						((EntityAirplane)trace.entityHit).WingColor = col;
				}
			}
			break;
		default:
			break;
		}
		return null;
	}
}