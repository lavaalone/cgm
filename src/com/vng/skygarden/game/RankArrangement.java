package com.vng.skygarden.game;

import com.vng.netty.*;
import com.vng.util.*;
import com.vng.log.*;
import com.vng.skygarden.DBConnector;
import com.vng.skygarden._gen_.ProjectConfig;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.poi.hssf.extractor.ExcelExtractor;

public class RankArrangement {
	private int _active_ranking_idx = -1;
	private int _previous_ranking_idx = -1;
	private int _active_ranking = -1; // running ranking
	private long _start_time = 0;
	private long _end_time = 0;
	private String _category = "";
	private int _rank_size = 100; // default rank size is 100
	private String _gift_rank_1 = "";
	private String _gift_rank_2 = "";
	private String _gift_rank_3 = "";
	private String _gift_rank_100 = "";
	
	
	private Timer _deliver_award = new Timer("DeliverAward");
	boolean _run_once = true;
	
	// harvest
	private ConcurrentHashMap<String, String> _harvest = new ConcurrentHashMap<String, String>();
	private List<TreeMap<String, String>> _harvest_result = new ArrayList<TreeMap<String, String>>();
	private long _harvest_floor_value = 0;
	private int _harvest_result_idx = 0;
	
	// collect product
	private ConcurrentHashMap<String, String> _machine = new ConcurrentHashMap<String, String>();
	private List<TreeMap<String, String>> _machine_result = new ArrayList<TreeMap<String, String>>();
	private long _machine_floor_value = 0;
	private int _machine_result_idx = 0;
	
	// catch bug
	private ConcurrentHashMap<String, String> _catch_bug = new ConcurrentHashMap<String, String>();
	private List<TreeMap<String, String>> _catch_bug_result = new ArrayList<TreeMap<String, String>>();
	private long _catch_bug_floor_value = 0;
	private int _catch_bug_result_idx = 0;
	
	// garden appraisal
	private ConcurrentHashMap<String, String> _garden_appraisal = new ConcurrentHashMap<String, String>();
	private List<TreeMap<String, String>> _garden_appraisal_result = new ArrayList<TreeMap<String, String>>();
	private long _garden_appraisal_floor_value = 0;
	private int _garden_appraisal_result_idx = 0;
	
	// deliver order
	private ConcurrentHashMap<String, String> _deliver_order = new ConcurrentHashMap<String, String>();
	private List<TreeMap<String, String>> _deliver_order_result = new ArrayList<TreeMap<String, String>>();
	private long _deliver_order_floor_value = 0;
	private int _deliver_order_result_idx = 0;
	
	public RankArrangement() {
//		// cheat delete all data
//		for (int i = 0; i < 20; i++) {
//			try {
//				LogHelper.Log("Delete ranking_" + i + ": " + DBConnector.GetMembaseServerForGeneralData().Delete("ranking" + "_" + i));
//				LogHelper.Log("Delete rannking_floor_value_" + i + ": " + DBConnector.GetMembaseServerForTemporaryData().Delete("rannking_floor_value" + "_" + i));
//				LogHelper.Log("Delete rannking_floor_value_" + i + ": " + DBConnector.GetMembaseServerForTemporaryData().Delete("ranking_result" + "_" + i));
//			} catch (Exception e) {
//				LogHelper.LogException("cheat", e);
//			}
//		}
//		if (true) return;
		if (ProjectConfig.IS_SERVER_RANKING != 1 && ProjectConfig.IS_SERVER_RANKING_FREESTYLE != 1) {
			return;
		}
		
		if (_deliver_award == null) {
			_deliver_award = new Timer("DeliverAward");
		}
		
		Timer t = new Timer("Sort");
		t.schedule(new TimerTask() 
		{
			@Override
			public void run() {
				try {
					LoadActiveRankingInfo(); 
					Sort();
				} catch (Exception e) {
					LogHelper.LogException("Sort", e);
				}
			}
		}, DatabaseID.SORT_INTERVAL * 1000, DatabaseID.SORT_INTERVAL * 1000);
		
		Timer tc = new Timer("Clean");
		tc.schedule(new TimerTask() 
		{
			@Override
			public void run() {
				try {
					Clean();
				} catch (Exception e) {
					LogHelper.LogException("Sort", e);
				}
			}
		}, 10 * DatabaseID.SORT_INTERVAL * 1000, 10 * DatabaseID.SORT_INTERVAL * 1000);
	}
	
