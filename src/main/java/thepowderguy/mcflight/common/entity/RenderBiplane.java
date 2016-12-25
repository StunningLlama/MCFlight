package thepowderguy.mcflight.common.entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;


@SideOnly(Side.CLIENT)
public class RenderBiplane extends Render
{
    private ResourceLocation TexturePath = new ResourceLocation("mcflight:textures/entity/biplane.png");  //refers to:assets/yourmod/textures/entity/yourtexture.png

    ModelBase model = new ModelBiplane();
	public static float scale = 1.25f;
	
	final Vec3 zero = new Vec3();
    
    static public boolean isVectorDrawing = false;
    
    public RenderBiplane()
    {
        super(Minecraft.getMinecraft().getRenderManager());
        this.shadowSize = 2.0f;
    }
    long start = System.currentTimeMillis();
    
    @Override
    protected ResourceLocation getEntityTexture(Entity par1Entity)
    {
        return TexturePath;
    }
    
    protected ResourceLocation getEntityTexture(EntityBiplane par1Entity)
    {
        return TexturePath;
    }
    
    //private static float intMul = 1.0f;
    public void doRender(EntityAirplane entity, double x, double y, double z, float par1, float partialTicks) //WTF is partialticks
    {
    	/*if (entity.getControllingPassenger() instanceof EntityPlayer)
    	{
    		//this.riddenByEntity.
    		entity.getControllingPassenger().rotationYaw = (-entity.rotationYaw);
    		entity.getControllingPassenger().rotationPitch = (entity.rotationPitch);
    	}*/

    	if (entity.getControllingPassenger() == Minecraft.getMinecraft().player && isVectorDrawing)
    	{

    		GlStateManager.pushMatrix();
    		//GlStateManager.translate(0, -RenderBiplane.scale * 6f/16f, 0);
    		GlStateManager.disableTexture2D();
    		GL11.glDisable(GL11.GL_LIGHTING);
    		//GlStateManager.depthMask(false);
    		Tessellator tessellator = Tessellator.getInstance();
    		VertexBuffer vertexbuffer = tessellator.getBuffer();
    		GL11.glLineWidth(4.0F);
    		vertexbuffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
    		renderVector(zero, entity.gravity_vec, vertexbuffer, 255, 255, 0, vscale);
    		renderVector(zero, entity.lift_vec, vertexbuffer, 0, 255, 0, vscale);
    		renderVector(zero, entity.thrust_vec, vertexbuffer, 0, 0, 255, vscale);
    		//renderVector(entity.drag_vec, vertexbuffer, 255, 0, 0, vscale);
    		//renderVector(entity.inddrag_vec, vertexbuffer, 255, 0, 255, vscale);
    		for (Vec3 i: entity.points)
    			renderVector(zero, i, vertexbuffer, 255, 255, 255, 1.0);
    		for (ControlSurface i: entity.controlSurfaces)
    			renderVector(i.getPosition(), i.getForce(), vertexbuffer, 0, 255, 255, vscale);
    		tessellator.draw();
    		GL11.glLineWidth(1.0F);
    		//GlStateManager.depthMask(true);
    		GL11.glEnable(GL11.GL_LIGHTING);
    		GlStateManager.enableTexture2D();
    		GlStateManager.popMatrix();
    	}



    	GlStateManager.pushMatrix();

    	//translate
    	GlStateManager.translate((float) x, (float) y, (float) z);
    	Vec3 angles;
    	if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
    		angles = entity.getInterpolatedRotation(partialTicks);
    	}
    	else 
    	{

    		Vec3 angVelocity = new Vec3(entity.angVelX*partialTicks, entity.angVelY*partialTicks, entity.angVelZ*partialTicks);

    		double rotationVelocity = angVelocity.mag();
    		angles = new Vec3(entity.rotationYaw, entity.rotationPitch, entity.rotationRoll);
    		if (rotationVelocity > 0.0) {
    			Vec3 axisRot = Vec3.mul(angVelocity, 1.0/rotationVelocity);
    			Vec3 vfwd_new = Vec3.AxisAngleRotation(axisRot, entity.vfwd, rotationVelocity);
    			Vec3 vup_new = Vec3.AxisAngleRotation(axisRot, entity.vup, rotationVelocity);
    			Vec3 vwing_new = Vec3.AxisAngleRotation(axisRot, entity.vside, rotationVelocity);
    			angles = Mat3.getangles(vwing_new, vup_new, vfwd_new);
    		}
    	}

// ((System.currentTimeMillis()-start)/50f)
    	//rotate
    	GlStateManager.rotate((float)angles.x, 0.0F, 1.0F, 0.0F);
    	GlStateManager.rotate((float)angles.y, 1.0F, 0.0F, 0.0F);
    	GlStateManager.rotate((float)angles.z, 0.0F, 0.0F, 1.0F);

    	//scale
    	float f4 = 2.0F;
    	GlStateManager.scale(f4, f4, f4);
    	GlStateManager.scale(1.0F / f4, 1.0F / f4, 1.0F / f4);
    	this.bindEntityTexture(entity);
    	GlStateManager.scale(-scale, -scale, scale);
    	//render
    	((ModelBiplane)model).render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, partialTicks, entity.getControllingPassenger() == Minecraft.getMinecraft().player && isVectorDrawing);

    	GlStateManager.popMatrix();
    	//System.out.println(partialTicks);


    	super.doRender(entity, x, y, z, par1, partialTicks);
    }

    private void renderVector(Vec3 start, Vec3 vec, VertexBuffer vertexbuffer, int r, int g, int b, double scale) {
        vertexbuffer.pos(start.x, start.y, start.z).color(r, g, b, 255).endVertex();
        vertexbuffer.pos((start.x+vec.x*scale), (start.y+vec.y*scale), (start.z+vec.z*scale)).color(r, g, b, 255).endVertex();
    }
    
    double vscale = 100.0;
    
    public void doRender(Entity entity, double x, double y, double z, float par1, float partialTicks)
    {
        this.doRender((EntityBiplane) entity, x, y, z, par1, partialTicks);
    }
}