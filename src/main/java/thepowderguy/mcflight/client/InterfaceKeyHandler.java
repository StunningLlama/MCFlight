package thepowderguy.mcflight.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import thepowderguy.mcflight.common.entity.EntityAirplaneCamera;
import thepowderguy.mcflight.util.Vec3;

public class InterfaceKeyHandler {
	public boolean debug_toggled = false;
	public boolean vectordrawing_toggled = false;
	public boolean hud_toggled = true;
	public int camera_mode = 0;
	
	public KeyBinding toggleDebug;
	public KeyBinding toggleVector;
	public KeyBinding toggleHud;
	public KeyBinding changeCamera;
	public KeyBinding zoom_in;
	public KeyBinding zoom_out;
	public KeyBinding alieron_cw	;
	public KeyBinding alieron_ccw  ;
	public KeyBinding elevator_up  ;
	public KeyBinding elevator_down;
	public KeyBinding rudder_left  ;
	public KeyBinding rudder_right ;
	public KeyBinding throttle_up  ;
	public KeyBinding throttle_down;
	public KeyBinding brake;
	public KeyBinding look_around  ;


	public void init() {
		toggleDebug   = new KeyBinding("key.airplanedebug", Keyboard.KEY_F9, "key.categories.mcflight");
		toggleVector  = new KeyBinding("key.airplaneshowvectors", Keyboard.KEY_F10, "key.categories.mcflight");
		toggleHud  	= new KeyBinding("key.airplaneshowhud", Keyboard.KEY_F8, "key.categories.mcflight");
		zoom_in  = new KeyBinding("key.zoomin", Keyboard.KEY_EQUALS, "key.categories.mcflight");
		zoom_out  	= new KeyBinding("key.zoomout", Keyboard.KEY_MINUS, "key.categories.mcflight");
		alieron_cw	  = new KeyBinding("key.alieron_cw", Keyboard.KEY_LEFT, "key.categories.mcflight");
		alieron_ccw   = new KeyBinding("key.alieron_ccw", Keyboard.KEY_RIGHT, "key.categories.mcflight");
		elevator_up   = new KeyBinding("key.elevator_up", Keyboard.KEY_UP, "key.categories.mcflight");
		elevator_down = new KeyBinding("key.elevator_down", Keyboard.KEY_DOWN, "key.categories.mcflight");
		rudder_left   = new KeyBinding("key.rudder_left", Keyboard.KEY_J, "key.categories.mcflight");
		rudder_right  = new KeyBinding("key.rudder_right", Keyboard.KEY_L, "key.categories.mcflight");
		throttle_up   = new KeyBinding("key.throttle_up", Keyboard.KEY_I, "key.categories.mcflight");
		throttle_down = new KeyBinding("key.throttle_down", Keyboard.KEY_K, "key.categories.mcflight");
		brake         = new KeyBinding("key.brake", Keyboard.KEY_Z, "key.categories.mcflight");
		look_around   = new KeyBinding("key.look_around", Keyboard.KEY_SPACE, "key.categories.mcflight");
		changeCamera   = new KeyBinding("key.change_camera", Keyboard.KEY_F6, "key.categories.mcflight");
		ClientRegistry.registerKeyBinding(toggleVector);
		ClientRegistry.registerKeyBinding(toggleDebug);
		ClientRegistry.registerKeyBinding(toggleHud);
		ClientRegistry.registerKeyBinding(zoom_in   );
		ClientRegistry.registerKeyBinding(zoom_out	);   
		ClientRegistry.registerKeyBinding(alieron_ccw   );
		ClientRegistry.registerKeyBinding(alieron_cw	);   
		ClientRegistry.registerKeyBinding(elevator_up   );
		ClientRegistry.registerKeyBinding(elevator_down );
		ClientRegistry.registerKeyBinding(rudder_left   );
		ClientRegistry.registerKeyBinding(rudder_right  );
		ClientRegistry.registerKeyBinding(throttle_up   );
		ClientRegistry.registerKeyBinding(throttle_down );
		ClientRegistry.registerKeyBinding(brake         );
		ClientRegistry.registerKeyBinding(look_around   );
		ClientRegistry.registerKeyBinding(changeCamera   );

	}
	public InterfaceKeyHandler() {

	}
	public double isInWater(Vec3 pos) {
		BlockPos bp = new BlockPos(pos.x, pos.y, pos.z);
		IBlockState bs = Minecraft.getMinecraft().world.getBlockState(bp);
		Material m = bs.getMaterial();
		if (m != Material.WATER && m != Material.LAVA)
			return -1;
		

		BlockPos bp1 = new BlockPos(pos.x, pos.y+1, pos.z);
		IBlockState bs1 = Minecraft.getMinecraft().world.getBlockState(bp1);
		Material m1 = bs1.getMaterial();
		float lvl;
		if (m1 == Material.WATER || m1 == Material.LAVA)
		{
			BlockPos bp2 = new BlockPos(pos.x, pos.y+2, pos.z);
			IBlockState bs2 = Minecraft.getMinecraft().world.getBlockState(bp2);
			Material m2 = bs2.getMaterial();
			if (m2 == Material.WATER || m2 == Material.LAVA)
			{
				lvl = 2.0f;
			} else {
			lvl = 2.0f - BlockLiquid.getLiquidHeightPercent(bs1.getValue(BlockLiquid.LEVEL));
			}
		} else {
			lvl = 1.0f - BlockLiquid.getLiquidHeightPercent(bs.getValue(BlockLiquid.LEVEL));
		}
		if ((pos.y - Math.floor(pos.y)) >= lvl)
			return -1;
		double finalvalue = lvl - (pos.y - Math.floor(pos.y));
		if (finalvalue > 1.0)
			return 1.0;
		return finalvalue;
	}
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (toggleDebug.isPressed()) {
			debug_toggled = !debug_toggled;

			EntityPlayer p = Minecraft.getMinecraft().player;
			double inwater = isInWater(new Vec3(p.posX, p.posY, p.posZ));
			p.sendMessage(new TextComponentString("water " + inwater));
			/*p.sendMessage(new TextComponentString("h1"));
			BlockPos bp = new BlockPos(p.posX, p.posY, p.posZ);
			IBlockState bs = p.world.getBlockState(bp);
			Material m = bs.getMaterial();
			p.sendMessage(new TextComponentString(m.toString()));
			if (m == Material.WATER || m == Material.LAVA)
			{
				p.sendMessage(new TextComponentString("h2"));
				float lvl = BlockLiquid.getLiquidHeightPercent(bs.getValue(BlockLiquid.LEVEL));
				p.sendMessage(new TextComponentString(String.valueOf(lvl)));
				if ((p.posY - Math.floor(p.posY)) < lvl)
				{
					p.sendMessage(new TextComponentString("asd"));
				}
			}*/
		}
		if (toggleVector.isPressed()) {
			vectordrawing_toggled = !vectordrawing_toggled;
		}
		if (toggleHud.isPressed()) {
			hud_toggled = !hud_toggled;
			
		}
		if (changeCamera.isPressed()) {
			camera_mode = (camera_mode+1)%7;
			Minecraft.getMinecraft().ingameGUI.setOverlayMessage(I18n.format(EntityAirplaneCamera.views[camera_mode].name), false);
		}
	}
}
