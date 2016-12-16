package thepowderguy.mcflight.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AirplaneStatePacket implements IMessage {
	public AirplaneStatePacket() {}
	int state;
	public AirplaneStatePacket(int st) {
		state = st;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		state = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(state);
	}
}
