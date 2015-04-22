package com.vng.skygarden.game;

import java.util.*;
import java.util.Map.Entry;

import com.vng.util.*;
import com.vng.log.*;
import com.vng.netty.*;
import com.vng.skygarden.SkyGarden;

public class OrderManager
{
	private byte		total_delivered_num;
	private byte		order_daily_free_delivered_num;
	private byte		order_daily_paid_delivered_num;
	private byte		order_daily_free_max;
	private byte		order_daily_paid_max;
	private boolean		order_daily_letter_selected;
	
	private int			order_daily_reset_time;
	
	private byte		order_count;
	ArrayList<Order>	order;
	
	private boolean		reward_package;
	OrderReward			reward;
	
	public boolean		isOrderPlant = false;
	public long			pot_bonus_ratio = 0;
	private Order		_order_event;

	public OrderManager()
	{
		order_daily_free_delivered_num = 0;
		order_daily_paid_delivered_num = 0;
		
		order_daily_paid_max = 0;
		order_daily_free_max = 0;
		total_delivered_num = 0;
		order_daily_letter_selected = false;
		
		order_daily_reset_time = 0;
		
		order_count = 0;
		order = new ArrayList<Order>();
		
		reward_package = false;
		reward = new OrderReward();
		
		// LogHelper.Log("Init OrderManager...OK!");
		_order_event = null;
	}
	
	public OrderManager(byte[] order_bin)
	{
		FBEncrypt _order = new FBEncrypt(order_bin);
		
		this.order_daily_free_delivered_num = _order.getByte(KeyID.KEY_ORDER_FREE_DELIVERED_NUM);
		this.order_daily_paid_delivered_num = _order.getByte(KeyID.KEY_ORDER_PAID_DELIVERED_NUM);
		this.order_daily_free_max = _order.getByte(KeyID.KEY_ORDER_DAILY_FREE_MAX);
		this.order_daily_paid_max = _order.getByte(KeyID.KEY_ORDER_DAILY_PAID_MAX);
		this.total_delivered_num = _order.getByte(KeyID.KEY_TOTAL_DELIVERED_NUM);
		this.order_daily_letter_selected = _order.getBoolean(KeyID.KEY_DAILY_ORDER_LETTER_SELECTED);
		
		this.order_daily_reset_time = _order.getInt(KeyID.KEY_ORDER_DAILY_RESET_TIME);
		
		this.order_count = _order.getByte(KeyID.KEY_USER_ORDER_COUNT);
		if (order_count > 8) order_count = 8;
		
		order = new ArrayList<Order>(order_count);
		
		for (int i = 0; i < order_count; i++)
		{
			byte[] _order_data = _order.getBinary(KeyID.KEY_USER_ORDER + i);
			
			if (_order_data != null)
			{
				Order _order_normal = new Order(_order_data);
				order.add(_order_normal);
			}
			else
			{
				LogHelper.Log("Cannot load Order " + i);
				return;
			}
		}
		
		this.reward_package = _order.getBoolean(KeyID.KEY_ORDER_REWARD);
		if (this.reward_package == true)
		{
			byte[] reward_bin = _order.getBinary(KeyID.KEY_USER_ORDER_REWARD);
			
			if (reward_bin == null || reward_bin.length == 0)
			{
				LogHelper.Log("Cannot load order reward!");
				return;
			}
			
			reward = new OrderReward(reward_bin);
		}
		else
		{
			reward = new OrderReward();
		}
		
		if (_order.hasKey(KeyID.KEY_ORDER_EVENT)) {
			byte[] _order_data = _order.getBinary(KeyID.KEY_ORDER_EVENT);
			_order_event = new Order(_order_data);
			LogHelper.LogHappy("Loaded order event");
		}
	}
	
	public void initDailyOrder(int do_free_max, int do_paid_max)
	{
		order_daily_free_delivered_num = 0;
		order_daily_paid_delivered_num = 0;
		
		order_daily_paid_max = (byte)do_paid_max;
		order_daily_free_max = (byte)do_free_max;
		total_delivered_num = 0;
		
		order_daily_reset_time = Misc.getDailyResetTime();
		// order_daily_reset_time = Misc.SECONDS() + 35;	// cheat reset time
	}
	
	public void resetDailyOrder()
	{
		order_daily_free_delivered_num = 0;
		order_daily_paid_delivered_num = 0;
		
		order_daily_paid_max = 0;
		order_daily_free_max = 0;
		total_delivered_num = 0;

		order_daily_reset_time = 0;
		order_daily_letter_selected = true;
	}

	public boolean isNewDate()
	{
		return (Misc.SECONDS() > order_daily_reset_time);
	}
	
	public void resetAllLetters(int index)
	{
		Order daily_order = order.get(index);
		
		daily_order.letter_select_index = -1;
		daily_order.letter_select_value = 0;
		daily_order.letter_reselect_diamond = 0;

		daily_order.letters_enable = new boolean[DatabaseID.ORDER_LETTER_COUNT];
		daily_order.letters_value = new byte[DatabaseID.ORDER_LETTER_COUNT];
		
		for (int i = 0; i < DatabaseID.ORDER_LETTER_COUNT; i++)
		{
			daily_order.letters_enable[i] = true;
			daily_order.letters_value[i] = 0;
		}
	}
	
