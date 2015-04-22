package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.db.*;

public class SlotMachine
{
    private short 	product_id = -1;
    private int 	product_time = -1;
    private boolean load_result = true;
    
    public SlotMachine(int _product_id, int _produt_time)
    {
//        setID(_product_id);
        

//        if (_product_id != -1)
//        {
//            setProductTime(_produt_time);
//        }
        
        product_id = (short)_product_id;
        
        product_time = _produt_time;
        
        if (product_id < 0 || product_time < 0)
        {
            LogHelper.Log("SlotMachine.. can not init slot machine.");
            load_result = false;
            return;
        }
    }
	
    public SlotMachine(byte[] slot_bin)
    {
        FBEncrypt slot = new FBEncrypt(slot_bin);

        this.product_id = slot.getShort(KeyID.KEY_MACHINE_SLOT_PRODUCT_ID);
        this.product_time = slot.getInt(KeyID.KEY_MACHINE_SLOT_PRODUCT_TIME);
        
        if (product_id < 0 || product_time < 0)
        {
            LogHelper.Log("SlotMachine.. can not load slot machine data.");
            load_result = false;
            return;
        }
    }
    
    public boolean isLoadSuccess()
    {
        return load_result;
    }
	
    public short getID()
    {
        return this.product_id;
    }

    public void setID(int product_id) 
    {
        if (product_id == -1)
        {
            this.product_time = 0;
        }
		
        this.product_id = (short)product_id;
    }
	
    public int getProductTime()
    {
        return this.product_time;
    }

    public void setProductTime(int product_time) 
    {
        this.product_time = product_time;
    }
	
    public byte[] getData()
    {
        FBEncrypt slot = new FBEncrypt();

        slot.addShort(KeyID.KEY_MACHINE_SLOT_PRODUCT_ID, this.product_id);
        slot.addInt(KeyID.KEY_MACHINE_SLOT_PRODUCT_TIME, this.product_time);

        return slot.toByteArray();
    }
	
    public void displayDataPackage()
    {
        FBEncrypt slot = new FBEncrypt(getData());
        slot.displayDataPackage();
    }
	
	public String LogSlotMachine()
	{
		StringBuilder log = new StringBuilder();
		
		log.append(this.product_id);
		log.append('\t').append(this.product_time);
	
		return log.toString();
	}
}