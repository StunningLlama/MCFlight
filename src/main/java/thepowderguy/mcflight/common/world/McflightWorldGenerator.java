package thepowderguy.mcflight.common.world;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;
import thepowderguy.mcflight.common.Mcflight;

public class McflightWorldGenerator implements IWorldGenerator  {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		// TODO Auto-generated method stub
		  switch (world.provider.getDimension())
		  {
		   case -1: generateNether(world, random, chunkX*16, chunkZ*16);
		   case 0: generateSurface(world, random, chunkX*16, chunkZ*16);
		  }
		 }

		  

		  private void generateSurface(World world, Random random, int blockX, int blockZ) 
		 {
		  int Xcoord = blockX + random.nextInt(16);
		  int Ycoord = random.nextInt(60);
		  int Zcoord = blockZ + random.nextInt(16);
		  
		  (new WorldGenOil(Mcflight.block_oil)).generate(world, random, new BlockPos(Xcoord, Ycoord, Zcoord));
		 }
		 
		 private void generateNether(World world, Random random, int blockX, int blockZ) 
		 {
		  int Xcoord = blockX + random.nextInt(16);
		  int Ycoord = random.nextInt(60);
		  int Zcoord = blockZ + random.nextInt(16);
		  
		  (new WorldGenOil(Mcflight.block_oil)).generate(world, random, new BlockPos(Xcoord, Ycoord, Zcoord));
		 }

}