	/**
	 * Add the target to the list, waiting to sort
	 * @param ranking_command : base on this value, add the target to the right list
	 * @param user_id
	 * @param value 
	 */
	public void Add(int ranking_command, String user_id, long value) {
		LogHelper.Log("RankArrangement: receive ranking command = " + ranking_command + ", user id = " + user_id + ", value = " + value);
		switch (ranking_command) {
			case CommandID.CMD_HARVEST:
			{
				// the new value is always larger than the old one of a same user (control by RankingTarget)
				if (value >= _harvest_floor_value) {
					if (_harvest.containsKey(user_id)) {
						_harvest.remove(user_id);
					}
					_harvest.put(user_id, value + ":" + System.currentTimeMillis());
				}
				break;
			}
			case CommandID.CMD_CATCH_BUG:
			{
				// the new value is always larger than the old one of a same user (control by RankingTarget)
				if (value >= _catch_bug_floor_value) {
					if (_catch_bug.containsKey(user_id)) {
						_catch_bug.remove(user_id);
					}
					_catch_bug.put(user_id, value + ":" + System.currentTimeMillis());
				}
				break;
			}
			case CommandID.CMD_MOVE_MACHINE_PRODUCT:
			{
				// the new value is always larger than the old one of a same user (control by RankingTarget)
				if (value >= _machine_floor_value) {
					if (_machine.containsKey(user_id)) {
						_machine.remove(user_id);
					}
					_machine.put(user_id, value + ":" + System.currentTimeMillis());
				}
				break;
			}
			case CommandID.CMD_UPDATE_GARDEN_APPRAISAL:
			{
				// the new value is always larger than the old one of a same user (control by RankingTarget)
				if (value >= _garden_appraisal_floor_value) {
					if (_garden_appraisal.containsKey(user_id)) {
						_garden_appraisal.remove(user_id);
					}
					_garden_appraisal.put(user_id, value + ":" + System.currentTimeMillis());
				}
				break;
			}
			case CommandID.CMD_DELIVERY_ORDER:
			{
				// the new value is always larger than the old one of a same user (control by RankingTarget)
				if (value >= _deliver_order_floor_value) {
					if (_deliver_order.containsKey(user_id)) {
						_deliver_order.remove(user_id);
					}
					_deliver_order.put(user_id, value + ":" + System.currentTimeMillis());
				}
				break;
			}
			default:
				break;
		}
	}

	/**
	 * Read/Refresh all current ranking info, like:
	 * - sort interval
	 * - sort list
	 * - sort target
	 */
	private void LoadActiveRankingInfo() {
		_active_ranking = _active_ranking_idx = -1;
		for (int i = 0; i < 40 ;i++) {
			String s = "";
			try {
				s = (String)DBConnector.GetMembaseServerForGeneralData().Get("ranking" + "_" + i);
			} catch (Exception e) {
				s = "";
			}
			
			if (s == null || s.equals("")) {
				break;
			} else {
//				LogHelper.Log("Ranking info [" + i + "], content = " + s);
				String[] sa = s.split(";");
				int command_id = Integer.parseInt(sa[0].split("_")[0]);
				long start_time = Long.parseLong(sa[2]);
				long end_time = Long.parseLong(sa[3]);
				if (System.currentTimeMillis() >= start_time && System.currentTimeMillis() <= end_time) {
					_run_once = true;
					_active_ranking_idx = i;
					_active_ranking = command_id;
					_end_time = end_time;
					_category = sa[5];
					_gift_rank_1 = sa[9];
					_gift_rank_2 = sa[10];
					_gift_rank_3 = sa[11];
					_gift_rank_100 = sa[12];
					_rank_size = Integer.parseInt(sa[14]);
					LogHelper.Log("RankingManager.. found active ranking = " + _active_ranking + ", ranking idx = " + _active_ranking_idx + ", rank size = " + _rank_size);
					break;
				}
			}
		}
		
		if (_active_ranking == -1) {
			LogHelper.Log("RankingManager.. err! no ranking is active!");
			return;
		}
		
		boolean new_season = _previous_ranking_idx != _active_ranking_idx;
		if (new_season) {
			LogHelper.Log("LoadActiveRankingInfo.. reset all value as there is new ranking season, previous idx = " + _previous_ranking_idx + ", new idx = " + _active_ranking_idx);
			_previous_ranking_idx = _active_ranking_idx;
			
			_harvest.clear();
			_machine.clear();
			_catch_bug.clear();
			_garden_appraisal.clear();
			_deliver_order.clear();
			
			_harvest_floor_value = 0;
			_machine_floor_value = 0;
			_catch_bug_floor_value = 0;
			_garden_appraisal_floor_value = 0;
			_deliver_order_floor_value = 0;
			
			_harvest_result_idx = 0;
			_machine_result_idx = 0;
			_catch_bug_result_idx = 0;
			_garden_appraisal_result_idx = 0;
			_deliver_order_result_idx = 0;
		}
	}
	
