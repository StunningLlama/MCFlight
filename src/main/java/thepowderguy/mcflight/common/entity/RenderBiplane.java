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
import thepowderguy.mcflight.math.Mat3x3;
import thepowderguy.mcflight.math.Vector;


@SideOnly(Side.CLIENT)
public class RenderBiplane extends Render
{
    private ResourceLocation TexturePath = new ResourceLocation("mcflight:textures/entity/biplane.png");  //refers to:assets/yourmod/textures/entity/yourtexture.png

    ModelBase model = new ModelBiplane();
    
    static public boolean isVectorDrawing = false;
    
    public RenderBiplane()
    {
        super(Minecraft.getMinecraft().getRenderManager());
        this.shadowSize = 2.0f;
    }
    
    
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
    		GlStateManager.disableTexture2D();
    		GL11.glDisable(GL11.GL_LIGHTING);
    		//GlStateManager.depthMask(false);
    		Tessellator tessellator = Tessellator.getInstance();
    		VertexBuffer vertexbuffer = tessellator.getBuffer();
    		GL11.glLineWidth(4.0F);
    		vertexbuffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
    		renderVector(entity.gravity_vec, vertexbuffer, 255, 255, 0);
    		renderVector(entity.lift_vec, vertexbuffer, 0, 255, 0);
    		renderVector(entity.thrust_vec, vertexbuffer, 0, 0, 255);
    		renderVector(entity.drag_vec, vertexbuffer, 255, 0, 0);
    		renderVector(entity.inddrag_vec, vertexbuffer, 255, 0, 255);
    		tessellator.draw();
    		GL11.glLineWidth(1.0F);
    		//GlStateManager.depthMask(true);
    		GL11.glEnable(GL11.GL_LIGHTING);
    		GlStateManager.enableTexture2D();
    	}
    	else
    	{
    		GlStateManager.pushMatrix();

    		//translate
    		GlStateManager.translate((float) x, (float) y, (float) z);
    		Vector angles;
    		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
    			angles = entity.getInterpolatedRotation(partialTicks);
    		}
    		else 
    		{

    			Vector angVelocity = new Vector(entity.angVelX*partialTicks, entity.angVelY*partialTicks, entity.angVelZ*partialTicks);

    			double rotationVelocity = angVelocity.mag();
    			angles = new Vector(entity.rotationYaw, entity.rotationPitch, entity.rotationRoll);
    			if (rotationVelocity > 0.0) {
    				Vector axisRot = Vector.mul(angVelocity, 1.0/rotationVelocity);
    				Vector vfwd_new = Vector.AxisAngleRotation(axisRot, entity.vfwd, rotationVelocity);
    				Vector vup_new = Vector.AxisAngleRotation(axisRot, entity.vup, rotationVelocity);
    				Vector vwing_new = Vector.AxisAngleRotation(axisRot, entity.vwing, rotationVelocity);
    				angles = Mat3x3.getangles(vwing_new, vup_new, vfwd_new);
    			}
    		}


    		//rotate
    		GlStateManager.rotate((float)angles.x, 0.0F, 1.0F, 0.0F);
    		GlStateManager.rotate((float)angles.y, 1.0F, 0.0F, 0.0F);
    		GlStateManager.rotate((float)angles.z, 0.0F, 0.0F, 1.0F);

    		//scale
    		float f4 = 2.0F;
    		GlStateManager.scale(f4, f4, f4);
    		GlStateManager.scale(1.0F / f4, 1.0F / f4, 1.0F / f4);
    		this.bindEntityTexture(entity);
    		float scale = 1.25f;
    		GlStateManager.scale(-scale, -scale, scale);

    		//render
    		model.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);




    		GlStateManager.popMatrix();
    		//System.out.println(partialTicks);

    	}

    	super.doRender(entity, x, y, z, par1, partialTicks);
    }

    private void renderVector(Vector v, VertexBuffer vertexbuffer, int r, int g, int b) {
        vertexbuffer.pos(0.0D, 0.0D, 0.0D).color(r, g, b, 255).endVertex();
        vertexbuffer.pos(v.x*vscale, v.y*vscale, v.z*vscale).color(r, g, b, 255).endVertex();
    }
    
    double vscale = 100.0;
    
    public void doRender(Entity entity, double x, double y, double z, float par1, float partialTicks)
    {
        this.doRender((EntityBiplane) entity, x, y, z, par1, partialTicks);
    }
}