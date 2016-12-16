package thepowderguy.mcflight.common.packet;

import thepowderguy.mcflight.common.entity.EntityAirplane;
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
		default:
			break;
		}
		return null;
	}
}