	/**
	 * Sort the list base on ranking info
	 * @throws Exception 
	 */
	private void Sort() throws Exception {
		if (_active_ranking == -1 || _active_ranking_idx == -1 || System.currentTimeMillis() > _end_time) {
			LogHelper.Log("RankingManager.. no ranking is active, canceled sort");
			return;
		}
		
		LogHelper.Log("Size of harvest: " + _harvest.size());
		LogHelper.Log("Size of machine: " + _machine.size());
		LogHelper.Log("Size of catch bug: " + _catch_bug.size());
		LogHelper.Log("Size of garden appraisal: " + _garden_appraisal.size());
		LogHelper.Log("Size of deliver order: " + _deliver_order.size());
		
		switch (_active_ranking) {
			case CommandID.CMD_HARVEST:
			{
				TreeMap<String, String> tmp_sort = new TreeMap<String, String>(new ValueComparatorDescending(_harvest));
				TreeMap<String, String> result = new TreeMap<String, String>(new ValueComparatorAscending(_harvest));
				
				// sort all the list harvest
				tmp_sort.putAll(_harvest);
				
				// get the first RANKING_SIZE ranks only
				for (Map.Entry<String, String> e : tmp_sort.entrySet()) {
					if (result.size() < _rank_size) {
						result.put(e.getKey(), e.getValue());
					}
				}
				
				// update floor value
				if (result.size() < _rank_size) {
					_harvest_floor_value = 0;
				} else {
					_harvest_floor_value = Long.parseLong(result.firstEntry().getValue().split(":")[0]);
				}
				
				
				// update _floor_value to db
				DBConnector.GetMembaseServerForTemporaryData().Set("rannking_floor_value" + "_" + _active_ranking_idx, _harvest_floor_value);
				
				// maintain the result list
				if (_harvest_result_idx == 0) {
					// in case the list is empty
					if (_harvest_result.size() > 0) {
						_harvest_result.remove(0);
					}
					
					_harvest_result.add(0, result);
					_harvest_result_idx++;
				} else {
					// in case the list is empty
					if (_harvest_result.size() > 1) {
						_harvest_result.remove(1);
					}
					
					_harvest_result.add(1, result);
					_harvest_result_idx--;
				}
				
				// analize ans save sort result to base
				if (_active_ranking != -1 && _active_ranking_idx != -1) {
					if (_harvest_result.size() == 2) {
						if (_harvest_result_idx == 0) {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx, _rank_size,  _harvest_result.get(0), _harvest_result.get(1));
							rra.AnalyzeAndSave();
						} else {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx,_rank_size,  _harvest_result.get(1), _harvest_result.get(0));
							rra.AnalyzeAndSave();
						}
					}
				}
				break;
			}
			case CommandID.CMD_CATCH_BUG:
			{
				TreeMap<String, String> tmp_sort = new TreeMap<String, String>(new ValueComparatorDescending(_catch_bug));
				TreeMap<String, String> result = new TreeMap<String, String>(new ValueComparatorAscending(_catch_bug));
				
				// sort all the list harvest
				tmp_sort.putAll(_catch_bug);
				
				// get the first RANKING_SIZE ranks only
				for (Map.Entry<String, String> e : tmp_sort.entrySet()) {
					if (result.size() < _rank_size) {
						result.put(e.getKey(), e.getValue());
					}
				}
				
				// update floor value
				if (result.size() < _rank_size) {
					_catch_bug_floor_value = 0;
				} else {
					_catch_bug_floor_value = Long.parseLong(result.firstEntry().getValue().split(":")[0]);
				}
				
				// update _floor_value to db
				DBConnector.GetMembaseServerForTemporaryData().Set("rannking_floor_value" + "_" + _active_ranking_idx, _catch_bug_floor_value);
				
				// maintain the result list
				if (_catch_bug_result_idx == 0) {
					// in case the list is empty
					if (_catch_bug_result.size() > 0) {
						_catch_bug_result.remove(0);
					}
					
					_catch_bug_result.add(0, result);
					_catch_bug_result_idx++;
				} else {
					// in case the list is empty
					if (_catch_bug_result.size() > 1) {
						_catch_bug_result.remove(1);
					}
					
					_catch_bug_result.add(1, result);
					_catch_bug_result_idx--;
				}
				
				// analize ans save sort result to base
				if (_active_ranking != -1 && _active_ranking_idx != -1) {
					if (_catch_bug_result.size() == 2) {
						if (_catch_bug_result_idx == 0) {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx, _rank_size,  _catch_bug_result.get(0), _catch_bug_result.get(1));
							rra.AnalyzeAndSave();
						} else {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx,_rank_size,  _catch_bug_result.get(1), _catch_bug_result.get(0));
							rra.AnalyzeAndSave();
						}
					}
				}
				break;
			}
			case CommandID.CMD_MOVE_MACHINE_PRODUCT:
			{
				TreeMap<String, String> tmp_sort = new TreeMap<String, String>(new ValueComparatorDescending(_machine));
				TreeMap<String, String> result = new TreeMap<String, String>(new ValueComparatorAscending(_machine));
				
				// sort all the list harvest
				tmp_sort.putAll(_machine);
				
				// get the first RANKING_SIZE ranks only
				for (Map.Entry<String, String> e : tmp_sort.entrySet()) {
					if (result.size() < _rank_size) {
						result.put(e.getKey(), e.getValue());
					}
				}
				
				// update floor value
				if (result.size() < _rank_size) {
					_machine_floor_value = 0;
				} else {
					_machine_floor_value = Long.parseLong(result.firstEntry().getValue().split(":")[0]);
				}
				
				// update _floor_value to db
				DBConnector.GetMembaseServerForTemporaryData().Set("rannking_floor_value" + "_" + _active_ranking_idx, _machine_floor_value);
				
				// maintain the result list
				if (_machine_result_idx == 0) {
					// in case the list is empty
					if (_machine_result.size() > 0) {
						_machine_result.remove(0);
					}
					
					_machine_result.add(0, result);
					_machine_result_idx++;
				} else {
					// in case the list is empty
					if (_machine_result.size() > 1) {
						_machine_result.remove(1);
					}
					
					_machine_result.add(1, result);
					_machine_result_idx--;
				}
				
				// analize ans save sort result to base
				if (_active_ranking != -1 && _active_ranking_idx != -1) {
					if (_machine_result.size() == 2) {
						if (_machine_result_idx == 0) {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx, _rank_size,  _machine_result.get(0), _machine_result.get(1));
							rra.AnalyzeAndSave();
						} else {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx,_rank_size,  _machine_result.get(1), _machine_result.get(0));
							rra.AnalyzeAndSave();
						}
					}
				}
				break;
			}
			case CommandID.CMD_UPDATE_GARDEN_APPRAISAL:
			{
				TreeMap<String, String> tmp_sort = new TreeMap<String, String>(new ValueComparatorDescending(_garden_appraisal));
				TreeMap<String, String> result = new TreeMap<String, String>(new ValueComparatorAscending(_garden_appraisal));
				
				// sort all the list harvest
				tmp_sort.putAll(_garden_appraisal);
				
				// get the first RANKING_SIZE ranks only
				for (Map.Entry<String, String> e : tmp_sort.entrySet()) {
					if (result.size() < _rank_size) {
						result.put(e.getKey(), e.getValue());
					}
				}
				
				// update floor value
				if (result.size() < _rank_size) {
					_garden_appraisal_floor_value = 0;
				} else {
					_garden_appraisal_floor_value = Long.parseLong(result.firstEntry().getValue().split(":")[0]);
				}
				
				// update _floor_value to db
				DBConnector.GetMembaseServerForTemporaryData().Set("rannking_floor_value" + "_" + _active_ranking_idx, _garden_appraisal_floor_value);
				
				// maintain the result list
				if (_garden_appraisal_result_idx == 0) {
					// in case the list is empty
					if (_garden_appraisal_result.size() > 0) {
						_garden_appraisal_result.remove(0);
					}
					
					_garden_appraisal_result.add(0, result);
					_garden_appraisal_result_idx++;
				} else {
					// in case the list is empty
					if (_garden_appraisal_result.size() > 1) {
						_garden_appraisal_result.remove(1);
					}
					
					_garden_appraisal_result.add(1, result);
					_garden_appraisal_result_idx--;
				}
				
				// analize ans save sort result to base
				if (_active_ranking != -1 && _active_ranking_idx != -1) {
					if (_garden_appraisal_result.size() == 2) {
						if (_garden_appraisal_result_idx == 0) {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx, _rank_size,  _garden_appraisal_result.get(0), _garden_appraisal_result.get(1));
							rra.AnalyzeAndSave();
						} else {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx,_rank_size,  _garden_appraisal_result.get(1), _garden_appraisal_result.get(0));
							rra.AnalyzeAndSave();
						}
					}
				}
				break;
			}
			case CommandID.CMD_DELIVERY_ORDER:
			{
				TreeMap<String, String> tmp_sort = new TreeMap<String, String>(new ValueComparatorDescending(_deliver_order));
				TreeMap<String, String> result = new TreeMap<String, String>(new ValueComparatorAscending(_deliver_order));
				
				// sort all the list harvest
				tmp_sort.putAll(_deliver_order);
				
				// get the first RANKING_SIZE ranks only
				for (Map.Entry<String, String> e : tmp_sort.entrySet()) {
					if (result.size() < _rank_size) {
						result.put(e.getKey(), e.getValue());
					}
				}
				
				// update floor value
				if (result.size() < _rank_size) {
					_deliver_order_floor_value = 0;
				} else {
					_deliver_order_floor_value = Long.parseLong(result.firstEntry().getValue().split(":")[0]);
				}
				
				// update _floor_value to db
				DBConnector.GetMembaseServerForTemporaryData().Set("rannking_floor_value" + "_" + _active_ranking_idx, _deliver_order_floor_value);
				
				// maintain the result list
				if (_deliver_order_result_idx == 0) {
					// in case the list is empty
					if (_deliver_order_result.size() > 0) {
						_deliver_order_result.remove(0);
					}
					
					_deliver_order_result.add(0, result);
					_deliver_order_result_idx++;
				} else {
					// in case the list is empty
					if (_deliver_order_result.size() > 1) {
						_deliver_order_result.remove(1);
					}
					
					_deliver_order_result.add(1, result);
					_deliver_order_result_idx--;
				}
				
				// analize ans save sort result to base
				if (_active_ranking != -1 && _active_ranking_idx != -1) {
					if (_deliver_order_result.size() == 2) {
						if (_deliver_order_result_idx == 0) {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx, _rank_size,  _deliver_order_result.get(0), _deliver_order_result.get(1));
							rra.AnalyzeAndSave();
						} else {
							ResultAnalyser rra = new ResultAnalyser(_active_ranking_idx,_rank_size,  _deliver_order_result.get(1), _deliver_order_result.get(0));
							rra.AnalyzeAndSave();
						}
					}
				}
				break;
			}
			default:
				break;
		}
		
		// deliver award
		if ((_end_time > System.currentTimeMillis()) && ((_end_time - System.currentTimeMillis()) < DatabaseID.SORT_INTERVAL * 1000 && _run_once)) {
			// last time sorting, the ranking is about to end
			_run_once = false;
			_deliver_award.schedule(new TimerTask() {
				@Override
				public void run() {
					// sort last time
					LogHelper.Log("RankArrangement.. run final sort at " + Misc.getCurrentDateTime());
					try {
						Sort();
					} catch (Exception e) {
						LogHelper.LogException("FinalSort", e);
					}
					
					// give award
					LogHelper.Log("RankArrangement.. start give award");
					try {
						String result = (String)DBConnector.GetMembaseServerForTemporaryData().Get("ranking_result" + "_" + _active_ranking_idx);
						String[] sa = result.split(";");
						for (int i = sa.length - 1; i > 0; i--) {
							String[] inner = sa[i].split(":");
							if (inner.length > 3) {
								int rank = Integer.parseInt(inner[0]);
								long user_id = Long.parseLong(inner[1]);
								long value = Long.parseLong(inner[2]);
								int delta_rank = Integer.parseInt(inner[3]);
								String gift_name = "";
								String gift_description = "";
								String item_list = "";
								switch (rank) {
									case 1:
										gift_name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][20][DatabaseID.GIFT_INFO_NAME]) + _category;
										gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][20][DatabaseID.GIFT_INFO_DESCRIPTION]);
										item_list = _gift_rank_1;
										break;
									case 2:
										gift_name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][21][DatabaseID.GIFT_INFO_NAME]) + _category;
										gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][21][DatabaseID.GIFT_INFO_DESCRIPTION]);
										item_list = _gift_rank_2;
										break;
									case 3:
										gift_name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][22][DatabaseID.GIFT_INFO_NAME]) + _category;
										gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][22][DatabaseID.GIFT_INFO_DESCRIPTION]);
										item_list = _gift_rank_3;
										break;
									default:
										gift_name = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][23][DatabaseID.GIFT_INFO_NAME]) + _category;
										gift_description = Misc.PARSE_STRING(Server.s_globalDB[DatabaseID.SHEET_GIFTS_INFO][23][DatabaseID.GIFT_INFO_DESCRIPTION]);
										item_list = _gift_rank_100;
										break;
								}
								
								GiftManager gift_mgr = new GiftManager(Long.toString(user_id));
								gift_mgr.SetDatabase(DBConnector.GetMembaseServer(user_id));
								gift_mgr.LoadFromDatabase(KeyID.KEY_GIFT);
								gift_mgr.AddGiftBox(gift_name, gift_description, item_list);
								gift_mgr.SaveDataToDatabase(KeyID.KEY_GIFT);
								LogHelper.Log("RankArrangement.GiveAward: rank = " + rank + ", user id = " + user_id + ", record = " + value + ", delta rank = " + delta_rank + ", gift = " + item_list);
							} else {
								LogHelper.Log("handleGetPreviousRankingInfo.. err! invalid ranking result!");
								break;
							}
						}
					} catch (Exception e) {
						LogHelper.LogException("GiveAward", e);
					}
				}
			}, new Date(_end_time - 1000));
		}
	}
	
	private void Clean() {
		if (_active_ranking == -1 || _active_ranking_idx == -1 || System.currentTimeMillis() > _end_time) {
			LogHelper.Log("RankingManager.. no ranking is active, canceled sort");
			return;
		}
		LogHelper.Log("Start cleanning process at : " + Misc.getCurrentDateTime());
		LogHelper.Log("Clean.. harvest floor value: " + _harvest_floor_value);
		for (Map.Entry<String, String> e : _harvest.entrySet()) {
			if (Long.parseLong(e.getValue().split(":")[0]) < _harvest_floor_value) {
				LogHelper.Log("Removed key = " + e.getKey() + ", value = " + e.getValue());
				_harvest.remove(e.getKey());
			}
		}
		
		LogHelper.Log("Clean.. machine floor value: " + _machine_floor_value);
		for (Map.Entry<String, String> e : _machine.entrySet()) {
			if (Long.parseLong(e.getValue().split(":")[0]) < _machine_floor_value) {
				LogHelper.Log("Removed key = " + e.getKey() + ", value = " + e.getValue());
				_machine.remove(e.getKey());
			}
		}
		
		LogHelper.Log("Clean.. catch bug floor value: " + _catch_bug_floor_value);
		for (Map.Entry<String, String> e : _catch_bug.entrySet()) {
			if (Long.parseLong(e.getValue().split(":")[0]) < _catch_bug_floor_value) {
				LogHelper.Log("Removed key = " + e.getKey() + ", value = " + e.getValue());
				_catch_bug.remove(e.getKey());
			}
		}
		
		LogHelper.Log("Clean.. garnden appraisal floor value: " + _garden_appraisal_floor_value);
		for (Map.Entry<String, String> e : _garden_appraisal.entrySet()) {
			if (Long.parseLong(e.getValue().split(":")[0]) < _garden_appraisal_floor_value) {
				LogHelper.Log("Removed key = " + e.getKey() + ", value = " + e.getValue());
				_garden_appraisal.remove(e.getKey());
			}
		}
		
		LogHelper.Log("Clean.. deliver order floor value: " + _deliver_order_floor_value);
		for (Map.Entry<String, String> e : _deliver_order.entrySet()) {
			if (Long.parseLong(e.getValue().split(":")[0]) < _deliver_order_floor_value) {
				LogHelper.Log("Removed key = " + e.getKey() + ", value = " + e.getValue());
				_deliver_order.remove(e.getKey());
			}
		}
		
		LogHelper.Log("End cleanning process at : " + Misc.getCurrentDateTime());
	}
}

