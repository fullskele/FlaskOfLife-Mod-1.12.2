package com.fullskele.flaskoflife;

import com.elenai.elenaidodge2.api.FeathersHelper;

import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.util.InventoryUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import java.util.Comparator;
import java.util.List;

import static com.fullskele.flaskoflife.FlaskOfLife.refillBlocks;

public class ItemFlask extends Item {


    public ItemFlask(String registryName) {
        super();
        this.setUnlocalizedName(registryName);
        this.setRegistryName(FlaskOfLife.MODID, registryName);
        this.setMaxStackSize(1);

        this.addPropertyOverride(new ResourceLocation("flaskoflife:sprite"), new IItemPropertyGetter() {

            @Override
            public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                NBTTagCompound nbttagcompound = stack.getTagCompound();
                return getFloatFromTag(nbttagcompound, "sprite", 0);
            }
        });
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {

        int choice = getIntFromTag(stack.getTagCompound(), "animation", 0);
        switch (choice) {
            case 0:
                return EnumAction.DRINK;
            case 1:
                return EnumAction.EAT;
            case 2:
                return EnumAction.BOW;
            case 3:
                return EnumAction.BLOCK;
        }
        return EnumAction.NONE;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack)
    {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        return getIntFromTag(nbttagcompound, "useTime", 32);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        return getIntFromTag(nbttagcompound, "durability", 5);
    }

    /*
    @Override
    public boolean isEnchantable(ItemStack item) {

        return getItemEnchantability(item) > 0;
    }

    @Override
    public int getItemEnchantability(ItemStack item) {
        NBTTagCompound nbttagcompound = item.getTagCompound();
        if (nbttagcompound != null && nbttagcompound.hasKey("enchantability")) {
            return nbttagcompound.getInteger("enchantability");
        }
        return 0;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack item, Enchantment enchantment) {
        if (getItemEnchantability(item) > 0)
            return enchantment.type == EnumEnchantmentType.BREAKABLE;
        return false;
    }
    */

    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        NBTTagCompound nbttagcompound = stack.getTagCompound();
        int durability = getIntFromTag(nbttagcompound, "durability", 5);

        if (durability < 1) {
            tooltip.add(I18n.format("tooltip.flaskoflife.unlimited"));
        } else {
            tooltip.add(I18n.format("tooltip.flaskoflife.charges", (stack.getMaxDamage()-stack.getItemDamage()), stack.getMaxDamage()));
        }

        if (ConfigHandler.TOOLTIP_NEEDS_SHIFT && !GuiScreen.isShiftKeyDown()) {
            tooltip.add(TextFormatting.GRAY + I18n.format("tooltip.flaskoflife.holdshift"));
            return;
        }

        tooltip.add(I18n.format("tooltip.flaskoflife.empty"));

