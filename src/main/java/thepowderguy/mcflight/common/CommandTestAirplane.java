package thepowderguy.mcflight.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import thepowderguy.mcflight.client.ClientEventHandler;
import thepowderguy.mcflight.client.RenderAirplaneInterface;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;

public class CommandTestAirplane implements ICommand {
	List<String> aliases = new ArrayList<String>();
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "testairplane";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO Auto-generated method stub
		return "/testairplane";
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return aliases;
	}
	
	public BlockPos addandmul(EntityPlayer p, Vec3 v) {
		return new BlockPos(v.x*10+p.posX, v.y*10+p.posY, v.z*10+p.posZ);
	}
	
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (sender instanceof EntityPlayer && !sender.getEntityWorld().isRemote) {
			EntityPlayer p = (EntityPlayer) sender;
			Vec3 vx = Mat3.transform(-p.rotationYaw, p.rotationPitch, ClientEventHandler.camroll, new Vec3(1.0, 0.0, 0.0));
			Vec3 vy = Mat3.transform(-p.rotationYaw, p.rotationPitch, ClientEventHandler.camroll, new Vec3(0.0, 1.0, 0.0));
			Vec3 vz = Mat3.transform(-p.rotationYaw, p.rotationPitch, ClientEventHandler.camroll, new Vec3(0.0, 0.0, 1.0));
			p.world.setBlockState(addandmul(p, vx), Blocks.IRON_BLOCK.getDefaultState());
			p.world.setBlockState(addandmul(p, vy), Blocks.EMERALD_BLOCK.getDefaultState());
			p.world.setBlockState(addandmul(p, vz), Blocks.DIAMOND_BLOCK.getDefaultState());
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

}
