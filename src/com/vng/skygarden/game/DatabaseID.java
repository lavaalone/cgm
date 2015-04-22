package com.vng.skygarden.game;

public final class DatabaseID
{
	// ------------------------------------- SHEET_INDEX -------------------------------
	public static final int SHEET_USER							= 0;
	public static final int SHEET_USER_LEVEL					= SHEET_USER + 1;
	public static final int SHEET_SEED							= SHEET_USER_LEVEL + 1;
	public static final int SHEET_POT							= SHEET_SEED + 1;
	public static final int SHEET_PEST							= SHEET_POT + 1;
	public static final int SHEET_STOCK							= SHEET_PEST + 1;
	public static final int SHEET_STOCK_UPGRADE					= SHEET_STOCK + 1;
	public static final int SHEET_MACHINE						= SHEET_STOCK_UPGRADE + 1;
	public static final int SHEET_MACHINE_UNLOCK				= SHEET_MACHINE + 1;
	public static final int SHEET_MACHINE_UNLOCK_PROD_SLOT		= SHEET_MACHINE_UNLOCK + 1;
	public static final int SHEET_PRODUCT						= SHEET_MACHINE_UNLOCK_PROD_SLOT + 1;
	public static final int SHEET_MATERIAL						= SHEET_PRODUCT + 1;
	public static final int SHEET_FLOOR_UNLOCK					= SHEET_MATERIAL + 1;
	public static final int SHEET_IBSHOP_ITEM					= SHEET_FLOOR_UNLOCK + 1;
	public static final int SHEET_PRIVATE_SHOP					= SHEET_IBSHOP_ITEM + 1;
	public static final int SHEET_ACHIEVEMENT					= SHEET_PRIVATE_SHOP + 1;
	public static final int SHEET_DIAMOND_SKIP_TIME				= SHEET_ACHIEVEMENT + 1;
	public static final int SHEET_ITEM_DROP						= SHEET_DIAMOND_SKIP_TIME + 1;
	public static final int SHEET_GOLD_XP_COEFFICIENT			= SHEET_ITEM_DROP + 1;
	public static final int SHEET_PAYMENT						= SHEET_GOLD_XP_COEFFICIENT + 1;
	public static final int SHEET_DAILY_ORDER					= SHEET_PAYMENT + 1;
	public static final int SHEET_TUTORIAL						= SHEET_DAILY_ORDER + 1;
	public static final int SHEET_VERSION						= SHEET_TUTORIAL + 1;
	public static final int SHEET_FRIEND_BUG					= SHEET_VERSION + 1;
	public static final int SHEET_DECOR							= SHEET_FRIEND_BUG + 1;
	public static final int SHEET_MACHINE_APPRAISAL				= SHEET_DECOR + 1;
	public static final int SHEET_OWL_SLOT_UNLOCK				= SHEET_MACHINE_APPRAISAL + 1;
	public static final int SHEET_GIFTS_INFO					= SHEET_OWL_SLOT_UNLOCK + 1;
	public static final int SHEET_NOTIFY_TEXT					= SHEET_GIFTS_INFO + 1;
	public static final int SHEET_DAILY_GIFT					= SHEET_NOTIFY_TEXT + 1;
	public static final int SHEET_FIRST_PAY						= SHEET_DAILY_GIFT + 1;
	public static final int SHEET_CONSTANT						= SHEET_FIRST_PAY + 1;
	public static final int SHEET_EVENT							= SHEET_CONSTANT + 1;
	public static final int SHEET_EVENT_MAIN_OBJECT				= SHEET_EVENT + 1;
	public static final int SHEET_EVENT_MAIN_ITEM				= SHEET_EVENT_MAIN_OBJECT + 1;
	public static final int SHEET_GIFT_ALPHA_TEST				= SHEET_EVENT_MAIN_ITEM + 1;
	public static final int SHEET_NPC_MERCHANT					= SHEET_GIFT_ALPHA_TEST + 1;
	public static final int SHEET_FEED_INFO						= SHEET_NPC_MERCHANT + 1;
	public static final int SHEET_CDN							= SHEET_FEED_INFO + 1;
	public static final int SHEET_PAYMENT_EVENT					= SHEET_CDN + 1;
	public static final int SHEET_EVENT_GLOBAL					= SHEET_PAYMENT_EVENT + 1;
	public static final int SHEET_AIRSHIP						= SHEET_EVENT_GLOBAL + 1;
	public static final int SHEET_ITEMS_VALUES					= SHEET_AIRSHIP + 1;
	public static final int SHEET_TOM_KID						= SHEET_ITEMS_VALUES + 1;
	public static final int SHEET_ROTA_FORTUNAE					= SHEET_TOM_KID + 1;
	public static final int SHEET_ROTA_FORTUNAE_ITEMS			= SHEET_ROTA_FORTUNAE + 1;
	public static final int SHEET_INVITE_FRIEND					= SHEET_ROTA_FORTUNAE_ITEMS + 1;
	public static final int SHEET_EVENT_MID_AUTUMN				= SHEET_INVITE_FRIEND + 1;
	public static final int SHEET_ORDER_EVENT					= SHEET_EVENT_MID_AUTUMN + 1;
	public static final int SHEET_OPEN_EVENT_ITEM				= SHEET_ORDER_EVENT + 1;
	public static final int SHEET_COMBO							= SHEET_OPEN_EVENT_ITEM + 1;
	public static final int SHEET_DROP_CUSTOM					= SHEET_COMBO + 1;
	public static final int SHEET_CLOSE_FRIEND					= SHEET_DROP_CUSTOM + 1;
	public static final int SHEET_TREASURE_TRUNK				= SHEET_CLOSE_FRIEND + 1;
	public static final int SHEET_GEO_IP						= SHEET_TREASURE_TRUNK + 1;
	public static final int SHEET_MAX							= SHEET_GEO_IP + 1;
	
	// ------------------------------------- USER -------------------------------
	public static final int USER_GOLD							= 0;
	public static final int USER_DIAMOND						= USER_GOLD + 1;
	public static final int USER_REPUTAION						= USER_DIAMOND + 1;
	public static final int USER_FLOOR							= USER_REPUTAION + 1;
	public static final int USER_PROPERTY_MAX					= USER_FLOOR + 1;
		
	// ------------------------------------- USER_LEVEL -------------------------------
	public static final int USER_LEVEL							= 0;
	public static final int USER_EXP_LVL						= USER_LEVEL + 1;
	public static final int USER_SEED_ID_UNLOCK					= USER_EXP_LVL + 1;
	public static final int USER_POT_ID_UNLOCK					= USER_SEED_ID_UNLOCK + 1;
	public static final int USER_PROD_ID_UNLOCK					= USER_POT_ID_UNLOCK + 1;
	public static final int USER_FLOOR_UNLOCK					= USER_PROD_ID_UNLOCK + 1;
	public static final int USER_MACHINE_UNLOCK					= USER_FLOOR_UNLOCK + 1;
	public static final int USER_ORDER_SLOT_UNLOCK				= USER_MACHINE_UNLOCK + 1;
	public static final int USER_REWARD_GOLD					= USER_ORDER_SLOT_UNLOCK + 1;
	public static final int USER_REWARD_REPUTATION				= USER_REWARD_GOLD + 1;
	public static final int USER_REWARD_DIAMOND					= USER_REWARD_REPUTATION + 1;
	public static final int USER_REWARD_ITEM					= USER_REWARD_DIAMOND + 1;
	public static final int USER_LEVEL_SHARE_REWARD				= USER_REWARD_ITEM + 1;
	public static final int USER_NEW_COMER_GIFT					= USER_LEVEL_SHARE_REWARD + 1;
	public static final int USER_UNLOCK_PROPERTY_MAX			= USER_NEW_COMER_GIFT + 1;

	// ------------------------------------- SEED -------------------------------
	public static final int SEED_ID								= 0;
	public static final int SEED_NAME							= SEED_ID + 1;
	public static final int SEED_GROW_TIME						= SEED_NAME + 1;
	public static final int SEED_HARVEST_EXP					= SEED_GROW_TIME + 1;
	public static final int SEED_ITEM_RECEIVE_RATIO				= SEED_HARVEST_EXP + 1;
	public static final int SEED_DIAMOND_SKIP_TIME				= SEED_ITEM_RECEIVE_RATIO + 1;
	public static final int SEED_DIAMOND_BUY					= SEED_DIAMOND_SKIP_TIME + 1;
	public static final int SEED_GOLD_BASIC						= SEED_DIAMOND_BUY + 1;
	public static final int SEED_EXP_BASIC						= SEED_GOLD_BASIC + 1;
	public static final int SEED_GOLD_SELL_DEFAULT				= SEED_EXP_BASIC + 1;
	public static final int SEED_GOLD_SELL_MIN					= SEED_GOLD_SELL_DEFAULT + 1;
	public static final int SEED_GOLD_SELL_MAX					= SEED_GOLD_SELL_MIN + 1;
	public static final int SEED_BUG_ID							= SEED_GOLD_SELL_MAX + 1;
	public static final int SEED_BUG_APPEAR_RATIO				= SEED_BUG_ID + 1;
	public static final int SEED_LEVEL_UNLOCK					= SEED_BUG_APPEAR_RATIO + 1;
	public static final int SEED_SEED_TIME						= SEED_LEVEL_UNLOCK + 1;
	public static final int SEED_GOLD_BASIC_DO					= SEED_SEED_TIME + 1;
	public static final int SEED_EXP_BASIC_DO_FREE				= SEED_GOLD_BASIC_DO + 1;
	public static final int SEED_EXP_BASIC_DO_PAID				= SEED_EXP_BASIC_DO_FREE + 1;
	public static final int SEED_NPC_BUY_PRICE					= SEED_EXP_BASIC_DO_PAID + 1;
	
	public static final int SEED_NAME_EN						= SEED_NPC_BUY_PRICE + 1;
	public static final int SEED_NAME_SC						= SEED_NAME_EN + 1;
	public static final int SEED_NAME_TC						= SEED_NAME_SC + 1;
	public static final int SEED_PROPERTY_MAX					= SEED_NAME_TC + 1;
		
	// ------------------------------------- POT -------------------------------
	public static final int POT_ID								= 0;
	public static final int POT_NAME							= POT_ID + 1;
	public static final int POT_EXP_INCREASE					= POT_NAME + 1;
	public static final int POT_TIME_DECREASE					= POT_EXP_INCREASE + 1;
	public static final int POT_GOLD_UPGRADE					= POT_TIME_DECREASE + 1;
	public static final int POT_UPGRADE_REQUIREMENT				= POT_GOLD_UPGRADE + 1;
	public static final int POT_UPGRADE_RATIO					= POT_UPGRADE_REQUIREMENT + 1;
	public static final int POT_GOLD_DEFAULT					= POT_UPGRADE_RATIO + 1;
	public static final int POT_GOLD_MIN						= POT_GOLD_DEFAULT + 1;
	public static final int POT_GOLD_MAX						= POT_GOLD_MIN + 1;
	public static final int POT_BUG_APPEAR_RATIO				= POT_GOLD_MAX + 1;
	public static final int POT_LEVEL_UNLOCK					= POT_BUG_APPEAR_RATIO + 1;
	public static final int POT_BLESSING_RATIO					= POT_LEVEL_UNLOCK + 1;
	public static final int POT_BAD_LUCK_RATIO					= POT_BLESSING_RATIO + 1;
	public static final int POT_COMBO_EXP_BONUS					= POT_BAD_LUCK_RATIO + 1;
	public static final int POT_COMBO_TIME_BONUS				= POT_COMBO_EXP_BONUS + 1;
	public static final int POT_COMBO_BUG_BONUS					= POT_COMBO_TIME_BONUS + 1;
	public static final int POT_ORDER_EXP_BONUS_PLANT			= POT_COMBO_BUG_BONUS + 1;
	public static final int POT_ORDER_EXP_BONUS_BUG				= POT_ORDER_EXP_BONUS_PLANT + 1;
	public static final int POT_APPRAISAL						= POT_ORDER_EXP_BONUS_BUG + 1;
	public static final int POT_ORDER_GOLD_BONUS				= POT_APPRAISAL + 1;
	public static final int POT_ORDER_XP_BONUS					= POT_ORDER_GOLD_BONUS + 1;
	
	public static final int POT_NAME_EN							= POT_ORDER_XP_BONUS + 1;
	public static final int POT_NAME_SC							= POT_NAME_EN + 1;
	public static final int POT_NAME_TC							= POT_NAME_SC + 1;
	public static final int POT_UPGRADABLE						= POT_NAME_TC + 1;
	public static final int POT_PROPERTY_MAX					= POT_UPGRADABLE + 1;
	
	// ------------------------------------- PEST -------------------------------
	public static final int PEST_ID								= 0;
	public static final int PEST_NAME							= PEST_ID + 1;
	public static final int PEST_DIAMOND_BUY					= PEST_NAME + 1;
	public static final int PEST_GOLD_BASIC						= PEST_DIAMOND_BUY + 1;
	public static final int PEST_EXP_BASIC						= PEST_GOLD_BASIC + 1;
	public static final int PEST_GOLD_DEFAULT					= PEST_EXP_BASIC + 1;
	public static final int PEST_GOLD_MIN						= PEST_GOLD_DEFAULT + 1;
	public static final int PEST_GOLD_MAX						= PEST_GOLD_MIN + 1;
	public static final int PEST_NPC_BUY_PRICE					= PEST_GOLD_MAX + 1;
	
