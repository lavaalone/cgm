package com.vng.skygarden.game;

public final class CommandID 
{
	public static final short CMD_TEST					 	= 0;
	public static final short CMD_LOGIN						= 1;
	public static final short CMD_PLACE_POT					= 2;
	public static final short CMD_HIDE_POT				 	= 3;
	public static final short CMD_UPGRADE_POT			 	= 4;
	public static final short CMD_PLANT						= 5;
	public static final short CMD_HARVEST				 	= 6;
	public static final short CMD_OPEN_NEW_FLOOR			= 7;
	public static final short CMD_UPGRADE_STOCK				= 8;
	public static final short CMD_START_MACHINE				= 9;
	public static final short CMD_CREATE_PRODUCT			= 10;
	public static final short CMD_MOVE_POT					= 11;
	public static final short CMD_UPGRADE_MACHINE			= 12;
	public static final short CMD_REPAIR_MACHINE			= 13;
	public static final short CMD_PRODUCT_COMPLETED			= 14;
	public static final short CMD_UPGRADE_PRODUCT_SLOT		= 15;
	public static final short CMD_MOVE_MACHINE_PRODUCT		= 16;
	public static final short CMD_PRODUCT_SKIP_TIME			= 17;
	public static final short CMD_CREATE_ORDER				= 18;
	public static final short CMD_CATCH_BUG					= 19;
	public static final short CMD_READY_MACHINE				= 20;
	public static final short CMD_SKIP_MACHINE_UNLOCK_TIME	= 21;
	public static final short CMD_BUY_ITEM_UPGRADE_STOCK	= 22;
	public static final short CMD_PLANT_INSTANT_GROW_UP		= 23;
	public static final short CMD_DELIVERY_ORDER			= 24;
	public static final short CMD_SKIP_ORDER				= 25;
	public static final short CMD_SKIP_ORDER_WAIT_TIME		= 26;
	public static final short CMD_COMPLETE_ORDER			= 27;
	public static final short CMD_BUY_ITEM_OPEN_FLOOR		= 28;
	public static final short CMD_BUY_IBSHOP_PACKAGE		= 29;
	public static final short CMD_LOAD_FRIEND_GARDEN		= 30;
	public static final short CMD_UNLOCK_PS_SLOT_FRIEND		= 31;
	public static final short CMD_UNLOCK_PS_SLOT_DIAMOND	= 32;
	public static final short CMD_PLACE_ITEM_PSHOP			= 33;
	
	
	public static final short CMD_REPAIR_MACHINE_FRIEND		= 34;
	public static final short CMD_CANCEL_ITEM_PSHOP			= 35;
	public static final short CMD_SKIP_ADS_PSHOP			= 36;
	public static final short CMD_SET_ADS_PSHOP				= 37;
	public static final short CMD_LOAD_FRIEND_PSHOP			= 38;
	
	public static final short CMD_RECONNECT					= 39;
	
	public static final short CMD_LOAD_FRIEND_LIST			= 40;
	
	public static final short CMD_COME_BACK_HOME			= 41;
	public static final short CMD_BUY_ITEM_PRIVATE_SHOP		= 42;

	public static final short CMD_RECEIVE_REWARD			= 43;
	public static final short CMD_LOAD_FRIEND_FB			= 44;
	public static final short CMD_COLLECT_MONEY_PSHOP		= 45;
	public static final short CMD_BUY_MATERIAL_UPGRADE_POT	= 46;
	
	public static final short CMD_GET_GAME_CONSTANT			= 47;
	
	public static final short CMD_UPDATE_PRIVATE_SHOP		= 48;
	public static final short CMD_LEAVE_FRIEND_SHOP			= 49;
	
	public static final short CMD_NOTIFY_SHOP_IS_MODIFIED	= 50;
	public static final short CMD_UDP_REQUEST_RELOAD_PSHOP	= 51;
	public static final short CMD_REQUEST_RELOAD_PSHOP		= 52;
	
	public static final short CMD_REFRESH_NEWS_BOARD		= 53;
	public static final short CMD_LOAD_IBSHOP				= 54;

	public static final short CMD_COMPLETE_ITEMS_TO_PRODUCT	= 55;
	public static final short CMD_INSTANT_BUY_SEED			= 56;
	
	public static final short CMD_LOAD_GAME_ACCOUNT_VIA_FB	= 57;
	
	public static final short CMD_CONFIRM_LOGIN				= 58;
	public static final short CMD_REQUEST_LOGIN				= 59;
	
