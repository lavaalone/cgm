package com.vng.skygarden.game;

public final class KeyID
{
	// ------------------------------------- REQUEST -------------------------------
	public static final String	KEY_USER_COMMAND_ID				= "command_id";
	public static final String	KEY_USER_REQUEST_STATUS			= "request_status";
	public static final String	KEY_USER_ID						= "user_id";
	public static final String	KEY_DEVICE_ID					= "device_id";
	public static final String	KEY_DEVICE_NAME					= "device_name";
	public static final String	KEY_DEVICE_FW					= "device_fw";
	public static final String	KEY_DEVICE_IMEI					= "device_imei";
	public static final String	KEY_EMAIL						= "email";
	public static final String	KEY_PHONE_NUMBER				= "phone_number";
	public static final String	KEY_USER_REQUEST_ID				= "request_id";
	public static final String	KEY_USER_SESSION_ID				= "session_id";
	public static final String	KEY_USER_NEW_SESSION_ID			= "new_session_id";
	public static final String	KEY_USER_SESSION_EXPIRED_TIME	= "session_expired_time";
	public static final String	KEY_REQUEST_STATUS				= "request";
	public static final String	KEY_CLIENT_DATA					= "client_data";
	public static final String	KEY_FACEBOOK_ID					= "fb_id";
	public static final String	KEY_FACEBOOK_ACCESS_TOKEN		= "fb_access_token";
	public static final String	KEY_FACEBOOK_LONG_LIVED_TOKEN	= "fb_long_lived_token";
	public static final String	KEY_FACEBOOK_TOKEN_ISSUE_DATE	= "fb_token_issue";
	public static final String	KEY_FACEBOOK_TOKEN_EXPIRE_DATE	= "fb_token_expire";
	public static final String	KEY_FACEBOOK_NAME				= "fb_name";
	public static final String	KEY_FACEBOOK_GENDER				= "fb_gender";
	public static final String	KEY_FACEBOOK_BIRTHDAY			= "fb_birthday";
	public static final String	KEY_FACEBOOK_LL_TOKEN			= "fb_long_lived_token";
	public static final String	KEY_FACEBOOK_LIKE				= "fb_like";
	public static final String	KEY_ZING_ID						= "zing_id";
	public static final String	KEY_ZING_NAME					= "zing_name";
	public static final String	KEY_ZING_DISPLAY_NAME			= "zing_display_name";
	public static final String	KEY_ZING_ACCESS_TOKEN			= "zing_access_token";
	public static final String	KEY_ZING_AVATAR					= "zing_avatar";
	public static final String	KEY_ZALO_ID						= "zalo_id";
	public static final String	KEY_ZALO_NAME					= "zalo_name";
	public static final String	KEY_ZALO_DISPLAY_NAME			= "zalo_display_name";
	public static final String	KEY_ZALO_ACCESS_TOKEN			= "zalo_access_token";
	public static final String	KEY_ZALO_AVATAR					= "zalo_avatar";
	public static final String	KEY_MONEY_REAL 					= "money_real";
	public static final String	KEY_MONEY_TOTAL 				= "money_total";
	public static final String	KEY_MONEY_BONUS 				= "money_bonus";
	public static final String	KEY_GARDEN_APPRAISAL			= "garden_total_appraisal";
	public static final String	KEY_USER_IP						= "user_ip";
	public static final String	KEY_USER_PHONE					= "phone_number";
	public static final String	KEY_VERIFY_PHONE				= "phone_verify_code";
	public static final String	KEY_FIRST_PLANT					= "first_plant";
	public static final String	KEY_INVITE_FRIEND				= "invite_friend";
	public static final String	KEY_USER_PHONE_LIST				= "user_phone_list";
	public static final String	KEY_RECEIVED_GIFT_INSTALL_APP	= "received_gift_install_app";
	public static final String	KEY_EVENT_MARKERS				= "event_markers";
	public static final String	KEY_EVENT_GIFT_NUM				= "event_gift_num";
	public static final String	KEY_EVENT_GIVE_FRIEND_GIFT_NUM	= "event_give_friend_gift_num";
	public static final String	KEY_EVENT_CURRENT_TOTAL_NUM		= "event_current_total_num";
	public static final String	KEY_NUM_GIFT_BOX_EVENT_RECEIVED		= "num_gift_box_event_received";
	public static final String	KEY_NUM_GIFT_BOX_EVENT_HALLOWEEN		= "_gift_box_received_event_halloween";
	public static final String	KEY_EVENT_ITEM_ID				= "event_item_id";
	public static final String	KEY_EVENT_ITEM_LINK				= "event_item_link";
	public static final String	KEY_EVENT_ITEM_MD5				= "event_item_md5";
	public static final String	KEY_EVENT_ITEM_SPRITE_ID		= "event_sprite_id";
	public static final String	KEY_EVENT_ITEM_SPRITE_CDN		= "event_sprite_cdn";
	public static final String	KEY_ANDROID_ID					= "android_id";
	public static final String	KEY_ADVERTISING_ID				= "advertising_id";
	public static final String	KEY_REFERENCE_CODE				= "ref_code";
	public static final String	KEY_BONUS_MARKER				= "bonus_marker";
	public static final String	KEY_CLOSE_FRIEND				= "close_friend";
	public static final String	KEY_TREASURE					= "treasure";
	
	public static final String	KEY_USER_INFOS					= "user";
	public static final String	KEY_SEED_INFOS					= "seed";
	public static final String	KEY_POT_INFOS					= "pot";
	public static final String	KEY_PEST_INFOS					= "pest";
	public static final String	KEY_WAREHOUSE_INFOS				= "warehouse";
	
	public static final String	KEY_FLOORS						= "floor_";
	public static final String	KEY_STOCKS						= "stock_";
	public static final String	KEY_MACHINES					= "machine_";	// notice to client if we change this key
	public static final String	KEY_SLOTS						= "slot_";
	public static final String	KEY_ORDER						= "order";
	public static final String	KEY_GAME_CONSTANT				= "gc_";
	public static final String	KEY_GAME_CONSTANT_MAX_ROW		= "gc_max_row_";
	public static final String	KEY_IBSHOP						= "ibshop";
	public static final String	KEY_PRIVATE_SHOP				= "pshop";
	public static final String	KEY_FRIENDS						= "friends";
	public static final String	KEY_FRIEND_PRIVATE_SHOP			= "friend_pshop";
	public static final String  KEY_GIFT						= "gift";
	public static final String	KEY_MERCHANT					= "merchant";
	public static final String	KEY_NEWS_BOARD					= "news_board";
	public static final String	KEY_ACHIEVEMENT					= "achievement";
	public static final String	KEY_DAILY_GIFT					= "dailygift";
	public static final String	KEY_TUTORIAL					= "tutorial";
	public static final String	KEY_DROP_BONUS_ITEM				= "dropbonusitem";
	public static final String	KEY_MACHINES_DURABILITY			= "machine_durability_";	// notice to client if we change this key
	public static final String	KEY_NPC_MACHINES_DURABILITY		= "npc_machine_durability_";
	public static final String  KEY_NEW_DAY						= "new_day";
	public static final String	KEY_MACHINES_REPAIR_LIMIT		= "machine_repair_limit";
	public static final String	KEY_AIRSHIP						= "airship";
	public static final String	KEY_FRIEND_AIRSHIP				= "friend_airship";
	public static final String	KEY_TOM_KID						= "tomkid";
	public static final String	KEY_FORTUNE						= "fortune";
	public static final String	KEY_NOTIFY						= "notify";
	public static final String	KEY_PRIVATE_INFO				= "private_info";
	public static final String	KEY_ITEMS_EVENT					= "items_event";
	public static final String	KEY_OFFER_MANAGER				= "offer_manager";
	public static final String	KEY_NEW_DAILYGIFT				= "new_daily_gift";

	// ------------------------------------- USER -------------------------------
	public static final String KEY_USER_LEVEL					= "user_level";
	public static final String KEY_USER_EXP						= "user_exp";
	public static final String KEY_USER_EXP_LVL					= "user_exp_lvl";
	public static final String KEY_USER_GOLD					= "user_gold";
	public static final String KEY_USER_DIAMOND					= "user_diamond";
	public static final String KEY_USER_REPUTATION				= "user_reputation";
	public static final String KEY_USER_FLOOR					= "user_floor";
	public static final String KEY_USER_VIP_LEVEL				= "user_vip_level";
	public static final String KEY_USER_VIP_ACTIVE				= "user_vip_active";
	public static final String KEY_USER_VIP_TIME				= "user_vip_time";
	public static final String KEY_USER_NAME					= "user_name";
	public static final String KEY_USER_VIP						= "user_vip";
	public static final String KEY_USER_FRIEND_COUNT			= "user_friend_count";
	public static final String KEY_USER_LAST_LOGIN				= "user_last_login";
	public static final String KEY_USER_CURRENT_LOGIN			= "user_current_login";
	public static final String KEY_USER_LAST_LEVELUP			= "user_last_levelup";
	public static final String KEY_USER_BUG_APPEAR_RATIO		= "user_bug_appear_ratio";

	public static final String KEY_USER_ORDER_COUNT				= "user_order_count";
	public static final String KEY_USER_ORDER					= "user_order_";
	public static final String KEY_USER_ORDER_REWARD			= "user_order_reward";
	
	public static final String KEY_HAS_ORDER_EVENT				= "has_order_event";
	
	public static final String KEY_USER_REVENU_DATE_MAX			= "user_revenu_date_max";
	public static final String KEY_USER_REVENU_DATE				= "user_revenu_date";
	public static final String KEY_USER_REVENU_WEEK_MAX			= "user_revenu_week_max";
	public static final String KEY_USER_REVENU_WEEK				= "user_revenu_week";
	public static final String KEY_USER_REVENU_MONTH_MAX		= "user_revenu_month_max";
	public static final String KEY_USER_REVENU_MONTH			= "user_revenu_month";
	
