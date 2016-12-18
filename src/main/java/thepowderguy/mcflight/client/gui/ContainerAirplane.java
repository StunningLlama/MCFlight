package thepowderguy.mcflight.client.gui;

import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thepowderguy.mcflight.common.entity.EntityAirplane;

public class ContainerAirplane extends Container
{
    private final IInventory horseInventory;
    private final EntityAirplane theHorse;
    int fuelslot = 0;
    public ContainerAirplane(IInventory playerInventory, final IInventory horseInventoryIn, final EntityAirplane horse, EntityPlayer player)
    {
        this.horseInventory = horseInventoryIn;
        this.theHorse = horse;
        horseInventoryIn.openInventory(player);

        for (int k = 0; k < 3; ++k)
        {
        	for (int l = 0; l < 5; ++l)
        	{
        		this.addSlotToContainer(new Slot(horseInventoryIn, l + k * 5, 80 + l * 18, 18 + k * 18));
        	}
        }

        for (int i1 = 0; i1 < 3; ++i1)
        {
            for (int k1 = 0; k1 < 9; ++k1)
            {
                this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 102 + i1 * 18 + -18));
            }
        }

        for (int j1 = 0; j1 < 9; ++j1)
        {
            this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 142));
        }
        

        this.addSlotToContainer(new Slot(horseInventoryIn, 15, 8, 18)
        {
            public void putStack(ItemStack stack)
            {
            	super.putStack(stack);
            	System.out.println(stack.getItem().getClass().getName() + " " + stack.getItem().getClass().getSimpleName());
            }
        });
        this.addSlotToContainer(new Slot(horseInventoryIn, 16, 8, 36)
        {
            public boolean isItemValid(ItemStack stack)
            {
                return true;
            }
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
    }

    /**
     * Determines whether supplied player can use this container
     */
    public boolean canInteractWith(EntityPlayer playerIn)
    {
        return this.horseInventory.isUsableByPlayer(playerIn) && this.theHorse.isEntityAlive() && this.theHorse.getDistanceToEntity(playerIn) < 8.0F;
    }


    public void putStackInSlot(int slotID, ItemStack stack)
    {
    	super.putStackInSlot(slotID, stack);
    }
    /**
     * Take a stack from the specified inventory slot.
     */
    /*public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.horseInventory.getSizeInventory())
            {
                if (!this.mergeItemStack(itemstack1, this.horseInventory.getSizeInventory(), this.inventorySlots.size(), true))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(101).isItemValid(itemstack1) && !this.getSlot(1).getHasStack())
            {
                if (!this.mergeItemStack(itemstack1, 1, 2, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.getSlot(100).isItemValid(itemstack1))
            {
                if (!this.mergeItemStack(itemstack1, 0, 1, false))
                {
                    return ItemStack.EMPTY;
                }
            }
            else if (this.horseInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.horseInventory.getSizeInventory(), false))
            {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty())
            {
                slot.putStack(ItemStack.EMPTY);
            }
            else
            {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }*/

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);
        this.horseInventory.closeInventory(playerIn);
    }
}