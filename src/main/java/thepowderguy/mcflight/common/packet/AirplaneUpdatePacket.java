package thepowderguy.mcflight.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class AirplaneUpdatePacket implements IMessage {
	
	double positionX;
	double positionY;
	double positionZ;
	double engine;
	float rotationPitch;
	float rotationYaw;
	float rotationRoll;
	public AirplaneUpdatePacket() { }
	public AirplaneUpdatePacket(double px, double py, double pz, double e, float rp, float ry, float rr) {
		positionX = px;
		positionY = py;
		positionZ = pz;
		engine = e;
		rotationPitch = rp;
		rotationYaw = ry;
		rotationRoll = rr;
	}
	
	
	@Override
	public void fromBytes(ByteBuf buf) {
		positionX = buf.readDouble();
		positionY = buf.readDouble();
		positionZ = buf.readDouble();
		engine = buf.readDouble();
		rotationPitch = buf.readFloat();
		rotationYaw = buf.readFloat();
		rotationRoll = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(positionX);
		buf.writeDouble(positionY);
		buf.writeDouble(positionZ);
		buf.writeDouble(engine);
		buf.writeFloat(rotationPitch);
		buf.writeFloat(rotationYaw);
		buf.writeFloat(rotationRoll);
	}

}