	public static final String KEY_USER_ORDER_NUM_DATE_MAX		= "user_order_num_date_max";
	public static final String KEY_USER_ORDER_NUM_DATE			= "user_order_num_date";
	public static final String KEY_USER_USED_AMULET				= "user_used_amulet";
	
	public static final String KEY_USER_ACTION					= "user_action";
	public static final String KEY_USER_LIKED_LIST				= "user_liked_list";
	public static final String KEY_USER_LIKED_COUNT				= "user_liked_count";
	public static final String KEY_USER_LIKED					= "user_liked";
	public static final String KEY_REGISTER_DATE				= "register_date";
	
	public static final String KEY_USER_NEED_HELP_REPAIR_MACHINE= "user_need_help_repair_machine";
	
	public static final String KEY_NOTIFY_TITLE_TEXT			= "notify_title_text";
	public static final String KEY_NOTIFY_DESCRIPTION_TEXT		= "notify_description_text";
	
	public static final String KEY_UPDATE_RECOMMEND				= "update_recommend";
	
	// ------------------------------------- FLOOR -------------------------------
	public static final String FLOOR_NUM						= "floor_num";
	public static final String HARVEST_FIRST_FLOOR				= "harvest_first_floor";
	public static final String HARVEST_SECOND_FLOOR				= "harvest_second_floor";
	public static final String PLANT_FIRST_FLOOR				= "plant_first_floor";
	public static final String PLANT_SECOND_FLOOR				= "plant_second_floor";
	public static final String PUT_POT_FIRST_FLOOR				= "put_pot_first_floor";
	public static final String PUT_POT_SECOND_FLOOR				= "put_pot_second_floor";
	
	// ------------------------------------- STOCK -------------------------------
	public static final String KEY_STOCK_ID						= "stock_id";
	public static final String KEY_STOCK_SILO					= "stock_silo";
	public static final String KEY_STOCK_BARN					= "stock_barn";
	public static final String KEY_STOCK_WAREHOUSE				= "stock_warehouse";
	public static final String KEY_STOCK_CAPACITY_MAX			= "capacity_max";
	public static final String KEY_STOCK_CAPACITY_CUR			= "capacity_cur";
	
	public static final String KEY_STOCK_PRODUCT_COUNT			= "stock_product_count";
	public static final String KEY_STOCK_PRODUCT_TYPE			= "stock_product_type";
	public static final String KEY_STOCK_PRODUCT_ID				= "stock_product_id";
	public static final String KEY_STOCK_PRODUCT_NUM			= "stock_product_num";
	
	public static final String KEY_STOCK_PRODUCT_INDEX			= "stock_product_index";
	public static final String KEY_STOCK_PRODUCT_EXP			= "stock_product_exp";
	
	public static final String KEY_STOCK_LEVEL					= "stock_level";
	public static final String KEY_STOCK_TOTAL_ITEM				= "stock_total_item";
	public static final String KEY_STOCK_ITEM_TYPE				= "item_type_";
	public static final String KEY_STOCK_ITEM_ID				= "item_id_";
	public static final String KEY_STOCK_ITEM_QUANTITY			= "item_quantity_";
	
	// ------------------------------------- SLOT -------------------------------
	public static final String SLOT_PROPERTY_POT_ID				= "pot_id";
	public static final String SLOT_PROPERTY_SEED_ID			= "seed_id";
	public static final String SLOT_PROPERTY_GROWTIME			= "growtime";
	public static final String SLOT_PROPERTY_SOWTIME			= "sowtime";
	public static final String SLOT_PROPERTY_XP_RECEIVE			= "xp";
	public static final String SLOT_PROPERTY_PEST_ID			= "pest";
	public static final String KEY_SLOT_BONUS_EXP				= "slot_bonus_exp";
	
	// ------------------------------------- SPECIAL OFFER ------------------------
	public static final String KEY_SPECIAL_OFFER_ID				= "key_offer_id";
	public static final String KEY_SPECIAL_OFFER_LASTTIME		= "key_offer_lasttime";
	public static final String KEY_SPECIAL_OFFER_DURATION		= "key_offer_duration";
	public static final String KEY_SPECIAL_OFFER_OFFERRING		= "key_offer_offerring";
	public static final String KEY_SPECIAL_OFFER_ACCEPTED		= "key_offer_accepted";
	public static final String KEY_SPECIAL_OFFER_REJECTED		= "key_offer_rejected";
	public static final String KEY_SPECIAL_OFFER_TYPE			= "key_offer_type";
	public static final String KEY_SPECIAL_OFFER_REMAINING_TIME = "key_offer_remaining_time";
	public static final String KEY_SPECIAL_OFFER_USER_GROUP		= "key_offer_user_group";
	public static final String KEY_SPECIAL_OFFER_DESCRIPTION	= "key_offer_description";
	public static final String KEY_SPECIAL_OFFER_NAME			= "key_offer_name";
	public static final String KEY_SPECIAL_OFFER_LINK_IMG		= "key_offer_link_img";
	public static final String KEY_SPECIAL_OFFER_MD5_IMG		= "key_offer_md5_img";
	public static final String KEY_SPECIAL_OFFER_BUTTON_LABEL	= "key_offer_button_label";
	public static final String KEY_SPECIAL_OFFER_WEB_LINK		= "key_offer_web_link";
	public static final String KEY_SPECIAL_OFFER_AVAILABLE_TIME = "key_offer_available_time";
	public static final String KEY_SPECIAL_OFFER				= "key_offer";
	public static final String KEY_SPECIAL_OFFER_PACK_INFO		= "key_offer_pack_info";
	public static final String KEY_SPECIAL_OFFER_RECEIVE_TIME	= "key_offer_receive_time";
	public static final String KEY_SPECIAL_OFFER_USE_TIME		= "key_offer_use_time";
	
	// ------------------------------------- MACHINE -------------------------------
	public static final String KEY_MACHINE_ID					= "machine_id";
	public static final String KEY_MACHINE_FLOOR				= "machine_floor";
	public static final String KEY_MACHINE_LEVEL				= "machine_level";
	public static final String KEY_MACHINE_ACTIVE_TIME			= "machine_active_time";
	public static final String KEY_MACHINE_STATUS				= "status";
	public static final String KEY_MACHINE_DURABILITY_CUR		= "durability_cur";
	public static final String KEY_MACHINE_DURABILITY_MAX		= "durability_max";
	public static final String KEY_MACHINE_SLOT_MAX				= "slot_max";
	public static final String KEY_MACHINE_SLOT_CUR				= "slot_cur";
	public static final String KEY_MACHINE_START_TIME			= "start_time";
	public static final String KEY_MACHINE_PRODUCT_COUNT		= "prod_count";
	
	public static final String KEY_MACHINE_ITEM_DROP_LIST		= "machine_item_drop_list";
	
	public static final String KEY_MACHINE_SLOT_PRODUCT_ID		= "slot_product_id";
	public static final String KEY_MACHINE_SLOT_PRODUCT_TIME	= "slot_product_time";
	
	public static final String KEY_MACHINE_SLOT					= "slot_";
	public static final String KEY_MACHINE_PRODUCT				= "prod_";
	
	public static final String KEY_MACHINE_DECOR				= "decor";

	public static final String KEY_MACHINE_LOCK_DATA			= "machine_lock_data";
	public static final String KEY_REPAIR_FRIEND_MACHINE		= "repair_friend_machine";
	public static final String KEY_MACHINE_DURABILITY_REPAIRED	= "machine_durability_repaired";
	public static final String KEY_NPC_MACHINE_DAILY_RESET_TIME	= "npc_machine_daily_reset_time";
	
	public static final String KEY_REPUTATION_MAX_PER_DATE		= "reputation_max_per_date";
	public static final String KEY_REPUTATION_COLLECTED_PER_DATE= "reputation_collected_per_date";
	public static final String KEY_MACHINE_REPAIR_RESET_TIME	= "machine_repair_reset_time";
	
	// ------------------------------------- PRODUCT -------------------------------
	public static final String KEY_PRODUCT_TYPE					= "product_type";
	public static final String KEY_PRODUCT_ID					= "product_id";
	public static final String KEY_MATERIAL_ID					= "material_id";
	
	public static final String KEY_PRODUCT_NUM					= "product_num";
	public static final String KEY_PRODUCT_EXP					= "product_exp";
	
	// ------------------------------------- PLANT -------------------------------
	public static final String KEY_PLANT_ID						= "plant_id";
	public static final String KEY_PLANT_NAME					= "plant_name";
	public static final String KEY_PLANT_PRICE					= "plant_price";
	public static final String KEY_PLANT_START_TIME				= "plant_start_time";
	public static final String KEY_PLANT_GROW_TIME				= "plant_grow_time";
	public static final String KEY_PLANT_HARVEST_EXP			= "plant_harvest_exp";
	public static final String KEY_PLANT_ITEM_RECEIVE_RATIO		= "plant_item_receive_ratio";
	public static final String KEY_PLANT_DIAMON_SKIP_TIME		= "plant_diamond_skip_time";
	public static final String KEY_PLANT_DIAMON_BUY				= "plant_diamond_buy";
	public static final String KEY_PLANT_GOLD_SELL_DEFAULT		= "plant_gold_sell_default";
	public static final String KEY_PLANT_GOLD_SELL_MIN			= "plant_gold_sell_min";
	public static final String KEY_PLANT_GOLD_SELL_MAX			= "plant_gold_sell_max";
	public static final String KEY_PLANT_BUG_ID					= "plant_bug_id";
	public static final String KEY_PLANT_HAS_BUG				= "plant_has_bug";
	public static final String KEY_PLANT_BUG_APPEAR_RATIO		= "plant_bug_appear_ratio";
	public static final String KEY_PLANT_CURRENT_LEVEL			= "plant_current_level";
	public static final String KEY_PLANT_HARVEST_TO_LEVEL_UP	= "plant_harvest_to_level_up";
	public static final String KEY_PLANT_AT_FLOOR			   	= "plant_at_floor";
	public static final String KEY_PLANT_AT_SLOT				= "plant_at_slot";
	public static final String KEY_PLANT_LEVEL				  	= "plant_level";
	public static final String KEY_PLANT_COMBO_ID				= "plant_combo_id";
	public static final String KEY_PLANT_COMBO_EXP_BONUS		= "plant_combo_exp_bonus";
	public static final String KEY_PLANT_FERTILIZER_ID			= "plant_fertilizer_id";
	public static final String KEY_PLANT_FERTILIZER_REDUCE_TIME = "plant_fertilizer_reduce_time";
	
