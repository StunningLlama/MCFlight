package thepowderguy.mcflight.common.packet;

import thepowderguy.mcflight.common.entity.EntityAirplane;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AirplanePacketListener implements IMessageHandler<AirplaneUpdatePacket, IMessage> {

	@Override
	public IMessage onMessage(AirplaneUpdatePacket message, MessageContext ctx) {
		//World w = ctx.getServerHandler().playerEntity.worldObj;
		EntityPlayer p = ctx.getServerHandler().playerEntity;
		if (p.getRidingEntity() instanceof EntityAirplane) {
			EntityAirplane a = ((EntityAirplane)p.getRidingEntity());
			a.posX = message.positionX;
			a.posY = message.positionY;
			a.posZ = message.positionZ;
			a.engine = message.engine;
			a.rotationPitch = message.rotationPitch;
			a.rotationRoll = message.rotationRoll;
			a.rotationYaw = message.rotationYaw;
		}
		return null;
	}

}
