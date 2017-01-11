package thepowderguy.mcflight.common.entity.biplane;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thepowderguy.mcflight.common.entity.RenderAirplane;


@SideOnly(Side.CLIENT)
public class RenderBiplane extends RenderAirplane<EntityBiplane>
{
    private ResourceLocation TexturePath = new ResourceLocation("mcflight:textures/entity/biplane.png");  //refers to:assets/yourmod/textures/entity/yourtexture.png

    {
    	scale = 1.25f;
    }
    
    public RenderBiplane(RenderManager rendermanager)
    {
        super(rendermanager);
        this.shadowSize = 2.0f;
        model = new ModelBiplane();
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
    }
}