	// ------------------------------------- POT -------------------------------
	public static final String KEY_POT_ID						= "pot_id";
	public static final String KEY_POT_NAME						= "pot_name";
	public static final String KEY_POT_PRICE					= "pot_price";
	public static final String KEY_POT_UNLOCK_EXP				= "pot_unlock_exp";
	public static final String KEY_POT_EXP_INCREASE				= "pot_exp_increase";
	public static final String KEY_POT_TIME_DECREASE			= "pot_time_decrease";
	public static final String KEY_POT_GOLD_UPGRADE				= "pot_gold_upgrade";
	public static final String KEY_POT_PEARL_ID_NUM				= "pot_pearl_id_num";
	public static final String KEY_POT_UPGRADE_RATIO			= "pot_upgrade_ratio";
	public static final String KEY_POT_GOLD_DEFAULT				= "pot_gold_default";
	public static final String KEY_POT_GOLD_MIN					= "pot_gold_min";
	public static final String KEY_POT_GOLD_MAX					= "pot_gold_max";
	public static final String KEY_POT_AT_FLOOR					= "pot_at_floor";
	public static final String KEY_POT_AT_SLOT					= "pot_at_slot";
	public static final String KEY_POT_LEVEL					= "pot_level";
		
	// ------------------------------------- DECOR -------------------------------
	public static final String KEY_DECOR_ID						= "decor_id";
	public static final String KEY_DECOR_AT_FLOOR				= "decor_at_floor";
	public static final String KEY_DECOR_AT_SLOT				= "decor_at_slot";
		
	// ------------------------------------- SLOT  -------------------------------
	public static final String KEY_SLOT_INDEX					= "slot_id";
	public static final String KEY_SLOT_HARVEST_EXP				= "slot_harvest_exp";
		
	// ------------------------------------- FLOOR  -------------------------------
	public static final String KEY_FLOOR_INDEX				  	= "floor_id";
	public static final String KEY_TOTAL_FLOOR_CHANGED			= "total_floor_changed";
	public static final String KEY_FLOOR_COMBO_ID				= "floor_combo_id";
	
	// ------------------------------------- VERSION  -------------------------------
	public static final String KEY_VERSION_PLATFORM			  	= "version_platform";
	public static final String KEY_VERSION_RESOLUTION			= "version_resolution";
	public static final String KEY_VERSION_NUMBER				= "version_number";
	public static final String KEY_VERSION_LINK					= "version_link";
	public static final String KEY_VERSION_UPDATE				= "version_update";
	public static final String KEY_VERSION_FORCE_UPDATE			= "version_force_update";
	public static final String KEY_INSTALL_REFERENCE			= "install_reference";
		
	// ------------------------------------- ORDER -------------------------------
	public static final String KEY_ORDER_TYPE					= "order_type";
	public static final String KEY_ORDER_FREE_DELIVERED_NUM		= "order_free_delivered_num";
	public static final String KEY_ORDER_PAID_DELIVERED_NUM		= "order_paid_delivered_num";
	public static final String KEY_ORDER_DAILY_FREE_MAX			= "order_daily_free_max";
	public static final String KEY_ORDER_DAILY_PAID_MAX			= "order_daily_paid_max";
	public static final String KEY_TOTAL_DELIVERED_NUM			= "total_order_daily_per_date";
	public static final String KEY_ORDER_DAILY_RESET_TIME		= "order_daily_reset_time";
	public static final String KEY_DAILY_ORDER_LETTER_SELECTED	= "order_daily_letter_selected";
	
	public static final String KEY_ORDER_DELIVERY_TIME			= "order_delivery_time";
	
	public static final String KEY_ORDER_REWARD_GOLD_RATIO_FROM_POT		= "order_reward_gold_ratio_from_pot";
	public static final String KEY_ORDER_REWARD_EXP_RATIO_FROM_POT		= "order_reward_exp_ratio_from_pot";
	public static final String KEY_ORDER_REWARD_GOLD_RATIO_FROM_MACHINE	= "order_reward_gold_ratio_from_machine";
	public static final String KEY_ORDER_REWARD_GOLD_RATIO_FROM_EVENT	= "order_reward_gold_ratio_from_event";
	public static final String KEY_ORDER_REWARD_EXP_RATIO_FROM_MACHINE	= "order_reward_exp_ratio_from_machine";
	public static final String KEY_ORDER_REWARD_EXP_RATIO_FROM_EVENT	= "order_reward_exp_ratio_from_event";
	public static final String KEY_ORDER_SKIPPING				= "order_skipping";
	
	public static final String KEY_ORDER_REWARD_GOLD			= "order_reward_gold";
	public static final String KEY_ORDER_REWARD_GOLD_BONUS		= "order_reward_gold_bonus";
	
	public static final String KEY_ORDER_REWARD_EXP				= "order_reward_exp";
	public static final String KEY_ORDER_REWARD_EXP_BONUS		= "order_reward_exp_bonus";
	
	public static final String KEY_ORDER_REWARD_DIAMOND			= "order_reward_diamond";
	public static final String KEY_ORDER_ITEM_COUNT				= "order_reward_item_count";
	public static final String KEY_ORDER_PRODUCT_COUNT			= "order_reward_prod_count";
	public static final String KEY_ORDER_NPC					= "order_npc";
	public static final String KEY_ORDER_NEW_WAIT_TIME			= "order_new_wait_time";
	
	public static final String KEY_ORDER_RECEIVE				= "order_receive";
	public static final String KEY_ORDER_RECEIVE_DIAMOND		= "order_receive_diamond";
	public static final String KEY_ORDER_LETTER_SELECT_INDEX	= "order_letter_select_index";
	public static final String KEY_ORDER_LETTER_SELECT_VALUE	= "order_letter_select_value";
	
	public static final String KEY_ORDER_LETTERS_ENABLE			= "order_letters_enable_";
	public static final String KEY_ORDER_LETTERS_VALUE			= "order_letters_value_";

	public static final String KEY_ORDER_INDEX					= "order_index";
	public static final String KEY_ORDER_REWARD					= "order_reward";
	public static final String KEY_ORDER_ITEM					= "item_";
	public static final String KEY_ORDER_PRODUCT				= "prod_";

	public static final String KEY_ORDER_LETTER_INDEX			= "order_letter_index";
	public static final String KEY_LETTER_RESELECT_DIAMOND		= "letter_reselect_diamond";
	public static final String KEY_ORDER_EVENT					= "order_event";
	
	// ------------------------------------- IBSHOP  -------------------------------
	public static final String KEY_IBS_PACKAGE_INDEX			= "ibs_pack_idx";
	public static final String KEY_IBS_PACKAGE_ID				= "ibs_package_";
	public static final String KEY_IBS_PACKAGE_TYPE				= "ibs_package_type";
	public static final String KEY_IBS_PACKAGE_NAME				= "ibs_package_name";
	public static final String KEY_IBS_PACKAGE_DES				= "ibs_package_des";
	public static final String KEY_IBS_PACKAGE_NAME_EN			= "ibs_package_name_en";
	public static final String KEY_IBS_PACKAGE_DES_EN			= "ibs_package_des_en";
	public static final String KEY_IBS_PACKAGE_NAME_SC			= "ibs_package_name_sc";
	public static final String KEY_IBS_PACKAGE_DES_SC			= "ibs_package_des_sc";
	public static final String KEY_IBS_PACKAGE_NAME_TC			= "ibs_package_name_tc";
	public static final String KEY_IBS_PACKAGE_DES_TC			= "ibs_package_des_tc";
	public static final String KEY_IBS_PACKAGE_CASH_ID			= "ibs_package_cash_id";
	public static final String KEY_IBS_ITEM_TYPE				= "ibs_item_type";
	public static final String KEY_IBS_ITEM_ID				  	= "ibs_item_id";
	public static final String KEY_IBS_ITEM_QUANTIFY			= "ibs_item_quantity";
	public static final String KEY_IBS_IS_ACTIVE				= "ibs_is_active";
	public static final String KEY_IBS_IS_NEW				   	= "ibs_is_new";
	public static final String KEY_IBS_IS_HOT				   	= "ibs_is_hot";
	public static final String KEY_IBS_IS_SALE_OFF			  	= "ibs_is_sale_off";
	public static final String KEY_IBS_SALE_OFF_PERCENT		 	= "ibs_sale_off_percent";
	public static final String KEY_IBS_HAS_PROMOTION			= "ibs_has_promotion";
	public static final String KEY_IBS_GIFT_WHEN_BUY			= "ibs_gift_when_buy";
	public static final String KEY_IBS_REQUIRED_GOLD			= "ibs_required_gold";
	public static final String KEY_IBS_REQUIRED_DIAMOND			= "ibs_required_diamond";
	public static final String KEY_IBS_REQUIRED_REPUTATION		= "ibs_required_reputation";
	public static final String KEY_IBS_HAS_TIME_LIMIT			= "ibs_sale_limit_time";
	public static final String KEY_IBS_SALE_DURATION			= "ibs_sale_duration";
	public static final String KEY_IBS_HAS_SALE_LIMIT			= "ibs_sale_limit_quantity";
	public static final String KEY_IBS_SALE_TOTAL_QUANTITY		= "ibs_sale_total_quantity";
	public static final String KEY_IBS_UNLOCK_LEVEL				= "ibs_unlock_level";
	public static final String KEY_IBS_REMAINING_QUANTITY		= "ibs_remaining_quantity";
	public static final String KEY_IBS_MAX_PACKAGE_NUMBER		= "ibs_max_package_number";
	public static final String KEY_IBS_DISPLAY_IDX				= "ibs_display_idx";
	