	public static final int PEST_NAME_EN						= PEST_NPC_BUY_PRICE + 1;
	public static final int PEST_NAME_SC						= PEST_NAME_EN + 1;
	public static final int PEST_NAME_TC						= PEST_NAME_SC + 1;
	public static final int PEST_PROPERTY_MAX					= PEST_NAME_TC + 1;
		
	// ------------------------------------- STOCK -------------------------------
	public static final int STOCK_ID							= 0;
	public static final int STOCK_NAME							= STOCK_ID + 1;
	public static final int STOCK_CAPACITY_MAX					= STOCK_NAME + 1;
	public static final int STOCK_CAPACITY_ADD					= STOCK_CAPACITY_MAX + 1;
	public static final int STOCK_CAPACITY_PLUS					= STOCK_CAPACITY_ADD + 1;
	public static final int STOCK_VIP_PLUS						= STOCK_CAPACITY_PLUS + 1;
	
	public static final int STOCK_NAME_EN						= STOCK_VIP_PLUS + 1;
	public static final int STOCK_NAME_SC						= STOCK_NAME_EN + 1;
	public static final int STOCK_NAME_TC						= STOCK_NAME_SC + 1;
	public static final int STOCK_PROPERTY_MAX					= STOCK_NAME_TC + 1;
	
	// ------------------------------------- STOCK_UPGRADE -------------------------------
	public static final int STOCK_1								= 0;
	public static final int STOCK_2								= STOCK_1 + 1;
	public static final int STOCK_3								= STOCK_2 + 1;
	public static final int STOCK_APPRAISAL						= STOCK_3 + 1;
	public static final int STOCK_UPGRADE_PROPERTY_MAX			= STOCK_APPRAISAL + 1;
	
	// ------------------------------------- MACHINE -------------------------------
	public static final int MACHINE_FLOOR						= 0;
	public static final int MACHINE_ID							= MACHINE_FLOOR + 1;
	public static final int MACHINE_NAME						= MACHINE_ID + 1;
	public static final int MACHINE_LEVEL_UNLOCK				= MACHINE_NAME + 1;
	public static final int MACHINE_GOLD_START					= MACHINE_LEVEL_UNLOCK + 1;
	public static final int MACHINE_TIME_START					= MACHINE_GOLD_START + 1;
	public static final int MACHINE_PRODUCT_ID					= MACHINE_TIME_START + 1;
	public static final int MACHINE_DURABILITY					= MACHINE_PRODUCT_ID + 1;
	
	public static final int MACHINE_NAME_EN						= MACHINE_DURABILITY + 1;
	public static final int MACHINE_NAME_SC						= MACHINE_NAME_EN + 1;
	public static final int MACHINE_NAME_TC						= MACHINE_NAME_SC + 1;
	public static final int MACHINE_PROPERTY_MAX				= MACHINE_NAME_TC + 1;
		
	// ------------------------------------- MACHINE_UNLOCK -------------------------------
	public static final int MACHINE_LEVEL1						= 0;
	public static final int MACHINE_LEVEL2						= MACHINE_LEVEL1 + 1;
	public static final int MACHINE_LEVEL3						= MACHINE_LEVEL2 + 1;
	public static final int MACHINE_LEVEL4						= MACHINE_LEVEL3 + 1;
	public static final int MACHINE_LEVEL5						= MACHINE_LEVEL4 + 1;
	public static final int MACHINE_LEVEL6						= MACHINE_LEVEL5 + 1;
	public static final int MACHINE_LEVEL7						= MACHINE_LEVEL6 + 1;
	public static final int MACHINE_LEVEL8						= MACHINE_LEVEL7 + 1;
	public static final int MACHINE_LEVEL_MAX					= MACHINE_LEVEL8 + 1;
		
	public static final int MACHINE_ACTIVE_TIME					= 0;
	public static final int MACHINE_GOLD_UNLOCK					= MACHINE_ACTIVE_TIME + 1;
	public static final int MACHINE_UPGRADE_RATIO				= MACHINE_GOLD_UNLOCK + 1;
	public static final int MACHINE_EXP_BONUS					= MACHINE_UPGRADE_RATIO + 1;
	public static final int MACHINE_REDUCE_TIME					= MACHINE_EXP_BONUS + 1;
	public static final int MACHINE_EXP_ORDER					= MACHINE_REDUCE_TIME + 1;
	public static final int MACHINE_GOLD_ORDER					= MACHINE_EXP_ORDER + 1;
	public static final int MACHINE_GOLD_MAINTAIN				= MACHINE_GOLD_ORDER + 1;
	public static final int MACHINE_UNLOCK_PROPERTY_MAX			= MACHINE_GOLD_MAINTAIN + 1;
	
	// ------------------------------------- MACHINE_UNLOCK PRODUCT SLOT -------------------------------
	public static final int MACHINE_SLOT_0						= 0;
	public static final int MACHINE_SLOT_1						= MACHINE_SLOT_0 + 1;
	public static final int MACHINE_SLOT_2						= MACHINE_SLOT_1 + 1;
	public static final int MACHINE_SLOT_3						= MACHINE_SLOT_2 + 1;
	public static final int MACHINE_SLOT_4						= MACHINE_SLOT_3 + 1;
	public static final int MACHINE_SLOT_5						= MACHINE_SLOT_4 + 1;
	public static final int MACHINE_SLOT_6						= MACHINE_SLOT_5 + 1;
	public static final int MACHINE_SLOT_7						= MACHINE_SLOT_6 + 1;
	public static final int MACHINE_SLOT_8						= MACHINE_SLOT_7 + 1;
	public static final int MACHINE_SLOT_PROPERTY_MAX			= MACHINE_SLOT_8 + 1;
		
	// ------------------------------------- PRODUCT -------------------------------
	public static final int PRODUCT_ID							= 0;
	public static final int PRODUCT_NAME						= PRODUCT_ID + 1;
	public static final int PRODUCT_LEVEL_UNLOCK				= PRODUCT_NAME + 1;
	public static final int PRODUCT_MACHINE_ID					= PRODUCT_LEVEL_UNLOCK + 1;
	public static final int PRODUCT_ITEMS_TYPE_ID_NUM			= PRODUCT_MACHINE_ID + 1;
	public static final int PRODUCT_PRODUCTION_TIME				= PRODUCT_ITEMS_TYPE_ID_NUM + 1;
	public static final int PRODUCT_EXP_RECEIVE					= PRODUCT_PRODUCTION_TIME + 1;
	public static final int PRODUCT_DIAMOND_BUY					= PRODUCT_EXP_RECEIVE + 1;
	public static final int PRODUCT_GOLD_BASIC					= PRODUCT_DIAMOND_BUY + 1;
	public static final int PRODUCT_EXP_BASIC					= PRODUCT_GOLD_BASIC + 1;
	public static final int PRODUCT_GOLD_DEFAULT				= PRODUCT_EXP_BASIC + 1;
	public static final int PRODUCT_GOLD_MIN					= PRODUCT_GOLD_DEFAULT + 1;
	public static final int PRODUCT_GOLD_MAX					= PRODUCT_GOLD_MIN + 1;
	public static final int PRODUCT_NPC_BUY_PRICE				= PRODUCT_GOLD_MAX + 1;
	
	public static final int PRODUCT_NAME_EN						= PRODUCT_NPC_BUY_PRICE + 1;
	public static final int PRODUCT_NAME_SC						= PRODUCT_NAME_EN + 1;
	public static final int PRODUCT_NAME_TC						= PRODUCT_NAME_SC + 1;
	public static final int PRODUCT_PROPERTY_MAX				= PRODUCT_NAME_TC + 1;
			
	// ------------------------------------- MATERIAL -------------------------------
	public static final int MATERIAL_ID							= 0;
	public static final int MATERIAL_NAME						= MATERIAL_ID + 1;
	public static final int MATERIAL_DIAMOND_BUY				= MATERIAL_NAME + 1;
	public static final int MATERIAL_GOLD_DEFAULT				= MATERIAL_DIAMOND_BUY + 1;
	public static final int MATERIAL_GOLD_MIN					= MATERIAL_GOLD_DEFAULT + 1;
	public static final int MATERIAL_GOLD_MAX					= MATERIAL_GOLD_MIN + 1;
	public static final int MATERIAL_4_LEAF_LUCKY_PERCENT		= MATERIAL_GOLD_MAX + 1;
	public static final int MATERIAL_FERTILIZER_TIME_REDUCE		= MATERIAL_4_LEAF_LUCKY_PERCENT + 1;
	
	public static final int MATERIAL_NAME_EN					= MATERIAL_FERTILIZER_TIME_REDUCE + 1;
	public static final int MATERIAL_NAME_SC					= MATERIAL_NAME_EN + 1;
	public static final int MATERIAL_NAME_TC					= MATERIAL_NAME_SC + 1;
	public static final int MATERIAL_PROPERTY_MAX				= MATERIAL_NAME_TC + 1;
		
	// ------------------------------------- FLOOR_UNLOCK -------------------------------
	public static final int FLOOR_UNLOCK_ID						= 0;
	public static final int FLOOR_UNLOCK_USER_LEVEL				= FLOOR_UNLOCK_ID + 1;
	public static final int FLOOR_UNLOCK_GOLD					= FLOOR_UNLOCK_USER_LEVEL + 1;
	public static final int FLOOR_UNLOCK_REPUTATION				= FLOOR_UNLOCK_GOLD + 1;
	public static final int FLOOR_UNLOCK_MATERIAL_ID_NUM		= FLOOR_UNLOCK_REPUTATION + 1;
	public static final int FLOOR_APPRAISAL						= FLOOR_UNLOCK_MATERIAL_ID_NUM + 1;
	public static final int FLOOR_UNLOCK_PROPERTY_MAX			= FLOOR_APPRAISAL + 1;
		
	// ------------------------------------- IBSHOP  -------------------------------
	public static final int IBSHOP_ID							= 0;
	public static final int IBSHOP_NAME							= IBSHOP_ID + 1;
	public static final int IBSHOP_DESCRIPTION					= IBSHOP_NAME + 1;
	public static final int IBSHOP_TYPE							= IBSHOP_DESCRIPTION + 1;
	public static final int IBSHOP_ITEM_NAME					= IBSHOP_TYPE + 1;
	public static final int IBSHOP_ITEM_QUANTITY				= IBSHOP_ITEM_NAME + 1;
	public static final int IBSHOP_IS_ACTIVE					= IBSHOP_ITEM_QUANTITY + 1;
	public static final int IBSHOP_IS_NEW						= IBSHOP_IS_ACTIVE + 1;
	public static final int IBSHOP_IS_HOT						= IBSHOP_IS_NEW + 1;
	public static final int IBSHOP_IS_SALE_OFF					= IBSHOP_IS_HOT + 1;
	public static final int IBSHOP_SALE_OFF_PERCENT				= IBSHOP_IS_SALE_OFF + 1;
	public static final int IBSHOP_HAS_PROMOTION				= IBSHOP_SALE_OFF_PERCENT + 1;
	public static final int IBSHOP_GIFT_WHEN_BUY				= IBSHOP_HAS_PROMOTION + 1;
	public static final int IBSHOP_REQUIRED_GOLD				= IBSHOP_GIFT_WHEN_BUY + 1;
	public static final int IBSHOP_REQUIRED_DIAMOND				= IBSHOP_REQUIRED_GOLD + 1;
	public static final int IBSHOP_REQUIRED_REPUTATION			= IBSHOP_REQUIRED_DIAMOND + 1;
	public static final int IBSHOP_HAS_TIME_LIMIT				= IBSHOP_REQUIRED_REPUTATION + 1;
	public static final int IBSHOP_SALE_DURATION				= IBSHOP_HAS_TIME_LIMIT + 1;
	public static final int IBSHOP_HAS_SALE_LIMIT				= IBSHOP_SALE_DURATION + 1;
	public static final int IBSHOP_SALE_TOTAL_QUANTITY			= IBSHOP_HAS_SALE_LIMIT + 1;
	public static final int IBSHOP_UNLOCK_LEVEL					= IBSHOP_SALE_TOTAL_QUANTITY + 1;
	public static final int IBSHOP_DISPLAY_INDEX				= IBSHOP_UNLOCK_LEVEL + 1;
	public static final int IBSHOP_EVENT_GIFT					= IBSHOP_DISPLAY_INDEX + 1;
	public static final int IBSHOP_NAME_EN						= IBSHOP_EVENT_GIFT + 1;
	public static final int IBSHOP_DESCRIPTION_EN				= IBSHOP_NAME_EN + 1;
	public static final int IBSHOP_NAME_SC						= IBSHOP_DESCRIPTION_EN + 1;
	public static final int IBSHOP_DESCRIPTION_SC				= IBSHOP_NAME_SC + 1;
	public static final int IBSHOP_NAME_TC						= IBSHOP_DESCRIPTION_SC + 1;
	public static final int IBSHOP_DESCRIPTION_TC				= IBSHOP_NAME_TC + 1;
	public static final int IBSHOP_PROPERTY_MAX					= IBSHOP_DESCRIPTION_TC + 1;
	
	
	// section in ibshop
	public static final int IBS_TAB_DIAMOND						= 0;
	public static final int IBS_TAB_GOLD						= 1;
	public static final int IBS_TAB_POT							= 2;
	public static final int IBS_TAB_DECOR						= 3;
	public static final int IBS_TAB_UPGRADE						= 4;
	public static final int IBS_TAB_MACHINE						= 5;
	public static final int IBS_TAB_MISCELLANEOUS				= 6;
	public static final int IBS_TAB_PLANT						= 7;
	public static final int IBS_TAB_OWL_FOOD					= 8;
	public static final int IBS_TAB_MAX							= 9;
	
