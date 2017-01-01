package thepowderguy.mcflight.common.entity.biplane;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.RenderAirplane;
import thepowderguy.mcflight.util.Vec3;


@SideOnly(Side.CLIENT)
public class RenderBiplane extends RenderAirplane<EntityBiplane>
{
    private ResourceLocation TexturePath = new ResourceLocation("mcflight:textures/entity/biplane.png");  //refers to:assets/yourmod/textures/entity/yourtexture.png

    ModelBase model = new ModelBiplane();
	public static float scale = 1.25f;
    
    public RenderBiplane(RenderManager rendermanager)
    {
        super(rendermanager);
        this.shadowSize = 2.0f;
    }
    long start = System.currentTimeMillis();
    
    @Override
    protected ResourceLocation getEntityTexture(EntityBiplane par1Entity)
    {
        return TexturePath;
    }
    
    @Override
    public void doRender(EntityBiplane entity, double x, double y, double z, float par1, float partialTicks) //WTF is partialticks
    {
    	super.doRender(entity, x, y, z, par1, partialTicks);
    	
    	GlStateManager.pushMatrix();

    	GlStateManager.translate((float) x, (float) y, (float) z);
    
    	Vec3 angles = entity.getInterpolatedRotation(partialTicks);
    	GlStateManager.rotate((float)angles.x, 0.0F, 1.0F, 0.0F);
    	GlStateManager.rotate((float)angles.y, 1.0F, 0.0F, 0.0F);
    	GlStateManager.rotate((float)angles.z, 0.0F, 0.0F, 1.0F);

    	this.bindEntityTexture(entity);
    	
    	GlStateManager.scale(-scale, -scale, scale);
 
    	((ModelBiplane)model).render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, partialTicks, Mcflight.keyhandler.vectordrawing_toggled);

    	GlStateManager.popMatrix();
    }
}