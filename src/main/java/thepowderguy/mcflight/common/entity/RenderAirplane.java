package thepowderguy.mcflight.common.entity;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.physics.CollisionPoint;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.util.Vec3;

public abstract class RenderAirplane<T extends EntityAirplane> extends Render<T>{

	protected RenderAirplane(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}

	final Vec3 zero = new Vec3();
    double vscale = 0.5;
    
	public void doRender(T entity, double x, double y, double z, float par1, float partialTicks) //WTF is partialticks
	{
		super.doRender(entity, x, y, z, par1, partialTicks);
		
    	if (Mcflight.keyhandler.vectordrawing_toggled && entity.vectorsInitialized)
    	{

    		GlStateManager.pushMatrix();
        	GlStateManager.translate((float) x, (float) y, (float) z);
    		//GlStateManager.translate(0, -RenderBiplane.scale * 6f/16f, 0);
    		GlStateManager.disableTexture2D();
    		GL11.glDisable(GL11.GL_LIGHTING);
    		//GlStateManager.depthMask(false);
    		Tessellator tessellator = Tessellator.getInstance();
    		VertexBuffer vertexbuffer = tessellator.getBuffer();
    		GL11.glLineWidth(4.0F);
    		vertexbuffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
    		renderVector(zero, entity.gravity_vec, vertexbuffer, 255, 255, 0, vscale);
    		renderVector(zero, entity.thrust_vec, vertexbuffer, 255, 0, 255, vscale);
    		renderVector(zero, entity.drag_vec, vertexbuffer, 255, 0, 0, vscale);
    		//renderVector(zero, entity.mo, vertexbuffer, 0, 255, 255, 10.0);
    		for (CollisionPoint i: entity.collisionPoints)
    			renderVector(i.getInterpolatedPosition(partialTicks), i.getForce(), vertexbuffer, 0, 0, 0, vscale);
    		for (ControlSurface i: entity.controlSurfaces) {
    			if (i.isStalled())
        			renderVector(i.getInterpolatedPosition(partialTicks), i.getForce(partialTicks), vertexbuffer, 255, 0, 0, vscale);
    			else
    				renderVector(i.getInterpolatedPosition(partialTicks), i.getForce(partialTicks), vertexbuffer, 0, 0, 255, vscale);
    		}
    		tessellator.draw();
    		GL11.glLineWidth(1.0F);
    		GL11.glPointSize(5.0F);
    		GL11.glBegin(GL11.GL_POINTS);
    		for (CollisionPoint i: entity.collisionPoints)
    			renderPoint(i.getInterpolatedPosition(partialTicks), 1.0f, 0.5f, 0);
    		GL11.glEnd();
    		//GlStateManager.depthMask(true);
    		GL11.glEnable(GL11.GL_LIGHTING);
    		GlStateManager.enableTexture2D();
    		GlStateManager.popMatrix();
    	}
	}
	

    private void renderPoint(Vec3 vec, float r, float g, float b) {
    	GlStateManager.color(r, g, b);
    	GL11.glVertex3f((float)vec.x, (float)vec.y, (float)vec.z);
    	GlStateManager.color(1, 1, 1);
    }
    private void renderVector(Vec3 start, Vec3 vec, VertexBuffer vertexbuffer, int r, int g, int b, double scale) {
        vertexbuffer.pos(start.x, start.y, start.z).color(r, g, b, 255).endVertex();
        vertexbuffer.pos((start.x+vec.x*scale), (start.y+vec.y*scale), (start.z+vec.z*scale)).color(r, g, b, 255).endVertex();
    }
}