	public void createOrder(int type, 
			HashMap<String, Short> products, 
			HashMap<String, Short> items, 
			List<Machine> machine, 
			List<Floor> floor, 
			int user_level, 
			boolean is_order_bug_pearl,
			int bonus_daily_order,
			int bonus_order_normal)
	{
		long reward_gold = 0;
		long reward_gold_bonus = 0;
		long reward_exp = 0;
		long reward_exp_bonus = 0;
		double gold_bonus = 0;
		double exp_bonus = 0;
		
		order_count++;
		
		if (floor.size() > 9 || machine.size() > 9)
		{
			LogHelper.Log("NEED CHECK LOGIN FUNCTION!!! " + floor.size() + " " + machine.size());
		}

		if (order_count == DatabaseID.ORDER_DAILY_INDEX+1)
		{
			type = DatabaseID.ORDER_DAILY;
		}
		
		Order _order = new Order(type);
		
		if (order_count == 1) // tutorial
		{
			_order.addProductToStock(DatabaseID.IT_PLANT, 0, 6);
			_order.setRewardGold(20);
			_order.setRewardGoldBonus(0);
			_order.setRewardExp(18);
			_order.setRewardExpBonus(0);
			_order.setNPC(DatabaseID.NPC_FROG_PRINCE_ID);
				
			order.add(_order);
			return;
		}
		
		if (type == DatabaseID.ORDER_DAILY && this.total_delivered_num < this.order_daily_free_max)
		{
			_order.setReceiveDailyOrder(true);
		}
		
		// test tang item trong order
		/*
		if (items == null)
		{
			items = new HashMap<String, Short>();
			items.put("0_4", (short)30);
			items.put("1_5", (short)40);
			items.put("4_6", (short)50);
		}
		*/
		
		if (items != null)
		{
			for (Entry<String, Short> item: items.entrySet())
			{
				String key = item.getKey();
				String[] values = key.split("_");
				
				int item_type = Integer.parseInt(values[0]);
				int item_id = Integer.parseInt(values[1]);
				int item_num = item.getValue();

				_order.addRewardItem(item_type, item_id, item_num);
			}
		}
		
		// gold, exp bonus of machine
		long machine_gold_bonus_ratio = 0;
		long machine_exp_bonus_ratio = 0;
		for (int i = 0; i < machine.size(); i++)
		{
			Machine _machine = machine.get(i);
			int _machine_level = _machine.getLevel();
			
			machine_gold_bonus_ratio += (int)Server.s_globalMachineUnlockData[i][_machine_level][DatabaseID.MACHINE_GOLD_ORDER];
			machine_exp_bonus_ratio += (int)Server.s_globalMachineUnlockData[i][_machine_level][DatabaseID.MACHINE_EXP_ORDER];
		}
		
		for (Entry<String, Short> product: products.entrySet())
		{
			String key = product.getKey();
			String[] values = key.split("_");
			
			int prod_type = Integer.parseInt(values[0]);
			int prod_id = Integer.parseInt(values[1]);
			int prod_num = product.getValue();
			
			if (prod_type == DatabaseID.IT_BUG) {
				if (user_level < 17) {
					prod_id = 0;
				}
			}

			if (prod_type == DatabaseID.IT_PLANT && type != DatabaseID.ORDER_DAILY && isOrderPlant == false)
			{
				prod_num = prod_num * (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_ORDER_ITEM_PLANT_PLUS]);
			}
			
			_order.addProductToStock(prod_type, prod_id, prod_num);
			
			boolean is_DO_paid = false;
			if (type == DatabaseID.ORDER_DAILY && total_delivered_num >= order_daily_free_max && total_delivered_num > 0)
			{
				is_DO_paid = true;
			}
			
			long prod_gold = Misc.getGoldXPCoefficientEstimate(type, DatabaseID.GOLD_ID, prod_type, prod_id, prod_num, is_DO_paid, user_level);
			long prod_exp = Misc.getGoldXPCoefficientEstimate(type, DatabaseID.EXP_ID, prod_type, prod_id, prod_num, is_DO_paid, user_level);
			
			/*
			if (prod_type == DatabaseID.IT_PRODUCT)
			{
				// if (Misc.isInPearlList(prod_id))
				// {
					//// do not increase exp bonus follow design
					//// reward_exp_bonus += getPotBonus(DatabaseID.EXP_ID, prod_exp, floor);
				// }
				// else
				{
					// reward_gold_bonus += getProductGoldExpBonus(DatabaseID.GOLD_ID, prod_id, prod_gold, machine, floor.size());
					// reward_exp_bonus += getProductGoldExpBonus(DatabaseID.EXP_ID, prod_id, prod_exp, machine, floor.size());
					
					gold_bonus += ((double)(prod_gold * machine_gold_bonus_ratio) / 100);
					exp_bonus += ((double)(prod_exp * machine_exp_bonus_ratio) / 100);
				}
			}
			*/

			/*
			if (prod_type == DatabaseID.IT_PLANT || prod_type == DatabaseID.IT_BUG)
			{
				// temporary rem this, do not increase pot bonus for plant order
				// reward_gold_bonus += getPotBonus(DatabaseID.GOLD_ID, prod_gold, floor);
				
				// exp_bonus += getPotBonus(DatabaseID.EXP_ID, prod_type, prod_exp, floor);
			}
			*/

			reward_gold += prod_gold;
			reward_exp += prod_exp;
		}
		