	public static final short CMD_ADS_ADD					= 60;
	public static final short CMD_ADS_REMOVE				= 61;
	
	public static final short CMD_LOGIN_DIFFERENT_ACCOUNT	= 62;
	public static final short CMD_INSTANT_BUY_BUG_ZAPPER	= 63;
	
	public static final short CMD_LEVEL_UP					= 64;
	
	public static final short CMD_BUY_PEARL_UPGRADE_POT		= 65;
	public static final short CMD_INSTANT_BUY_LUCKY_LEAF	= 66;
	
	public static final short CMD_LOAD_NPC_PSHOP			= 67;
	
	public static final short CMD_RECEIVE_MACHINE_DURABILITY= 68;
	
	public static final short CMD_LOAD_OWN_PSHOP			= 69;
	
	
	public static final short CMD_ORDER_LETTER_SELECT		= 70;
	public static final short CMD_ORDER_LETTER_RESELECT		= 71;
	public static final short CMD_RECEIVE_DAILY_ORDER_FREE	= 72;
	public static final short CMD_RECEIVE_DAILY_ORDER_PAID	= 73;
	
	public static final short CMD_REFILL_CARD				= 74;
	
	public static final short CMD_UDP_REFRESH_DIAMOND		= 75;
	
	public static final short CMD_TUTORIAL_UPDATE_STEP		= 76;
	
	public static final short CMD_OPEN_GIFT					= 77;
	public static final short CMD_ACCEPT_GIFT				= 78;
	public static final short CMD_DISCARD_GIFT				= 79;
	public static final short CMD_CATCH_BUG_FRIEND			= 80;
	public static final short CMD_INSTANT_BUY_LONG_HANDNET	= 81;
	
	public static final short CMD_PLACE_DECOR				= 82;
	public static final short CMD_HIDE_DECOR				= 83;
	
	public static final short CMD_USE_FERTILIZER			= 84;
	public static final short CMD_NOTIFY_SHARE_FB_FINISH	= 85;
	
	public static final short CMD_FEED_OWL					= 86;
	public static final short CMD_BUY_OWL_LOT				= 87;
	public static final short CMD_DIGEST_COMPLETED			= 88;
	public static final short CMD_DIGEST_COMPLETE_INSTANT	= 89;
	
	public static final short CMD_NPC_BUY_EXPIRED_ITEM		= 90;
	
	public static final short CMD_LOAD_FRIEND_ZING			= 91;
	public static final short CMD_LOAD_ACCOUNT_VIA_ZING		= 92;

	public static final short CMD_AUTHENTICATE_GIFT_CODE	= 93;
	
	public static final short CMD_REGISTER_PHONE			= 94;
	public static final short CMD_VERIFY_PHONE				= 95;
	
	public static final short CMD_NOTIFY_LIKE_FB_FINISH		= 96;

	public static final short CMD_ACCEPT_NPC_BUY_ITEM		= 97;
	public static final short CMD_CANCEL_NPC_BUY_ITEM		= 98;
	
	public static final short CMD_RECEIVE_DAILY_GIFT		= 99;
	public static final short CMD_DISCARD_DAILY_GIFT		= 100;
	
	public static final short CMD_SIMPLE_LOGIN				= 101;
	public static final short CMD_INTERACT_EMO				= 102;
	public static final short CMD_BUY_ITEM_EMI				= 103;

	// optimize data out
	public static final short CMD_CREATE_PRODUCT_OPTIMIZE_DATA_OUT			= 104;
	public static final short CMD_PRODUCT_COMPLETED_OPTIMIZE_DATA_OUT		= 105;
	public static final short CMD_MOVE_MACHINE_PRODUCT_OPTIMIZE_DATA_OUT	= 106;
	public static final short CMD_PRODUCT_SKIP_TIME_OPTIMIZE_DATA_OUT		= 107;
	
	public static final short CMD_FORCE_QUIT				= 108;
	public static final short CMD_REFILL_ATM				= 109;
	
	public static final short CMD_LIKE_GARDEN				= 110;
	
	public static final short CMD_ACCEPT_MERCHANT_REQUEST	= 111;
	public static final short CMD_DISCARD_MERCHANT_REQUEST	= 112;
	public static final short CMD_REQUEST_MERCHANT			= 113;
	public static final short CMD_BUY_ITEMS_FOR_MERCHANT	= 114;
	public static final short CMD_SET_STATE_MERCHANT		= 115;
	
	public static final short CMD_REQUEST_FEED_INFO			= 116;
	public static final short CMD_PROVIDE_GCM_REG_ID		= 117;
	
