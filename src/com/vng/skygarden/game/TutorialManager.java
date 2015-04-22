package com.vng.skygarden.game;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.netty.*;

public class TutorialManager
{
	private boolean enable;
	private Tutorial[] tutorial;
	private byte step;
	
	private boolean first_plant_has_bug;
	
	public TutorialManager()
	{
		enable = true;
		step = 0;
		first_plant_has_bug = false;
		
		tutorial = new Tutorial[DatabaseID.TUTORIAL_MAX];
		for (int i = 0; i < tutorial.length; i++)
		{
			tutorial[i] = new Tutorial(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TUTORIAL][i][DatabaseID.TUTORIAL_EXP]));
		}
	}
	
	public TutorialManager(byte[] bin)
	{
		FBEncrypt tut = new FBEncrypt(bin);
		
		enable = tut.getBoolean(KeyID.KEY_TUTORIAL_ENABLE);
		step = tut.getByte(KeyID.KEY_TUTORIAL_STEP);
		first_plant_has_bug = tut.getBoolean(KeyID.KEY_TUTORIAL_FIRST_PLANT_HAS_BUG);
		
		tutorial = new Tutorial[DatabaseID.TUTORIAL_MAX];
		
		for (int i = 0; i < tutorial.length; i++)
		{
			tutorial[i] = new Tutorial(Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_TUTORIAL][i][DatabaseID.TUTORIAL_EXP]));
			
			tutorial[i].started = tut.getBoolean(KeyID.KEY_TUTORIAL_STARTED + i);
			tutorial[i].finished = tut.getBoolean(KeyID.KEY_TUTORIAL_FINISHED + i);
			// tutorial[i].exp = tut.getShort(KeyID.KEY_TUTORIAL_EXP + i);
		}
	}
	
	public boolean isFinishedAll()
	{
		for (int i = 0; i < tutorial.length; i++)
		{
			if (tutorial[i].finished == false) return false;
		}
		
		return true;
	}
	
	public Tutorial getTutorial(int step)
	{
		return tutorial[step];
	}
	
	public boolean getStartedStep(int step)
	{
		return tutorial[step].started;
	}

	public void setStartedStep(int step)
	{
		if (enable == false) return;
		
		this.step = (byte)step;
		tutorial[step].started = true;
		
		if (step >= DatabaseID.TUTORIAL_PLANT)
		{
			for (int i = 0; i < step; i++)
			{
				tutorial[i].started = true;
				tutorial[i].finished = true;
			}
		}
	}

	public boolean getFinishedStep(int step)
	{
		return tutorial[step].finished;
	}
	
	public short setFinishedStep(int step)
	{
		if (enable == false) return 0;
		
		if (tutorial[step].finished == false)
		{
			tutorial[step].finished = true;
			return tutorial[step].exp;
		}
		
		return 0;
	}
	
	public boolean getFirstPlantHasBug()
	{
		return first_plant_has_bug;
	}
	
	public void setFirstPlantHasBug(boolean value)
	{
		first_plant_has_bug = value;
	}
	
	public byte[] getData()
	{
		FBEncrypt tut = new FBEncrypt();
		
		tut.addBoolean(KeyID.KEY_TUTORIAL_ENABLE, enable);
		tut.addByte(KeyID.KEY_TUTORIAL_STEP, step);
		tut.addBoolean(KeyID.KEY_TUTORIAL_FIRST_PLANT_HAS_BUG, first_plant_has_bug);
		
		for (int i = 0; i < tutorial.length; i++)
		{
			tut.addBoolean(KeyID.KEY_TUTORIAL_STARTED + i, tutorial[i].started);
			tut.addBoolean(KeyID.KEY_TUTORIAL_FINISHED + i, tutorial[i].finished);
			tut.addShort(KeyID.KEY_TUTORIAL_EXP + i, tutorial[i].exp);
		}
		
		return tut.toByteArray();
	}
	
	public void displayDataPackage()
	{
		LogHelper.Log("TUTORIAL_ENABLE: " + enable);
		LogHelper.Log("tutorial_step: " + step);
		LogHelper.Log("tutorial_first_plant_has_bug: " + first_plant_has_bug);
		
		for (int i = 0; i < tutorial.length; i++)
		{
			LogHelper.Log(getTutorialStepName(i) + ": " + "[" + tutorial[i].started + "][" + tutorial[i].finished + "][" + tutorial[i].exp + "]");
		}
	}
	
	private String getTutorialStepName(int index)
	{
		switch (index)
		{
			case DatabaseID.TUTORIAL_SLIDE_TO_FIRST_FLOOR		: return  "TUTORIAL_SLIDE_TO_FIRST_FLOOR";
			case DatabaseID.TUTORIAL_HARVEST					: return  "TUTORIAL_HARVEST";
			case DatabaseID.TUTORIAL_AFTER_HARVEST				: return  "TUTORIAL_AFTER_HARVEST";
			case DatabaseID.TUTORIAL_PLANT						: return  "TUTORIAL_PLANT";
			case DatabaseID.TUTORIAL_AFTER_PLANT				: return  "TUTORIAL_AFTER_PLANT";
			case DatabaseID.TUTORIAL_RECEIVE_PLANT_REWARD		: return  "TUTORIAL_RECEIVE_PLANT_REWARD";
			case DatabaseID.TUTORIAL_CATCH_BUG					: return  "TUTORIAL_CATCH_BUG";
			case DatabaseID.TUTORIAL_AFTER_CATCH_BUG			: return  "TUTORIAL_AFTER_CATCH_BUG";
			case DatabaseID.TUTORIAL_OPEN_MACHINE				: return  "TUTORIAL_OPEN_MACHINE";
			case DatabaseID.TUTORIAL_SKIP_TIME_FREEZE_MACHINE	: return  "TUTORIAL_SKIP_TIME_FREEZE_MACHINE";
			case DatabaseID.TUTORIAL_PRODUCT					: return  "TUTORIAL_PRODUCT";
			case DatabaseID.TUTORIAL_SKIP_TIME					: return  "TUTORIAL_SKIP_TIME";
			case DatabaseID.TUTORIAL_AFTER_SKIP_TIME			: return  "TUTORIAL_AFTER_SKIP_TIME";
			case DatabaseID.TUTORIAL_RECEIVE_ORDER				: return  "TUTORIAL_RECEIVE_ORDER";
			case DatabaseID.TUTORIAL_DELIVERY_ORDER				: return  "TUTORIAL_DELIVERY_ORDER";
			case DatabaseID.TUTORIAL_RECEIVE_ORDER_REWARD		: return  "TUTORIAL_RECEIVE_ORDER_REWARD";
			case DatabaseID.TUTORIAL_AFTER_RECEIVE_ORDER_REWARD	: return  "TUTORIAL_AFTER_RECEIVE_ORDER_REWARD";
			case DatabaseID.TUTORIAL_OPEN_NEW_FLOOR				: return  "TUTORIAL_OPEN_NEW_FLOOR";
			case DatabaseID.TUTORIAL_NPC_MACHINE_REPAIR			: return  "TUTORIAL_NPC_MACHINE_REPAIR";
			case DatabaseID.TUTORIAL_AFTER_NPC_MACHINE_REPAIR	: return  "TUTORIAL_AFTER_NPC_MACHINE_REPAIR";
			case DatabaseID.TUTORIAL_PUT_POT					: return  "TUTORIAL_PUT_POT";
			case DatabaseID.TUTORIAL_BUY_POT					: return  "TUTORIAL_BUY_POT";
			case DatabaseID.TUTORIAL_MOVE_POT					: return  "TUTORIAL_MOVE_POT";
			case DatabaseID.TUTORIAL_CONNECT_SNS				: return  "TUTORIAL_CONNECT_SNS";
			case DatabaseID.TUTORIAL_PRIVATE_SHOP_GUIDE			: return  "TUTORIAL_PRIVATE_SHOP_GUIDE";
			case DatabaseID.TUTORIAL_BUY_ITEM_PRIVATE_SHOP		: return  "TUTORIAL_BUY_ITEM_PRIVATE_SHOP";
			case DatabaseID.TUTORIAL_NEWSBOARD					: return  "TUTORIAL_NEWSBOARD";
			case DatabaseID.TUTORIAL_UPGRADE_POT				: return  "TUTORIAL_UPGRADE_POT";
		}

		return "tutorial_unknow_step";
	}
}

class Tutorial
{
	public boolean started;
	public boolean finished;
	public short exp;
	
	public Tutorial(long exp)
	{
		this.started = false;
		this.finished = false;
		this.exp = (short)exp;
	}
}