		if (reward_gold == 0) reward_gold = 1;
		if (reward_exp == 0) reward_exp = 1;
		
		// machine bonus
		gold_bonus += ((double)(reward_gold * machine_gold_bonus_ratio) / 100);
		exp_bonus += ((double)(reward_exp * machine_exp_bonus_ratio) / 100);
		
		// pot bonus
		long reward_gold_bonus_from_pot = getPotGoldXPBonus(DatabaseID.GOLD_ID, reward_gold, floor);
		long gold_bonus_ratio = pot_bonus_ratio;
		
		long reward_exp_bonus_from_pot = getPotGoldXPBonus(DatabaseID.EXP_ID, reward_exp, floor);
		long exp_bonus_ratio = pot_bonus_ratio;
		
		gold_bonus += reward_gold_bonus_from_pot;
		exp_bonus += reward_exp_bonus_from_pot ;
		
		//----------------FOR EVENT BONUS GOLD/EXP----------------------------
		int event_gold_bonus_ratio = 0;
		int event_exp_bonus_ratio = 0;
		
		if (type == DatabaseID.ORDER_DAILY)
		{
			if (SkyGarden._server_config.containsKey(KeyID.KEY_BONUS_ORDER_DAILY))
			{
				int bonus_percent = SkyGarden._server_config.get(KeyID.KEY_BONUS_ORDER_DAILY);
				LogHelper.Log("Order daily bonus percent := " + bonus_percent);
				
				exp_bonus += (bonus_percent * reward_exp/100);
				event_exp_bonus_ratio = bonus_percent;
				
				gold_bonus += (bonus_percent * reward_gold/100);
				event_gold_bonus_ratio = bonus_percent;
			}
			
			// compute other bonus
			exp_bonus += (bonus_daily_order * reward_exp/100);
			event_exp_bonus_ratio += bonus_daily_order;

			gold_bonus += (bonus_daily_order * reward_gold/100);
			event_gold_bonus_ratio += bonus_daily_order;
		}
		else if (type == DatabaseID.ORDER_NORMAL)
		{
			if (SkyGarden._server_config.containsKey(KeyID.KEY_BONUS_ORDER_NORMAL))
			{
				int bonus_percent = SkyGarden._server_config.get(KeyID.KEY_BONUS_ORDER_NORMAL);
				LogHelper.Log("Order normal bonus percent := " + bonus_percent);
				
				exp_bonus += (bonus_percent * reward_exp/100);
				event_exp_bonus_ratio = bonus_percent;
				
				gold_bonus += (bonus_percent * reward_gold/100);
				event_gold_bonus_ratio = bonus_percent;
			}
			
			// compute other bonus
			exp_bonus += (bonus_order_normal * reward_exp/100);
			event_exp_bonus_ratio += bonus_order_normal;

			gold_bonus += (bonus_order_normal * reward_gold/100);
			event_gold_bonus_ratio += bonus_order_normal;
		}
		//---------------------------------------------------------------------

		reward_gold_bonus = (long)Math.round(gold_bonus);
		reward_exp_bonus = (long)Math.round(exp_bonus);
		
		_order.setRewardGoldRatioFromPot(gold_bonus_ratio);
		_order.setRewardExpRatioFromPot(exp_bonus_ratio);
		_order.setRewardGoldRatioFromMachine(machine_gold_bonus_ratio);
		_order.setRewardExpRatioFromMachine(machine_exp_bonus_ratio);
		
		_order.setRewardGoldRatioFromEvent(event_gold_bonus_ratio);
		_order.setRewardExpRatioFromEvent(event_exp_bonus_ratio);
//		LogHelper.Log("order type = " + type + ", exp rate = " + event_exp_bonus_ratio + ", gold rate = " + event_gold_bonus_ratio);
		
		_order.setRewardGold(reward_gold);
		_order.setRewardGoldBonus(reward_gold_bonus);
		_order.setRewardExp(reward_exp);
		_order.setRewardExpBonus(reward_exp_bonus);
		if (is_order_bug_pearl && type != DatabaseID.ORDER_DAILY) _order.setNPC(DatabaseID.NPC_TINKER_BELL_ID);
		
		_order.displayDataPackage();
		