	// ------------------------------------- PRIVATESHOP  -------------------------------
	public static final String KEY_PS_SLOT						= "ps_slot_";
	public static final String KEY_PS_CURRENT_SLOT_NUMBER		= "ps_current_slot_number";
	public static final String KEY_PS_ADVERTISE_AVAILABLE_TIME	= "ps_advertise_available_time";
	public static final String KEY_PS_SLOT_ID					= "ps_slot_id";
	public static final String KEY_PS_SLOT_STATUS				= "ps_slot_status";
	public static final String KEY_PS_NEXT_REQUIRED_FRIEND		= "ps_slot_next_required_friend";
	public static final String KEY_PS_NEXT_REQUIRED_DIAMOND		= "ps_slot_next_required_diamond";
	
	public static final String KEY_PS_START_HIRE_DATE			= "ps_start_hire";
	public static final String KEY_PS_END_HIRE_DATE				= "ps_end_hire";
	public static final String KEY_PS_ITEM						= "ps_item";
	public static final String KEY_PS_ITEM_TYPE					= "ps_item_type";
	public static final String KEY_PS_ITEM_ID					= "ps_item_id";
	public static final String KEY_PS_ITEM_NUMBER				= "ps_item_number";
	public static final String KEY_PS_ITEM_START_SELL_DATE		= "ps_item_start_date";
	public static final String KEY_PS_ITEM_END_SELL_DATE		= "ps_item_end_date";
	public static final String KEY_PS_ITEM_STATUS				= "ps_item_status";
	public static final String KEY_PS_ITEM_MONEY_TYPE			= "ps_item_money_type";
	public static final String KEY_PS_ITEM_PRICE				= "ps_item_price";
	public static final String KEY_PS_ITEM_CANCEL_PRICE			= "ps_item_cancel_price";
	public static final String KEY_PS_ITEM_ADVERTISE			= "ps_item_advertise";
	public static final String KEY_PS_ITEM_BUYER_ID				= "ps_item_buyer_id";
	public static final String KEY_PS_ITEM_END_ADVERTISE_TIME	= "ps_item_end_advertise_time";
	
	public static final String KEY_PS_LOCK_DATA					= "ps_lock_data";
	public static final String KEY_PS_VIEWER					= "ps_viewer";
	
	// ------------------------------------- GIFT  -------------------------------
	public static final String KEY_GIFT_LIST					= "gift_";
	public static final String KEY_GIFT_ID						= "gift_id";
	public static final String KEY_GIFT_NAME					= "gift_name";
	public static final String KEY_GIFT_DESCRIPTION				= "gift_description";
	public static final String KEY_GIFT_ITEM_LIST				= "gift_item_list";
	public static final String KEY_GIFT_AVAILABLE				= "gift_available";
	public static final String KEY_GIFT_INFO					= "gift_info";

	public static final String KEY_DAILY_GIFTS_RECEIVED			= "daily_gifts_received";
	public static final String KEY_DAILY_GIFTS					= "daily_gifts";
	public static final String KEY_DAILY_GIFTS_TIME_RANGE		= "daily_gifts_time_range";
	public static final String KEY_DAILY_GIFTS_TIME_RANGE_S		= "daily_gifts_time_range_s";
	
	// ------------------------------------- GIFT CODE -------------------------------
	public static final String KEY_GIFT_CODE_ID					= "gift_code_id";
	public static final String KEY_GIFT_CODE_SERVER_CODE		= "gift_code_server_code";
	public static final String KEY_GIFT_CODE_CLIENT_CODE		= "gift_code_client_code";
	public static final String KEY_GIFT_CODE_GIFT_CODE			= "gift_code_gift_code";
	public static final String KEY_GIFT_CODE_NAME				= "gift_code_name";
	public static final String KEY_GIFT_CODE_DESCRIPTION		= "gift_code_description";
	public static final String KEY_GIFT_CODE_GIFT				= "gift_code_gift";
	public static final String KEY_GIFT_CODE_START_TIME			= "gift_code_start_time";
	public static final String KEY_GIFT_CODE_END_TIME			= "gift_code_end_time";
	public static final String KEY_GIFT_CODE_USE_TIME			= "gift_code_use_time";
	public static final String KEY_GIFT_CODE_RECEIVED			= "gift_code_received";
	
	public static final String KEY_GIFT_CODE_ENTER				= "gift_code_enter";
	public static final String KEY_GIFT_CODE_ENTER_MAX			= "gift_code_enter_max";
	public static final String KEY_GIFT_CODE_ENTER_NUM			= "gift_code_enter_num";
	public static final String KEY_GIFT_CODE_TOTAL				= "gift_code_total";
	public static final String KEY_GIFT_TYPE_TOTAL				= "gift_type_total";
	public static final String KEY_GIFT_TYPE					= "gift_type";
	
	public static final String KEY_GIFT_TYPE_ID					= "gift_type_id";
	public static final String KEY_GIFT_TYPE_USE_TIME			= "gift_type_use_time";

	// ------------------------------------- DAILY GIFT -------------------------------
	public static final String KEY_USER_DAILY_GIFT_RECEIVE_TIME	= "user_daily_gift_receive_time";
	public static final String KEY_USER_DAILY_GIFT_GIFTS		= "user_daily_gift_gifts";
	
	// ------------------------------------- FRIEND -------------------------------
	public static final String KEY_FRIEND_COUNT					= "friend_count";
	public static final String KEY_FRIEND_LIST					= "friend_list";
	public static final String KEY_FRIEND_INDEX					= "friend_";
	public static final String KEY_FRIEND_NEED_HELP				= "friend_need_help_";
	public static final String KEY_FRIEND_LIST_STEP				= "friend_list_step";//for loadind step friend list	
	public static final String KEY_FRIEND_LIST_STEP_MAX			= "friend_list_step_max";//for loadind step friend list

	public static final String KEY_FRIEND_LIST_TOKEN			= "friend_list_token";
	public static final String KEY_FRIEND_LIST_INFO				= "friend_list_info";
	public static final String KEY_FRIEND_FB_LIST_INFO			= "friend_fb_list_info";
	public static final String KEY_FRIEND_ZING_LIST_INFO		= "friend_zing_list_info";
	public static final String KEY_FRIEND_ZALO_LIST_INFO		= "friend_zalo_list_info";
	public static final String KEY_PREVIOUS_FRIEND_DEVICE_ID	= "previous_friend_device_id";
	public static final String KEY_FRIEND_BUG					= "friend_bug";
	public static final String KEY_FRIEND_BUG_TYPE				= "friend_bug_type";
	public static final String KEY_FRIEND_BUG_ID				= "friend_bug_id";
	public static final String KEY_FRIEND_BUG_POS_X				= "friend_bug_pos_x";
	public static final String KEY_FRIEND_BUG_POS_Y				= "friend_bug_pos_y";
	
	// ------------------------------------- OTHERS -------------------------------
	public static final String KEY_SERVER_TIME_INT				= "second";
	public static final String KEY_SERVER_VERSION				= "server_version";
	
	public static final String KEY_FRIEND_INFOS					= "friend";
	public static final String KEY_FRIEND_ID					= "friend_id";
	public static final String KEY_FRIEND_DEVICE_ID				= "friend_device_id";
	
	public static final String KEY_RESET_ACCOUNT				= "reset_account";
	public static final String KEY_LOAD_FRIEND_SHOP				= "load_friend_shop";
	
	// ------------------------------------- ACHIEVEMENT -------------------------------
	public static final String KEY_ACHIEVEMENT_ID				= "achievement_id";
	public static final String KEY_ACHIEVEMENT_LEVEL			= "achievement_level";
	public static final String KEY_ACHIEVEMENT_ITEM_CUR			= "achievement_item_cur";
	public static final String KEY_ACHIEVEMENT_ITEM_MAX			= "achievement_item_max";
	public static final String KEY_ACHIEVEMENT_REWARD_COUNT		= "achievement_reward_count";
	public static final String KEY_ACHIEVEMENT_REWARD			= "achievement_reward_";
	public static final String KEY_ACHIEVEMENT_TOTAL			= "achievement_total";
	public static final String KEY_ACHIEVEMENT_INDEX			= "achievement_index_";
	public static final String KEY_ACHIEVEMENT_RECEIVED_GIFT	= "achievement_received_gift_";
	public static final String KEY_ACHIEVEMENT_RECEIVED_GIFT_SPECIAL = "achievement_received_gift_special";
	public static final String KEY_ACHIEVEMENT_GIFT_INDEX		= "achievement_gift_index";
	
	// ------------------------------------- PRODUCT/ITEM -------------------------------
	public static final String KEY_PROD_TYPE					= "prod_type";
	public static final String KEY_PROD_ID						= "prod_id";
	public static final String KEY_PROD_NUM						= "prod_num";
	
	// ------------------------------------- TUTORIAL -------------------------------
	public static final String KEY_TUTORIAL_ENABLE				= "tutorial_enable";
	public static final String KEY_TUTORIAL_STEP				= "tutorial_step";
	public static final String KEY_TUTORIAL_FIRST_PLANT_HAS_BUG	= "tutorial_first_plant_has_bug";
	public static final String KEY_TUTORIAL_STARTED				= "tutorial_started_";
	public static final String KEY_TUTORIAL_FINISHED			= "tutorial_finished_";
	public static final String KEY_TUTORIAL_EXP					= "tutorial_exp_";
	
	public static final String KEY_TUTORIAL_EXP_REWARD			= "tutorial_exp_reward";
	public static final String KEY_TUTORIAL_START				= "tutorial_start";
	public static final String KEY_TUTORIAL_FINISH				= "tutorial_finish";

	// ------------------------------------- OWL -------------------------------
	public static final String	KEY_OWL							= "owl";
	
