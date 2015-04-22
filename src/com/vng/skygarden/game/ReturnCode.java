package com.vng.skygarden.game;

public final class ReturnCode
{
	public static final byte	RESPONSE_ERROR 								= 0; // request fail
	public static final byte	RESPONSE_OK 								= 1;
	
	public static final byte	RESPONSE_SAVE_ERROR							= 2; // critical error, should reload all data
	public static final byte	RESPONSE_LOAD_ERROR							= 3; // critical error, should reload all data
	
	public static final byte	RESPONSE_INVALID_PARAMS						= 4;
	public static final byte	RESPONSE_INVALID_FLOOR_PARAM 				= 5; // floor param < 0 or >= userTotalFloor. 
	public static final byte	RESPONSE_INVALID_SLOT_PARAM 				= 6; // slot < 0 or slot >= max_slot_per_floor.
	
	public static final byte	RESPONSE_STOCK_IS_FULL 						= 7; // full stock, can't increase more item.
	
	public static final byte	RESPONSE_POT_IS_NOT_EMPTY 					= 8; // pot is containing plant, can not move/hide/delete
	public static final byte	RESPONSE_CANT_FIND_POT 						= 9; // slot is empty, can not find pot
	
	public static final byte	RESPONSE_INVALID_PLANT_ID 					= 10; // invalid plant id ( plant id < 0 or plant id > max_plant_id)
	public static final byte	RESPONSE_INVALID_POT_ID 					= 11; // invalid pot id ( pot id < 0 or pot id > max_pot_id)
	
	public static final byte	RESPONSE_CANT_DELETE_POT 					= 12; // concurrent access problem
	public static final byte	RESPONSE_CANT_CREATE_NEW_POT				= 13; // concurrent access problem
	
	public static final byte	RESPONSE_CANT_INCREASE_OBJECT 				= 14; // stock
	public static final byte	RESPONSE_CANT_DECREASE_OBJECT 				= 15; // stock
	public static final byte	RESPONSE_CANT_SET_OBJECT_VALUE 				= 16; // stock
	
	public static final byte	RESPONSE_SLOT_IS_NOT_EMPTY					= 17; // slot is not empty, can not place more pot
	
	public static final byte	RESPONSE_NOT_ENOUGH_GOLD					= 18;
	public static final byte	RESPONSE_NOT_ENOUGH_REPUTATION				= 19;
	public static final byte	RESPONSE_NOT_ENOUGH_DIAMOND					= 20;
	public static final byte	RESPONSE_NOT_ENOUGH_LEVEL					= 21;
	
	public static final byte	RESPONSE_UPGRADE_POT_SUCCESS				= 22;
	public static final byte	RESPONSE_UPGRADE_POT_FAIL					= 23;
			
	public static final byte	RESPONSE_INVALID_MACHINE_ID					= 24;
	public static final byte    RESPONSE_PRODUCT_NOT_COMPLETE				= 25;
	public static final byte	RESPONSE_MACHINE_IS_EMPTY					= 26;
	
	public static final byte    RESPONSE_ORDER_PRODUCT_NOT_ENOUGH			= 27;
	public static final byte    RESPONSE_ORDER_STILL_WAITING				= 28;
			
	public static final byte	RESPONSE_IBS_INVALID_PACK_ID				= 29;
	public static final byte	RESPONSE_IBS_PACK_NOT_ACTIVE				= 30;
	public static final byte	RESPONSE_STOCK_ADD_ITEM_FAILED				= 31;
	public static final byte	RESPONSE_IBS_INVALID_ITEM_TYPE				= 32;
	public static final byte	RESPONSE_IBS_ITEM_SOLD_OUT					= 33;
	public static final byte	RESPONSE_IBS_INVALID_PRICE					= 34;
	public static final byte	RESPONSE_CANT_SUBSTRACT_MONEY				= 35;
	public static final byte	RESPONSE_NOT_ENOUGH_PRODUCT					= 36;
	public static final byte	RESPONSE_IBS_INVALID_SALE_DATE				= 37;
	public static final byte	RESPONSE_IBS_PACKAGE_OUT_DATE				= 38;

	public static final byte	RESPONSE_INVALID_PRODUCT_ID					= 39;
	
	public static final byte	RESPONSE_PS_CANT_FIND_UNOCCUPIED_SLOT_FRIEND= 40;
	public static final byte	RESPONSE_PS_CANT_ADD_SLOT					= 41;
	public static final byte	RESPONSE_PS_INVALID_FRIEND_NUMBER			= 42;
		