	// ------------------------------------- PRIVATE SHOP -------------------------------
	public static final int PS_SHOP_SLOT_ID						= 0;
	public static final int PS_SHOP_REQUIRED_FRIEND				= PS_SHOP_SLOT_ID + 1;
	public static final int PS_SHOP_REQUIRED_DIAMOND			= PS_SHOP_REQUIRED_FRIEND + 1;
	public static final int PS_SHOP_REQUIRED_DIAMOND_CANCEL		= PS_SHOP_REQUIRED_DIAMOND + 1;
	public static final int PS_SHOP_PROPERTY_MAX				= PS_SHOP_REQUIRED_DIAMOND_CANCEL + 1;
		
	// ------------------------------------- ACHIEVEMENT -------------------------------
	public static final int ACHIEVEMENT_INDEX						= 0;
	public static final int ACHIEVEMENT_COMMAND_ID					= 1;
	public static final int ACHIEVEMENT_EXTRA_COMMAND_INFO			= 2;
	public static final int ACHIEVEMENT_DESCRIPTION					= 3;
	public static final int ACHIEVEMENT_TARGET_1					= 4;
	public static final int ACHIEVEMENT_TARGET_2					= 5;
	public static final int ACHIEVEMENT_TARGET_3					= 6;
	public static final int ACHIEVEMENT_GIFT_1						= 7;
	public static final int ACHIEVEMENT_GIFT_2						= 8;
	public static final int ACHIEVEMENT_GIFT_3						= 9;
	public static final int ACHIEVEMENT_GIFT_SPECIAL				= 10;
	public static final int ACHIEVEMENT_NAME						= 11;
	public static final int ACHIEVEMENT_NAME_EN						= 12;
	public static final int ACHIEVEMENT_DESCRIPTION_EN				= 13;
	public static final int ACHIEVEMENT_PROPERTY_MAX				= 14;
	
	public static final int ACHIEVEMENT_GIFT_INDEX_1				= 1;
	public static final int ACHIEVEMENT_GIFT_INDEX_2				= 2;
	public static final int ACHIEVEMENT_GIFT_INDEX_3				= 3;
	
	// ------------------------------------- DIAMOND SKIP TIME -------------------------------
	public static final int DIAMOND_SKIP_TIME_PLANT_TIME_RANGE			= 0;
	public static final int DIAMOND_SKIP_TIME_PLANT_RATIO				= DIAMOND_SKIP_TIME_PLANT_TIME_RANGE + 1;
	public static final int DIAMOND_SKIP_TIME_PLANT_DIAMOND_DEFAULT		= DIAMOND_SKIP_TIME_PLANT_RATIO + 1;
	public static final int DIAMOND_SKIP_TIME_PRODUCT_TIME_RANGE		= DIAMOND_SKIP_TIME_PLANT_DIAMOND_DEFAULT + 1;
	public static final int DIAMOND_SKIP_TIME_PRODUCT_RATIO				= DIAMOND_SKIP_TIME_PRODUCT_TIME_RANGE + 1;
	public static final int DIAMOND_SKIP_TIME_PRODUCT_DIAMOND_DEFAULT	= DIAMOND_SKIP_TIME_PRODUCT_RATIO + 1;
	public static final int DIAMOND_SKIP_TIME_MACHINE_TIME_RANGE		= DIAMOND_SKIP_TIME_PRODUCT_DIAMOND_DEFAULT + 1;
	public static final int DIAMOND_SKIP_TIME_MACHINE_RATIO				= DIAMOND_SKIP_TIME_MACHINE_TIME_RANGE + 1;
	public static final int DIAMOND_SKIP_TIME_MACHINE_DIAMOND_DEFAULT 	= DIAMOND_SKIP_TIME_MACHINE_RATIO + 1;
	public static final int DIAMOND_SKIP_TIME_PROPERTY_MAX				= DIAMOND_SKIP_TIME_MACHINE_DIAMOND_DEFAULT + 1;

	public static final int DIAMOND_SKIP_TIME_PLANT						= 0;
	public static final int DIAMOND_SKIP_TIME_PRODUCT					= DIAMOND_SKIP_TIME_PLANT + 1;
	public static final int DIAMOND_SKIP_TIME_MACHINE					= DIAMOND_SKIP_TIME_PRODUCT + 1;
	
	// ------------------------------------- DROP ITEM LIST -------------------------------
	public static final int DROP_INDEX							= 0;
	public static final int DROP_LEVEL							= 0;
	public static final int DROP_ITEM_GACH						= 1; 
	public static final int DROP_ITEM_SONDO						= 2; 
	public static final int DROP_ITEM_GO						= 3; 
	public static final int DROP_ITEM_DA						= 4; 
	public static final int DROP_ITEM_SONVANG					= 5; 
	public static final int DROP_ITEM_DINH						= 6; 
	public static final int DROP_ITEM_NGOI						= 7; 
	public static final int DROP_ITEM_SONDEN					= 8; 
	public static final int DROP_ITEM_SAT						= 9; 
	public static final int DROP_ITEM_VOT						= 10; 
	public static final int DROP_ITEM_NUOCTHAN					= 11;
	public static final int DROP_ITEM_KEODANMAY					= 12;
	public static final int DROP_ITEM_VOTDAI					= 13;
	public static final int DROP_ITEM_BANH_TRUNG_THU			= 14;
	public static final int DROP_ITEM_EVENT_1					= 15;
	public static final int DROP_ITEM_EVENT_2					= 16;
	public static final int DROP_ITEM_EVENT_3					= 17;
	public static final int DROP_ITEM_TREASURE					= 18;
	public static final int DROP_ITEM_PROPERTY_MAX				= DROP_ITEM_TREASURE + 1;

	// ------------------------------------- GOLD XP COEFFICIENT -------------------------------
	public static final int GOLD_XP_COEFFICIENT_RATE			= 0;
	public static final int GOLD_XP_COEFFICIENT_RATE_RANGE		= GOLD_XP_COEFFICIENT_RATE + 1;
	public static final int GOLD_XP_COEFFICIENT_MIN				= GOLD_XP_COEFFICIENT_RATE_RANGE + 1;
	public static final int GOLD_XP_COEFFICIENT_MAX				= GOLD_XP_COEFFICIENT_MIN + 1;
	public static final int GOLD_XP_COEFFICIENT_PROPERTY_MAX	= GOLD_XP_COEFFICIENT_MAX + 1;
	
	// ------------------------------------- PAYMENT -------------------------------
	public static final int PAYMENT_PACK_ID						= 0;
	public static final int PAYMENT_PACK_NAME					= 1;
	public static final int PAYMENT_PACK_DESCRIPTION			= 2;
	public static final int PAYMENT_TYPE						= 3;
	public static final int PAYMENT_OPERATOR					= 4;
	public static final int PAYMENT_GROSS_AMOUNT				= 5;
	public static final int PAYMENT_SMS_STRUCTURE				= 6;
	public static final int PAYMENT_SMS_ADDRESS					= 7;
	public static final int PAYMENT_IS_ACTIVE					= 8;
	public static final int PAYMENT_IS_NEW						= 9;
	public static final int PAYMENT_IS_HOT						= 10;
	public static final int PAYMENT_HAS_PROMOTION				= 11;
	public static final int PAYMENT_PROMOTION_PERCENT			= 12;
	public static final int PAYMENT_DIAMOND_RECEIVE_REAL				= 13;
	public static final int PAYMENT_DIAMOND_RECEIVE_BONUS				= 14;
	public static final int PAYMENT_DIAMOND_RECEIVE_REAL_DISPLAY		= 15;
	public static final int PAYMENT_DIAMOND_RECEIVE_BONUS_DISPLAY		= 16;
	public static final int PAYMENT_DIAMOND_RECEIVE_BONUS_FIRST_PAY		= 17;
	
	public static final int PAYMENT_PACK_NAME_EN				= 18;
	public static final int PAYMENT_PACK_DESCRIPTION_EN			= 19;
	public static final int PAYMENT_PACK_NAME_SC				= 20;
	public static final int PAYMENT_PACK_DESCRIPTION_SC			= 21;
	public static final int PAYMENT_PACK_NAME_TC				= 22;
	public static final int PAYMENT_PACK_DESCRIPTION_TC			= 23;
	public static final int PAYMENT_PACK_ANDROID_PRODUCT_ID		= 24;
	public static final int PAYMENT_PACK_APPLE_PRODUCT_ID		= 25;
	public static final int PAYMENT_PACK_WIN_PRODUCT_ID			= 26;
	public static final int PAYMENT_PROPERTY_MAX				= 27;
	
	public static final int PAYMENT_CARD_ZING					= 0;
	public static final int PAYMENT_CARD_VNP					= 1;
	public static final int PAYMENT_CARD_VMS					= 2;
	public static final int PAYMENT_CARD_VTT					= 3;

	// ------------------------------------- DAILY ORDER -------------------------------
	public static final int DAILY_ORDER_INDEX					= 0;
	public static final int DAILY_ORDER_GOLD_PER_DIAMOND		= DAILY_ORDER_INDEX + 1;
	public static final int DAILY_ORDER_PROPERTY_MAX			= DAILY_ORDER_GOLD_PER_DIAMOND + 1;

	// ------------------------------------- TUTORIAL -------------------------------
	public static final int TUTORIAL_ID							= 0;
	public static final int TUTORIAL_EXP						= TUTORIAL_ID + 1;
	
	public static final int TUTORIAL_SLIDE_TO_FIRST_FLOOR		= 0;
	public static final int TUTORIAL_HARVEST					= TUTORIAL_SLIDE_TO_FIRST_FLOOR + 1;
	public static final int TUTORIAL_AFTER_HARVEST				= TUTORIAL_HARVEST + 1;
	public static final int TUTORIAL_PLANT						= TUTORIAL_AFTER_HARVEST + 1;
	public static final int TUTORIAL_AFTER_PLANT				= TUTORIAL_PLANT + 1;
	public static final int TUTORIAL_RECEIVE_PLANT_REWARD		= TUTORIAL_AFTER_PLANT + 1;
	public static final int TUTORIAL_CATCH_BUG					= TUTORIAL_RECEIVE_PLANT_REWARD + 1;
	public static final int TUTORIAL_AFTER_CATCH_BUG			= TUTORIAL_CATCH_BUG + 1;
	public static final int TUTORIAL_OPEN_MACHINE				= TUTORIAL_AFTER_CATCH_BUG + 1;
	public static final int TUTORIAL_SKIP_TIME_FREEZE_MACHINE	= TUTORIAL_OPEN_MACHINE + 1;
	public static final int TUTORIAL_PRODUCT					= TUTORIAL_SKIP_TIME_FREEZE_MACHINE + 1;
	public static final int TUTORIAL_SKIP_TIME					= TUTORIAL_PRODUCT + 1;
	public static final int TUTORIAL_AFTER_SKIP_TIME			= TUTORIAL_SKIP_TIME + 1;
	public static final int TUTORIAL_RECEIVE_ORDER				= TUTORIAL_AFTER_SKIP_TIME + 1;
	public static final int TUTORIAL_DELIVERY_ORDER				= TUTORIAL_RECEIVE_ORDER + 1;
	public static final int TUTORIAL_RECEIVE_ORDER_REWARD		= TUTORIAL_DELIVERY_ORDER + 1;
	public static final int TUTORIAL_AFTER_RECEIVE_ORDER_REWARD	= TUTORIAL_RECEIVE_ORDER_REWARD + 1;
	public static final int TUTORIAL_OPEN_NEW_FLOOR				= TUTORIAL_AFTER_RECEIVE_ORDER_REWARD + 1;
	public static final int TUTORIAL_NPC_MACHINE_REPAIR			= TUTORIAL_OPEN_NEW_FLOOR + 1;
	public static final int TUTORIAL_AFTER_NPC_MACHINE_REPAIR	= TUTORIAL_NPC_MACHINE_REPAIR + 1;
	public static final int TUTORIAL_PUT_POT					= TUTORIAL_AFTER_NPC_MACHINE_REPAIR + 1;
	public static final int TUTORIAL_BUY_POT					= TUTORIAL_PUT_POT + 1;
	public static final int TUTORIAL_MOVE_POT					= TUTORIAL_BUY_POT + 1;
	public static final int TUTORIAL_PRIVATE_SHOP_GUIDE			= TUTORIAL_MOVE_POT + 1;
	public static final int TUTORIAL_BUY_ITEM_PRIVATE_SHOP		= TUTORIAL_PRIVATE_SHOP_GUIDE + 1;
	public static final int TUTORIAL_NEWSBOARD					= TUTORIAL_BUY_ITEM_PRIVATE_SHOP + 1;
	public static final int TUTORIAL_CONNECT_SNS				= TUTORIAL_NEWSBOARD + 1;
	public static final int TUTORIAL_UPGRADE_POT				= TUTORIAL_CONNECT_SNS + 1;
	public static final int TUTORIAL_MAX						= TUTORIAL_UPGRADE_POT + 1;

