package thepowderguy.mcflight.client;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.ViewFrustum;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import thepowderguy.mcflight.client.util.CustomEntityRenderer;
import thepowderguy.mcflight.client.util.CustomRenderPlayer;
import thepowderguy.mcflight.common.MCFlightCommonProxy;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplaneCamera;
import thepowderguy.mcflight.common.entity.biplane.EntityBiplane;
import thepowderguy.mcflight.common.entity.biplane.RenderBiplane;
import thepowderguy.mcflight.common.item.AircraftPaint;

public class MCFlightClientProxy extends MCFlightCommonProxy {

	public static InterfaceKeyHandler keyhandler = new InterfaceKeyHandler();
	
	public void InjectStuff() {
		Minecraft.getMinecraft().entityRenderer = new CustomEntityRenderer(Minecraft.getMinecraft(), Minecraft.getMinecraft().getResourceManager());
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		try {
			Field renderPlayerField = RenderManager.class.getDeclaredField("playerRenderer");
			renderPlayerField.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
		    modifiersField.setInt(renderPlayerField, renderPlayerField.getModifiers() & ~Modifier.FINAL);
		    RenderPlayer prender = new CustomRenderPlayer(rendermanager);
		    renderPlayerField.set(rendermanager, prender);

			Field playerListField = RenderManager.class.getDeclaredField("skinMap");
			playerListField.setAccessible(true);
			Field modifiersField2 = Field.class.getDeclaredField("modifiers");
			modifiersField2.setAccessible(true);
		    modifiersField2.setInt(playerListField, playerListField.getModifiers() & ~Modifier.FINAL);
		    Map<String, RenderPlayer> map = (Map<String, RenderPlayer>) playerListField.get(rendermanager);
		    
		    map.put("default", prender);
		    map.put("slim", new CustomRenderPlayer(rendermanager, true));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
	
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
		RenderingRegistry.registerEntityRenderingHandler(EntityBiplane.class, new IRenderFactory() {
			@Override
			public Render createRenderFor(RenderManager manager) {
				return new RenderBiplane(manager);
			}
		});
		RenderingRegistry.registerEntityRenderingHandler(EntityAirplaneCamera.class, new IRenderFactory() {
			@Override
			public Render createRenderFor(RenderManager manager) {
				return new RenderAirplaneRider(manager);
			}
		});
	}
	
	@Override
	public void RegisterRenderGUI() {
		//FMLCommonHandler.instance().bus().register(new RenderAirplaneDebug());
		MinecraftForge.EVENT_BUS.register(new RenderAirplaneInterface());
		MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
	}
	
	@Override
	public void RegisterKeyBindings() {
		FMLCommonHandler.instance().bus().register(keyhandler);
		keyhandler.init();
	}

	public static void reloadChunks() {
		try {
			RenderGlobal r = Minecraft.getMinecraft().renderGlobal;
			Minecraft mc = Minecraft.getMinecraft();

			Field f = RenderGlobal.class.getDeclaredField("viewFrustum");
			f.setAccessible(true);
			Field fac;
			fac = RenderGlobal.class.getDeclaredField("renderChunkFactory");
			fac.setAccessible(true);
			IRenderChunkFactory factory = (IRenderChunkFactory) fac.get(r);
			ViewFrustum frustum = new ViewFrustum(mc.world, mc.gameSettings.renderDistanceChunks, r, factory);
			f.set(r, frustum);
			if (mc.world != null)
			{
				Entity entity = mc.getRenderViewEntity();

				if (entity != null)
				{
					frustum.updateChunkPositions(entity.posX, entity.posZ);
				}
			}

		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