	public static final short CMD_CDN_LOADER				= 118;
	public static final short CMD_PROVIDE_FB_SHORT_TOKEN	= 119;
	public static final short CMD_PROVIDE_FB_BIRTHDAY		= 120;
	public static final short CMD_REQUEST_FB_SHORT_TOKEN	= 121;
	public static final short CMD_UPDATE_RANKING_INFO		= 122;
	public static final short CMD_GET_ACTIVE_RANKING_INFO	= 123;
	public static final short CMD_GET_PREVIOUS_RANKING_INFO	= 124;
	public static final short CMD_GET_RANKING_ACCUMULATION	= 125;
	public static final short CMD_GET_ACTIVE_RANKING_RESULT	= 126;
	public static final short CMD_GET_BASIC_RANKING_GIFT	= 127;
	public static final short CMD_GET_ALL_RANKING_INFO		= 128;
	public static final short CMD_UPDATE_GARDEN_APPRAISAL	= 129;
	
	// airship
	public static final short CMD_UNLOCK_AIRSHIP			= 130;
	public static final short CMD_SKIP_UNLOCK_TIME_AIRSHIP	= 134;
	public static final short CMD_SKIP_DEPART_TIME_AIRSHIP	= 135;
	public static final short CMD_QUICK_COMPLETE_AIRSHIP	= 136;
	public static final short CMD_DISPOSE_AIRSHIP			= 137;
	public static final short CMD_COMPLETE_CARGO			= 138;
	public static final short CMD_QUICK_COMPLETE_CARGO		= 139;
	public static final short CMD_LOAD_AIRSHIP				= 140;
	public static final short CMD_DELETE_AIRSHIP			= 141;
	public static final short CMD_COMPLETE_AIRSHIP			= 142;
	
	// tomkid
	public static final short CMD_LOAD_TOM_KID				= 143;
	public static final short CMD_REQUEST_TOM_KID_ITEM		= 144;
	public static final short CMD_ACCEPT_TOM_KID_ITEM		= 145;
	public static final short CMD_DENY_TOM_KID_ITEM			= 146;
	public static final short CMD_HIRE_TOM_KID				= 147;
	public static final short CMD_DELETE_TOM_KID			= 148;
	public static final short CMD_START_TOM					= 149;


	
	// for testing only, will remove later
	public static final short CMD_RESET_ALL_DATA			= 150;
	public static final short CMD_RESET_ACCOUNT				= 151;
	public static final short CMD_KEEP_ALIVE				= 152;
	public static final short CMD_CHANGE_TO_STABLE_USER		= 153;
	public static final short CMD_ADD_DIAMOND				= 154;
	
	public static final short CMD_BUY_ITEM_UPGRADE_STOCK_OPTIMIZE_DATA_OUT		= 155;
	public static final short CMD_UPGRADE_STOCK_OPTIMIZE_DATA_OUT				= 156;
	public static final short CMD_LOAD_FRIEND_LIST_STEP							= 157;
	public static final short CMD_LOAD_SPECIAL_OFFER							= 158;
	public static final short CMD_BUY_SALE_OFF_SPECIAL_OFFER					= 159;
	public static final short CMD_PRELOAD_IMG_SPECIAL_OFFER						= 160;

	// Zalo SDK
	public static final short CMD_LOAD_FRIEND_ZALO			= 161;
	public static final short CMD_LOAD_ACCOUNT_VIA_ZALO		= 162;
	
	public static final short CMD_CANCEL_AIRSHIP			= 163;
	
	public static final short CMD_GET_ADS					= 164;
	public static final short CMD_RECEIVE_ADS				= 165;
	
	// achievement
	public static final short CMD_RECEIVE_ACM_GIFT			= 166;
	public static final short CMD_DELETE_ACM				= 167;
	
	// fortunae
	public static final short CMD_LOAD_FORTUNE				= 168;
	public static final short CMD_LOAD_ACM					= 169;
	public static final short CMD_BUY_FORTUNE				= 170;
	public static final short CMD_USE_FORTUNE				= 171;
	
	// continue airship
	public static final short CMD_ASK_FOR_HELP_AIRSHIP		= 172;
	public static final short CMD_HELP_FRIEND_AIRSHIP		= 173;
	public static final short CMD_QUICK_HELP_FRIEND_AIRSHIP	= 174;
	
	// test zalo
	public static final short CMD_UNLINK_ZALO				= 175;
	public static final short CMD_GET_ZALO_TOKEN			= 176;
	