	// ------------------------------------- VERSION -------------------------------
	public static final int VERSION_ID							= 0;
	public static final int VERSION_PLATFORM					= VERSION_ID + 1;
	public static final int VERSION_RESOLUTION					= VERSION_PLATFORM + 1;
	public static final int VERSION_DISTRIBUTOR					= VERSION_RESOLUTION + 1;
	public static final int VERSION_NUMBER						= VERSION_DISTRIBUTOR + 1;
	public static final int VERSION_LINK						= VERSION_NUMBER + 1;
	public static final int VERSION_FORCE_UPDATE				= VERSION_LINK + 1;
	public static final int VERSION_MAX							= VERSION_FORCE_UPDATE + 1;
	
	public static final int VERSION_WRONG_UPDATE				= 0;
	public static final int VERSION_HAS_NOT_UPDATE				= VERSION_WRONG_UPDATE + 1;
	public static final int VERSION_HAS_UPDATE					= VERSION_HAS_NOT_UPDATE + 1;
	public static final int VERSION_HAS_FORCE_UPDATE			= VERSION_HAS_UPDATE + 1;
	
	// ------------------------------------- FRIEND -------------------------------
	public static final int FRIEND_BUG_LEVEL					= 0;
	public static final int FRIEND_BUG_TIME_RANGE_1				= 1;
	public static final int FRIEND_BUG_TIME_RANGE_2				= 2;
	public static final int FRIEND_BUG_TIME_RANGE_3				= 3;
	public static final int FRIEND_BUG_TIME_RANGE_4				= 4;
	public static final int FRIEND_BUG_TIME_RANGE_5				= 5;
	public static final int FRIEND_BUG_PERCENT_TIME_RANGE_1		= 6;
	public static final int FRIEND_BUG_PERCENT_TIME_RANGE_2		= 7;
	public static final int FRIEND_BUG_PERCENT_TIME_RANGE_3		= 8;
	public static final int FRIEND_BUG_PERCENT_TIME_RANGE_4		= 9;
	public static final int FRIEND_BUG_PERCENT_TIME_RANGE_5		= 10;
	public static final int FRIEND_BUG_NUM_MIN					= 11;
	public static final int FRIEND_BUG_NUM_MAX					= 12;
	public static final int FRIEND_BUG_PERCENT_FAKE_BUG			= 13;
	public static final int FRIEND_BUG_1						= 14;
	public static final int FRIEND_BUG_2						= 15;
	public static final int FRIEND_BUG_3						= 16;
	public static final int FRIEND_BUG_MAX						= 17;
	
	// ------------------------------------- DECOR -------------------------------
	public static final int DECOR_ID							= 0;
	public static final int DECOR_NAME							= 1;
	public static final int DECOR_EXP_INCREASE					= 2;
	public static final int DECOR_TIME_DECREASE					= 3;
	public static final int DECOR_GOLD_UPGRADE					= 4;
	public static final int DECOR_PEARL_ID_NUM					= 5;
	public static final int DECOR_UPGRADE_RATIO					= 6;
	public static final int DECOR_GOLD_DEFAULT					= 7;
	public static final int DECOR_GOLD_MIN						= 8;
	public static final int DECOR_GOLD_MAX						= 9;
	public static final int DECOR_BUG_APPEAR_RATIO				= 10;
	public static final int DECOR_LEVEL_UNLOCK					= 11;
	public static final int DECOR_BLESSING_RATIO				= 12;
	public static final int DECOR_BAD_LUCK_RATIO				= 13;
	public static final int DECOR_COMBO_EXP_BONUS				= 14;
	public static final int DECOR_COMBO_TIME_BONUS				= 15;
	public static final int DECOR_COMBO_BUG_BONUS				= 16;
	public static final int DECOR_USE_DURATION					= 17;
	public static final int DECOR_APPRAISAL						= 18;
	
	public static final int DECOR_NAME_EN						= 19;
	public static final int DECOR_NAME_SC						= 20;
	public static final int DECOR_NAME_TC						= 21;
	public static final int DECOR_PROPERTY_MAX					= 22;
	
	// ------------------------------------- MACHINE APPRAISAL -------------------------------
	public static final int APPRAISAL_MACHINE_ID				= 0;
	public static final int APPRAISAL_MACHINE_ID_0				= APPRAISAL_MACHINE_ID + 1;
	public static final int APPRAISAL_MACHINE_ID_1				= APPRAISAL_MACHINE_ID_0 + 1;
	public static final int APPRAISAL_MACHINE_ID_2				= APPRAISAL_MACHINE_ID_1 + 1;
	public static final int APPRAISAL_MACHINE_ID_3				= APPRAISAL_MACHINE_ID_2 + 1;
	public static final int APPRAISAL_MACHINE_ID_4				= APPRAISAL_MACHINE_ID_3 + 1;
	public static final int APPRAISAL_MACHINE_ID_5				= APPRAISAL_MACHINE_ID_4 + 1;
	public static final int APPRAISAL_MACHINE_ID_6				= APPRAISAL_MACHINE_ID_5 + 1;
	public static final int APPRAISAL_MACHINE_ID_7				= APPRAISAL_MACHINE_ID_6 + 1;
	public static final int APPRAISAL_MACHINE_ID_8				= APPRAISAL_MACHINE_ID_7 + 1;
	public static final int APPRAISAL_MACHINE_ID_9				= APPRAISAL_MACHINE_ID_8 + 1;
	public static final int APPRAISAL_MACHINE_ID_10				= APPRAISAL_MACHINE_ID_9 + 1;
	public static final int APPRAISAL_MACHINE_ID_11				= APPRAISAL_MACHINE_ID_10 + 1;
	public static final int APPRAISAL_MACHINE_ID_12				= APPRAISAL_MACHINE_ID_11 + 1;
	public static final int APPRAISAL_MACHINE_ID_13				= APPRAISAL_MACHINE_ID_12 + 1;
	public static final int APPRAISAL_MACHINE_ID_14				= APPRAISAL_MACHINE_ID_13 + 1;
	public static final int APPRAISAL_MACHINE_ID_15				= APPRAISAL_MACHINE_ID_14 + 1;
	public static final int APPRAISAL_MACHINE_ID_16				= APPRAISAL_MACHINE_ID_15 + 1;
	public static final int APPRAISAL_MACHINE_ID_17				= APPRAISAL_MACHINE_ID_16 + 1;
	public static final int APPRAISAL_MACHINE_ID_18				= APPRAISAL_MACHINE_ID_17 + 1;
	public static final int APPRAISAL_MACHINE_ID_19				= APPRAISAL_MACHINE_ID_18 + 1;
	public static final int APPRAISAL_MACHINE_MAX				= APPRAISAL_MACHINE_ID_19 + 1;
	
	// ------------------------------------- SHEET_DAILY_GIFT -------------------------------
	public static final int DAILY_GIFT_USER_LEVEL				= 0;
	public static final int DAILY_GIFT_PACK_1					= DAILY_GIFT_USER_LEVEL + 1;
	public static final int DAILY_GIFT_PACK_2					= DAILY_GIFT_PACK_1 + 1;
	public static final int DAILY_GIFT_PACK_3					= DAILY_GIFT_PACK_2 + 1;
	public static final int DAILY_GIFT_PACK_4					= DAILY_GIFT_PACK_3 + 1;
	public static final int DAILY_GIFT_PACK_5					= DAILY_GIFT_PACK_4 + 1;
	public static final int DAILY_GIFT_PACK_6					= DAILY_GIFT_PACK_5 + 1;
	public static final int DAILY_GIFT_PACK_7					= DAILY_GIFT_PACK_6 + 1;
	public static final int DAILY_GIFT_PACK_8					= DAILY_GIFT_PACK_7 + 1;
	public static final int DAILY_GIFT_PACK_9					= DAILY_GIFT_PACK_8 + 1;
	public static final int DAILY_GIFT_PACK_10					= DAILY_GIFT_PACK_9 + 1;
	public static final int DAILY_GIFT_PACK_11					= DAILY_GIFT_PACK_10 + 1;
	public static final int DAILY_GIFT_PACK_12					= DAILY_GIFT_PACK_11 + 1;
	public static final int DAILY_GIFT_PACK_13					= DAILY_GIFT_PACK_12 + 1;
	public static final int DAILY_GIFT_PACK_14					= DAILY_GIFT_PACK_13 + 1;
	public static final int DAILY_GIFT_PACK_15					= DAILY_GIFT_PACK_14 + 1;
	public static final int DAILY_GIFT_PACK_MAX					= DAILY_GIFT_PACK_15 + 1;
	
	// ------------------------------------- SHEET_FIRST_PAY_GIFT -------------------------------
	public static final int FIRSTPAY_GIFT_PACK_1				= 0;
	public static final int FIRSTPAY_GIFT_PACK_2				= 1;
	public static final int FIRSTPAY_GIFT_PACK_3				= 2;
	public static final int FIRSTPAY_GIFT_PACK_4				= 3;
	
	// ------------------------------------- GIFT_ALPHA_TEST -------------------------------
	public static final int GIFT_ALPHA_TEST_ID					= 0;
	public static final int GIFT_ALPHA_TEST_FB_ID				= 1;
	public static final int GIFT_ALPHA_TEST_ZING_ID				= 2;
	public static final int GIFT_ALPHA_TEST_DIAMOND_AMOUNT		= 3;
	