        int healing = (int)getFloatFromTag(nbttagcompound, "healing", 8);
        if (healing > 0) {
            tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.flaskoflife.plus") + I18n.format("tooltip.flaskoflife.healing", healing));
        } else if (healing < 0) {
            tooltip.add(TextFormatting.RED + I18n.format("tooltip.flaskoflife.healing", healing));

        }
        if (nbttagcompound != null) {
            if (nbttagcompound.hasKey("healingPercent")) {
                float healingPercent = getFloatFromTag(nbttagcompound, "healingPercent", 10);
                if (healingPercent > 0) {
                    tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.flaskoflife.plus") + I18n.format("tooltip.flaskoflife.percenthealing", healingPercent));
                } else if (healingPercent < 0) {
                    tooltip.add(TextFormatting.RED + I18n.format("tooltip.flaskoflife.percenthealing", healingPercent));
                }
            }
            if (Loader.isModLoaded("ebwizardry")) {
                if (nbttagcompound.hasKey("mana")) {
                    int mana = getIntFromTag(nbttagcompound, "mana", 20);
                    if (mana > 0) {
                        tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.flaskoflife.plus") + I18n.format("tooltip.flaskoflife.mana", mana));
                    } else if (mana < 0) {
                        tooltip.add(TextFormatting.RED + I18n.format("tooltip.flaskoflife.mana", mana));
                    }
                }
            }
            if (Loader.isModLoaded("elenaidodge2")) {
                if (nbttagcompound.hasKey("feathers")) {
                    int feathers = getIntFromTag(nbttagcompound, "feathers", 5);
                    if (feathers > 0) {
                        tooltip.add(TextFormatting.BLUE + I18n.format("tooltip.flaskoflife.plus") + I18n.format("tooltip.flaskoflife.feathers", feathers));

                    } else if (feathers < 0) {
                        tooltip.add(TextFormatting.RED + I18n.format("tooltip.flaskoflife.feathers", feathers));

                    }
                }
            }
            if (nbttagcompound.hasKey("potion")) {
                PotionEffect potionEffect = new PotionEffect(getPotionFromTag(nbttagcompound, "potion", Potion.getPotionFromResourceLocation("minecraft:regeneration")), getIntFromTag(nbttagcompound, "duration", 100), getIntFromTag(nbttagcompound, "amp", 0), false, false);

                if (potionEffect.getPotion().isBeneficial()) {
                    tooltip.add(TextFormatting.BLUE + I18n.format(potionEffect.getEffectName()) + " "+ I18n.format("potion.potency." + potionEffect.getAmplifier()).trim() + " (" + Potion.getPotionDurationString(potionEffect, 1) + ")");
                } else {
                    tooltip.add(TextFormatting.RED + I18n.format(potionEffect.getEffectName()) + " "+ I18n.format("potion.potency." + potionEffect.getAmplifier()).trim() + " (" + Potion.getPotionDurationString(potionEffect, 1) + ")");
                }
            }
            if (nbttagcompound.hasKey("cooldown")) {
                int cooldown = getIntFromTag(nbttagcompound, "cooldown", 8);
                tooltip.add(TextFormatting.RED + "Cooldown (" + String.format("%d:%02d", (int)(cooldown * 0.05) / 60, (int)(cooldown * 0.05) % 60) + ")");
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        NBTTagCompound nbttagcompound = itemStack.getTagCompound();

            // Get the position of the block the player is looking at


        if (GuiScreen.isShiftKeyDown() && getBooleanFromTag(nbttagcompound, "refillable", true)) {
            RayTraceResult rayTraceResult = this.rayTrace(worldIn, playerIn, true);
            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {

                BlockPos blockPos = rayTraceResult.getBlockPos();

                if (refillBlocks.contains(worldIn.getBlockState(blockPos).getBlock())) {
                    itemStack.setItemDamage(0);
                    playerIn.getCooldownTracker().setCooldown(this, (int) (getIntFromTag(nbttagcompound, "cooldown", 8) * ConfigHandler.REFILL_COOLDOWN_SCALING));
                    playerIn.world.playSound(null, playerIn.getPosition(), SoundEvent.REGISTRY.getObject(getResourceLocationFromTag(nbttagcompound, "fillSound", new ResourceLocation("item.firecharge.use"))), SoundCategory.PLAYERS, ConfigHandler.REFILL_VOLUME, ConfigHandler.REFILL_PITCH);

                } else {

                    //If block isnt a refill block just have normal functionality
                    if ((itemStack.getItemDamage() == itemStack.getMaxDamage()) && !getBooleanFromTag(nbttagcompound, "breakable", false))
                        return new ActionResult<>(EnumActionResult.FAIL, itemStack);

                    playerIn.setActiveHand(handIn);
                }
                return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
            }
        }
        if ((itemStack.getItemDamage() == itemStack.getMaxDamage()) && !getBooleanFromTag(nbttagcompound, "breakable", false))
            return new ActionResult<>(EnumActionResult.FAIL, itemStack);

        playerIn.setActiveHand(handIn);
        return new ActionResult<>(EnumActionResult.SUCCESS, itemStack);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;

            boolean harmful = false;
            float healing = getFloatFromTag(nbttagcompound, "healing", 8);
            if (healing > 0) {
                player.heal(healing);
            } else if (healing < 0) {
                player.setHealth(player.getHealth()+healing);
                harmful = true;
            }
            player.getCooldownTracker().setCooldown(this, getIntFromTag(nbttagcompound, "cooldown", 8));


            //Dont want the base potion to have these traits
            if (nbttagcompound != null) {
                if (nbttagcompound.hasKey("potion")) {//duration, then amp
                    PotionEffect nbtPotion = new PotionEffect(getPotionFromTag(nbttagcompound, "potion", Potion.getPotionFromResourceLocation("minecraft:regeneration")), getIntFromTag(nbttagcompound, "duration", 100), getIntFromTag(nbttagcompound, "amp", 0), false, false);
                    player.addPotionEffect(nbtPotion);
                }
                if (nbttagcompound.hasKey("healSound")) {
                    player.world.playSound(null, player.getPosition(), SoundEvent.REGISTRY.getObject(getResourceLocationFromTag(nbttagcompound, "healSound", new ResourceLocation(""))), SoundCategory.PLAYERS, ConfigHandler.USE_VOLUME, ConfigHandler.USE_PITCH);

                }
                if (nbttagcompound.hasKey("healingPercent")) {
                    float healingPercent = getFloatFromTag(nbttagcompound, "healingPercent", 10) /100;
                    if (healingPercent > 0) {
                        player.heal(player.getMaxHealth() * (healingPercent));
                    } else if (healingPercent < 0) {
                        player.setHealth(player.getHealth() + (player.getMaxHealth() * (healingPercent)));
                        harmful = true;
                    }
                }

                if (Loader.isModLoaded("ebwizardry")) {
                    if (nbttagcompound.hasKey("mana")) {
                        findAndChargeItem(player, getIntFromTag(nbttagcompound, "mana", 20));
                    }
                }

                //may need to run method serverside only
                if (Loader.isModLoaded("elenaidodge2")) {
                    if (nbttagcompound.hasKey("feathers")) {
                        if (player instanceof EntityPlayerMP) {
                            FeathersHelper.increaseFeathers((EntityPlayerMP) player, getIntFromTag(nbttagcompound, "feathers", 5));
                        }
                    }
                }
            }

            if (harmful) {
                player.performHurtAnimation();
                player.world.playSound(null, player.getPosition(), SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.player.hurt")), SoundCategory.PLAYERS, 1.0f, (1.0f * worldIn.rand.nextFloat() * 0.25f) - 0.125f);
            }
        }


        stack.damageItem(getIntFromTag(nbttagcompound, "durabilityDamage", 1), entityLiving);
        return stack;
    }


    private int getIntFromTag(NBTTagCompound nbtTagCompound, String tag, int defaultValue) {
        if (nbtTagCompound != null && nbtTagCompound.hasKey(tag))
            return nbtTagCompound.getInteger(tag);
        return defaultValue;
    }

    private boolean getBooleanFromTag(NBTTagCompound nbtTagCompound, String tag, boolean defaultValue) {
        if (nbtTagCompound != null && nbtTagCompound.hasKey(tag))
            return nbtTagCompound.getBoolean(tag);
        return defaultValue;
    }

    private float getFloatFromTag(NBTTagCompound nbtTagCompound, String tag, float defaultValue) {
        if (nbtTagCompound != null && nbtTagCompound.hasKey(tag))
            return nbtTagCompound.getFloat(tag);
        return defaultValue;
    }

    private Potion getPotionFromTag(NBTTagCompound nbtTagCompound, String tag, Potion defaultValue) {
        Potion chosenPot = Potion.getPotionFromResourceLocation(nbtTagCompound.getString(tag));

        if (chosenPot != null) {
            return chosenPot;
        }
        return defaultValue;
    }

    private ResourceLocation getResourceLocationFromTag(NBTTagCompound nbtTagCompound, String tag, ResourceLocation defaultValue) {
        if (nbtTagCompound != null && nbtTagCompound.hasKey(tag))
            return new ResourceLocation(nbtTagCompound.getString(tag));
        return defaultValue;
    }




    //WizardryUtils doesn't seem to have anything like this
    //Below code taken from EBWizardry. I take no credit for this!
    private void findAndChargeItem(EntityPlayer player, int amount) {
        List<ItemStack> stacks = InventoryUtils.getPrioritisedHotbarAndOffhand(player);
        stacks.addAll(player.inventory.armorInventory); // player#getArmorInventoryList() only returns an Iterable

        ItemStack toCharge;
        if (amount > 0) {
            toCharge = stacks.stream()
                    .filter(s -> s.getItem() instanceof IManaStoringItem && !((IManaStoringItem)s.getItem()).isManaFull(s))
                    .min(Comparator.comparingDouble(s -> ((IManaStoringItem)s.getItem()).getFullness(s))).orElse(null);
        } else {
            toCharge = stacks.stream()
                    .filter(s -> s.getItem() instanceof IManaStoringItem)
                    .min(Comparator.comparingDouble(s -> ((IManaStoringItem)s.getItem()).getFullness(s))).orElse(null);
        }
        // Find the chargeable item with the least mana


        if(toCharge != null) {

            ((IManaStoringItem)toCharge.getItem()).rechargeMana(toCharge, amount);

        }
    }

    //FUTURE PLANS
    //Multiple potion effects per flask
    //(Custom) Enchantments
    //Custom break noise (glass) - via mixin?
    //Option to be unusable when a specified potion effect is active
    //Metadata selection for refill blocks


}