		order.add(_order);
	}

	public void createOrder(int type, 
			int index, 
			HashMap<String, Short> products, 
			HashMap<String, Short> items, 
			List<Machine> machine, 
			List<Floor> floor, 
			int user_level, 
			boolean is_order_bug_pearl,
			int bonus_daily_order,
			int bonus_order_normal)
	{
		long reward_gold = 0;
		long reward_gold_bonus = 0;
		long reward_exp = 0;
		long reward_exp_bonus = 0;
		double gold_bonus = 0;
		double exp_bonus = 0;

		if (floor.size() > 9 || machine.size() > 9)
		{
			LogHelper.Log("NEED CHECK LOGIN FUNCTION!!! " + floor.size() + " " + machine.size());
		}

		if (index == DatabaseID.ORDER_DAILY_INDEX)
		{
			type =  DatabaseID.ORDER_DAILY;
		}
		
		Order _order = new Order(type);
		// _order.setRewardGold(reward_gold);
		// _order.setRewardExp(reward_exp);
		// _order.setDeliveryTime(delivery_time);
		_order.setNewWaitTime(0);
		
		if (type == DatabaseID.ORDER_DAILY && this.total_delivered_num < this.order_daily_free_max)
		{
			_order.setReceiveDailyOrder(true);
		}

		if (items != null)
		{
			for (Entry<String, Short> item: items.entrySet())
			{
				String key = item.getKey();
				String[] values = key.split("_");
				
				int item_type = Integer.parseInt(values[0]);
				int item_id = Integer.parseInt(values[1]);
				int item_num = item.getValue();

				_order.addRewardItem(item_type, item_id, item_num);
			}
		}
		
		// gold, exp bonus of machine
		long machine_gold_bonus_ratio = 0;
		long machine_exp_bonus_ratio = 0;

		for (int i = 0; i < machine.size(); i++)
		{
			Machine _machine = machine.get(i);
			int _machine_level = _machine.getLevel();
			
			int g = (int)Server.s_globalMachineUnlockData[i][_machine_level][DatabaseID.MACHINE_GOLD_ORDER];
			int e = (int)Server.s_globalMachineUnlockData[i][_machine_level][DatabaseID.MACHINE_EXP_ORDER];
			
			machine_gold_bonus_ratio += g;
			machine_exp_bonus_ratio += e;
		}
		
		for (Entry<String, Short> product: products.entrySet())
		{
			String key = product.getKey();
			String[] values = key.split("_");
			
			int prod_type = Integer.parseInt(values[0]);
			int prod_id = Integer.parseInt(values[1]);
			int prod_num = product.getValue();
			
			if (prod_type == DatabaseID.IT_BUG) {
				if (user_level < 17) {
					prod_id = 0;
				}
			}

			if (prod_type == DatabaseID.IT_PLANT && type != DatabaseID.ORDER_DAILY && isOrderPlant == false)
			{
				prod_num = prod_num * (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_CONSTANT][user_level][DatabaseID.CONSTANT_ORDER_ITEM_PLANT_PLUS]);
			}
			
			_order.addProductToStock(prod_type, prod_id, prod_num);
			
			boolean is_DO_paid = false;
			if (type == DatabaseID.ORDER_DAILY && total_delivered_num >= order_daily_free_max && total_delivered_num > 0)
			{
				is_DO_paid = true;
			}

			long prod_gold = Misc.getGoldXPCoefficientEstimate(type, DatabaseID.GOLD_ID, prod_type, prod_id, prod_num, is_DO_paid, user_level);
			long prod_exp = Misc.getGoldXPCoefficientEstimate(type, DatabaseID.EXP_ID, prod_type, prod_id, prod_num, is_DO_paid, user_level);
			
			/*
			if (prod_type == DatabaseID.IT_PRODUCT)
			{
				// if (Misc.isInPearlList(prod_id))
				// {
					//// do not increase exp bonus follow design
					//// reward_exp_bonus += getPotBonus(DatabaseID.EXP_ID, prod_exp, floor);
				// }
				// else
				{
					// reward_gold_bonus += getProductGoldExpBonus(DatabaseID.GOLD_ID, prod_id, prod_gold, machine, floor.size());
					// reward_exp_bonus += getProductGoldExpBonus(DatabaseID.EXP_ID, prod_id, prod_exp, machine, floor.size());
					
					gold_bonus += ((double)(prod_gold * machine_gold_bonus_ratio) / 100);
					exp_bonus += ((double)(prod_exp * machine_exp_bonus_ratio) / 100);
				}
			}
			*/
			
			/*
			if (prod_type == DatabaseID.IT_PLANT || prod_type == DatabaseID.IT_BUG)
			{
				// temporary rem this, do not increase pot bonus for plant order
				// reward_gold_bonus += getPotBonus(DatabaseID.GOLD_ID, prod_gold, floor);
				
				// exp_bonus += getPotBonus(DatabaseID.EXP_ID, prod_type, prod_exp, floor);
			}
			*/

			reward_gold += prod_gold;
			reward_exp += prod_exp;
		}
		
		if (reward_gold == 0) reward_gold = 1;
		if (reward_exp == 0) reward_exp = 1;
		
		// machine bonus
		gold_bonus += ((double)(reward_gold * machine_gold_bonus_ratio) / 100);
		exp_bonus += ((double)(reward_exp * machine_exp_bonus_ratio) / 100);
		
		// pot bonus
		long reward_gold_bonus_from_pot = getPotGoldXPBonus(DatabaseID.GOLD_ID, reward_gold, floor);
		long gold_bonus_ratio = pot_bonus_ratio;
		
		long reward_exp_bonus_from_pot = getPotGoldXPBonus(DatabaseID.EXP_ID, reward_exp, floor);
		long exp_bonus_ratio = pot_bonus_ratio;
		
		gold_bonus += reward_gold_bonus_from_pot;
		exp_bonus += reward_exp_bonus_from_pot;
		
		//----------------FOR EVENT BONUS GOLD/EXP----------------------------
		int event_gold_bonus_ratio = 0;
		int event_exp_bonus_ratio = 0;
		
		if (type == DatabaseID.ORDER_DAILY)
		{
			if (SkyGarden._server_config.containsKey(KeyID.KEY_BONUS_ORDER_DAILY))
			{
				int bonus_percent = SkyGarden._server_config.get(KeyID.KEY_BONUS_ORDER_DAILY);
				LogHelper.Log("Order daily bonus percent := " + bonus_percent);
				
				exp_bonus += (bonus_percent * reward_exp/100);
				event_exp_bonus_ratio = bonus_percent;
				
				gold_bonus += (bonus_percent * reward_gold/100);
				event_gold_bonus_ratio = bonus_percent;
			}
			
			// compute other bonus
			exp_bonus += (bonus_daily_order * reward_exp/100);
			event_exp_bonus_ratio += bonus_daily_order;

			gold_bonus += (bonus_daily_order * reward_gold/100);
			event_gold_bonus_ratio += bonus_daily_order;
		}
		else if (type == DatabaseID.ORDER_NORMAL)
		{
			if (SkyGarden._server_config.containsKey(KeyID.KEY_BONUS_ORDER_NORMAL))
			{
				int bonus_percent = SkyGarden._server_config.get(KeyID.KEY_BONUS_ORDER_NORMAL);
				LogHelper.Log("Order normal bonus percent := " + bonus_percent);
				
				exp_bonus += (bonus_percent * reward_exp/100);
				event_exp_bonus_ratio = bonus_percent;
				
				gold_bonus += (bonus_percent * reward_gold/100);
				event_gold_bonus_ratio = bonus_percent;
			}
			
			// compute other bonus
			exp_bonus += (bonus_order_normal * reward_exp/100);
			event_exp_bonus_ratio += bonus_order_normal;

			gold_bonus += (bonus_order_normal * reward_gold/100);
			event_gold_bonus_ratio += bonus_order_normal;
		}
		//---------------------------------------------------------------------

		reward_gold_bonus = (long)Math.round(gold_bonus);
		reward_exp_bonus = (long)Math.round(exp_bonus);
		
		_order.setRewardGoldRatioFromPot(gold_bonus_ratio);
		_order.setRewardExpRatioFromPot(exp_bonus_ratio);
		_order.setRewardGoldRatioFromMachine(machine_gold_bonus_ratio);
		_order.setRewardExpRatioFromMachine(machine_exp_bonus_ratio);

		_order.setRewardGoldRatioFromEvent(event_gold_bonus_ratio);
		_order.setRewardExpRatioFromEvent(event_exp_bonus_ratio);