	public static final String	KEY_OWL_POWER_LIMIT				= "owl_limit";
	public static final String	KEY_OWL_POWER_CURRENT			= "owl_limit_current";
	public static final String	KEY_OWL_SLOT_MAX				= "owl_slot_max";
	public static final String	KEY_OWL_SLOT_CUR				= "owl_slot_cur";
	public static final String	KEY_OWL_SLOT					= "owl_slot_";

	public static final String	KEY_OWL_FOOD_ID					= "owl_food_id";
	public static final String	KEY_OWL_DIGEST_TIME				= "owl_digest_time";
	
	public static final String	KEY_OWL_TOTAL_SLOT				= "owl_total_slot";
	public static final String	KEY_OWL_DIAMOND_UNLOCK_NEXT_SLOT= "owl_diamond_unlock_next_slot";
	public static final String	KEY_OWL_GOLD_UNLOCK_NEXT_SLOT	= "owl_gold_unlock_next_slot";
	
	// ------------------------------------- NPC BUY ITEM INFOS -------------------------------
	public static final String	KEY_NPC_BUY_ITEM_INFOS			= "npc_buy_item_infos";
	
	public static final String	KEY_NPC_BUY_ITEM_TIMER			= "npc_buy_item_timer";
	public static final String	KEY_NPC_BUY_ITEM_ID				= "npc_buy_item_id";
	public static final String	KEY_NPC_BUY_ITEM_NUM			= "npc_buy_item_num";
	public static final String	KEY_NPC_BUY_ITEM_PRICE			= "npc_buy_item_price";

	// ------------------------------------- EVENT -------------------------------
	public static final String	KEY_EVENT						= "event";
	public static final String	KEY_EVENT_MAIN_OBJECT			= "event_main_object";
	
	public static final String	KEY_EVENT_ID					= "event_id";
	public static final String	KEY_EVENT_NAME					= "event_name";
	public static final String	KEY_EVENT_START_TIME			= "event_start_time";
	public static final String	KEY_EVENT_END_TIME				= "event_end_time";
	public static final String	KEY_EVENT_START_TIME_BEFORE		= "event_start_time_before";
	public static final String	KEY_EVENT_END_TIME_BEFORE		= "event_end_time_before";
	public static final String	KEY_EVENT_ITEM_PRICE			= "event_item_price";
	public static final String	KEY_EVENT_ITEM_NAME				= "event_item_name";
	
	public static final String	KEY_EVENT_MO_ID					= "event_mo_id";
	public static final String	KEY_EVENT_MO_POS				= "event_mo_pos";
	public static final String	KEY_EVENT_MO_HIT_COUNT			= "event_mo_hit_count";
	public static final String	KEY_EVENT_MO_HIT_CURRENT		= "event_mo_hit_current";
	public static final String	KEY_EVENT_MO_FIRST_ACTION		= "event_mo_first_action";
	public static final String	KEY_EVENT_MO_LAST_ACTION		= "event_mo_last_action";
	public static final String	KEY_EVENT_MO_HIT_ACTION			= "event_mo_hit_action";
	public static final String	KEY_EVENT_MO_SPRITE_ID			= "event_mo_sprite_id";
	
	public static final String	KEY_EVENT_MI_ID					= "event_mi_id";
	
	// ------------------------------------- AIRSHIP -------------------------------
	public static final String KEY_AIRSHIP_ID								= "airship_id";
	public static final String KEY_AIRSHIP_REQUIRE_LEVEL					= "airship_require_level";
	public static final String KEY_AIRSHIP_REQUIRE_ITEMS					= "airship_require_items";
	public static final String KEY_AIRSHIP_UNLOCK_DURATION					= "airship_unlock_duration";
	public static final String KEY_AIRSHIP_STAY_DURATION					= "airship_stay_duration";
	public static final String KEY_AIRSHIP_LEAVE_DURATION					= "airship_leave_duration";
	public static final String KEY_AIRSHIP_MIN_POINT						= "airship_min_point";
	public static final String KEY_AIRSHIP_MAX_POINT						= "airship_max_point";
	public static final String KEY_AIRSHIP_HELP_TIME						= "airship_help_time";
	public static final String KEY_AIRSHIP_MIN_BONUS_GOLD					= "airship_min_bonus_gold";
	public static final String KEY_AIRSHIP_MAX_BONUS_GOLD					= "airship_max_bonus_gold";
	public static final String KEY_AIRSHIP_MIN_BONUS_EXP					= "airship_min_bonus_exp";
	public static final String KEY_AIRSHIP_MAX_BONUS_EXP					= "airship_max_bonus_exp";
	public static final String KEY_AIRSHIP_POINT_REDUCE						= "airship_point_reduce";
	public static final String KEY_AIRSHIP_CARGO_COUNT						= "airship_cargo_count";
	public static final String KEY_AIRSHIP_CARGO							= "airship_cargo_";
	public static final String KEY_AIRSHIP_UNLOCK_TIME						= "airship_unlock_time";
	public static final String KEY_AIRSHIP_UNLOCK_DIAMOND					= "airship_unlock_diamond";
	public static final String KEY_AIRSHIP_STATUS							= "airship_status";
	public static final String KEY_AIRSHIP_DIAMOND_PRICE					= "airship_diamond_price";
	public static final String KEY_AIRSHIP_LAST_LANDING_TIME				= "airship_last_landing_time";
	public static final String KEY_AIRSHIP_NEXT_LANDING_TIME				= "airship_next_landing_time";
	public static final String KEY_AIRSHIP_DEPART_TIME						= "airship_depart_time";
	public static final String KEY_AIRSHIP_GOLD								= "airship_gold";
	public static final String KEY_AIRSHIP_EXP								= "airship_exp";
	public static final String KEY_AIRSHIP_POINT							= "airship_point";
	public static final String KEY_AIRSHIP_REPUTATION						= "airship_reputation";
	public static final String KEY_AIRSHIP_PREVIEW_INFO						= "airship_preview_info";
	public static final String KEY_AIRSHIP_SKIP_DEPART_TIME_PRICE			= "airship_skip_depart_time_price";
	public static final String KEY_AIRSHIP_REPUTATION_REDUCE_PER_HOUR		= "airship_reputation_reduce_per_hour";
	public static final String KEY_AIRSHIP_CURRENT_NUM						= "airship_current_num";
	public static final String KEY_AIRSHIP_MAX_NUM_PER_DAY					= "airship_max_num_per_day";
	public static final String KEY_AIRSHIP_SECONDS_REDUCE_REPUTATION		= "airship_seconds_reduce_reputation";
	public static final String KEY_AIRSHIP_SECONDS_PER_DIAMOND				= "airship_seconds_per_diamond";
	public static final String KEY_AIRSHIP_LOCKER							= "airship_locker";
	
	// ------------------------------------- AIRSHIP CARGO -------------------------------
	public static final String KEY_CARGO_ID									= "cargo_id";
	public static final String KEY_CARGO_ITEM_TYPE							= "cargo_item_type";
	public static final String KEY_CARGO_ITEM_ID							= "cargo_item_id";
	public static final String KEY_CARGO_ITEM_NUM							= "cargo_item_num";
	public static final String KEY_CARGO_EXP								= "cargo_exp";
	public static final String KEY_CARGO_GOLD								= "cargo_gold";
	public static final String KEY_CARGO_REPUTATION							= "cargo_reputation";
	public static final String KEY_CARGO_IS_FINISHED						= "cargo_is_finished";
	public static final String KEY_CARGO_ASK_FOR_HELP						= "cargo_ask_for_help";
	public static final String KEY_CARGO_DIAMOND_PRICE						= "cargo_diamond_price";
	public static final String KEY_CARGO_FRIEND_ID							= "cargo_friend_id";
	
	// ------------------------------------- TOM THE KID -------------------------------
	public static final String KEY_TOM_KID_STATUS							= "tomkid_status";
	public static final String KEY_TOM_KID_FIRST_USED						= "tomkid_first_used";
	public static final String KEY_TOM_KID_LAST_HIRE_TIME					= "tomkid_last_hire_time";
	public static final String KEY_TOM_KID_EXPIRE_HIRE_TIME					= "tomkid_expire_hire_time";
	public static final String KEY_TOM_KID_PRICE_HIRE_1_DAY					= "tomkid_price_1_day";
	public static final String KEY_TOM_KID_PRICE_HIRE_3_DAY					= "tomkid_price_3_day";
	public static final String KEY_TOM_KID_PRICE_HIRE_7_DAY					= "tomkid_price_7_day";
	public static final String KEY_TOM_KID_LONG_REST_DURATION				= "tomkid_long_rest";
	public static final String KEY_TOM_KID_SHORT_REST_DURATION				= "tomkid_short_rest";
	public static final String KEY_TOM_KID_SUGGEST_ITEM						= "tomkid_suggest_item_";
	public static final String KEY_TOM_KID_SUGGEST_ITEM_ID					= "tomkid_suggest_item_id";
	public static final String KEY_TOM_KID_ITEM_TYPE						= "tomkid_item_type";
	public static final String KEY_TOM_KID_ITEM_ID							= "tomkid_item_id";
	public static final String KEY_TOM_KID_ITEM_NUM							= "tomkid_item_num";
	public static final String KEY_TOM_KID_ITEM_GOLD_PRICE					= "tomkid_item_gold_price";
	public static final String KEY_TOM_KID_SUGGEST_COUNT					= "tomkid_suggest_count";
	public static final String KEY_TOM_KID_LAST_WORKING_TIME				= "tomkid_last_working_time";
	public static final String KEY_TOM_KID_NEXT_WORKING_TIME				= "tomkid_next_working_time";
	public static final String KEY_TOM_KID_HIRE_PACK						= "tomkid_hire_pack";
	public static final String KEY_TOM_KID_UNLOCK_LEVEL						= "tomkid_unlock_level";

