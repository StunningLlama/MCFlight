package thepowderguy.mcflight.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AirplaneStatePacket implements IMessage {
	public AirplaneStatePacket() {}
	int state;
	int value;
	public AirplaneStatePacket(int st, int val) {
		state = st;
		value = val;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		state = buf.readInt();
		value = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(state);
		buf.writeInt(value);
	}
}