	// ------------------------------------- CONSTANT -------------------------------
	public static final int CONSTANT_USER_LEVEL					= 0;
	public static final int CONSTANT_DO_PAID_UNLOCK				= CONSTANT_USER_LEVEL + 1;
	public static final int CONSTANT_ORDER_CONTROL_ENOUGH		= CONSTANT_DO_PAID_UNLOCK + 1;
	public static final int CONSTANT_ORDER_CONTROL_MISS			= CONSTANT_ORDER_CONTROL_ENOUGH + 1;
	public static final int CONSTANT_ORDER_BUG_PEARL			= CONSTANT_ORDER_CONTROL_MISS + 1;
	public static final int CONSTANT_ORDER_PEARL_RATE			= CONSTANT_ORDER_BUG_PEARL + 1;
	public static final int CONSTANT_ORDER_BUG_RATE				= CONSTANT_ORDER_PEARL_RATE + 1;
	public static final int CONSTANT_REPUTATION_COLLECT_MAX		= CONSTANT_ORDER_BUG_RATE + 1;
	public static final int CONSTANT_NPC_MACHINE_CORRUPT		= CONSTANT_REPUTATION_COLLECT_MAX + 1;
	public static final int CONSTANT_EXP_REPAIR_MACHINE			= CONSTANT_NPC_MACHINE_CORRUPT + 1;
	public static final int CONSTANT_EXP_CATCH_BUG				= CONSTANT_EXP_REPAIR_MACHINE + 1;
	public static final int CONSTANT_ITEM_PER_ORDER				= CONSTANT_EXP_CATCH_BUG + 1;
	public static final int CONSTANT_ITEM_MAX					= CONSTANT_ITEM_PER_ORDER + 1;
    public static final int CONSTANT_ITEM_PER_ORDER_BUG_PEARL	= CONSTANT_ITEM_MAX + 1;
    public static final int CONSTANT_ITEM_MAX_ORDER_BUG_PEARL	= CONSTANT_ITEM_PER_ORDER_BUG_PEARL + 1;
    public static final int CONSTANT_ITEM_MIN_ORDER_DAILY       = CONSTANT_ITEM_MAX_ORDER_BUG_PEARL + 1;
    public static final int CONSTANT_ITEM_MAX_ORDER_DAILY       = CONSTANT_ITEM_MIN_ORDER_DAILY + 1;
	public static final int CONSTANT_NEW_ORDER_WAIT_TIME		= CONSTANT_ITEM_MAX_ORDER_DAILY + 1;
	public static final int CONSTANT_DAILY_ORDER_DIAMOND_RATIO	= CONSTANT_NEW_ORDER_WAIT_TIME + 1;
	public static final int CONSTANT_DO_LETTER_1				= CONSTANT_DAILY_ORDER_DIAMOND_RATIO + 1;
	public static final int CONSTANT_DO_LETTER_2				= CONSTANT_DO_LETTER_1 + 1;
	public static final int CONSTANT_DO_LETTER_3				= CONSTANT_DO_LETTER_2 + 1;
	public static final int CONSTANT_DO_LETTER_4				= CONSTANT_DO_LETTER_3 + 1;
	public static final int CONSTANT_DO_LETTER_5				= CONSTANT_DO_LETTER_4 + 1;
	public static final int CONSTANT_DO_LETTER_6				= CONSTANT_DO_LETTER_5 + 1;
	public static final int CONSTANT_DO_FREE_GOLD_COEFFICIENT_RATE	= CONSTANT_DO_LETTER_6 + 1;
	public static final int CONSTANT_DO_FREE_EXP_COEFFICIENT_RATE	= CONSTANT_DO_FREE_GOLD_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_DO_PAID_GOLD_COEFFICIENT_RATE	= CONSTANT_DO_FREE_EXP_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_DO_PAID_EXP_COEFFICIENT_RATE	= CONSTANT_DO_PAID_GOLD_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_DO_RANDOM_PLANT			= CONSTANT_DO_PAID_EXP_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_DO_PAID_ITEM_MIN			= CONSTANT_DO_RANDOM_PLANT + 1;
	public static final int CONSTANT_DO_PAID_ITEM_MAX			= CONSTANT_DO_PAID_ITEM_MIN + 1;
	public static final int CONSTANT_DO_BUG_PEARL_COEFFICIENT_RATE	= CONSTANT_DO_PAID_ITEM_MAX + 1;
	public static final int CONSTANT_ORDER_PLANT_ITEM_MIN		= CONSTANT_DO_BUG_PEARL_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_ORDER_PLANT_ITEM_MAX		= CONSTANT_ORDER_PLANT_ITEM_MIN + 1;
	public static final int CONSTANT_ITEM_PER_ORDER_PLANT		= CONSTANT_ORDER_PLANT_ITEM_MAX + 1;
	public static final int CONSTANT_ORDER_PLANT				= CONSTANT_ITEM_PER_ORDER_PLANT + 1;
	public static final int CONSTANT_OWL_POWER_LIMIT			= CONSTANT_ORDER_PLANT + 1;
	public static final int CONSTANT_ORDER_ITEM_PLANT_PLUS		= CONSTANT_OWL_POWER_LIMIT + 1;
	public static final int CONSTANT_NO_GOLD_COEFFICIENT_RATE	= CONSTANT_ORDER_ITEM_PLANT_PLUS + 1;
	public static final int CONSTANT_NO_XP_COEFFICIENT_RATE		= CONSTANT_NO_GOLD_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_NO_ITEM_PRODUCT_RATE		= CONSTANT_NO_XP_COEFFICIENT_RATE + 1;
	public static final int CONSTANT_PS_ITEM_EXPIRED_TIME		= CONSTANT_NO_ITEM_PRODUCT_RATE + 1;
	public static final int CONSTANT_MAX_AIRSHIP_PER_DAY		= CONSTANT_PS_ITEM_EXPIRED_TIME + 1;
	public static final int CONSTANT_AIRSHIP_MIN_ITEM_TYPE		= CONSTANT_MAX_AIRSHIP_PER_DAY + 1;
	public static final int CONSTANT_AIRSHIP_MAX_ITEM_TYPE		= CONSTANT_AIRSHIP_MIN_ITEM_TYPE + 1;
	public static final int CONSTANT_AIRSHIP_MIN_CARGO_NUM_PER_ITEM_TYPE		= CONSTANT_AIRSHIP_MAX_ITEM_TYPE + 1;
	public static final int CONSTANT_AIRSHIP_MAX_CARGO_NUM_PER_ITEM_TYPE		= CONSTANT_AIRSHIP_MIN_CARGO_NUM_PER_ITEM_TYPE + 1;
	public static final int CONSTANT_AIRSHIP_EASY_REQUEST						= CONSTANT_AIRSHIP_MAX_CARGO_NUM_PER_ITEM_TYPE + 1;
	public static final int CONSTANT_AIRSHIP_MEDIUM_REQUEST						= CONSTANT_AIRSHIP_EASY_REQUEST + 1;
	public static final int CONSTANT_AIRSHIP_HARD_REQUEST						= CONSTANT_AIRSHIP_MEDIUM_REQUEST + 1;
	public static final int CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_EASY			= CONSTANT_AIRSHIP_HARD_REQUEST + 1;
	public static final int CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_EASY			= CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_EASY + 1;
	public static final int CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_MEDIUM		= CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_EASY + 1;
	public static final int CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_MEDIUM		= CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_MEDIUM + 1;
	public static final int CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_HARD			= CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_MEDIUM + 1;
	public static final int CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_HARD			= CONSTANT_AIRSHIP_MIN_NUM_REQUIRE_ITEM_HARD + 1;
	public static final int CONSTANT_FORTUNE_WHEEL_GOLD							= CONSTANT_AIRSHIP_MAX_NUM_REQUIRE_ITEM_HARD + 1;
	public static final int CONSTANT_FORTUNE_WHEEL_NORMAL_BASIC_PRICE			= CONSTANT_FORTUNE_WHEEL_GOLD + 1;
	public static final int CONSTANT_FORTUNE_WHEEL_LAST_BASIC_PRICE				= CONSTANT_FORTUNE_WHEEL_NORMAL_BASIC_PRICE + 1;
	public static final int CONSTANT_AIRSHIP_REPUTATION							= CONSTANT_FORTUNE_WHEEL_LAST_BASIC_PRICE + 1;
	public static final int CONSTANT_AIRSHIP_MIN_BONUS_GOLD						= CONSTANT_AIRSHIP_REPUTATION + 1;
	public static final int CONSTANT_AIRSHIP_MAX_BONUS_GOLD						= CONSTANT_AIRSHIP_MIN_BONUS_GOLD + 1;
	public static final int CONSTANT_AIRSHIP_MIN_BONUS_EXP						= CONSTANT_AIRSHIP_MAX_BONUS_GOLD + 1;
	public static final int CONSTANT_AIRSHIP_MAX_BONUS_EXP						= CONSTANT_AIRSHIP_MIN_BONUS_EXP + 1;
	public static final int CONSTANT_AIRSHIP_REPUTATION_REDUCE_PER_HOUR			= CONSTANT_AIRSHIP_MAX_BONUS_EXP + 1;
	public static final int CONSTANT_PERCENT_DROP_MOONCAKE_CATCH_BUG			= CONSTANT_AIRSHIP_REPUTATION_REDUCE_PER_HOUR + 1;
	public static final int CONSTANT_NUM_DROP_MOONCAKE							= CONSTANT_PERCENT_DROP_MOONCAKE_CATCH_BUG + 1;
	public static final int CONSTANT_NUM_DROP_HALLOWEEN_1						= CONSTANT_NUM_DROP_MOONCAKE + 1;
	public static final int CONSTANT_NUM_DROP_HALLOWEEN_2						= CONSTANT_NUM_DROP_HALLOWEEN_1 + 1;
	public static final int CONSTANT_NUM_DROP_HALLOWEEN_3						= CONSTANT_NUM_DROP_HALLOWEEN_2 + 1;
	public static final int CONSTANT_FORTUNE_FAKE_DIAMOND						= CONSTANT_NUM_DROP_HALLOWEEN_3 + 1;
	public static final int CONSTANT_MAX										= CONSTANT_FORTUNE_FAKE_DIAMOND + 1;
	
	// ------------------------------------- OWL_SLOT_UNLOCK -------------------------------
	public static final int OWL_SLOT_UNLOCK_DIAMOND				= 0;
	public static final int OWL_SLOT_UNLOCK_GOLD				= OWL_SLOT_UNLOCK_DIAMOND + 1;
	
	public static final int OWL_SLOT_UNLOCK 					= 0;
	public static final int OWL_SLOT_UNLOCK_DIAMOND_TYPE 		= 1;
	public static final int OWL_SLOT_UNLOCK_GOLD_TYPE 			= 2;
	
	
	// ------------------------------------- GIFTS_INFO SHEET -------------------------------
	public static final int GIFT_INFO							= 0;
	public static final int GIFT_INFO_ID						= 0;
	public static final int GIFT_INFO_NAME						= 1;
	public static final int GIFT_INFO_DESCRIPTION				= 2;
	public static final int GIFT_INFO_ITEMS_LIST				= 3;

	public static final int GIFT_INFO_NAME_EN					= 4;
	public static final int GIFT_INFO_DESCRIPTION_EN			= 5;
	public static final int GIFT_INFO_NAME_SC					= 6;
	public static final int GIFT_INFO_DESCRIPTION_SC			= 7;
	public static final int GIFT_INFO_NAME_TC					= 8;
	public static final int GIFT_INFO_DESCRIPTION_TC			= 9;
	
	// gift id
	public static final int GIFT_CONNECT_FB						= 0;
	public static final int GIFT_CONNECT_ZM						= 1;
	public static final int GIFT_LIKE_FB						= 3;
	public static final int GIFT_SHARE_FB						= 4;
	public static final int GIFT_CLOSED_USER					= 5;
	public static final int GIFT_FIRST_PAY						= 6;
	public static final int GIFT_CONNECT_ZALO					= 29;
	
	
	// ------------------------------------- NOTIFY_TEXT SHEET -------------------------------
	public static final int NOTIFY_TEXT_ID							= 0;
	public static final int NOTIFY_TEXT_TITLE						= NOTIFY_TEXT_ID + 1;
	public static final int NOTIFY_TEXT_DESCRIPTION					= NOTIFY_TEXT_TITLE + 1;

	public static final int NOTIFY_TEXT_TITLE_EN					= NOTIFY_TEXT_DESCRIPTION + 1;
	public static final int NOTIFY_TEXT_DESCRIPTION_EN				= NOTIFY_TEXT_TITLE_EN + 1;
	public static final int NOTIFY_TEXT_TITLE_SC					= NOTIFY_TEXT_DESCRIPTION_EN + 1;
	public static final int NOTIFY_TEXT_DESCRIPTION_SC				= NOTIFY_TEXT_TITLE_SC + 1;
	public static final int NOTIFY_TEXT_TITLE_TC					= NOTIFY_TEXT_DESCRIPTION_SC + 1;
	public static final int NOTIFY_TEXT_DESCRIPTION_TC				= NOTIFY_TEXT_TITLE_TC + 1;
	
	public static final int NOTIFY_SERVER_OVERLOADED				= 0;
	public static final int NOTIFY_SERVER_MAINTAINED				= 1;
	public static final int NOTIFY_NEW_UPDATE						= 9;
	
	// ------------------------------------- GIFT_CODE -------------------------------
	public static final int SHEET_GIFT_CODE						= 0;
	
	public static final int GIFT_CODE_ID						= 0;
	public static final int GIFT_CODE_TYPE						= GIFT_CODE_ID + 1;
	public static final int GIFT_CODE_CODE						= GIFT_CODE_TYPE + 1;
	public static final int GIFT_CODE_NAME						= GIFT_CODE_CODE + 1;
	public static final int GIFT_CODE_DESCRIPTION				= GIFT_CODE_NAME + 1;
	public static final int GIFT_CODE_GIFT						= GIFT_CODE_DESCRIPTION + 1;
	public static final int GIFT_CODE_START_TIME				= GIFT_CODE_GIFT + 1;
	public static final int GIFT_CODE_END_TIME					= GIFT_CODE_START_TIME + 1;
	
	public static final int GIFT_CODE_USE_TIME					= GIFT_CODE_END_TIME + 1;
	
	// ------------------------------------- SHEET_EVENT -------------------------------
	public static final int EVENT_ID							= 0;
	public static final int EVENT_NAME							= EVENT_ID + 1;
	public static final int EVENT_START							= EVENT_NAME + 1;
	public static final int EVENT_END							= EVENT_START + 1;
	public static final int EVENT_START_BEFORE					= EVENT_END + 1;
	public static final int EVENT_END_BEFORE					= EVENT_START_BEFORE + 1;
	public static final int EVENT_EMO_ID						= EVENT_END_BEFORE + 1;

	public static final int EVENT_NAME_EN						= EVENT_EMO_ID + 1;
	public static final int EVENT_NAME_SC						= EVENT_NAME_EN + 1;
	public static final int EVENT_NAME_TC						= EVENT_NAME_SC + 1;
	public static final int EVENT_MAX							= EVENT_NAME_TC + 1;
	
	// ------------------------------------- SHEET_EVENT_MAIN_OBJECT -------------------------------
	public static final int EMO_ID								= 0;
	public static final int EMO_NAME							= EMO_ID + 1;
	public static final int EMO_APPEAR_RATIO					= EMO_NAME + 1;
	public static final int EMO_POS								= EMO_APPEAR_RATIO + 1;
	public static final int EMO_HIT_COUNT						= EMO_POS + 1;
	public static final int EMO_RESET_HIT_FLAG					= EMO_HIT_COUNT + 1;
	public static final int EMO_EMI_ID							= EMO_RESET_HIT_FLAG + 1;
	public static final int EMO_FIRST_ACTION					= EMO_EMI_ID + 1;
	public static final int EMO_LAST_ACTION						= EMO_FIRST_ACTION + 1;
	public static final int EMO_HIT_ACTION						= EMO_LAST_ACTION + 1;
	public static final int EMO_SPRITE_ID						= EMO_HIT_ACTION + 1;
	
