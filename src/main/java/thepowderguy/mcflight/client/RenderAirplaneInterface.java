package thepowderguy.mcflight.client;

import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import thepowderguy.mcflight.common.entity.EntityAirplane;
import thepowderguy.mcflight.common.entity.EntityBiplane;
import thepowderguy.mcflight.math.Mat3;
import thepowderguy.mcflight.math.Vec3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

public class RenderAirplaneInterface extends Gui {

	public static RenderAirplaneInterface instance;
	FontRenderer font;
	int y = 2;
	static int dy = 9;
	static int colorzero = 0xffff77;
	static int colorpos = 0x77ff77;
	static int colorneg = 0xff7777;
	
	static int green = 0xff00ff00;
	static int red = 0xffff0000;
	static DecimalFormat numformat = new DecimalFormat("#.##");
	
	public boolean isDebugEnabled = false;


	private boolean isPlayerRidingAirplane() {
		return Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().player.getRidingEntity() instanceof EntityAirplane;
	}
	
	public RenderAirplaneInterface()
	{
    	Minecraft mc = Minecraft.getMinecraft();
    	font = mc.fontRendererObj;
    	instance = this;
	}


	
	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent event)
	{
		if (!isPlayerRidingAirplane()) {
					return;
		}
		
		EntityAirplane entity = (EntityAirplane)Minecraft.getMinecraft().player.getRidingEntity();
		
		if (event.getType() == ElementType.CROSSHAIRS)
			event.setCanceled(true);
		
		if (event.getType() != ElementType.CROSSHAIRS)
			return;
		
		int cX = event.getResolution().getScaledWidth()/2;
		int cY = event.getResolution().getScaledHeight()/2-20;

		float[] arr = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.color(0f, 1f, 0f, 1.0f);
		//GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
		GL11.glLineWidth(3.0f);
		GL11.glBegin(GL11.GL_LINES);
		//GL11.glColor3f(1.0f, 0.0f, 1.0f);
		int len = 0;
		Vec3 rot = entity.getInterpolatedRotation(event.getPartialTicks());
		float pRoll = (float) rot.z;
		float pPitch = (float) rot.y;
		int start = 5*(int)Math.ceil((pitch-10.0)/5.0);
		for (int i = start; i < 5*Math.floor(pitch+10)/5.0+2.5; i+= 5) {
			float x1 = -15;
			float x2 = 15;
			float y1 = (float) ((i-pPitch)*1.5);
			float y2 = (float) ((i-pPitch)*1.5);
			float x1p = (float) (x1*Math.cos(Math.toRadians(-pRoll)) - y1*Math.sin(Math.toRadians(-pRoll)));
			float y1p = (float) (x1*Math.sin(Math.toRadians(-pRoll)) + y1*Math.cos(Math.toRadians(-pRoll)));
			float x2p = (float) (x2*Math.cos(Math.toRadians(-pRoll)) - y2*Math.sin(Math.toRadians(-pRoll)));
			float y2p = (float) (x2*Math.sin(Math.toRadians(-pRoll)) + y2*Math.cos(Math.toRadians(-pRoll)));
			GL11.glVertex2f(cX+x1p*3, cY+y1p*3);
			GL11.glVertex2f(cX+x2p*3, cY+y2p*3);
			arr[len*4] = x1p;
			arr[len*4+1] = y1p;
			arr[len*4+2] = x2p;
			arr[len*4+3] = y2p;
			len++;
		}
		GL11.glEnd();

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		for (int i = 0; i < len; i++) {
			float avgx = (float) ((arr[i*4]+arr[i*4+2])/2.0);
			float avgy = (float) ((arr[i*4+1]+arr[i*4+3])/2.0);
			float total = (float)Math.sqrt((cX-avgx)*(cX-avgx)+(cY-avgy)*(cY-avgy));
			font.drawString(toStr2d(-start-5*i),
					cX+(int) (arr[i*4]*3 + 200*(arr[i*4]-avgx)/total - 5),
					cY+(int) (arr[i*4+1]*3 + 200*(arr[i*4+1]-avgy)/total - 6),
					green);
			font.drawString(toStr2d(-start-5*i),
					cX+(int) (arr[i*4+2]*3 + 200*(arr[i*4+2]-avgx)/total - 5),
					cY+(int) (arr[i*4+3]*3 + 200*(arr[i*4+3]-avgy)/total - 6),
					green);
		}

		//GL11.glEnable(GL11.GL_LIGHTING);
		//GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		//GL11.gl
		
		//GLStateManager.
		/*GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
		GL11.glVertex2f(200f,100f);
		GL11.glVertex2f(100f,100f);
		GL11.glVertex2f(200f,200f);
		GL11.glVertex2f(100f,200f);
		GL11.glEnd();*/
		/*
*/
		
		
		font.drawString("^", cX-2, cY-1, green);
		font.drawString("+", (int)EntityAirplane.mouseX/2+cX - 2, cY-(int)EntityAirplane.mouseY/2 - 3, 0xffffffff);
		//Alt
		this.drawVerticalLine(cX+100, cY-80, cY+80, green);
		this.drawHorizontalLine(cX+100, cX+130, cY-80, green);
		this.drawHorizontalLine(cX+100, cX+130, cY+80, green);
		for (int i = 20*(int)Math.ceil((alt-50.0)/20.0-0.1); i < alt+50; i+= 20)
			font.drawString(toStr2d(i), cX+108, (int) (cY + (alt - i) * (32.0/20.0)) - 3, green);
		for (int i = 10*(int)Math.ceil((alt-50.0)/10.0); i < alt+50; i+= 10)
			this.drawHorizontalLine(cX+100, cX+105, (int) (cY + (alt - i) * (32.0/20.0)), green);
		
		
		//72 * speed
		
		//Speed
		double KPH = (72.0*speed);
		this.drawVerticalLine(cX-100, cY-80, cY+80, green);
		this.drawHorizontalLine(cX-100, cX-130, cY-80, green);
		this.drawHorizontalLine(cX-100, cX-130, cY+80, green);
		for (int i = 10*(int)Math.ceil((KPH-25.0)/10.0-0.1); i < KPH+25; i+= 10)
			font.drawString(toStr2d(i), cX-125, (int) (cY + (KPH - i) * (32.0/10.0)) - 3, green);
		for (int i = 5*(int)Math.ceil((KPH-25.0)/5.0); i < KPH+25; i+= 5)
			this.drawHorizontalLine(cX-100, cX-105, (int) (cY + (KPH - i) * (32.0/10.0)), green);


		this.drawHorizontalLine(cX-100, cX+100, cY+110, green);
		this.drawVerticalLine(cX-100, cY+110, cY+140, green);
		this.drawVerticalLine(cX+100, cY+110, cY+140, green);
		for (int i = 10*(int)Math.ceil((yaw-45)/10.0); i < yaw+45+5; i+= 10)
			font.drawString(toStr2d((36-((int)(i<0? i+360 : i)/10)%36)), (int) (cX + (yaw-i)*2.0) - 6, cY+122, green);
		for (int i = 10*(int)Math.ceil((yaw-45)/10.0); i < yaw+45+5; i+= 5)
			this.drawVerticalLine((int) (cX + (yaw-i)*2.0), cY+110, cY+120, green);
		
		font.drawString("Fuel: " + numformat.format(100.0*C_FUEL/EntityBiplane.fuelCapacity) + "%", cX-100, cY-92, green);
		if (entity.stall)
			font.drawString("STALL!", cX-130, cY-92, red);
		font.drawString("Acceleraton: " + numformat.format(acc/EntityAirplane.gravity_const) + "g", cX-20, cY-92, green);
		
		if (isDebugEnabled)
			drawDebugScr();
	}
	
	
	public double speed = 0;
	public double alt = 0;
	public double pitch = 0;
	public double yaw = 0;
	public double roll = 0;
	public double acc = 0;
	
	public void setVars(double s, double ac, double a, double p, double y, double r) {
		speed = s;
		alt = a;
		pitch = p;
		yaw = y;
		roll = r;
		acc = ac;
	}
	
	
	
 //   public double tmp1 = 0.0;
 //   public double tmp2 = 0.0;
