package thepowderguy.mcflight.client;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import thepowderguy.mcflight.common.entity.RenderBiplane;

public class InterfaceKeyHandler {
	public static KeyBinding toggleDebug;
	public static KeyBinding toggleVector;
	public static void init() {
		toggleDebug = new KeyBinding("key.airplanedebug", Keyboard.KEY_F9, "key.categories.mcflight");
		ClientRegistry.registerKeyBinding(toggleDebug);
		toggleVector = new KeyBinding("key.airplaneshowvectors", Keyboard.KEY_F10, "key.categories.mcflight");
		ClientRegistry.registerKeyBinding(toggleDebug);
	}
	public InterfaceKeyHandler() {
		
	}
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if (toggleDebug.isPressed()) {
			RenderAirplaneInterface.instance.isDebugEnabled = !RenderAirplaneInterface.instance.isDebugEnabled;
		}
		if (toggleVector.isPressed()) {
			RenderBiplane.isVectorDrawing = !RenderBiplane.isVectorDrawing;
		}
	}
}