	public static final int EMO_NAME_EN							= EMO_SPRITE_ID + 1;
	public static final int EMO_NAME_SC							= EMO_NAME_EN + 1;
	public static final int EMO_NAME_TC							= EMO_NAME_SC + 1;
	public static final int EMO_MAX								= EMO_NAME_TC + 1;
	
	// ------------------------------------- SHEET_EVENT_MAIN_ITEM -------------------------------
	public static final int EMI_ID								= 0;
	public static final int EMI_NAME							= EMI_ID + 1;
	public static final int EMI_DESCRIPTION						= EMI_NAME + 1;
	public static final int EMI_DIAMOND_BUY						= EMI_DESCRIPTION + 1;
	public static final int EMI_GOLD_DEFAULT					= EMI_DIAMOND_BUY + 1;
	public static final int EMI_GOLD_MIN						= EMI_GOLD_DEFAULT + 1;
	public static final int EMI_GOLD_MAX						= EMI_GOLD_MIN + 1;
	public static final int EMI_STOCK_ID						= EMI_GOLD_MAX + 1;
	public static final int EMI_ANIM_ID							= EMI_STOCK_ID + 1;

	public static final int EMI_NAME_EN							= EMI_ANIM_ID + 1;
	public static final int EMI_DESCRIPTION_EN					= EMI_NAME_EN + 1;
	public static final int EMI_NAME_SC							= EMI_DESCRIPTION_EN + 1;
	public static final int EMI_DESCRIPTION_SC					= EMI_NAME_SC + 1;
	public static final int EMI_NAME_TC							= EMI_DESCRIPTION_SC + 1;
	public static final int EMI_DESCRIPTION_TC					= EMI_NAME_TC + 1;
	public static final int EMI_MAX								= EMI_DESCRIPTION_TC + 1;
	
	// ------------------------------------- SHEET_NPC_MERCHANT -------------------------------
	public static final int MERCHANT_LEVEL						= 0;
	public static final int MERCHANT_LIST_SIZE					= 1;
	public static final int MERCHANT_APPEAR_TIME				= 2;
	public static final int MERCHANT_MIN_PERCENT				= 3;
	public static final int MERCHANT_MAX_PERCENT				= 4;
	public static final int MERCHANT_MAX_REQUEST_NUM_STOCK_SILO = 5;
	public static final int MERCHANT_PRICE_RATIO_STOCK_SILO		= 6;
	public static final int MERCHANT_MAX_REQUEST_NUM_STOCK_BARN	= 7;
	public static final int MERCHANT_PRICE_RATIO_STOCK_BARN		= 8;
	public static final int MERCHANT_GOLD_TO_ITEM				= 9;
	public static final int MERCHANT_MAX						= 10;
	
	// ------------------------------------- SHEET_FEED_INFO -------------------------------
	public static final int FEED_ID								= 0;
	public static final int FEED_KEY							= 1;
	public static final int FEED_TITLE							= 2;
	public static final int FEED_DESCRIPTION					= 3;
	public static final int FEED_LINK							= 4;
	public static final int FEED_IMG_LINK						= 5;
	public static final int FEED_CAPTION						= 6;
	
	public static final int FEED_TITLE_EN						= 7;
	public static final int FEED_DESCRIPTION_EN					= 8;
	public static final int FEED_TITLE_SC						= 9;
	public static final int FEED_DESCRIPTION_SC					= 10;
	public static final int FEED_TITLE_TC						= 11;
	public static final int FEED_DESCRIPTION_TC					= 12;
	public static final int FEED_MAX							= 13;
	
	// ------------------------------------- SHEET_CDN -------------------------------
	public static final int CDN_PLATFORM						= 0;
	public static final int CDN_RESOLUTION						= CDN_PLATFORM + 1;
	public static final int CDN_DISTRIBUTOR						= CDN_RESOLUTION + 1;
	public static final int CDN_NEWEST_VERSION					= CDN_DISTRIBUTOR + 1;
	public static final int CDN_LINK							= CDN_NEWEST_VERSION + 1;
	public static final int CDN_SPRITE_ID						= CDN_LINK + 1;
	public static final int CDN_NAME							= CDN_SPRITE_ID + 1;
	public static final int CDN_MD5								= CDN_NAME + 1;
	public static final int CDN_TYPE							= CDN_MD5 + 1;
	public static final int CDN_ID								= CDN_TYPE + 1;
	public static final int CDN_ACTIVE							= CDN_ID + 1;
	public static final int CDN_MAX								= CDN_ACTIVE + 1;
	
	// ------------------------------------- SHEET_EVENT_GLOBAL -------------------------------
	public static final int EVENT_GLOBAL_ID						= 0;
	public static final int EVENT_GLOBAL_NAME					= EVENT_GLOBAL_ID + 1;
	public static final int EVENT_GLOBAL_START_DATE				= EVENT_GLOBAL_NAME + 1;
	public static final int EVENT_GLOBAL_END_DATE				= EVENT_GLOBAL_START_DATE + 1;
	public static final int EVENT_GLOBAL_BONUS_EXP_RATE				= EVENT_GLOBAL_END_DATE + 1;
	public static final int EVENT_GLOBAL_BONUS_GOLD_RATE				= EVENT_GLOBAL_BONUS_EXP_RATE + 1;
	
	// ------------------------------------- SHEET AIRSHIP -------------------------------
	public static final int AIRSHIP_ID								= 0;
	public static final int AIRSHIP_REQUIRE_LEVEL					= 1;
	public static final int AIRSHIP_REQUIRE_ITEMS					= 2;
	public static final int AIRSHIP_UNLOCK_DURATION					= 3;
	public static final int AIRSHIP_STAY_DURATION					= 4;
	public static final int AIRSHIP_LEAVE_DURATION					= 5;
	public static final int AIRSHIP_MIN_POINT						= 6;
	public static final int AIRSHIP_MAX_POINT						= 7;
	public static final int AIRSHIP_HELP_TIME						= 8;
	public static final int AIRSHIP_MIN_BONUS_GOLD					= 9;
	public static final int AIRSHIP_MAX_BONUS_GOLD					= 10;
	public static final int AIRSHIP_MIN_BONUS_EXP					= 11;
	public static final int AIRSHIP_MAX_BONUS_EXP					= 12;
	public static final int AIRSHIP_POINT_REDUCE					= 13;
	public static final int AIRSHIP_MIN_REPUTATION					= 14;
	public static final int AIRSHIP_MAX_REPUTATION					= 15;
	public static final int AIRSHIP_REPUTATION_REDUCE				= 16;
	
	// ------------------------------------- SHEET ITEMS EXP -------------------------------
	public static final int ITEM_VALUE_INDEX								= 0;
	public static final int ITEM_VALUE_NAME								= 1;
	public static final int ITEM_VALUE_EXP								= 2;
	public static final int ITEM_VALUE_GOLD								= 3;
	public static final int ITEM_VALUE_REPUTATION						= 4;
	public static final int ITEM_TOMKID_GOLD_BASIC						= 5;
	public static final int ITEM_TOMKID_MIN_NUM_PACK_1					= 6;
	public static final int ITEM_TOMKID_MAX_NUM_PACK_1					= 7;
	public static final int ITEM_TOMKID_MIN_RATIO_PACK_1				= 8;
	public static final int ITEM_TOMKID_MAX_RATIO_PACK_1				= 9;
	public static final int ITEM_TOMKID_MIN_NUM_PACK_2					= 10;
	public static final int ITEM_TOMKID_MAX_NUM_PACK_2					= 11;
	public static final int ITEM_TOMKID_MIN_RATIO_PACK_2				= 12;
	public static final int ITEM_TOMKID_MAX_RATIO_PACK_2				= 13;
	public static final int ITEM_TOMKID_MIN_NUM_PACK_3					= 14;
	public static final int ITEM_TOMKID_MAX_NUM_PACK_3					= 15;
	public static final int ITEM_TOMKID_MIN_RATIO_PACK_3				= 16;
	public static final int ITEM_TOMKID_MAX_RATIO_PACK_3				= 17;
	public static final int ITEM_FORTUNE_WHEEL_REAL_RATIO				= 18;
	public static final int ITEM_FORTUNE_WHEEL_FAKE_RATIO				= 19;
	
	// ------------------------------------- SHEET TOM KID -------------------------------
	public static final int TOMKID_INDEX								= 0;
	public static final int TOMKID_LEVEL_UNLOCK							= 1;
	public static final int TOMKID_FIRST_USE_DURATION					= 2;
	public static final int TOMKID_LONG_REST_DURATION					= 3;
	public static final int TOMKID_SHORT_REST_DURATION					= 4;
	public static final int TOMKID_DIAMOND_1_DAY						= 5;
	public static final int TOMKID_DIAMOND_3_DAY						= 6;
	public static final int TOMKID_DIAMOND_7_DAY						= 7;
	
	// ------------------------------------- SHEET FORTUNE WHEEL -------------------------------
	public static final int ROTA_FORTUNAE_INDEX							= 0;
	public static final int ROTA_FORTUNAE_PAID_OR_FREE					= 1;
	public static final int ROTA_FORTUNAE_DIAMOND_RATIO					= 2;
	
	// ------------------------------------- SHEET FORTUNE WHEEL ITEMS  -------------------------------
	public static final int ROTA_FORTUNAE_FREE_1						= 0;
	public static final int ROTA_FORTUNAE_PAID_1						= 1;
	public static final int ROTA_FORTUNAE_PAID_2						= 2;
	public static final int ROTA_FORTUNAE_PAID_3						= 3;
	public static final int ROTA_FORTUNAE_PAID_4						= 4;
	public static final int ROTA_FORTUNAE_PAID_5						= 5;
	public static final int ROTA_FORTUNAE_FREE_2						= 6;
	
	// ------------------------------------- SHEET INVITE FRIENDS  -------------------------------
	public static final int INVITE_FRIEND_ID							= 0;
	public static final int INVITE_FRIEND_NUM_REQUIRED					= 1;
	public static final int INVITE_FRIEND_GIFT							= 2;
	
	// ------------------------------------- SHEET EVENT MID AUTUMN  -------------------------------
	public static final int EVENT_MID_AUTUMN_ID						= 0;
	public static final int EVENT_MID_AUTUMN_NAME					= 1;
	public static final int EVENT_MID_AUTUMN_GIFT_ITEM				= 2;
	public static final int EVENT_MID_AUTUMN_NUM_REQUIRE			= 3;
	public static final int EVENT_MID_AUTUMN_NAME_EN				= 4;
	public static final int EVENT_MID_AUTUMN_HINT_1					= 5;
	public static final int EVENT_MID_AUTUMN_HINT_2					= 6;
	public static final int EVENT_MID_AUTUMN_HINT_1_EN				= 7;
	public static final int EVENT_MID_AUTUMN_HINT_2_EN				= 8;
	public static final int EVENT_MID_AUTUMN_TOTAL					= 9;
	
	// retry 1
	public static final int ROTA_FORTUNAE_FREE_1_1ST_RETRY				= 7;
	public static final int ROTA_FORTUNAE_PAID_1_1ST_RETRY				= 8;
	public static final int ROTA_FORTUNAE_PAID_2_1ST_RETRY				= 9;
	public static final int ROTA_FORTUNAE_PAID_3_1ST_RETRY				= 10;
	public static final int ROTA_FORTUNAE_PAID_4_1ST_RETRY				= 11;
	public static final int ROTA_FORTUNAE_PAID_5_1ST_RETRY				= 12;
	public static final int ROTA_FORTUNAE_FREE_2_1ST_RETRY				= 13;
	
	// retry 2
	public static final int ROTA_FORTUNAE_FREE_1_2ND_RETRY				= 14;
	public static final int ROTA_FORTUNAE_PAID_1_2ND_RETRY				= 15;
	public static final int ROTA_FORTUNAE_PAID_2_2ND_RETRY				= 16;
	public static final int ROTA_FORTUNAE_PAID_3_2ND_RETRY				= 17;
	public static final int ROTA_FORTUNAE_PAID_4_2ND_RETRY				= 18;
	public static final int ROTA_FORTUNAE_PAID_5_2ND_RETRY				= 19;
	public static final int ROTA_FORTUNAE_FREE_2_2ND_RETRY				= 20;
	
	// retry 3
	public static final int ROTA_FORTUNAE_FREE_1_3RD_RETRY				= 21;
	public static final int ROTA_FORTUNAE_PAID_1_3RD_RETRY				= 22;
	public static final int ROTA_FORTUNAE_PAID_2_3RD_RETRY				= 23;
	public static final int ROTA_FORTUNAE_PAID_3_3RD_RETRY				= 24;
	public static final int ROTA_FORTUNAE_PAID_4_3RD_RETRY				= 25;
	public static final int ROTA_FORTUNAE_PAID_5_3RD_RETRY				= 26;
	public static final int ROTA_FORTUNAE_FREE_2_3RD_RETRY				= 27;
	
