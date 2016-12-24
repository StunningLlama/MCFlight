package thepowderguy.mcflight.common;

import thepowderguy.mcflight.client.CustomEntityRenderer;
import thepowderguy.mcflight.client.CustomRenderPlayer;
import thepowderguy.mcflight.client.InterfaceKeyHandler;
import thepowderguy.mcflight.client.gui.McflightGUIHandler;
import thepowderguy.mcflight.common.entity.*;
import thepowderguy.mcflight.common.item.*;
import thepowderguy.mcflight.common.packet.AirplanePacketListener;
import thepowderguy.mcflight.common.packet.AirplaneStateListener;
import thepowderguy.mcflight.common.packet.AirplaneStatePacket;
import thepowderguy.mcflight.common.packet.AirplaneUpdatePacket;
import thepowderguy.mcflight.common.world.McflightWorldGenerator;
import thepowderguy.mcflight.common.world.Oil;
import thepowderguy.mcflight.common.world.BlockOil;
import thepowderguy.mcflight.common.world.WorldGenOil;
import unused.OilFlowing;
import unused.OilStill;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityTracker;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "mcflight", name = "Minecraft Flight Simulator", version = "0.1")
public class Mcflight {
	
	public static String MODID = "mcflight";
	public static String VERSION = "0.1";
	public static SimpleNetworkWrapper network = null;
	public static SimpleNetworkWrapper network2 = null;
	
	public static InterfaceKeyHandler keyhandler = new InterfaceKeyHandler();

	//parts
	public static Item item_flap;
	public static Item item_wing;
	public static Item item_doublewing;
	public static Item item_propeller;
	public static Item item_jetengine;
	public static Item item_bigfuselage;
	public static Item item_smallfuselage;
	public static Item item_tail;
	public static Item item_kerosene;
	
	//aircraft items
	public static Item item_airplane_biplane;
	public static Item item_airplane_cessna_skyhawk;
	public static Item item_airplane_boeing_737;
	public static Item item_airplane_douglas_dc3;
	
	public static Item item_paint;
	
	public static SoundEvent sound_engine;
	public static Oil fluid_oil;
	public static Block block_oil;
	
	public static McflightGUIHandler guihandler = new McflightGUIHandler();
	
	public static Mcflight instance;
	
	//blocks
	
	public static CreativeTabs tab_aircraft = new CreativeTabs("Aircraft")
	{
		public ItemStack getTabIconItem()
		{
				return item_propeller.getDefaultInstance();
		}
	};
	
	{
		FluidRegistry.enableUniversalBucket();
	}
	