//		LogHelper.Log("order type = " + type + ", exp rate = " + event_exp_bonus_ratio + ", gold rate = " + event_gold_bonus_ratio);
		
		// double a = 14.49;
		// long x = (long)Math.round(a); 	// round
		// long y = (long)Math.floor(a); 	// round down
		// long z = (long)Math.ceil(a);	// round up

		_order.setRewardGold(reward_gold);
		_order.setRewardGoldBonus(reward_gold_bonus);
		_order.setRewardExp(reward_exp);
		_order.setRewardExpBonus(reward_exp_bonus);
		if (is_order_bug_pearl && type != DatabaseID.ORDER_DAILY) _order.setNPC(DatabaseID.NPC_TINKER_BELL_ID);
		
		_order.displayDataPackage();
		
		order.remove(index);
		order.add(index, _order);
	}
	
	public void deleteOrderEvent() {
		_order_event = null;
	}
	
	public void createOrderEvent(List<Machine> machine, List<Floor> floor, int user_level)
	{
//		_order_event = null;
//		if (true) return;
		
		long reward_gold = 0;
		long reward_gold_bonus = 0;
		long reward_exp = 0;
		long reward_exp_bonus = 0;
		double gold_bonus = 0;
		double exp_bonus = 0;

		if (floor.size() > 9 || machine.size() > 9)
		{
			LogHelper.Log("NEED CHECK LOGIN FUNCTION!!! " + floor.size() + " " + machine.size());
		}
		
		Order _order = new Order(DatabaseID.ORDER_EVENT);
		_order.setNewWaitTime(0);
		
		// gold, exp bonus of machine
		long machine_gold_bonus_ratio = 0;
		long machine_exp_bonus_ratio = 0;

		for (int i = 0; i < machine.size(); i++)
		{
			Machine _machine = machine.get(i);
			int _machine_level = _machine.getLevel();
			
			int g = (int)Server.s_globalMachineUnlockData[i][_machine_level][DatabaseID.MACHINE_GOLD_ORDER];
			int e = (int)Server.s_globalMachineUnlockData[i][_machine_level][DatabaseID.MACHINE_EXP_ORDER];
			
			machine_gold_bonus_ratio += g;
			machine_exp_bonus_ratio += e;
		}
		
		String require_items = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_ORDER_EVENT][user_level][DatabaseID.ORDER_EVENT_REQUIRE_ITEMS]);

		String[] aos = require_items.split(":");
		for (int i = 0; i < aos.length - 2; i+=3)
		{
			int type = Integer.parseInt(aos[i]);
			int id = Integer.parseInt(aos[i+1]);
			int num = Integer.parseInt(aos[i+2]);
			
			if (type == DatabaseID.IT_MATERIAL)
			{
				if (id == DatabaseID.MATERIAL_ITEM_EVENT_XMAS_2014_1 || 
					id == DatabaseID.MATERIAL_ITEM_EVENT_XMAS_2014_2 || 
					id == DatabaseID.MATERIAL_ITEM_EVENT_XMAS_2014_3)
				{

					switch (id)
					{
						case DatabaseID.MATERIAL_ITEM_EVENT_XMAS_2014_1:
							id = DatabaseID.ITEM_EVENT_XMAS_2014_1;
							type = DatabaseID.IT_EVENT;
							break;
						case DatabaseID.MATERIAL_ITEM_EVENT_XMAS_2014_2:
							id = DatabaseID.ITEM_EVENT_XMAS_2014_2;
							type = DatabaseID.IT_EVENT;
							break;
						case DatabaseID.MATERIAL_ITEM_EVENT_XMAS_2014_3:
							id = DatabaseID.ITEM_EVENT_XMAS_2014_3;
							type = DatabaseID.IT_EVENT;
							break;
					}
				}
			}
//			LogHelper.Log("required items := " + type + ":" + id + ":" + num);
			_order.addProductToStock(type, id, num);
		}
		
		reward_gold = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ORDER_EVENT][user_level][DatabaseID.ORDER_EVENT_BASIC_GOLD]);
		reward_exp = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_ORDER_EVENT][user_level][DatabaseID.ORDER_EVENT_BASIC_EXP]);
		
		if (reward_gold == 0) reward_gold = 1;
		if (reward_exp == 0) reward_exp = 1;
		
		// machine bonus
		gold_bonus += ((double)(reward_gold * machine_gold_bonus_ratio) / 100);
		exp_bonus += ((double)(reward_exp * machine_exp_bonus_ratio) / 100);
		
		// pot bonus
		long reward_gold_bonus_from_pot = getPotGoldXPBonus(DatabaseID.GOLD_ID, reward_gold, floor);
		long gold_bonus_ratio = pot_bonus_ratio;
		
		long reward_exp_bonus_from_pot = getPotGoldXPBonus(DatabaseID.EXP_ID, reward_exp, floor);
		long exp_bonus_ratio = pot_bonus_ratio;
		
		gold_bonus += reward_gold_bonus_from_pot;
		exp_bonus += reward_exp_bonus_from_pot;
		
		//----------------FOR EVENT BONUS GOLD/EXP----------------------------
		//EXP
		int event_gold_bonus_ratio = 0;
		int event_exp_bonus_ratio = 0;

		reward_gold_bonus = (long)Math.round(gold_bonus);
		reward_exp_bonus = (long)Math.round(exp_bonus);
		
		_order.setRewardGoldRatioFromPot(gold_bonus_ratio);
		_order.setRewardExpRatioFromPot(exp_bonus_ratio);
		_order.setRewardGoldRatioFromMachine(machine_gold_bonus_ratio);
		_order.setRewardExpRatioFromMachine(machine_exp_bonus_ratio);

		_order.setRewardGoldRatioFromEvent(event_gold_bonus_ratio);
		_order.setRewardExpRatioFromEvent(event_exp_bonus_ratio);

		_order.setRewardGold(reward_gold);
		_order.setRewardGoldBonus(reward_gold_bonus);
		_order.setRewardExp(reward_exp);
		_order.setRewardExpBonus(reward_exp_bonus);
		
