package thepowderguy.mcflight.client.util;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import thepowderguy.mcflight.common.Mcflight;
import thepowderguy.mcflight.common.entity.EntityAirplane;
import thepowderguy.mcflight.common.entity.EntityAirplaneCamera;
import thepowderguy.mcflight.util.Vec3;

public class CustomRenderPlayer extends RenderPlayer
{

	public CustomRenderPlayer(RenderManager renderManager) {
		super(renderManager);
		// TODO Auto-generated constructor stub
	}

    public CustomRenderPlayer(RenderManager rendermanager, boolean b) {
		super(rendermanager, b);
	}

	public void doRender(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Pre(entity, this, partialTicks, x, y, z))) return;
        if (!entity.isUser() || this.renderManager.renderViewEntity == entity || (entity.getRidingEntity() instanceof EntityAirplane && EntityAirplaneCamera.views[Mcflight.keyhandler.camera_mode].renderPlayer))
        {
            double d0 = y;

            if (entity.isSneaking() && !(entity instanceof EntityPlayerSP))
            {
                d0 = y - 0.125D;
            }

            this.setModelVisibilities(entity);
            GlStateManager.enableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
            {
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Pre<AbstractClientPlayer>(entity, this, x, y, z))) return;
                GlStateManager.pushMatrix();
                GlStateManager.disableCull();
                this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
                boolean shouldSit = entity.isRiding() && (entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit());
                this.mainModel.isRiding = shouldSit;
                this.mainModel.isChild = entity.isChild();

                try
                {
                    float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                    float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
                    float f2 = f1 - f;

                    if (shouldSit && entity.getRidingEntity() instanceof EntityLivingBase)
                    {
                        EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
                        f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
                        f2 = f1 - f;
                        float f3 = MathHelper.wrapDegrees(f2);

                        if (f3 < -85.0F)
                        {
                            f3 = -85.0F;
                        }

                        if (f3 >= 85.0F)
                        {
                            f3 = 85.0F;
                        }

                        f = f1 - f3;

                        if (f3 * f3 > 2500.0F)
                        {
                            f += f3 * 0.2F;
                        }
                    }

                    float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
                    this.renderLivingAt(entity, x, y, z);
                    float f8 = this.handleRotationFloat(entity, partialTicks);
                    this.applyRotations(entity, f8, f, partialTicks);
                    float f4 = this.prepareScale(entity, partialTicks);
                    float f5 = 0.0F;
                    float f6 = 0.0F;

                    if (!entity.isRiding())
                    {
                        f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
                        f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);

                        if (entity.isChild())
                        {
                            f6 *= 3.0F;
                        }

                        if (f5 > 1.0F)
                        {
                            f5 = 1.0F;
                        }
                    }

                    GlStateManager.enableAlpha();
                    this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
                    this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);

                    if (this.renderOutlines)
                    {
                        boolean flag1 = this.setScoreTeamColor(entity);
                        GlStateManager.enableColorMaterial();
                        GlStateManager.enableOutlineMode(this.getTeamColor(entity));

                        if (!this.renderMarker)
                        {
                            this.renderModel(entity, f6, f5, f8, f2, f7, f4);
                        }

                        if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                        {
                            this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                        }

                        GlStateManager.disableOutlineMode();
                        GlStateManager.disableColorMaterial();

                        if (flag1)
                        {
                            this.unsetScoreTeamColor();
                        }
                    }
                    else
                    {
                        boolean flag = this.setDoRenderBrightness(entity, partialTicks);
                        this.renderModel(entity, f6, f5, f8, f2, f7, f4);

                        if (flag)
                        {
                            this.unsetBrightness();
                        }

                        GlStateManager.depthMask(true);

                        if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator())
                        {
                            this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
                        }
                    }

                    GlStateManager.disableRescaleNormal();
                }
                catch (Exception exception)
                {
             //       LOGGER.error((String)"Couldn\'t render entity", (Throwable)exception);
                	exception.printStackTrace();
                }

                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
                GlStateManager.enableTexture2D();
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                GlStateManager.enableCull();
                GlStateManager.popMatrix();
                {
                    if (!this.renderOutlines)
                    {
                        this.renderName(entity, x, y, z);
                    }
                }
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderLivingEvent.Post<AbstractClientPlayer>(entity, this, x, y, z));
            }
            GlStateManager.disableBlendProfile(GlStateManager.Profile.PLAYER_SKIN);
        }
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Post(entity, this, partialTicks, x, y, z));
    }
   
    private void setModelVisibilities(AbstractClientPlayer clientPlayer)
    {
        ModelPlayer modelplayer = this.getMainModel();

        if (clientPlayer.isSpectator())
        {
            modelplayer.setInvisible(false);
            modelplayer.bipedHead.showModel = true;
            modelplayer.bipedHeadwear.showModel = true;
        }
        else
        {
            ItemStack itemstack = clientPlayer.getHeldItemMainhand();
            ItemStack itemstack1 = clientPlayer.getHeldItemOffhand();
            modelplayer.setInvisible(true);
            modelplayer.bipedHeadwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.HAT);
            modelplayer.bipedBodyWear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.JACKET);
            modelplayer.bipedLeftLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_PANTS_LEG);
            modelplayer.bipedRightLegwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_PANTS_LEG);
            modelplayer.bipedLeftArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.LEFT_SLEEVE);
            modelplayer.bipedRightArmwear.showModel = clientPlayer.isWearing(EnumPlayerModelParts.RIGHT_SLEEVE);
            modelplayer.isSneak = clientPlayer.isSneaking();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;

            if (!itemstack.isEmpty())
            {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction = itemstack.getItemUseAction();

                    if (enumaction == EnumAction.BLOCK)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    }
                    else if (enumaction == EnumAction.BOW)
                    {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (!itemstack1.isEmpty())
            {
                modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;

                if (clientPlayer.getItemInUseCount() > 0)
                {
                    EnumAction enumaction1 = itemstack1.getItemUseAction();

                    if (enumaction1 == EnumAction.BLOCK)
                    {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                    }
                    // FORGE: fix MC-88356 allow offhand to use bow and arrow animation
                    else if (enumaction1 == EnumAction.BOW)
                    {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (clientPlayer.getPrimaryHand() == EnumHandSide.RIGHT)
            {
                modelplayer.rightArmPose = modelbiped$armpose;
                modelplayer.leftArmPose = modelbiped$armpose1;
            }
            else
            {
                modelplayer.rightArmPose = modelbiped$armpose1;
                modelplayer.leftArmPose = modelbiped$armpose;
            }
        }
    }

    protected void applyRotations(AbstractClientPlayer entityLiving, float p_77043_2_, float p_77043_3_, float partialTicks)
    {
    	boolean flagRenderCustom = entityLiving.getRidingEntity() instanceof EntityAirplane &&  EntityAirplaneCamera.views[Mcflight.keyhandler.camera_mode].renderPlayer;
        if (flagRenderCustom)
        {
        	EntityAirplane entity = (EntityAirplane)entityLiving.getRidingEntity();
        	Vec3 angles = entity.getInterpolatedRotation(partialTicks);
        	GlStateManager.rotate(180f+(float)angles.x, 0.0F, 1.0F, 0.0F);
        	GlStateManager.rotate((float)-angles.y, 1.0F, 0.0F, 0.0F);
        	GlStateManager.rotate((float)-angles.z, 0.0F, 0.0F, 1.0F);
        }
        if (entityLiving.isEntityAlive() && entityLiving.isPlayerSleeping())
        {
            GlStateManager.rotate(entityLiving.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        }
        else if (entityLiving.isElytraFlying())
        {
            GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);

            if (entityLiving.deathTime > 0)
            {
                float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
                f = MathHelper.sqrt(f);

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                GlStateManager.rotate(f * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
            }
            else
            {
                String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());

                if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof EntityPlayer) || ((EntityPlayer)entityLiving).isWearing(EnumPlayerModelParts.CAPE)))
                {
                    GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                }
            }
            float f = (float)entityLiving.getTicksElytraFlying() + partialTicks;
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f1 * (-90.0F - entityLiving.rotationPitch), 1.0F, 0.0F, 0.0F);
            Vec3d vec3d = entityLiving.getLook(partialTicks);
            double d0 = entityLiving.motionX * entityLiving.motionX + entityLiving.motionZ * entityLiving.motionZ;
            double d1 = vec3d.xCoord * vec3d.xCoord + vec3d.zCoord * vec3d.zCoord;

            if (d0 > 0.0D && d1 > 0.0D)
            {
                double d2 = (entityLiving.motionX * vec3d.xCoord + entityLiving.motionZ * vec3d.zCoord) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = entityLiving.motionX * vec3d.zCoord - entityLiving.motionZ * vec3d.xCoord;
                GlStateManager.rotate((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI, 0.0F, 1.0F, 0.0F);
            }
        }
        else
        {
            if (!flagRenderCustom)
            	GlStateManager.rotate(180.0F - p_77043_3_, 0.0F, 1.0F, 0.0F);

            if (entityLiving.deathTime > 0)
            {
                float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
                f = MathHelper.sqrt(f);

                if (f > 1.0F)
                {
                    f = 1.0F;
                }

                GlStateManager.rotate(f * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
            }
            else
            {
                String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName());

                if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof EntityPlayer) || ((EntityPlayer)entityLiving).isWearing(EnumPlayerModelParts.CAPE)))
                {
                    GlStateManager.translate(0.0F, entityLiving.height + 0.1F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                }
            }
        }
    }
}