	public static final byte	RESPONSE_LOAD_FRIEND_DATA_ERROR				= 43;
	public static final byte	RESPONSE_CANCEL_ITEM_PS_FAILED				= 44;
	public static final byte	RESPONSE_PS_ADS_NOT_AVAILABLE				= 45;
	public static final byte	RESPONSE_PS_ITEM_NOT_SELLING				= 46;
	public static final byte	RESPONSE_PS_CANT_FIND_UNOCCUPIED_SLOT_DIAMOND	= 47;
	public static final byte	RESPONSE_CONCURRENT_ACCESS_PROBLEM			= 48;
	public static final byte	RESPONSE_FB_ID_ALREADY_USED					= 49;
	public static final byte	RESPONSE_PS_ITEM_IS_NOT_SOLD				= 50;
	public static final byte	RESPONSE_FB_AUTHENTICATE_FAIL				= 51;
	public static final byte	RESPONSE_POT_AT_MAX_ID						= 52;
	public static final byte	RESPONSE_CANT_GET_PS_MANAGER				= 53;
	public static final byte	RESPONSE_PS_DATA_IS_LOCKED					= 54; // should display the message please try again later (max 12 seconds)
	
	public static final byte	RESPONSE_INVALID_ORDER_INDEX				= 55;
	
	public static final byte	RESPONSE_REQUIRE_LOGIN						= 56; // should send command login
	
	public static final byte	RESPONSE_UPGRADE_MACHINE_SUCCESS			= 57;
	public static final byte	RESPONSE_UPGRADE_MACHINE_FAIL				= 58;
	
	public static final byte	RESPONSE_MACHINE_DATA_IS_LOCKED				= 59;
	
	public static final byte	RESPONSE_MACHINE_HAD_BEEN_REPAIRED			= 60;
	
	public static final byte	RESPONSE_ALREADY_LOGIN						= 61;
	
	public static final byte	RESPONSE_INVALID_ORDER_LETTER_INDEX			= 62;
	
	public static final byte	RESPONSE_WRONG_REQUEST_ID					= 63;
	
	public static final byte	RESPONSE_UPGRADE_FAIL_LOSE_POT				= 64;
	
	public static final byte	RESPONSE_WRONG_UPDATE_VERSION				= 65;
	
	public static final byte	RESPONSE_RETRY_LOGIN						= 66;
	public static final byte	RESPONSE_BAN								= 67;
	
	public static final byte	RESPONSE_NEW_DATE							= 68;

	public static final byte	RESPONSE_INVALID_ORDER_TYPE					= 69;
	
	public static final byte	RESPONSE_PROCESSING_SOCIAL_LOGIN			= 70;
	
	public static final byte	RESPONSE_ZING_ID_ALREADY_USED				= 71;
	public static final byte	RESPONSE_ZING_AUTHENTICATE_FAIL				= 72;
	public static final byte	RESPONSE_MISMATCH_SESSION					= 73;
	
	public static final byte	RESPONSE_LEVEL_UP_FAILED					= 74;
	
	public static final byte	RESPONSE_AUTHENTICATE_GIFT_CODE_FAILED		= 75;
	public static final byte	RESPONSE_INVALID_GIFT_CODE_SIZE				= 76;
	public static final byte	RESPONSE_GIFT_CODE_NOT_EXISTED				= 77;
	public static final byte	RESPONSE_GIFT_CODE_ENTER_LIMITED			= 78;
	
	public static final byte	RESPONSE_SERVER_OVERLOADED					= 79;
	public static final byte	RESPONSE_SERVER_MAINTAIN					= 80;
	public static final byte	RESPONSE_MISMATCH_UID						= 81;
	
	public static final byte	RESPONSE_PHONE_NUMBER_ALREADY_USED			= 82;
	public static final byte	RESPONSE_ALREADY_LINKED_PHONE_NUMBER		= 83;
	
	public static final byte	RESPONSE_ITEM_IS_NOT_FOR_SALED				= 84;
	public static final byte	RESPONSE_UPGRADE_POT_BLESSING				= 85;

	public static final byte	RESPONSE_GIFT_CODE_HAD_BEEN_RECEIVED		= 86;
	public static final byte	RESPONSE_GIFT_CODE_OUT_OF_DATE				= 87;
	public static final byte	RESPONSE_GIFT_TYPE_HAD_BEEN_RECEIVED		= 88;
	public static final byte	RESPONSE_AUTHENTICATE_GIFT_CODE_OK			= 89;
	public static final byte	RESPONSE_GIFT_CODE_FOR_FRIEND				= 90;
	
	public static final byte	RESPONSE_ALREADY_LIKED_GARDEN				= 91;
	public static final byte	RESPONSE_REQUIRE_FACEBOOK_LOGIN				= 92;
	public static final byte	RESPONSE_EMPTY_RANKING_INFO					= 93;
	
	public static final byte	RESPONSE_ZALO_ID_ALREADY_USED				= 94;
	public static final byte	RESPONSE_ZALO_AUTHENTICATE_FAIL				= 95;	
	
	public static final byte	RESPONSE_AIRSHIP_ASK_FOR_HELP_FULL			= 96;
	// CHAT
	public static final byte	RESPONSE_ROOM_FULL							= 97;
	public static final byte	RESPONSE_INVALID_ROOM						= 98;
	
	public static final byte	RESPONSE_MULTIPLE_LOGIN						= 99;
	public static final byte	RESPONSE_FORCE_QUIT							= 100;

	public static final byte	RESPONSE_EXIT_GAME							= 127;
}