	// ------------------------------------- SHEET COMBO  -------------------------------
	public static final int COMBO_ID									= 0;
	public static final int COMBO_NAME									= 1;
	public static final int COMBO_REQUIRE								= 2;
	public static final int COMBO_BONUS_PLANT_EXP						= 3;
	public static final int COMBO_BONUS_PLANT_TIME						= 4;
	public static final int COMBO_BONUS_BUG								= 5;
	public static final int COMBO_BONUS_AIRSHIP_GOLD					= 6;
	public static final int COMBO_BONUS_AIRSHIP_EXP						= 7;
	public static final int COMBO_BONUS_ORDER_NORMAL					= 8;
	public static final int COMBO_BONUS_ORDER_DAILY						= 9;
	
	// ------------------------------------- SHEET DROP CUSTOM  --------------------------
	public static final int DROP_CUSTOM_ID								= 0;
	public static final int DROP_CUSTOM_ITEM							= 1;
	public static final int DROP_CUSTOM_LEVEL							= 2;
	
	// ------------------------------------- CONST -------------------------------
	public static final int GIFT_CODE_ENTER_MAX					= 5;
	
	public static final int MAX_SLOT_PER_FLOOR					= 6;
	public static final int SLOT_TUTORIAL						= 6;
			
	public static final int STOCK_SILO							= 0;
	public static final int STOCK_BARN							= 1;
	public static final int STOCK_WAREHOUSE						= 2;
	public static final int STOCK_EVENT							= 3;
		
	public static final int MACHINE_LOCK						= 0;
	public static final int MACHINE_UNLOCK						= MACHINE_LOCK + 1;
	public static final int MACHINE_READY						= MACHINE_UNLOCK + 1;
			
	public static final int MACHINE_DURABILITY_ADD				= 10;
	public static final int MACHINE_SLOT_MAX					= 16;
	public static final int MACHINE_DEFAULT_STOCK_CAPACITY		= 13;
			
	public static final int MACHINE_STATUS_PROCESSING			= 1;
	public static final int MACHINE_STATUS_COMPLETED			= MACHINE_STATUS_PROCESSING + 1;
				
	public static final int FLOOR_MAX							= 20;
	public static final int STOCK_MAX							= 3;
	
	// order
	public static final int ORDER_NORMAL						= 0;
	public static final int ORDER_DAILY							= ORDER_NORMAL + 1;
	public static final int ORDER_EVENT							= ORDER_DAILY + 1;
	public static final int ORDER_TYPE_MAX						= ORDER_EVENT + 1;
			
	public static final int ORDER_DAILY_INDEX					= 4; // start from 0
	public static final int ORDER_MAX							= 9;
	
	public static final int NEW_DAILY_ORDER_PAID_WAIT_TIME		= 3 * 60;	// 3 minutes
	
	public static final int USER_LEVEL_ORDER_CONTROL			= 10;
	public static final int USER_LEVEL_ORDER_BUG_PEARL			= 8;
	
	public static final int ORDER_LETTER_COUNT					= 6;
	
	public static final int NPC_SNOW_WHITE_ID					= 0;	// follow client data
	public static final int NPC_FROG_PRINCE_ID					= 2;	// follow client data
	public static final int NPC_TINKER_BELL_ID					= 3;	// follow client data
	
	public static final int ORDER_RANDOM_ITEM_FOLLOW_USER_STOCK	= 0;
	public static final int ORDER_RANDOM_ITEM					= 1;
	public static final int ORDER_RANDOM_BUG_AND_PEARL			= 2;
	
	public static final int MAX_FLOOR_ON_SCREEN					= 2;
		
	public static final int GOLD_ID								= 0;
	public static final int DIAMOND_ID							= 1;
	public static final int REPUTATION_ID						= 2;
	public static final int EXP_ID								= 3;
	public static final int EXP_GIFT							= 6;
		
	public static final int NPC_MAX								= 10;
			
	public static final int	IT_GAMEITEM							= -1;
	public static final int	IT_POT								= 0;
	public static final int	IT_PLANT							= 1;
	public static final int	IT_BUILDING							= 2;
	public static final int	IT_BUG								= 3;
	public static final int	IT_PRODUCT							= 4;
	public static final int	IT_MONEY							= 5;
	public static final int	IT_COUNT							= 6;
	public static final int	IT_DECOR							= 7;
	public static final int	IT_MATERIAL							= 8;
	public static final int	IT_EMO								= 9;
	public static final int	IT_EMI								= 10;
	public static final int IT_CLOUD_FLOOR						= 12;
	public static final int IT_EVENT							= 13;
	
	// APPRAISAL
	public static final int APPRAISAL_POT						= 0;
	public static final int APPRAISAL_FLOOR						= 1;
	public static final int APPRAISAL_MACHINE					= 2;
	public static final int APPRAISAL_STOCK						= 3;
	public static final int APPRAISAL_DECOR						= 4;
	
	// SHARE SOCIAL
	public static final int SHARE_FB_LEVEL_UP					= 0;
	
	// private shop
	public static final int PRIVATE_SHOP_MAX_SLOT				= 20;
	public static final int PRIVATE_SHOP_DEFAULT_SLOT_NUMBER	= 2;
	public static final int PRIVATE_SHOP_START_SLOT_FRIEND		= 2;
	public static final int PRIVATE_SHOP_NUMBER_OF_SLOT_FRIEND	= 4;
	public static final int PRIVATE_SHOP_START_SLOT_DIAMOND		= 6;
	public static final int PRIVATE_SHOP_ADS_PENDING_DURATION	= 5; // minutes
	public static final int PRIVATE_SHOP_LOCK_EXPIRED_DURATION	= 1; // after 12 seconds, the lock (if any) will be automatically unlocked.
	public static final int PRIVATE_SHOP_ADS_MAX_TIME			= 3 * 60 * 60;	// 3 hours
	public static final int PRIVATE_SHOP_ITEM_EXPIRE_TIME		= 24 * 60 * 60;
	
	// npc private shop
	public static final int NPC_SHOP_REFRESH_TIME				= 24 * 60 * 60; // once a day
	public static final int NPC_SHOP_NUM_ITEM_SELL				= 6;
	
	// machine
	public static final int MAGIC_WATER_BOTTLE_DROP_RATIO		= 10;
	public static final int MACHINE_REDUCE_GOLD_MAINTAIN_RATIO	= 10;
	public static final int MACHINE_DATA_LOCK_TIME				= 5;
	
	// private shop item
	public static final int PS_ITEM_STATUS_EMPTY				= 0;
	public static final int PS_ITEM_STATUS_SELLING				= 1;
	public static final int PS_ITEM_STATUS_SOLD					= 2;
	public static final int PS_ITEM_EXPIRING					= 3;
	public static final int PS_ITEM_EXPIRED						= 4;
			
	// newsboard		
	public static final int NEWS_BOARD_MAX_SLOT					= 36;
	public static final int NEWS_BOARD_ADS_MAX_TIME				= 5 * 60;//5 * 60; // 5 minutes
	public static final int NEWS_BOARD_REFRESH_TIME				= 1 * 60 * 60;//3 * 60 * 60; // 3 hours
	public static final int NEWS_BOARD_REFRESH_PRICE			= 1;
	
	// gift
	public static final int MAX_GIFT_DAILY_NUM					= 4;

	// drop item
	public static final int ACTION_HARVEST_PLANT				= 2;
	public static final int ACTION_HARVEST_PRODUCT				= 3;
	public static final int ACTION_REPAIR_FRIEND_MACHINE		= 4;
	
	// merchant
	public static final int MERCHANT_RED_HOOD					= 0;
	public static final int MERCHANT_PUSS_IN_BOOST				= 1;
	public static final int MERCHANT_GROUCHY					= 2;
	public static final int MERCHANT_MAX_CONCURRENT				= 2;
	
	// server ranking
	public static final int RANKING_SIZE						= 3;
	public static final int SORT_INTERVAL						= 10; // seconds
	
	// airship constant
	public static final int AIRSHIP_LOCKED								= -1;
	public static final int AIRSHIP_PENDING								= 0;
	public static final int AIRSHIP_UNLOCKED							= 1;
	public static final int AIRSHIP_LANDING								= 2;
	public static final int AIRSHIP_DEPARTING							= 3;
	public static final int AIRSHIP_FULL								= 4;
	public static final int AIRSHIP_LOCK_TIME							= 5;
	public static final int AIRSHIP_MAX_HELP							= 3;
	
	// tomkid constant
	public static final int TOMKID_LOCKED								= -1;
	public static final int TOMKID_READY								= 0;
	public static final int TOMKID_LONG_REST							= 1;
	public static final int TOMKID_SHORT_REST							= 2;
	public static final int TOMKID_NOT_HIRED							= 3;
	public static final int TOMKID_RESTING								= 4;
	public static final int TOMKID_PROVIDING_GOODS						= 5;
	public static final int TOMKID_DEFAULT_SUGGEST_NUM					= 3;
	public static final int TOMKID_HIRE_PACK_1							= 1;
	public static final int TOMKID_HIRE_PACK_2							= 2;
	public static final int TOMKID_HIRE_PACK_3							= 3;
	
	
	// for OFFER
	public static final int OFFER_USER_GROUP_UNPAID					= 0;
	public static final int OFFER_USER_GROUP_PAID_LESS_10			= 1;
	public static final int OFFER_USER_GROUP_PAID_LESS_20			= 2;
	public static final int OFFER_USER_GROUP_PAID_LESS_30			= 3;
	public static final int OFFER_USER_GROUP_PAID_LESS_40			= 4;
	public static final int OFFER_USER_GROUP_PAID_LESS_50			= 5;
	public static final int OFFER_USER_GROUP_PAID_LESS_60			= 6;
	public static final int OFFER_USER_GROUP_PAID_LESS_70			= 7;
	public static final int OFFER_USER_GROUP_PAID_LESS_80			= 8;
	public static final int OFFER_USER_GROUP_PAID_LESS_90			= 9;
	public static final int OFFER_USER_GROUP_PAID_LESS_100			= 10;
	public static final int OFFER_USER_GROUP_PAID_LESS_150			= 11;
	public static final int OFFER_USER_GROUP_PAID					= 12;
	public static final int OFFER_USER_GROUP_NEW_REG				= 13;
	
	public static final int OFFER_START								= 1;
	public static final int OFFER_END								= 2;
	public static final int OFFER_DURA								= 3;
	public static final int OFFER_NAME								= 4;
	public static final int OFFER_DESCRIPTION						= 5;
	public static final int OFFER_LINK_IMAGE						= 6;
	public static final int OFFER_MD5_IMAGE							= 7;
	public static final int OFFER_CONTENT_DIAMOND					= 8;
	public static final int OFFER_CONTENT_GOLD						= 9;
	public static final int OFFER_CONTENT_CASHIN					= 10;
	public static final int OFFER_CONTENT_SALE_OFF					= 11;
	public static final int OFFER_CONTENT_TOM_KID					= 12;
	public static final int OFFER_BUTTON_LABEL						= 13;
	public static final int OFFER_WEB_LINK							= 14;
	public static final int OFFER_AVAILABLE_TIME					= 15;
	
	// for unlock level
	public static final int USER_MATERIAL_UNLOCK					= USER_PROD_ID_UNLOCK + 1;
	public static final int USER_BUG_UNLOCK							= USER_MATERIAL_UNLOCK + 1;
	public static final int USER_DECOR_UNLOCK						= USER_BUG_UNLOCK + 1;
	

	// public static final int OFFER_PACK_UNPAID						= 0;
	// public static final int OFFER_PACK_PAID							= 1;
	// public static final int OFFER_PACK_PAID_LESS_10					= 10;
	// public static final int OFFER_PACK_PAID_LESS_20					= 20;
	// public static final int OFFER_PACK_PAID_LESS_30					= 30;
	// public static final int OFFER_PACK_PAID_LESS_40					= 40;
	// public static final int OFFER_PACK_PAID_LESS_50					= 50;
	// public static final int OFFER_PACK_PAID_LESS_60					= 60;
	// public static final int OFFER_PACK_PAID_LESS_70					= 70;
	// public static final int OFFER_PACK_PAID_LESS_80					= 80;
	// public static final int OFFER_PACK_PAID_LESS_90					= 90;
	// public static final int OFFER_PACK_PAID_LESS_100				= 100;
	// public static final int OFFER_PACK_PAID_LESS_150				= 150;
	
	// feature version
	public static final int FEATURE_RANKING_VERSION				= 146;
	public static final int FEATURE_REFRESH_NEWSBOARD			= 147;
	
	// PAYMENT TYPE
	public static final int PAYMENT_TYPE_LOCAL					= 0;
	public static final int PAYMENT_TYPE_IAP					= 1;
	public static final int PAYMENT_TYPE_ALL					= 2;
	
	// NEWSBOARD UDP
	public static final int RESULT_PER_LEVEL = 200;
	
	// NOTIFY
	public static final int NOTIFY_TYPE_MACHINE					= 0;
	public static final int NOTIFY_TYPE_AIRSHIP					= 1;
	