	// ------------------------------------- FUNCTION PARAMS -------------------------------
	// MOVE POT
	public static final String KEY_MOVE_POT_SRC_FLOOR_INDEX		= "floor_index_src";
	public static final String KEY_MOVE_POT_SRC_SLOT_INDEX		= "slot_index_src";
	public static final String KEY_MOVE_POT_DES_FLOOR_INDEX		= "floor_index_des";
	public static final String KEY_MOVE_POT_DES_SLOT_INDEX		= "slot_index_des";
	
	public static final String KEY_LUCKY_LEAF			   		= "lucky_leaf_id";
	public static final String KEY_AMULET						= "amulet";
	
	public static final String KEY_PUT_POT						= "put_pot_";
	public static final String KEY_PLANT_FLOWER					= "plant_";
	public static final String KEY_HARVEST						= "harvest_";
	public static final String KEY_CATCH_BUG					= "catch_bug_";
	public static final String KEY_PLANT_INSTANT_GROW			= "instant_grow_";
	
	// DECOR
	public static final String KEY_PLACE_DECOR					= "place_decor_";
	
	// FERTILIZER
	public static final String KEY_USE_FERTILIZER				= "use_fertilizer_";
	
	// SHARE SOCIAL
	public static final String KEY_SHARE_CONTENT				= "social_share_content";
	
	public static final String KEY_EXP_RECEIVE					= "exp_receive";
	public static final String KEY_EXP_RECEIVE_SLOT				= "exp_receive_slot_";
	public static final String KEY_BUG_COUNT					= "bug_count";
	public static final String KEY_NEXT_TIME_BUG_APPEAR			= "next_time_bug_appear";
	public static final String KEY_BUG_POOL						= "bug_pool";
		
	// SHEET
	public static final String KEY_MAX_ROW						= "max_row";
	public static final String KEY_MAX_COL						= "max_column"; 
	
	// FRIEND
	public static final String FRIEND_DEVICE_ID					= "friend_device_id";
	public static final String FRIEND_USER_ID					= "friend_user_id";
	public static final String FRIEND_FB_ID						= "friend_fb_id";
	public static final String KEY_FACEBOOK_FRIEND_PARAM		= "fb_friend_param";
	public static final String KEY_ZING_FRIEND_PARAM			= "zing_friend_param";
	public static final String KEY_RECEIVED_GIFT_INVITE_FRIEND	= "received_gift_invite_";
	public static final String KEY_GIFT_INVITE_FRIEND_INDEX		= "gift_invite_friend_index";
	public static final String KEY_NUM_FRIEND_INVITED			= "num_friend_invited";
	
	//
	public static final String REQUESTED_USER_ID				= "requested_user_id";
	public static final String REQUESTED_SESSION_ID				= "requested_session_id";
	
	// NEWS BOARD
	public static final String KEY_ADS_LIMIT_TIME				= "ads_limit_time";
	public static final String KEY_ADS_NUM						= "ads_num";
	public static final String KEY_ADS_USER_ID					= "ads_user_id_";
	public static final String KEY_ADS_USER_ITEM				= "ads_user_item_";

	public static final String KEY_ADS_USER_INFO				= "ads_user_info_";
	
	public static final String KEY_ADS_KEY						= "ads_key";
	public static final String KEY_ADS_ITEM						= "ads_item";
	public static final String KEY_ADS_LIST						= "ads_list";
	
	// SERVER COMMUNICATION
	public static final String KEY_SERVER_ADDRESS				= "s_address";
	public static final String KEY_SERVER_PORT					= "s_port";
	public static final String KEY_SERVER_LIST					= "server_list";
	public static final String KEY_TOTAL_SERVER					= "total_server_number";
	public static final String KEY_SERVER_INDEX					= "server_";
	
	// MONEY USED
	public static final String KEY_GOLD_USED					= "gold_used";
	public static final String KEY_EXP_USED						= "exp_used";
	public static final String KEY_DIAMOND_USED					= "diamond_used";
	public static final String KEY_REPUTATION_USED				= "reputation_used";
	public static final String KEY_ITEM_NUM_USED				= "item_num_used";
	
	// GMKEY
	public static final String KEY_GM_ID						= "gm_id";
	public static final String KEY_GM_TOKEN						= "gm_token";
	public static final String KEY_GM_COMMAND					= "gm_command";
	public static final String KEY_GM_SESSION					= "gm_session";
	public static final String KEY_GM_LOGIN_TIME				= "gm_login_time";
	public static final String KEY_BROADCAST_CONTENT			= "broadcast_content";
	public static final String KEY_BROADCAST_CONTENT_LIST		= "broadcast_content_list";
	public static final String KEY_BROADCAST_REPEAT_TIMES		= "broadcast_repeat_times";
	public static final String KEY_BROADCAST_DURATION           = "broadcast_duration";
	public static final String KEY_CLOUD_MESSAGE_CONTENT		= "cloud_content";
	public static final String KEY_CLOUD_MESSAGE_REG_LIST		= "cloud_reg_list";
	public static final String KEY_CLOUD_MESSAGE_OS				= "cloud_os";
	
	// PAYMENT
	public static final String KEY_PAYMENT_CARD_SERIALNO		= "card_serialno";
	public static final String KEY_PAYMENT_CARD_PIN				= "card_pin";
	public static final String KEY_PAYMENT_CARD_TYPE			= "card_type";
	public static final String KEY_PAYMENT_RESULT				= "transaction_result";
	public static final String KEY_PAYMENT_ENABLE				= "payment_enable";
	public static final String KEY_PAYMENT_APPSTORE_ENABLE		= "payment_appstore_enable";
	public static final String KEY_PAYMENT_USE_FIRST_PAY		= "payment_first_pay";
	
	public static final String KEY_USE_WIFI						= "use_wifi";
	public static final String KEY_CARRIER_NAME					= "carrier_name";
	public static final String KEY_DISTRIBUTOR					= "distributor";
	public static final String KEY_FLURRY						= "flurry";
	public static final String KEY_EMAIL_SUPPORT				= "email_support";
	public static final String KEY_PHONE_SUPPORT				= "phone_support";
	public static final String KEY_ONLINE_SUPPORT				= "online_support";
	
	// npc name	
	public static final String NPC_NAME							= "NPC_JACK";
	
	public static final String ONLINE							= "online";
	public static final String BAN								= "ban";
	
	public static final String KEY_LAST_PAY_TIME				= "last_pay";
	public static final String KEY_FIRST_PAY_CAMPAIGN_STATUS	= "firstpay_status";
	public static final String KEY_TOTAL_PAID					= "total_paid";
	
	public static final String KEY_EVENT_NOTIFY_ENABLE			= "key_event_notify_enable";
	public static final String KEY_EVENT_NOTIFY_TYPE			= "key_event_notify_type";
	public static final String KEY_EVENT_NOTIFY_NAME			= "key_event_notify_name";
	public static final String KEY_EVENT_NOTIFY_DETAILS			= "key_event_notify_details";
	public static final String KEY_EVENT_NOTIFY_PLATFORM		= "key_event_notify_platform";
	public static final String KEY_EVENT_NOTIFY_IMG_SMALL		= "key_event_notify_img_small";
	public static final String KEY_EVENT_NOTIFY_IMG_LARGE		= "key_event_notify_img_large";
	public static final String KEY_EVENT_NOTIFY_IMG_SMALL_MD5	= "key_event_notify_img_small_md5";
	public static final String KEY_EVENT_NOTIFY_IMG_LARGE_MD5	= "key_event_notify_img_large_md5";
	
	// ATM
	public static final String KEY_ATM_GAME_ID					= "key_game_id";
	public static final String KEY_ATM_GAME_TOKEN				= "key_atm_token";
	public static final String KEY_ATM_ADD_INFO					= "key_atm_add_info";	
	public static final String KEY_ATM_TARGET_URL				= "key_atm_target_url";
	public static final String KEY_ATM_IMAGE					= "key_atm_image";
	public static final String KEY_ATM_MD5						= "key_atm_md5";
	public static final String KEY_ATM_HAS_PROMOTION			= "key_atm_has_promotion";
	
	// MERCHANT
	public static final String KEY_MERCHANT_LIST				= "merchant_";
	public static final String KEY_MERCHANT_ID					= "merchant_id";
	
	// FEED
	public static final String KEY_FEED							= "key_feed";
	public static final String KEY_FEED_TITLE					= "key_feed_title";
	public static final String KEY_FEED_DESCRIPTION				= "key_feed_description";
	public static final String KEY_FEED_LINK					= "key_feed_link";
	public static final String KEY_FEED_IMG_LINK				= "key_feed_img_link";
	public static final String KEY_FEED_CAPTION					= "key_feed_caption";
	
	public static final String FEED_TYPE_POT					= "p";
	public static final String FEED_TYPE_MACHINE				= "m";
	public static final String FEED_TYPE_LEVEL					= "l";
	public static final String FEED_TYPE_FLOOR					= "f";
	
	public static final String FEED_ACTION_UPGRADE				= "0";
	public static final String FEED_ACTION_UPGRADE_BLESSING		= "1";		// nang cap chau vuot cap
	public static final String FEED_ACTION_UNLOCK_MACHINE		= "2";
	
	public static final String FEED_ACTION_LEVEL_UP				= "0";
	public static final String FEED_ACTION_OVERTAKE_FRIEND		= "1";		// vuot level ban be

	public static final String FEED_ACTION_OPEN_FLOOR			= "0";
	
	// GCM
	public static final String KEY_GCM_ID						= "gcm_id";
	public static final String KEY_REGISTERED_GCM_ANDROID		= "gcm_has_reg_id_android";
	public static final String KEY_REGISTER_GCM_IOS				= "gcm_has_reg_id_ios";
	