	public static final short CMD_QUICK_REFRESH_NEWSBOARD	= 177;
	
	public static final short CMD_RECEIVED_GIFT_INVITE_FRIEND	= 178;
	public static final short CMD_FILTER_ZING_FRIEND		= 179;
	public static final short CMD_RESET_INVITE_FRIEND		= 180;
	public static final short CMD_NOTIFY_INVITE_FRIEND		= 181;
	public static final short CMD_UPDATE_PHONE_NUMBER		= 182;
	
	public static final short CMD_RECEIVED_GIFT_CROSS_INSTALL	= 183;
	public static final short CMD_HANDLE_REFILL_ZALO			= 184;
	public static final short CMD_RECEIVED_BANNER_CROSS_INSTALL	= 185;
	public static final short CMD_RECEIVED_GIFT_MID_AUTUMN_FESTIVAL = 186;
	public static final short CMD_VIEW_EVENT_SCORE				= 187;
	
	public static final short CMD_LOAD_USER_INFO			= 188;
	public static final short CMD_LOAD_MAIL_BOX				= 189;
	public static final short CMD_SET_MAIL_READ				= 190;
	public static final short CMD_DELETE_MAIL				= 191;
	public static final short CMD_SEND_MAIL					= 192;
	public static final short CMD_ACCEPT_MAIL_GIFT			= 193;
	
	// APPLE IAP VALIDATING
	public static final short CMD_VALIDATE_APPLE_IAP		= 194;
	
	// ANDROID IAB VALIDATING
	public static final short CMD_GET_ANDROID_DEVELOPER_PAYLOAD = 195;
	public static final short CMD_VALIDATE_ANDROID_RECEIPT = 196;
	
	public static final short CMD_SET_STATE_NEW_TUTORIAL	= 197;
	
	public static final short CMD_PLACE_ITEM_XMAS_TREE		= 198;
	// FORCE CHANGE SERVER
	public static final short CMD_FORCE_CHANGE_SERVER		= 199;
	public static final short CMD_VALIDATE_WP_RECEIPT		= 206;
	
	// NEW OFFER SYSTEM
	public static final short CMD_BUY_OFFER_BUG				= 207;
	public static final short CMD_BUY_OFFER_GEM				= 208;
	public static final short CMD_BUY_OFFER_FLOOR			= 209;
	public static final short CMD_BUY_OFFER_MACHINE			= 210;
	public static final short CMD_REQUEST_OFFER_FLOOR		= 211;
	public static final short CMD_REQUEST_OFFER_MACHINE		= 212;
	public static final short CMD_REQUEST_OFFER_GOLD		= 213;
	public static final short CMD_BUY_OFFER_GOLD			= 214;
	public static final short CMD_BUY_OFFER_LUCKY_LEAF		= 215;
	public static final short CMD_BUY_OFFER_LUCKY_LEAF_PURPLE = 216;
	
	// EVENT GIVE GIFT FRIEND
	public static final short CMD_GIVE_FRIEND_EVENT_GIFT			= 217;
	public static final short CMD_RECEIVE_EVENT_GIFT_FROM_FRIEND	= 218;
	public static final short CMD_OPEN_ITEM_EVENT_GIFT				= 219;
	public static final short CMD_GET_EVENT_GIFT_LIST				= 220;
	
	// NEW DAILY GIFT
	public static final short CMD_REATTEND_DAILY			= 221;
	
	// CLOSE FRIEND
	public static final short CMD_ADD_CLOSE_FRIEND			= 222;
	
	// TREASURE TRUNK
	public static final short CMD_OPEN_TREASURE_TRUNK		= 223;
	
	// GM command
	public static final short CMD_BROADCAST					= 200;
	public static final short CMD_KICK_USER					= 201;
	public static final short CMD_SET_SERVER_STATUS			= 202;
	public static final short CMD_CRASH_LOG					= 203;
	public static final short CMD_SET_EVENT_NOTIFICATION	= 204;
	public static final short CMD_PUSH_CLOUD_MESSAGE		= 205;
	
	
	public static final short CMD_MAX_VALUE					= 300;
	public static final short CMD_LOG_IN_CHAT				= 1000;
	public static final short CMD_LOG_OUT_CHAT				= 1001;
	public static final short CMD_ENTER_ROOM_CHAT			= 1002;
	public static final short CMD_LEAVE_ROOM_CHAT			= 1003;
	public static final short CMD_ADD_MESSAGE				= 1004;
	public static final short CMD_GET_MESSAGE				= 1005;
}