/**
 * Comparator for TreeMap, sorting by value, ascending.
 */
class ValueComparatorAscending implements Comparator<String> {
    Map<String, String> base;
    public ValueComparatorAscending(Map<String, String> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.      
    public int compare(String a, String b) {
		long va = Long.parseLong(base.get(a).split(":")[0]);
		long vb = Long.parseLong(base.get(b).split(":")[0]);
		if (va > vb) {
            return 1;
        } else {
			if (va == vb) {
				long ta = Long.parseLong(base.get(a).split(":")[1]);
				long tb = Long.parseLong(base.get(b).split(":")[1]);
				if (ta < tb) {
					return 1;
				}
			}
			return -1;
		}
		// returning 0 would merge keys
		// return > 0: ascending
		// return < 0: descending
		// return = 0: merge keys 
    }
}
/**
 * Comparator for TreeMap, sorting by value, descending.
 */
class ValueComparatorDescending implements Comparator<String> {
    Map<String, String> base;
    public ValueComparatorDescending(Map<String, String> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.      
    public int compare(String a, String b) {
		long va = Long.parseLong(base.get(a).split(":")[0]);
		long vb = Long.parseLong(base.get(b).split(":")[0]);
		if (va > vb) {
            return -1;
        } else {
			if (va == vb) {
				long ta = Long.parseLong(base.get(a).split(":")[1]);
				long tb = Long.parseLong(base.get(b).split(":")[1]);
				if (ta < tb) {
					return -1;
				}
			}
			return 1;
		}
		// returning 0 would merge keys
		// return > 0: ascending
		// return < 0: descending
		// return = 0: merge keys 
    }
}

/**
 * Analyze the sort result to have ranking details
 */
class ResultAnalyser {
	private int _idx;
	private TreeMap<String, String> _old;
	private TreeMap<String, String> _new;
	private LinkedList<String> _old_list = new LinkedList<String>();
	private LinkedList<String> _new_list = new LinkedList<String>();
	private int _rank_size = 100;
	public ResultAnalyser(int idx, int rank_size, TreeMap<String, String> old_base, TreeMap<String, String> new_base) {
		this._idx = idx;
		this._rank_size = rank_size;
		this._old = old_base;
		this._new = new_base;
	}
	