	public static final String FIRST_PAY_ADS_LINK				= "http://sgmb.static.g6.zing.vn/ads/firstpay.png";
	public static final String FIRST_PAY_ADS_LINK_IOS			= "http://sgmb.static.g6.zing.vn/ads/firstpay_ios.png";
	public static final String FIRST_PAY_ADS_LINK_SD			= "http://sgmb.static.g6.zing.vn/ads/firstpay_sd.png";
	public static final String KEY_FIRST_PAY_ADS_LINK			= "key_first_pay_link";
	public static final String KEY_FIRST_PAY_ADS_MD5			= "key_first_pay_md5";
	public static final String FIRST_PAY_ADS_MD5				= "1f2e5bc377b745954addacf0f237fb54";
	public static final String FIRST_PAY_ADS_MD5_IOS			= "61026a43b78f7430950e15c4fe1e0cf5";
	public static final String FIRST_PAY_ADS_MD5_SD				= "e956a6fce03cdf0a8025d360d12fbcdd";
	
	// CROSS PROMOTION
	public static final String KEY_CROSS_PROMOTION				= "cross_promotion";
	public static final String KEY_CROSS_PROMOTION_ANDROID		= "cross_promotion_android";
	public static final String KEY_CROSS_PROMOTION_IOS			= "cross_promotion_ios";
	public static final String KEY_CROSS_PROMOTION_START_TIME	= "cross_promotion_start_time";
	public static final String KEY_CROSS_PROMOTION_END_TIME		= "cross_promotion_end_time";
	public static final String KEY_CROSS_PROMOTION_AVAILABLE	= "cross_promotion_available";
	public static final String KEY_CROSS_PROMOTION_IMAGE		= "cross_promotion_img";
	public static final String KEY_CROSS_PROMOTION_IMAGE_MD5	= "cross_promotion_img_md5";
	public static final String KEY_CROSS_PROMOTION_GIFT			= "cross_promotion_gift";
	public static final String KEY_CROSS_PROMOTION_TARGET_LINK	= "cross_promotion_target_link";
	public static final String KEY_CROSS_PROMOTION_PACKAGE_NAME	= "cross_promotion_package_name";
	public static final String KEY_CROSS_PROMOTION_APP_INFO		= "cross_promotion_app_info";
	
	// ZALO PAYMENT
	public static final String KEY_ZALO_PAYMENT_APP_TIME				= "app_time";
	public static final String KEY_ZALO_PAYMENT_APP_TRANSACTION_ID		= "app_transaction_id";
	public static final String KEY_ZALO_PAYMENT_AMOUNT					= "amount";
	public static final String KEY_ZALO_PAYMENT_DESCRIPTION				= "description";
	public static final String KEY_ZALO_PAYMENT_EMBED_DATA				= "embed_data";
	public static final String KEY_ZALO_PAYMENT_MAC						= "mac";
	public static final String KEY_ZALO_SECRET_KEY						= "secrect_key";
	
	public static final String NEWSBOARD_SLOT_CHAR				= "~";
	public static final String NEWSBOARD_ITEMS_LIST_PATH		= "./..log/newsboard_items_list.log";
	
	public static final String KEY_CDN							= "key_cdn";
	public static final String KEY_CDN_LINK						= "key_cdn_link_";
	public static final String KEY_CDN_SPRITE_ID				= "key_cdn_sprite_id_";
	public static final String KEY_CDN_NAME						= "key_cdn_name_";
	public static final String KEY_CDN_MD5						= "key_cdn_md5_";
	public static final String KEY_CDN_PLATFORM					= "key_cdn_platform_";
	public static final String KEY_CDN_RESOLUTION				= "key_cdn_resolution_";
	public static final String KEY_CDN_TYPE						= "key_cdn_type_";
	public static final String KEY_CDN_ID						= "key_cdn_id_";
	public static final String KEY_CDN_TEXT_ID					= "key_cdn_text_id_";

	public static final String KEY_SHOW_MACHINE_ALERT			= "key_show_machine_alert";
	public static final String KEY_NUM_FRIEND_ALERT				= "key_num_friend_alert";
	public static final String KEY_ALERT						= "key_alert";
	
	public static final String KEY_USER_RANK					= "user_rank";
	public static final String KEY_RANK_RESULT					= "rank_result_";
	public static final String KEY_RANKING_USER_RECORD			= "ranking_user_record";
	public static final String KEY_RANKING_DELTA				= "ranking_user_delta";
	public static final String KEY_USER_RANKING_TARGET			= "user_ranking_target";
	public static final String KEY_RANKING_COUNT				= "ranking_count";
	public static final String KEY_RANKING_LAST_UDPATE			= "ranking_last_update";
	public static final String KEY_RANKING_INFO					= "ranking_info";
	public static final String KEY_RANKING_INFO_ACTIVE			= "ranking_info_active";
	public static final String KEY_RANKING_INFO_PREVIOUS		= "ranking_info_previous";
	public static final String KEY_RANKING_HARVEST				= "ranking_harvest";
	public static final String KEY_RANKING_SHOW_DETAISL_TIME	= "ranking_show_details_time";
	public static final String KEY_RANKING_START_TIME			= "ranking_start_time";
	public static final String KEY_RANKING_START_TIME_STR		= "ranking_start_time_str";
	public static final String KEY_RANKING_END_TIME				= "ranking_end_time";
	public static final String KEY_RANKING_END_TIME_STR			= "ranking_end_time_str";
	public static final String KEY_RANKING_DESCRIPTION			= "ranking_description";
	public static final String KEY_RANKING_CATEGORY				= "ranking_ranking_category";
	public static final String KEY_RANKING_TEXT_TEMPLATE		= "ranking_text_template";
	public static final String KEY_RANKING_TARGET				= "ranking_target";
	public static final String KEY_RANKING_UNIT					= "ranking_unit";
	public static final String KEY_RANKING_GIFT_1				= "ranking_gift_rank_1";
	public static final String KEY_RANKING_GIFT_2				= "ranking_gift_rank_2";
	public static final String KEY_RANKING_GIFT_3				= "ranking_gift_rank_3";
	public static final String KEY_RANKING_GIFT_100				= "ranking_gift_rank_100";
	public static final String KEY_RANKING_GIFT_BASIC			= "ranking_gift_rank_basic";
	public static final String KEY_RANKING_SIZE					= "ranking_size";
	public static final String KEY_RANKING_ACCUMULATION			= "ranking_current_accumulation";
	public static final String KEY_RANKING_FINAL_RESULT			= "ranking_final_result";
	public static final String KEY_RANKING_GIFT_RECEIVED		= "ranking_gift_received";
	public static final String KEY_RANKING_RECEIVED_BASIC_GIFT	= "ranking_received_basic_gift";
	public static final String KEY_RANKING_CLIENT_REFRESH		= "ranking_client_refresh_time";
	public static final String KEY_RANKING_IMG					= "ranking_img";
	public static final String KEY_RANKING_IMG_MD5				= "ranking_img_md5";
	public static final String KEY_RANKING_DETAILS				= "ranking_details";
	public static final String KEY_RANKING_DETAILS_MD5			= "ranking_details_md5";
	public static final String KEY_RANKING_TEMP_TEXT_1			= "ranking_tmp_text_1";
	
	// Rota fortunae
	public static final String KEY_ROTA_FORTUNAE_ID				= "rota_fortunae_id";
	public static final String KEY_ROTA_FORTUNAE_IS_PAID		= "rota_fortunae_is_paid";
	public static final String KEY_ROTA_FORTUNAE_DIAMOND_PRICE	= "rota_fortunae_price";
	public static final String KEY_ROTA_FORTUNAE_GIFT_REAL		= "rota_fortunae_gift_real";
	public static final String KEY_ROTA_FORTUNAE_GIFT_FAKE		= "rota_fortunae_gift_fake";
	public static final String KEY_ROTA_FORTUNAE_IS_USED		= "rota_fortunae_is_used";
	public static final String KEY_ROTA_FORTUNAE_TOTAL			= "rota_fortunae_total";
	public static final String KEY_ROTA_FORTUNAE_TOTAL_RETRY	= "rota_fortunae_total_retry";
	public static final String KEY_ROTA_FORTUNAE_START_TIME		= "rota_fortunae_start_time";
	public static final String KEY_ROTA_FORTUNAE_END_TIME		= "rota_fortunae_end_time";
	public static final String KEY_ROTA_FORTUNAE_GOLD_LIST		= "rota_fortunae_gold_list";
	public static final String KEY_ROTA_FORTUNAE_TOTAL_STAR		= "rota_fortunae_total_star";
	public static final String KEY_ROTA_FORTUNAE_IS_BOUGHT		= "rota_fortunae_is_bought";
	public static final String KEY_ROTA_FORTUNAE_IS_SALE_OFF	= "rota_fortunae_is_sale_off";
	public static final String KEY_ROTA_FORTUNAE_DIAMOND_SALE_OFF	= "rota_fortunae_price_sale_off";

	public static final String KEY_ROTA_FORTUNAE_INDEX			= "rota_fortunae_";
	
	// EVENTS
	public static final String KEY_EVENT_INFO					= "event_info";
	public static final String KEY_EVENT_MID_AUTUMN_FESTIVAL	= "event_mid_autumn_festival";
	public static final String KEY_EVENT_EXPORT					= "event_export";
	public static final String KEY_EVENT_HALLOWEEN				= "event_halloween";
	public static final String KEY_EVENT_20_11					= "event_20_11";
	public static final String KEY_EVENT_UPGRADE_POT			= "event_upgrade_pot";
	public static final String KEY_EVENT_XMAS_2014				= "event_xmas_2014";
	public static final String KEY_EVENT_XMAS_TREE_2014			= "event_xmas_tree_2014";
	public static final String KEY_EVENT_XMAS_MINI				= "event_xmas_mini";
	public static final String KEY_EVENT_NEW_YEAR_2015			= "event_new_year_2015";
	public static final String KEY_EVENT_ORDER_JAN_2015			= "event_order_jan_2015";
	public static final String KEY_EVENT_8_3_2015				= "event_new_year_2015";
	public static final String KEY_EVENT_BIRTDAY_2015			= "event_birthday_2015";
	