	@SidedProxy(clientSide="thepowderguy.mcflight.client.MCFlightClientProxy", serverSide="thepowderguy.mcflight.common.MCFlightCommonProxy")
	public static MCFlightCommonProxy proxy;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		instance = this;
		network = NetworkRegistry.INSTANCE.newSimpleChannel("McflightPos");
		network.registerMessage(AirplanePacketListener.class, AirplaneUpdatePacket.class, 0, Side.SERVER);
		network2 = NetworkRegistry.INSTANCE.newSimpleChannel("McflightState");
		network2.registerMessage(AirplaneStateListener.class, AirplaneStatePacket.class, 0, Side.SERVER);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, guihandler);

		item_flap = new Flap().setUnlocalizedName("flap").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_wing = new Wing().setUnlocalizedName("wing").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_doublewing = new DoubleWing().setUnlocalizedName("doublewing").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_propeller = new Propeller().setUnlocalizedName("propeller").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_jetengine = new TurboFan().setUnlocalizedName("jetengine").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_bigfuselage = new BigFuselage().setUnlocalizedName("bigfuselage").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_smallfuselage = new SmallFuselage().setUnlocalizedName("smallfuselage").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_tail = new Tail().setUnlocalizedName("tail").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		item_kerosene = new Kerosene().setUnlocalizedName("kerosene").setCreativeTab(tab_aircraft).setMaxStackSize(4);

		item_airplane_biplane = new ItemBiplane().setUnlocalizedName("biplane").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		
		item_paint = new AircraftPaint().setUnlocalizedName("aircraftpaint").setCreativeTab(tab_aircraft).setMaxStackSize(1);
		/******* uncomment to enable oil test **********/

		//Blocks.WATER
       // GameRegistry.register((new OilFlowing(Material.WATER)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water"), new ResourceLocation("mcflight:oil"));
       // registerBlock(9, "water", (new BlockStaticLiquid(Material.WATER)).setHardness(100.0F).setLightOpacity(3).setUnlocalizedName("water").disableStats());

		
		//item_airplane_cessna_skyhawk = new ItemCessnaSkyhawk().setUnlocalizedName("cessna_skyhawk");
		//item_airplane_boeing_737 = new ItemBoeing737().setUnlocalizedName("boeing_737");
		//item_airplane_douglas_dc3 = new ItemDouglasDC3().setUnlocalizedName("douglas_dc3");
		
		GameRegistry.register(item_flap.setRegistryName("flap"));
		GameRegistry.register(item_wing.setRegistryName("wing")			);
		GameRegistry.register(item_doublewing.setRegistryName("doublewing")		);
		GameRegistry.register(item_propeller.setRegistryName("propeller")		);
		GameRegistry.register(item_jetengine.setRegistryName("jetengine")		);
		GameRegistry.register(item_bigfuselage.setRegistryName("bigfuselage")		);
		GameRegistry.register(item_smallfuselage.setRegistryName("smallfuselage")	);
		GameRegistry.register(item_tail.setRegistryName("tail")			);
		GameRegistry.register(item_kerosene.setRegistryName("kerosene")			);
		
		GameRegistry.register(item_airplane_biplane.setRegistryName("biplane"));
		GameRegistry.register(item_paint.setRegistryName("aircraftpaint"));
		//GameRegistry.register(block_oil.setRegistryName("oil"));
        
		fluid_oil = new Oil("oil", new ResourceLocation("mcflight:blocks/oilstill"), new ResourceLocation("mcflight:blocks/oilflowing"));
		FluidRegistry.registerFluid(fluid_oil);
		FluidRegistry.addBucketForFluid(fluid_oil);
		block_oil = new BlockOil(fluid_oil, Material.WATER).setCreativeTab(tab_aircraft).setUnlocalizedName("oil");
		GameRegistry.register(block_oil, new ResourceLocation("mcflight:oil_block"));
		proxy.RegisterFluidModel((IFluidBlock)block_oil);
		

		ResourceLocation soundloc = new ResourceLocation("mcflight", "airplane.biplane.engine");
		sound_engine = new SoundEvent(soundloc);
		GameRegistry.register(sound_engine, soundloc);
	}

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
	    // register server commands

		event.registerServerCommand(new CommandTestAirplane());
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.addRecipe(i(item_flap), "   ", "pii", "   ",
				'p', i(Blocks.PISTON), 'i', i(Items.IRON_INGOT));
		GameRegistry.addRecipe(i(item_wing), " f ", "bbb", "   ",
				'f', i(item_flap), 'b', i(Blocks.IRON_BLOCK));
		GameRegistry.addShapelessRecipe(i(item_doublewing), 
				i(item_wing), i(item_wing));
		GameRegistry.addRecipe(i(item_propeller), "iii", "ipi", "iii",
				'p', i(Blocks.PISTON), 'i', i(Items.IRON_INGOT));
		GameRegistry.addRecipe(i(item_jetengine), "ipi", "ifi", "i i",
				'p', i(item_propeller), 'i', i(Items.IRON_INGOT), 'f', i(Blocks.FURNACE));
		GameRegistry.addRecipe(i(item_bigfuselage), "bgb", "b b", "bbb",
				'g', i(Blocks.GLASS), 'b', i(Blocks.IRON_BLOCK));
		GameRegistry.addRecipe(i(item_smallfuselage), "   ", "bgb", "bbb",
				'g', i(Blocks.GLASS), 'b', i(Blocks.IRON_BLOCK));
		GameRegistry.addRecipe(i(item_tail), " b ", "fbf", " f ",
				'f', i(item_flap), 'b', i(Blocks.IRON_BLOCK));
		
		GameRegistry.addRecipe(i(item_airplane_biplane), " p ", "dfd", " t ",
				'p', i(item_propeller), 'd', i(item_doublewing), 'f', i(item_smallfuselage), 't', i(item_tail));
		GameRegistry.addSmelting((UniversalBucket.getFilledBucket(ForgeModContainer.getInstance().universalBucket, (Fluid)fluid_oil)), i(item_kerosene), 0f);
		ForgeModContainer.getInstance().universalBucket.setCreativeTab(tab_aircraft); // this is kind of not good, prehaps forge will implement a better method to do this
		for (int i = 0; i < 16; ++i)
        {
           GameRegistry.addRecipe(new ItemStack(item_paint, 1, i), new Object[] {"###", "#X#", "###", 'X', new ItemStack(Items.BUCKET), '#', new ItemStack(Items.DYE, 8, i)});
        }//
        //ItemColors.
		proxy.RegisterRenderItems();
		EntityRegistry.registerModEntity(new ResourceLocation("mcflight:biplane"), EntityBiplane.class, "biplane", 0, this, 64, 20, true);
		proxy.RegisterRenderEntities();
		proxy.RegisterRenderGUI();
		MinecraftForge.EVENT_BUS.register(new McflightEventHandler());
		//GameRegistry.registerWorldGenerator(new McflightWorldGenerator(), 10);
		proxy.RegisterKeyBindings();
	}

	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) {
		injectStuff(event.getSide());
	}
	
	public static void injectStuff(Side side) {
		if (side != Side.CLIENT)
			return;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static ItemStack i(Block b)
	{
		return new ItemStack(b);
	}
	
	public static ItemStack i(Item i)
	{
		return new ItemStack(i);
	}
	
	private void registerFluidModel(IFluidBlock fluidBlock) {
		Item item = Item.getItemFromBlock((Block) fluidBlock);

		ModelBakery.registerItemVariants(item);

		final ModelResourceLocation modelResourceLocation = new ModelResourceLocation("", fluidBlock.getFluid().getName());

		ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition(){
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack) {
				return modelResourceLocation;
			}});

		ModelLoader.setCustomStateMapper((Block) fluidBlock, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState p_178132_1_) {
				return modelResourceLocation;
			}
		});
	}
}
