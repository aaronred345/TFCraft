package TFC.TileEntities;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import TFC.Core.TFC_ItemHeat;

public class TileEntityLogPile extends TileEntity implements IInventory
{
    public ItemStack[] storage;
    private int logPileOpeners;

    public TileEntityLogPile()
    {
        storage = new ItemStack[4];
        logPileOpeners = 0;
    }

    public void addContents(int index, ItemStack is)
    {
        if(storage[index] == null) {
            storage[index] = is;
        }
    }

    public void clearContents()
    {
        storage[0] = null;
        storage[1] = null;
        storage[2] = null;
        storage[3] = null;
    }

    @Override
    public void closeChest() {
    	--logPileOpeners;
    	
    	if(logPileOpeners == 0 && storage[0] == null && storage[1] == null &&
				storage[2] == null && storage[3] == null)
		{
			worldObj.setBlock(xCoord, yCoord, zCoord, 0);
		}

    }

    public boolean contentsMatch(int index, ItemStack is)
    {
        if(storage[index] != null && storage[index].getItem() == is.getItem() && storage[index].getItemDamage() == is.getItemDamage() &&
                storage[index].stackSize < storage[index].getMaxStackSize() && storage[index].stackSize+1 <= this.getInventoryStackLimit())
        {
            return true;
        } else {
            return false;
        }
    }
    
    public int getNumberOfLogs()
    {
    	int[] count = new int[4];
    	count[0] = storage[0] != null ? storage[0].stackSize : 0;
    	count[1] = storage[1] != null ? storage[1].stackSize : 0;
    	count[2] = storage[2] != null ? storage[2].stackSize : 0;
    	count[3] = storage[3] != null ? storage[3].stackSize : 0;
    	
    	return count[0] + count[1] + count[2] + count[3];
    }

    @Override
    public ItemStack decrStackSize(int i, int j)
    {
        if(storage[i] != null)
        {
            if(storage[i].stackSize <= j)
            {
                ItemStack itemstack = storage[i];
                storage[i] = null;
                return itemstack;
            }
            ItemStack itemstack1 = storage[i].splitStack(j);
            if(storage[i].stackSize == 0)
            {
                storage[i] = null;
            }
            return itemstack1;
        } else
        {
            return null;
        }
    }

    public void ejectContents()
    {
        float f3 = 0.05F;
        EntityItem entityitem;
        Random rand = new Random();
        float f = rand.nextFloat() * 0.8F + 0.1F;
        float f1 = rand.nextFloat() * 2.0F + 0.4F;
        float f2 = rand.nextFloat() * 0.8F + 0.1F;

        for (int i = 0; i < getSizeInventory(); i++)
        {
            if(storage[i]!= null)
            {
                entityitem = new EntityItem(worldObj, xCoord + f, yCoord + f1, zCoord + f2, 
                        storage[i]);
                entityitem.motionX = (float)rand.nextGaussian() * f3;
                entityitem.motionY = (float)rand.nextGaussian() * f3 + 0.2F;
                entityitem.motionZ = (float)rand.nextGaussian() * f3;
                worldObj.spawnEntityInWorld(entityitem);
            }
        }
    }

    @Override
    public int getInventoryStackLimit()
    {
        // TODO Auto-generated method stub
        return 4;
    }

    @Override
    public String getInvName()
    {
        return "Log Pile";
    }
    
    @Override
    public int getSizeInventory()
    {
        return storage.length;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return storage[i];
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        // TODO Auto-generated method stub
        return null;
    }

    public void injectContents(int index, int count)
    {
        if(storage[index] != null) {
            storage[index] = 
                    new ItemStack(storage[index].getItem(),
                            storage[index].stackSize+count,
                            storage[index].getItemDamage());
        }
    }


    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void openChest() {
        // TODO Auto-generated method stub
    	++logPileOpeners;
    }
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readFromNBT(nbttagcompound);

        NBTTagList nbttaglist = nbttagcompound.getTagList("Items");
        storage = new ItemStack[getSizeInventory()];
        for(int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttaglist.tagAt(i);
            byte byte0 = nbttagcompound1.getByte("Slot");
            if(byte0 >= 0 && byte0 < storage.length)
            {
                storage[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack) 
    {
        storage[i] = itemstack;
        if(itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }
    
    @Override
    public void updateEntity()
    {
        TFC_ItemHeat.HandleContainerHeat(this.worldObj,storage, xCoord,yCoord,zCoord);
    }
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeToNBT(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();
        for(int i = 0; i < storage.length; i++)
        {
            if(storage[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                storage[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }
        nbttagcompound.setTag("Items", nbttaglist);
    }

    public void handlePacketData() 
    {
        TileEntityLogPile pile = this;
    }

    @Override
	public boolean isInvNameLocalized() 
	{
		return false;
	}

	@Override
	public boolean isStackValidForSlot(int i, ItemStack itemstack) 
	{
		return false;
	}
}