	// MAILBOX
	public static final String KEY_MAIL_BOX						= "mailbox";
	public static final String KEY_MAIL							= "mail";
	public static final String KEY_MAIL_NUM						= "mail_num";
	public static final String KEY_MAIL_DATE					= "mail_date";
	public static final String KEY_MAIL_SENDER					= "mail_sender";
	public static final String KEY_MAIL_TITLE					= "mail_title";
	public static final String KEY_MAIL_CONTENT					= "mail_content";
	public static final String KEY_MAIL_GIFT_LIST				= "mail_gift";
	public static final String KEY_MAIL_READ					= "mail_read";
	public static final String KEY_MAIL_INDEX					= "mail_index";
	public static final String KEY_NEW_MAIL						= "mail_new";
	
	// CHAT
	public static final String KEY_CHAT_SESSION					= "chat_session";
	public static final String KEY_CHAT_MESSAGE					= "chat_message";
	public static final String KEY_LAST_MESSAGE_IDX				= "last_message_idx";
	public static final String KEY_MESSAGE_NUM					= "message_num";
	public static final String KEY_SENDER_ID					= "sender_id";
	public static final String KEY_SENDER_NAME					= "sender_name";
	public static final String KEY_MESSAGE_TYPE					= "message_type";
	public static final String KEY_MESSAGE_CONTENT				= "message_content";
	public static final String KEY_ROOM_ID						= "room_id";
	public static final String KEY_MESSAGE_PACK					= "message_pack_";
	public static final String KEY_ROOM_CAPACITY_MAX			= "room_capacity_max";
	public static final String KEY_ROOM_REFRESH_DURATION		= "room_refresh_duration";
	public static final String KEY_ROOM_MESSAGE_LENGTH			= "room_message_length";
	public static final String KEY_ROOM_MESSAGE_DELAY			= "room_message_delay";
	public static final String KEY_ROOM_WELCOME					= "room_welcome_message";
	public static final String KEY_ROOM_OWNER					= "room_owner";
	public static final String KEY_CHAT_ENABLE					= "chat_enable";
	public static final String KEY_CHAT_ADDRESS					= "chat_address";
	public static final String KEY_CHAT_PORT					= "chat_port";
	
	// APPLE PAYMENT VALIDATE
	public static final String KEY_RECEIPT_DATA					= "receipt_data";
	public static final String KEY_RECEIPT_PASSWORD				= "receipt_password";
	public static final String KEY_APPLE_IAB_PRODUCT_ID			= "apple_IAB_product_id";
	
	// ANDROID PAYMENT VALIDATE
	public static final String KEY_ANDROID_DEVELOPER_PAYLOAD	= "android_developer_payload";
	public static final String KEY_ANDROID_IAB_RESPONSE_CODE	= "android_IAB_response_code";
	public static final String KEY_ANDROID_IAB_PURCHASE_DATA	= "android_IAP_purchase_data";
	public static final String KEY_ANDROID_IAP_DATA_SIGNATURE	= "andorid_IAP_data_signature";
	public static final String KEY_ANDROID_IAB_PRODUCT_ID		= "android_IAB_product_id";
	
	// WIN PAYMENT VALIDATE
	public static final String KEY_WIN_PRODUCT_ID				= "win_IAB_product_id";
	
	// event halloween
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_DAILY_ORDER	= "event_drop_num_feature_daily_order";
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_ORDER_NORMAL = "event_drop_num_feature_normal";
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_ORDER_EVENT	= "event_drop_num_feature_order_event";
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_AIRSHIP		= "event_drop_num_feature_airship";
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_TOM			= "event_drop_num_feature_tom";
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_FORTUNE		= "event_drop_num_feature_fortune";
	public static final String KEY_ITEM_EVENT_DROP_FEATURE_MERCHANTE	= "event_drop_num_feature_merchant";
	public static final String KEY_STOCK_EVENT							= "stock_event";
	
	// GAME FEATURE
	public static final String KEY_USE_NPC_CUSTOME						= "use_npc_custome";
	public static final String KEY_SNOW									= "use_snow";
	public static final String KEY_FORTUNE_WHEEL						= "fortune_wheel";
	public static final String KEY_USE_NPC_FRIEND_GARDEN				= "npc_friend_garden";
	public static final String KEY_BROADCAST_FREQUENCY					= "broadcast_frequency";
	public static final String KEY_CLOSE_FRIEND_IMG						= "close_friend_img";
	public static final String KEY_CLOSE_FRIEND_LINK					= "close_friend_link";
	
	// APPLYER
	public static final String APPFLYER_MEDIA_SOURCE_KEY 		= "media_source";
	public static final String APPFLYER_CAMPAIGN_NAME_KEY 		= "campaign_name";
	public static final String APPFLYER_COMPAIGN_ID_KEY			= "campaign_id";
	public static final String APPFLYER_IS_FB_KEY 				= "is_fb";
	public static final String APPFLYER_ADGROUP_NAME_KEY		= "adgroup_name";
	public static final String APPFLYER_ADGROUP_ID_KEY			= "adgroup_id";
	public static final String APPFLYER_ADSET_NAME_KEY			= "adset_name";
	public static final String APPFLYER_ADSET_ID_KEY			= "adset_id";
	public static final String APPFLYER_AD_ID_KEY				= "ad_id";
	public static final String APPFLYER_AF_SITEID_KEY			= "af_siteid";
	public static final String APPFLYER_AF_SUB1_KEY				= "af_sub1";
	public static final String APPFLYER_AF_SUB2_KEY				= "af_sub2";
	public static final String APPFLYER_AF_SUB3_KEY				= "af_sub3";
	public static final String APPFLYER_AF_SUB4_KEY				= "af_sub4";
	public static final String APPFLYER_AF_SUB5_KEY				= "af_sub5";
	public static final String APPFLYER_INSTALL_TIME_KEY		= "install_time";
	public static final String APPFLYER_ID						= "af_id";
	public static final String PIG_ID							= "pig_id";
	public static final String DEVICE_OS						= "device_os";
	
	// force change server
	public static final String KEY_FORWARD_SERVER_IP					= "forward_server_ip";
	public static final String KEY_FORWARD_SERVER_PORT					= "forward_server_port";
	
	// new offer system
	public static final String KEY_OFFER_BUG					= "offer_bug";
	public static final String KEY_OFFER_GEM					= "offer_gem";
	public static final String KEY_OFFER_FLOOR					= "offer_floor";
	public static final String KEY_OFFER_MACHINE				= "offer_machine";
	public static final String KEY_OFFER_LUCKY_LEAF_GREEN		= "offer_lucky_leaf";
	public static final String KEY_OFFER_LUCKY_LEAF_PURPLE		= "offer_lucky_leaf_purple";
	public static final String KEY_OFFER_GOLD					= "offer_gold";
	
	// bonus system
	public static final String KEY_BONUS_PLANT					= "bonus_plant";
	public static final String KEY_BONUS_AIRSHIP_GOLD			= "bonus_airship_gold";
	public static final String KEY_BONUS_AIRSHIP_EXP			= "bonus_airship_exp";
	public static final String KEY_BONUS_ORDER_NORMAL			= "bonus_order_normal";
	public static final String KEY_BONUS_ORDER_DAILY			= "bonus_order_daily";
	
	// decor bonus
	public static final String KEY_BONUS_PLANT_EXP				= "bonus_plant_exp";
	public static final String KEY_BONUS_PLANT_TIME				= "bonus_plant_time";
	public static final String KEY_BONUS_BUG					= "bonus_bug";
	
	public static final String KEY_NUM_GIFT_FROM_FRIEND			= "num_gift_from_friend";
	
	public static final String KEY_RESPONSE_STRING				= "resonse_string";
	
	// new daily gift
	public static final String KEY_ID							= "key_id";
	public static final String KEY_START						= "key_start";
	public static final String KEY_END							= "key_end";
	public static final String KEY_DIAMOND						= "key_diamond";
	public static final String KEY_ATTEND						= "key_attend";
	public static final String KEY_REATTEND						= "key_reattend";
	public static final String KEY_MAX							= "key_max";
	public static final String KEY_DAY							= "key_day_";
	public static final String KEY_CURRENT_DAY					= "key_current_day";
	public static final String KEY_NEXT_RESET_TIME				= "key_next_reset_time";
	public static final String KEY_RECEIVED_GIFT				= "key_received_gift";
	public static final String KEY_ATTENT_COUNT					= "key_last_attend_id";
	public static final String KEY_UNATTEND_DAY					= "key_unattend_day";
	public static final String KEY_IS_SPECIAL						= "key_is_special";
	
	// combo
	public static final String KEY_COMBO						= "key_combo";
	
	public static final String KEY_MAX_DEFAULT					= "key_max_default";
	public static final String KEY_ALLOW						= "key_allow";
	
	public static final String KEY_PERSONAL_BONUS				= "key_personal_bonus";
	public static final String KEY_PERSONAL_BONUS_END_TIME		= "key_personal_bonus_end_time";
	public static final String KEY_PERSONAL_BONUS_GOLD			= "key_personal_bonus_gold";
	public static final String KEY_PERSONAL_BONUS_EXP			= "key_personal_bonus_exp";
	
	public static final String KEY_NUM_BRONZE					= "num_bronze";
	public static final String KEY_NUM_SILVER					= "num_silver";
	public static final String KEY_NUM_GOLD						= "num_gold";
	public static final String KEY_NUM_BRONZE_KEY				= "num_bronze_key";
	public static final String KEY_NUM_SILVER_KEY				= "num_silver_key";
	public static final String KEY_NUM_GOLD_KEY					= "num_gold_key";
	public static final String KEY_COUNT_BRONZE					= "num_count_bronze";
	public static final String KEY_COUNT_SILVER					= "num_count_silver";
	public static final String KEY_COUNT_GOLD					= "num_count_gold";
	public static final String KEY_GIFT_BRONZE					= "gift_bronze";
	public static final String KEY_GIFT_SILVER					= "gift_silver";
	public static final String KEY_GIFT_GOLD					= "gift_gold";
	
	public static final String KEY_PAYMENT_TYPE					= "payment_type";
}