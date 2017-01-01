package thepowderguy.mcflight.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.packet.AirplaneStatePacket;

public class GuiPaintSelect extends GuiScreen {
	private GuiButton FuselageButton;
	private GuiButton WingButton;
	protected FontRenderer font = Minecraft.getMinecraft().fontRendererObj;
	
	public int LastEntityID = 0;
	
	public GuiPaintSelect(int id) {
		super();
		LastEntityID = id;
	}
	
	@Override
	public void initGui() {
		this.FuselageButton = new GuiButton(0, this.width/2 - 100, this.height/2 + 00, 200, 20, "Fuselage");
		this.WingButton = new GuiButton(1, this.width/2 - 100, this.height/2 + 30, 200, 20, "Wings");
		this.buttonList.add(FuselageButton);
		this.buttonList.add(WingButton);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		font.drawStringWithShadow("Select the part of the aircraft to paint", this.width/2 - 85, this.height/2 - 15, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		AirplaneStatePacket pack = new AirplaneStatePacket(button.id + 2, LastEntityID);
		Mcflight.network2.sendToServer(pack);

		this.mc.displayGuiScreen(null);
	}
}