	public void AnalyzeAndSave() {
		LogHelper.Log("AnalyzeAndSave.. before tree map, size = " + _old.size() + ", content = " + _old);
		LogHelper.Log("AnalyzeAndSave.. after tree map, size = " + _new.size() + ", content = " + _new);
		
		for (Map.Entry<String, String> e : _old.entrySet()) {
			_old_list.add(e.getKey() + "_" + e.getValue());
		}
		for (Map.Entry<String, String> e : _new.entrySet()) {
			_new_list.add(e.getKey() + "_" + e.getValue());
		}
		
		LogHelper.Log("AnalyzeAndSave.. before list = " + _old_list);
		LogHelper.Log("AnalyzeAndSave.. after list = " + _new_list);
		
//		if (_old_list.size() < _rank_size || _new_list.size() < _rank_size) {
//			LogHelper.Log("AnalyzeAndSave.. not enough data to analyze");
//			return;
//		}
		
		StringBuilder sb = new StringBuilder();
		sb.append(Misc.getCurrentHour()); // last update time
		
		// analyze details
		int pos_new = _new_list.size() + 1;
		int rank_delta = 0;
		for (String s : _new_list) {
			pos_new--;
			rank_delta = 0;
			String key = s.split("_")[0];
			long value = Long.parseLong(s.split("_")[1].split(":")[0]);
			int pos_old = _old_list.size() + 1;
			boolean found = false;
			for (String is : _old_list) {
				pos_old--;
				String ikey = is.split("_")[0];
				if (key.equals(ikey)) {
					found = true;
				}
				
				if (found) {
					break;
				}
			}
			if (found) {
				rank_delta = pos_old - pos_new;
			} else {
				rank_delta = _new_list.size() + 1 - pos_new;
			}
			
			 LogHelper.Log("Position [" + pos_new + "] : uid = " + key + ", value = " + value + ", change = " + rank_delta);
			
			 sb.append(";");
			 sb.append(pos_new).append(":").append(key).append(":").append(value).append(":").append(rank_delta);
		}
		
		// save to base
		LogHelper.Log("Analize result: " + sb.toString());
		DBConnector.GetMembaseServerForTemporaryData().Set("ranking_result" + "_" + _idx, sb.toString());
	}
}