///    public double tmpmx = 0.0;
//    public double tmpmy = 0.0;
 //   public double tmpmz = 0.0;
    public double C_VEL = 0.0;
    public double C_AOA = 0.0;
    public double C_ROT_PITCH = 0.0;
    public double C_ROT_YAW = 0.0;
    public double C_ROT_ROLL = 0.0;
    public double C_LIFT = 0.0;
    public double C_DRAG = 0.0;
    public double C_ANGVEL = 0.0;
    public double C_INDDRAG = 0.0;
    public double C_THRUST = 0.0;
    public double C_AIR = 0.0;
    public boolean B_OG = false;
    public double C_FUEL = 0.0;
    public double C_WEIGHT = 0.0;
    public double C_X = 0.0;
    public double C_Y = 0.0;
    public double C_Z = 0.0;
    String asd = "";
   // public double tmpmrr = 0.0;
    
    public void setDebugVars(double vel, double aoa, double lift, double drag, double inddrag, double thrust, double air, double angVel, EntityAirplane airplane)
    {
    	C_VEL = vel;
    	C_AOA = aoa;
    	C_LIFT = lift;
    	C_DRAG = drag;
    	C_INDDRAG = inddrag;
    	C_THRUST = thrust;
//    	tmpmx = airplane.motionX;
//    	tmpmy = airplane.motionY;
//    	tmpmz = airplane.motionZ;
    	C_ROT_PITCH = airplane.rotationPitch;
    	C_ROT_YAW = airplane.rotationYaw;
    	C_ROT_ROLL = airplane.rotationRoll;
    	C_X = airplane.posX;
    	C_Y = airplane.posY;
    	C_Z = airplane.posZ;
    	B_OG = airplane.isOnGround;
    	C_FUEL = airplane.fuel;
    	C_AIR = air;
//    	tmpmrr = airplane.rotationRoll - airplane.prevRotationRoll;
    	C_ANGVEL = angVel;
    	C_WEIGHT = airplane.weight;
    	asd = airplane.text;
    }
    
	public void drawDebugScr()
	{
    	//mc.entityRenderer.setupOverlayRendering();
    	drawStr("Velocity: " 				, C_VEL);
    	drawStr("Acceleration: " 				, acc);
    	drawStr("Thrust: " 					, C_THRUST);
    	drawStr("Angle of Attack: " 		, C_AOA);
    	drawStr("Lift: " 					, C_LIFT);
    	drawStr("Induced Drag: " 					, C_INDDRAG);
    	drawStr("Drag: " 					, C_DRAG);
    	drawStr("Pitch: " 				, C_ROT_PITCH);
    	drawStr("Yaw: " 				, C_ROT_YAW);
    	drawStr("Roll: " 				, C_ROT_ROLL);
    	drawStr("X: " 						, C_X);
    	drawStr("Y: " 						, C_Y);
    	drawStr("Z: " 						, C_Z);
    	drawStr("Air density: " 		, C_AIR);
    	drawStr("Angular Velocity: " 		, C_ANGVEL);
    	drawStr("Fuel: " 					, C_FUEL);
    	drawStr("Weight: " 					, C_WEIGHT);
    	drawStr("On ground: " 				, B_OG);
    	drawStr(asd, 0);
    	//drawStr("Prev: " 					, tmp1);
    	//drawStr("Correct: " 				, tmp2);
    	y = 2;
	}
	
	private void drawStr(String str, double val)
	{
		font.drawStringWithShadow(str+String.format("%.4f", val), 2, y, (val == 0.0? colorzero: (val > 0.0? colorpos: colorneg)));
		y += dy;
	}

//	private void drawVec(String str, double val)
//	{
//		font.drawStringWithShadow(str+String.format("%.4f", val), 2, y, (val == 0.0? colorzero: (val > 0.0? colorpos: colorneg)));
//		y += dy;
//	}
	private void drawStr(String str, boolean val)
	{
		font.drawStringWithShadow(str+val, 2, y, (val? colorpos: colorneg));
		y += dy;
	}
	
//	private String numToStr3d(int n) {
//		
//	}
	private String toStr2d(int n) {
		if (n < 10 && n >= 0)
			return "0" + String.valueOf(n);
		return String.valueOf(n);
	}
}