	public static final int NOTIFY_INDEX_DEFAULT				= 0;
	public static final int NOTIFY_INDEX_MACHINE_0				= 1;
	public static final int NOTIFY_INDEX_MACHINE_1				= 2;
	public static final int NOTIFY_INDEX_MACHINE_2				= 3;
	public static final int NOTIFY_INDEX_MACHINE_3				= 4;
	public static final int NOTIFY_INDEX_MACHINE_4				= 5;
	public static final int NOTIFY_INDEX_MACHINE_5				= 6;
	public static final int NOTIFY_INDEX_MACHINE_6				= 7;
	public static final int NOTIFY_INDEX_MACHINE_7				= 8;
	public static final int NOTIFY_INDEX_MACHINE_8				= 9;
	public static final int NOTIFY_INDEX_MACHINE_9				= 10;
	public static final int NOTIFY_INDEX_MACHINE_10				= 11;
	public static final int NOTIFY_INDEX_MACHINE_11				= 12;
	public static final int NOTIFY_INDEX_MACHINE_12				= 13;
	public static final int NOTIFY_INDEX_MACHINE_13				= 14;
	public static final int NOTIFY_INDEX_MACHINE_14				= 15;
	public static final int NOTIFY_INDEX_MACHINE_15				= 16;
	public static final int NOTIFY_INDEX_MACHINE_16				= 17;
	public static final int NOTIFY_INDEX_MACHINE_17				= 18;
	public static final int NOTIFY_INDEX_MACHINE_18				= 19;
	public static final int NOTIFY_INDEX_MACHINE_19				= 20;
	public static final int NOTIFY_INDEX_AIRSHIP				= 21;
	
	// MARKER EVENT
	public static final int EVENT_MARKER_TOM					= (1<<0);
	public static final int EVENT_MARKER_FORTUNE_WHEEL			= (1<<1);
	public static final int EVENT_MARKER_DAILY_ORDER			= (1<<2);
	public static final int EVENT_MARKER_AIRSHIP				= (1<<3);
	
	// BONUS MARKER
	public static final int BONUS_PLANT					= (1<<0);
	public static final int BONUS_ORDER_DAILY			= (1<<1);
	public static final int BONUS_ORDER_NORMAL			= (1<<2);
	public static final int BONUS_AIRSHIP_GOLD			= (1<<3);
	public static final int BONUS_AIRSHIP_EXP			= (1<<4);
	public static final int BONUS_TOM					= (1<<5);
	public static final int BONUS_FORTUNE_WHEEL			= (1<<6);
	public static final int BONUS_MACHINE_TIME			= (1<<7);
	public static final int BONUS_MACHINE_EXP			= (1<<8);
	public static final int BONUS_MACHINE_GOLD			= (1<<9);
	public static final int BONUS_MACHINE_UPGRADE		= (1<<10);
	public static final int BONUS_POT_TIME				= (1<<11);
	public static final int BONUS_POT_EXP				= (1<<12);
	public static final int BONUS_POT_GOLD				= (1<<13);
	public static final int BONUS_POT_BUG				= (1<<14);
	public static final int BONUS_POT_UPGRADE			= (1<<15);
	public static final int BONUS_MERCHANT				= (1<<16);
	
	public static final int FEATURE_ID_TOM						= 0;
	public static final int FEATURE_ID_FORTUNE_WHEEL			= 1;
	public static final int FEATURE_ID_ORDER					= 2;
	public static final int FEATURE_ID_AIRSHIP					= 3;
	public static final int FEATURE_ID_FIRST_LOGIN				= 4;
	
	public static final int ORDER_NORMAL_ITEM_EVENT_NUM			= 0;
	
	// ORDER EVENT
	public static final int ORDER_EVENT_LEVEL					= 0;
	public static final int ORDER_EVENT_REQUIRE_ITEMS			= 1;
	public static final int ORDER_EVENT_REWARD_ITEMS			= 2;
	public static final int ORDER_EVENT_BASIC_GOLD				= 3;
	public static final int ORDER_EVENT_BASIC_EXP				= 4;
	
	public static final int ORDER_EVENT_SLOT_INDEX				= 8;
	
	// OPEN EVENT ITEM
	public static final int OPEN_EVENT_ITEM_INDEX				= 0;
	public static final int OPEN_EVENT_ITEM_NAME				= 1;
	public static final int OPEN_EVENT_ITEM_PERCENT_GROUP_0		= 2;
	
	// CLOSE FRIEND
	public static final int CLOSE_FRIEND_LEVEL					= 0;
	public static final int CLOSE_FRIEND_MY_GIFT				= 1;
	public static final int CLOSE_FRIEND_GIFT					= 2;
	
	// TREASURE TRUNK
	public static final int TREASURE_TRUNK_IDX					= 0;
	public static final int TREASURE_TRUNK_ITEM					= 1;
	public static final int TREASURE_TRUNK_COUNT_0				= 2;
	public static final int TREASURE_TRUNK_COUNT_1				= 3;
	public static final int TREASURE_TRUNK_COUNT_2				= 4;
	public static final int TREASURE_TRUNK_COUNT_3				= 5;
	public static final int TREASURE_TRUNK_COUNT_4				= 6;
	public static final int TREASURE_TRUNK_COUNT_5				= 7;
	public static final int TREASURE_TRUNK_COUNT_6				= 8;
	
	// GEO IP
	public static final int GEO_IP_IDX							= 0;
	public static final int GEO_IP_FROM							= 1;
	public static final int GEO_IP_TO							= 2;
	public static final int GEO_IP_LONG_FROM					= 3;
	public static final int GEO_IP_LONG_TO						= 4;
	public static final int GEO_IP_COUNTRY_CODE					= 5;
	public static final int GEO_IP_COUNTRY_NAME					= 6;
	
	// NOTE: THESE DEFINES SHOULD BE GENERATE FROM DB.XLS ----------------------------------
	public static final int PRODUCT_ID_BORUA					= 1;	// bo rua
	public static final int PRODUCT_ID_DOMDOM					= 4;	// dom dom
	public static final int PRODUCT_ID_OCSEN					= 9;	// oc sen
	public static final int PRODUCT_ID_NGOCDO					= 10;	// ngoc do
	public static final int PRODUCT_ID_NGOCVANG					= 11;	// ngoc vang
	public static final int PRODUCT_ID_NGOCXANHBIEN				= 12;	// ngoc xanh bien
	public static final int PRODUCT_ID_NGOCCAM					= 26;	// ngoc cam
	public static final int PRODUCT_ID_NGOCTIM					= 33;	// ngoc tim
	public static final int PRODUCT_ID_NGOCXANHLA				= 35;	// ngoc xanh la
	public static final int PRODUCT_ID_HATHUONGDUONG			= 18;	// hat huong duong
	public static final int PRODUCT_ID_CHUONCHUON				= 64;
	public static final int PRODUCT_ID_BUOM						= 65;
	public static final int PRODUCT_ID_ONG						= 66;
	public static final int PRODUCT_ID_BINHSUA					= 67;
	public static final int PRODUCT_ID_BANHDONU					= 68;
	public static final int PRODUCT_ID_KEOCHANH					= 69;
	
	public static final int MATERIAL_GACH						= 0;
	public static final int MATERIAL_SON_DO						= 1;
	public static final int MATERIAL_GO							= 2;
	public static final int MATERIAL_DA							= 3;
	public static final int MATERIAL_SON_VANG					= 4;
	public static final int MATERIAL_DINH						= 5;
	public static final int MATERIAL_NGOI						= 6;
	public static final int MATERIAL_SON_DEN					= 7;
	public static final int MATERIAL_SAT						= 8;
	public static final int MATERIAL_MAGIC_WATER_BOTTLE			= 9;
	public static final int MATERIAL_PROTECT_AMULET				= 10;
	public static final int MATERIAL_LUCKY_LEAF_RED_SMALL		= 11;
	public static final int MATERIAL_LUCKY_LEAF_RED_MEDIUM		= 12;
	public static final int MATERIAL_LUCKY_LEAF_RED_LARGE		= 13;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_SMALL	= 14;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_MEDIUM	= 15;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_LARGE	= 16;
	public static final int MATERIAL_LUCKY_LEAF_YELLOW_SMALL	= 17;
	public static final int MATERIAL_LUCKY_LEAF_YELLOW_MEDIUM	= 18;
	public static final int MATERIAL_LUCKY_LEAF_YELLOW_LARGE	= 19;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_SMALL		= 20;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_MEDIUM	= 21;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_LARGE		= 22;
	public static final int MATERIAL_LUCKY_LEAF_BLUE_SMALL		= 23;
	public static final int MATERIAL_LUCKY_LEAF_BLUE_MEDIUM		= 24;
	public static final int MATERIAL_LUCKY_LEAF_BLUE_LARGE		= 25;
	public static final int MATERIAL_NET_ID						= 26;
	public static final int MATERIAL_CLOUD_CLUE					= 27;
	public static final int MATERIAL_LONG_HANDNET				= 28;
	public static final int MATERIAL_FERTILIZER_1				= 29;
	public static final int MATERIAL_FERTILIZER_2				= 30;
	public static final int MATERIAL_FERTILIZER_3				= 31;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_SUPER_LARGE	= 32;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_SUPER_LARGE	= 33;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_34	= 34;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_35	= 35;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_36	= 36;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_37	= 37;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_38	= 38;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_39	= 39;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_40	= 40;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_41	= 41;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_42	= 42;
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_43	= 43;
	public static final int MATERIAL_MOON_CAKE				= 44;
	public static final int MATERIAL_ITEM_EVENT_HALLOWEEN_1	= 45;
	public static final int MATERIAL_ITEM_EVENT_HALLOWEEN_2	= 46;
	public static final int MATERIAL_ITEM_EVENT_HALLOWEEN_3	= 47;
	public static final int MATERIAL_ITEM_EVENT_HALLOWEEN_4 = 48;
	public static final int MATERIAL_ITEM_EVENT_FLOWER_20_11 = 49;
	public static final int MATERIAL_ITEM_EVENT_XMAS_2014_1 = 50;
	public static final int MATERIAL_ITEM_EVENT_XMAS_2014_2 = 51;
	public static final int MATERIAL_ITEM_EVENT_XMAS_2014_3 = 52;
	public static final int MATERIAL_ITEM_EVENT_XMAS_2014_4 = 53;
	public static final int MATERIAL_ITEM_XMAS_TREE_2014	= 54;
	public static final int MATERIAL_ITEM_BANH_CHUNG		= 55;
	public static final int MATERIAL_ITEM_BAO_LIXI_NORMAL	= 56;
	public static final int MATERIAL_ITEM_BAO_LIXI_SILVER	= 57;
	public static final int MATERIAL_ITEM_BAO_LIXI_GOLD		= 58;
	public static final int MATERIAL_ITEM_GLASS_ROSE		= 59;
	public static final int MATERIAL_ITEM_TRUNK_BRONZE		= 60;
	public static final int MATERIAL_ITEM_TRUNK_SILVER		= 61;
	public static final int MATERIAL_ITEM_TRUNK_GOLD		= 62;
	public static final int MATERIAL_ITEM_KEY_BRONZE		= 63;
	public static final int MATERIAL_ITEM_KEY_SILVER		= 64;
	public static final int MATERIAL_ITEM_KEY_GOLD			= 65;
	public static final int MATERIAL_ITEM_CANDLE			= 66;
	
	// ITEM EVENT
	public static final int ITEM_EVENT_MOON_CAKE		= 0;
	public static final int ITEM_EVENT_HALLOWEEN_1		= 1;
	public static final int ITEM_EVENT_HALLOWEEN_2		= 2;
	public static final int ITEM_EVENT_HALLOWEEN_3		= 3;
	public static final int ITEM_EVENT_HALLOWEEN_4		= 4;
	public static final int ITEM_EVENT_FLOWER_20_11		= 5;
	public static final int ITEM_EVENT_XMAS_TREE_2014	= 10;
	public static final int ITEM_EVENT_BANH_CHUNG		= 11;
	public static final int ITEM_EVENT_BAO_LIXI_NORMAL	= 12;
	public static final int ITEM_EVENT_BAO_LIXI_SILVER	= 13;
	public static final int ITEM_EVENT_BAO_LIXI_GOLD	= 14;
	public static final int ITEM_EVENT_XMAS_2014_1		= 16; // cung
	public static final int ITEM_EVENT_XMAS_2014_2		= 17; // ten
	public static final int ITEM_EVENT_XMAS_2014_3		= 8;
	public static final int ITEM_EVENT_XMAS_2014_4		= 15;
	public static final int ITEM_EVENT_GLASS_ROSE		= 18;
	public static final int ITEM_EVENT_CANDLE			= 19;
	
	public static final int ITEM_EVENT_HALLOWEEN_1_PRICE = 2; // cung
	public static final int ITEM_EVENT_HALLOWEEN_2_PRICE = 1; // ten
	public static final int ITEM_EVENT_HALLOWEEN_3_PRICE = 4;
	
	public static final int MATERIAL_LUCKY_LEAF_PURPLE_NUM		= 4;
	public static final int MATERIAL_LUCKY_LEAF_GREEN_NUM		= 4;
	
	public static final int SERVER_STATUS_PAUSE = 0;
	public static final int SERVER_STATUS_READY = 1;
	public static final int SERVER_STATUS_FORWARD = 2;
	
	public static final int MAX_LEVEL					= 200;
	public static final int MAX_IBSHOP_PACK				= 299;
	
	public static final int NEW_COMER_ID				= 2575500;
}