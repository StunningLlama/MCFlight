package thepowderguy.mcflight.client;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thepowderguy.mcflight.common.MCFlightCommonProxy;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplaneCamera;
import thepowderguy.mcflight.common.entity.EntityBiplane;
import thepowderguy.mcflight.common.entity.RenderBiplane;
import thepowderguy.mcflight.common.item.AircraftPaint;

public class MCFlightClientProxy extends MCFlightCommonProxy {
	@Override
	public void RegisterRenderItems() {

        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {

			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				if (tintIndex == 1)
					return (AircraftPaint.getCustColor(stack));
				return -1;
			}
        	
        }, Mcflight.item_paint);
        
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_flap, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "flap", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_wing, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "wing", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_doublewing, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "doublewing", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_propeller, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "propeller", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_jetengine, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "jetengine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_bigfuselage, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "bigfuselage", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_smallfuselage, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "smallfuselage", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_tail, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "tail", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_airplane_biplane, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "biplane", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_kerosene, 0, new ModelResourceLocation(Mcflight.MODID + ":" + "kerosene", "inventory"));
		for (int i = 0; i < 16; i++) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Mcflight.item_paint, i, new ModelResourceLocation(Mcflight.MODID + ":" + "aircraftpaint", "inventory"));
		}
		
		//Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, meta, location);
		//ModelLoader.setCustomStateMapper(Main.block_oil, (new StateMap.Builder()).addPropertiesToIgnore(BlockFluidBase.LEVEL).build());Item item = Item.getItemFromBlock((Block) fluidBlock);
	
	}
	
	private abstract class ItemMeshDefinitionImplementation implements ItemMeshDefinition {
		@Override
		public abstract ModelResourceLocation getModelLocation(ItemStack stack);
	}
	
	@Override
	public void RegisterFluidModel(IFluidBlock fluidBlock) {
		Item item = Item.getItemFromBlock((Block) fluidBlock);


		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("mcflight:oil_block", "oil");

		ModelBakery.registerItemVariants(item, modelResourceLocation);
		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinitionImplementation() {
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return modelResourceLocation;
			}
		}
		);

		ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
	}
	
	
	@Override
	public void RegisterRenderEntities() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBiplane.class, new RenderBiplane());
		RenderingRegistry.registerEntityRenderingHandler(EntityAirplaneCamera.class, new RenderAirplaneRider(Minecraft.getMinecraft().getRenderManager()));
	}
	
	@Override
	public void RegisterRenderGUI() {
		//FMLCommonHandler.instance().bus().register(new RenderAirplaneDebug());
		MinecraftForge.EVENT_BUS.register(new RenderAirplaneInterface());
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	@Override
	public void RegisterKeyBindings() {
		FMLCommonHandler.instance().bus().register(Mcflight.keyhandler);
		Mcflight.keyhandler.init();
	}
}
