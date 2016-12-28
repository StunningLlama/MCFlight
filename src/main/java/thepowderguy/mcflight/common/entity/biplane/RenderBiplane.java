package thepowderguy.mcflight.common.entity.biplane;

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
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.RenderAirplane;
import thepowderguy.mcflight.physics.ControlSurface;
import thepowderguy.mcflight.util.Mat3;
import thepowderguy.mcflight.util.Vec3;


@SideOnly(Side.CLIENT)
public class RenderBiplane extends RenderAirplane<EntityBiplane>
{
    private ResourceLocation TexturePath = new ResourceLocation("mcflight:textures/entity/biplane.png");  //refers to:assets/yourmod/textures/entity/yourtexture.png

    ModelBase model = new ModelBiplane();
	public static float scale = 1.25f;
    
    public RenderBiplane()
    {
        super(Minecraft.getMinecraft().getRenderManager());
        this.shadowSize = 2.0f;
    }
    long start = System.currentTimeMillis();
    
    @Override
    protected ResourceLocation getEntityTexture(EntityBiplane par1Entity)
    {
        return TexturePath;
    }
    
    //private static float intMul = 1.0f;
    @Override
    public void doRender(EntityBiplane entity, double x, double y, double z, float par1, float partialTicks) //WTF is partialticks
    {
    	super.doRender(entity, x, y, z, par1, partialTicks);
    	
    	GlStateManager.pushMatrix();

    	//translate
    	GlStateManager.translate((float) x, (float) y, (float) z);
    	Vec3 angles;
    	//if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
    		angles = entity.getInterpolatedRotation(partialTicks);
    	/*}
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
    	}*/

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
    	((ModelBiplane)model).render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, partialTicks, Mcflight.keyhandler.vectordrawing_toggled);

    	GlStateManager.popMatrix();
    	//System.out.println(partialTicks);
    }
}