////		order.remove(DatabaseID.ORDER_EVENT_SLOT_INDEX);
//		order.add(DatabaseID.ORDER_EVENT_SLOT_INDEX, _order);
//		LogHelper.Log("Added order event to order manager, size := " + order.size());
		_order_event = _order;
		LogHelper.LogHappy("###Create new order event");
	}
	
	public Order getOrderEvent() {
		return _order_event;
	}
	
	/*
	private double getPotBonus(int type, int prod_type, long prod_gold_exp, List<Floor> floor)
	{
		double bonus_value = 0;	// gold, exp
		int bonus_rate = 0;	// %
		
		for (int i = 0; i < floor.size(); i++)
		{
			
			for (int j = 0; j < DatabaseID.MAX_SLOT_PER_FLOOR; j++)
			{
				int pot_id = floor.get(i).slot[j].pot.getID();
				
				if (pot_id > 0)
				{
					int prod_type_index = (prod_type == DatabaseID.IT_PLANT ? DatabaseID.POT_ORDER_EXP_BONUS_PLANT : DatabaseID.POT_ORDER_EXP_BONUS_BUG);
					int gold_exp_type = (type == DatabaseID.GOLD_ID ? DatabaseID.POT_ORDER_GOLD_BONUS : prod_type_index);
					int pot_order_bonus = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][pot_id][gold_exp_type]);
					bonus_rate += pot_order_bonus;
				}
			}
		}
		
		if (bonus_rate > 0)
		{
			bonus_value = ((double)(prod_gold_exp * bonus_rate) / 100);
		}
		
		return bonus_value;
	}
	*/

	private long getPotGoldXPBonus(int type, long order_reward, List<Floor> floor)
	{
		long bonus_value = 0;	// gold, exp
		long bonus_rate = 0;	// %
		
		for (int i = 0; i < floor.size(); i++)
		{
			for (int j = 0; j < DatabaseID.MAX_SLOT_PER_FLOOR; j++)
			{
				int pot_id = floor.get(i).slot[j].pot.getID();
				
				if (pot_id > 0)
				{
					int gold_exp_type = (type == DatabaseID.GOLD_ID ? DatabaseID.POT_ORDER_GOLD_BONUS : DatabaseID.POT_ORDER_XP_BONUS);
					long pot_order_bonus = Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_POT][pot_id][gold_exp_type]);
					bonus_rate += pot_order_bonus;
				}
			}
		}
		
		if (bonus_rate > 0)
		{
			double a = (double)(order_reward * bonus_rate) / 100;
			long x = (long)Math.round(a); 	// round
			bonus_value = x;

			// bonus_value = Math.round((order_reward * bonus_rate) / 100);
		}
		
		pot_bonus_ratio = bonus_rate;
		
		return bonus_value;
	}

	/*
	private long getProductGoldExpBonus(int type, int prod_id, long prod_gold, List<Machine> machine, int user_floor)
	{
		int _floor = (int)Misc.PARSE_LONG(Server.s_globalDB[DatabaseID.SHEET_PRODUCT][prod_id][DatabaseID.PRODUCT_MACHINE_ID]);
		
		if (_floor >= user_floor)
		{
			LogHelper.Log("type = " + type + ", prod_id = " + prod_id); 
			LogHelper.Log("_floor = " + _floor + " " + user_floor); 
			LogHelper.Log("user hadn't unlock machine " + _floor);
			return 0;
		}
		
		Machine _machine = machine.get(_floor);
		
		int _machine_level = _machine.getLevel();
		
		// String s_gold_exp_bonus_ratio = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_MACHINE_UNLOCK][_floor][_machine_level]).split(":")[type == DatabaseID.GOLD_ID ? DatabaseID.MACHINE_GOLD_ORDER : DatabaseID.MACHINE_EXP_ORDER];
		// int prod_gold_exp_bonus_ratio = Integer.parseInt(s_gold_exp_bonus_ratio);
		int prod_gold_exp_bonus_ratio = (int)Server.s_globalMachineUnlockData[_floor][_machine_level][type == DatabaseID.GOLD_ID ? DatabaseID.MACHINE_GOLD_ORDER : DatabaseID.MACHINE_EXP_ORDER];
		
		long prod_gold_exp_bonus = (prod_gold * prod_gold_exp_bonus_ratio) / 100;
		// long prod_gold_exp_bonus = (long)Math.ceil(((prod_gold * prod_gold_exp_bonus_ratio) / 100));
		
		if (prod_gold_exp_bonus < 0)
		{
			LogHelper.Log("prod_gold_exp_bonus < 0, NEED RE-CHECK getProductGoldExpBonus()");
			LogHelper.Log(type + " " + prod_id + " " + prod_gold + " " + machine.size() + " " + prod_gold_exp_bonus);
			prod_gold_exp_bonus = 0;
		}
		
		return prod_gold_exp_bonus;
	}
	*/

	public void addRewards(long reward_gold, long reward_gold_bonus, long reward_exp, long reward_exp_bonus, ArrayList<byte[]> reward_items)
	{
		this.reward_package = true;
		
		reward.setRewardGold(reward_gold);
		reward.setRewardGoldBonus(reward_gold_bonus);
		reward.setRewardExp(reward_exp);
		reward.setRewardExpBonus(reward_exp_bonus);
		reward.setItems(reward_items);
	}
	
	public byte[] getData()
	{
		FBEncrypt encrypt = new FBEncrypt();

		encrypt.addByte(KeyID.KEY_ORDER_FREE_DELIVERED_NUM, this.order_daily_free_delivered_num);
		encrypt.addByte(KeyID.KEY_ORDER_PAID_DELIVERED_NUM, this.order_daily_paid_delivered_num);
		encrypt.addByte(KeyID.KEY_ORDER_DAILY_FREE_MAX, this.order_daily_free_max);
		encrypt.addByte(KeyID.KEY_ORDER_DAILY_PAID_MAX, this.order_daily_paid_max);
		encrypt.addByte(KeyID.KEY_TOTAL_DELIVERED_NUM, this.total_delivered_num);
		encrypt.addBoolean(KeyID.KEY_DAILY_ORDER_LETTER_SELECTED, this.order_daily_letter_selected);
		encrypt.addInt(KeyID.KEY_ORDER_DAILY_RESET_TIME, this.order_daily_reset_time);
		encrypt.addByte(KeyID.KEY_USER_ORDER_COUNT, this.order_count);
		
		encrypt.addBoolean(KeyID.KEY_ORDER_REWARD, this.reward_package);
		if (this.reward_package == true)
		{
			encrypt.addBinary(KeyID.KEY_USER_ORDER_REWARD, reward.getData());
		}
		
		for (int i = 0; i < this.order_count; i++)
		{
			encrypt.addBinary(KeyID.KEY_USER_ORDER + i, order.get(i).getData());
		}

		try
		{
//			if (order.size() > 8)
//			{
//				Order order_event = order.get(DatabaseID.ORDER_EVENT_SLOT_INDEX);
//				if (order_event != null && order_event.getType() == DatabaseID.ORDER_EVENT)
//				{
//					encrypt.addBoolean(KeyID.KEY_HAS_ORDER_EVENT, true);
//					encrypt.addBinary(KeyID.KEY_USER_ORDER + DatabaseID.ORDER_EVENT_SLOT_INDEX, order_event.getData());
//				}
//			}
			
			if (_order_event != null) {
				encrypt.addBoolean(KeyID.KEY_HAS_ORDER_EVENT, true);
//				encrypt.addBinary(KeyID.KEY_USER_ORDER + DatabaseID.ORDER_EVENT_SLOT_INDEX, order_event.getData());
				encrypt.addBinary(KeyID.KEY_ORDER_EVENT, _order_event.getData());
				LogHelper.LogHappy("###Add KEY_HAS_ORDER_EVENT");
			}
		}
		catch (Exception e)
		{
			LogHelper.LogException("OrderManager.getData", e);
		}
		
		return encrypt.toByteArray();
	}
	
	public void displayDataPackage()
	{
		if (true) return;
		
		LogHelper.Log("\nORDER_FREE_DELIVERED_NUM: " + this.order_daily_free_delivered_num);
		LogHelper.Log("ORDER_PAID_DELIVERED_NUM: " + this.order_daily_paid_delivered_num);
		LogHelper.Log("order_daily_paid_max: " + this.order_daily_paid_max);
		LogHelper.Log("order_daily_free_max: " + this.order_daily_free_max);
		LogHelper.Log("total_delivered_num: " + this.total_delivered_num);
		LogHelper.Log("daily_order_letter_selected: " + this.order_daily_letter_selected);
		LogHelper.Log("order_daily_reset_time: " + this.order_daily_reset_time);
		LogHelper.Log("ORDER_COUNT: " + this.order_count);
		LogHelper.Log("REWARD_PACKAGE: " + this.reward_package);
		
		if (this.reward_package == true)
		{
			LogHelper.Log("\n****************** REWARD");
			reward.displayDataPackage();
			LogHelper.Log("\n*************************");
		}
		
		for (int i = 0; i < this.order_count; i++)
		{
			LogHelper.Log("\n****************** ORDER " + i);
			order.get(i).displayDataPackage();
		}
		
		LogHelper.Log("-----------------------------\n");
	}
	
	public boolean isDailyOrderLimited()
	{
		return (total_delivered_num >= order_daily_free_max + order_daily_paid_max);
	}
	
	// -------------------------------------------------------------------------------------
	
	public short getOrderCount()
	{
		return this.order_count;
	}
	
	public boolean getRewardPackage()
	{
		return this.reward_package;
	}
	
	public void setRewardPackage(boolean _reward_package)
	{
		this.reward_package = _reward_package;
	}
	
	public Order getOrder(int index)
	{
		return order.get(index);
	}
	
	public byte getTotalDeliveredNum()
	{
		return this.total_delivered_num;
	}
	
	public void setTotalDeliveredNum(int total_delivered_num)
	{
		this.total_delivered_num = (byte)total_delivered_num;
	}
	
	public int getOrderDailyResetTime()
	{
		return this.order_daily_reset_time;
	}
	
	public void setOrderDailyResetTime(int order_daily_reset_time)
	{
		this.order_daily_reset_time = order_daily_reset_time;
	}
	
	public byte getOrderDailyFreeMax()
	{
		return this.order_daily_free_max;
	}
	
	public void setOrderDailyFreeMax(int order_daily_free_max)
	{
		this.order_daily_free_max = (byte)order_daily_free_max;
	}
	
	public byte getOrderDailyPaidMax()
	{
		return this.order_daily_paid_max;
	}
	
	public void setOrderDailyPaidMax(int order_daily_paid_max)
	{
		this.order_daily_paid_max = (byte)order_daily_paid_max;
	}
	
	public byte getOrderDailyFreeDeliveredNum()
	{
		return this.order_daily_free_delivered_num;
	}
	
	public void setOrderDailyFreeDeliveredNum(int order_daily_free_delivered_num)
	{
		this.order_daily_free_delivered_num = (byte)order_daily_free_delivered_num;
	}
	
	public byte getOrderDailyPaidDeliveredNum()
	{
		return this.order_daily_paid_delivered_num;
	}
	
	public void setOrderDailyPaidDeliveredNum(int order_daily_paid_delivered_num)
	{
		this.order_daily_paid_delivered_num = (byte)order_daily_paid_delivered_num;
	}
	
	public boolean getOrderDailyLetterSelected()
	{
		return this.order_daily_letter_selected;
	}
	
	public void setOrderDailyLetterSelected(boolean order_daily_letter_selected)
	{
		this.order_daily_letter_selected = order_daily_letter_selected;
	}
	
	public void LogOrderManager()
	{
		StringBuilder log = new StringBuilder();
		log.append("order_manager");
		log.append('\t').append(this.order_daily_free_delivered_num);
		log.append('\t').append(this.order_daily_paid_delivered_num);
		log.append('\t').append(this.order_daily_free_max);
		log.append('\t').append(this.order_daily_paid_max);
		log.append('\t').append(this.total_delivered_num);
		log.append('\t').append(this.order_daily_letter_selected);
		log.append('\t').append(this.order_daily_reset_time);
		log.append('\t').append(this.order_count);
		
		for (int i = 0; i < order_count; i++)
		{
			log.append("\torder" + i + '\t');
			order.get(i).LogOrder();
		}
		
		if (this.reward_package == true)
		{
			log.append('\t');
			reward.LogOrderReward();
		}
		
		LogHelper.Log(log.toString());
	}
	
	public void Clear()
	{
		order.clear();
		order_count = 0;
	}
}