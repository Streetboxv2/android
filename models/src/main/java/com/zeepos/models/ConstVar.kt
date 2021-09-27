package com.zeepos.models

/**
 * Created by Arif S. on 5/1/20
 */
object ConstVar {
    const val TAG = "StreetBox"
    const val TAG_DIALOG = "dialog"
    const val TAG_FRAGMENT = "fragment"
    const val EMPTY_STRING = ""

    /**
     * App type
     * - merchant
     * - customer
     * - recruiter
     */
    const val MERCHANT_ID = "merchantId"
    const val APP_TYPE = "app_type"
    const val APP_MERCHANT = "app_merchant"
    const val APP_RECRUITER = "app_recruiter"
    const val APP_CUSTOMER = "app_customer"
    const val APP_POS = "app_pos"
    const val BUKALAPAK = "Kamu bisa membuka lapak secara bebas"
    const val COUNT_TEMP = "count_temp"
    const val COUNT = "count"
    const val FIRSTTIME = "first_time"
    const val INDEX = "index"

    /**
     * User key
     */
    const val TOKEN = "token"
    const val SHIFT = "shift"
    const val USER_AUTH = "userAuth"
    const val USER_ROLE_SUPER_ADMIN = "superadmin"
    const val USER_ROLE_MERCHANT = "admin"
    const val USER_ROLE_OPERATOR = "foodtruck"
    const val USER_ROLE_RECRUITER = "recruiter"
    const val USER_ROLE_CUSTOMER = "consumer"

    //dev
//    const val PATH_IMAGE = "https://streetbox-api.cbs.co.id/static/image/"

    //prod
    const val PATH_IMAGE = "https://api.streetbox.id/static/image/"

    /**
     * Task operator status
     */
    const val TASK_STATUS_OPEN = 1
    const val TASK_STATUS_IN_PROGRESS = 2
    const val TASK_STATUS_ARRIVED = 3
    const val TASK_STATUS_COMPLETE = 4
    const val TASK_STATUS_CLOSE = 5
    const val TASK_STATUS_INCOMPLETE = 6
    const val TASK_STATUS_REJECTED = 7

    /**
     * API
     */
    const val SUCCESS = 1
    const val ERROR = -999
    const val DATA_NULL = "No data found!"
    const val ERROR_MESSAGE = "Failure! Something wrong"

    /**
     * Map type
     * Map action type
     */
    const val MAP_TYPE_NEAR_BY = "nearBy"
    const val MAP_TYPE_DIRECTION = "direction"
    const val MAP_TYPE_FREE_TASK_DIRECTION = "free_task_direction"
    const val MAP_TYPE_LIVE_TRACK = "liveTrack"
    const val MAP_TYPE_LOCATION = "location"
    const val MAP_TYPE_CHECK_IN_LOCATION = "checkInLocation"
    const val MAP_TYPE_FREE_TASK = "freetask"
    const val MAP_TYPE_PICK_LOCATION = "pickLocation"
    const val MAP_ACTION_TYPE_TRIP_START = "tripStart"
    const val MAP_ACTION_TYPE_TRIP_PATH = "tripPath"
    const val MAP_ACTION_TYPE_TRIP_LOCATION = "location"
    const val MAP_NEARBY_CHECK_IN = "checkin"
    const val MAP_NEARBY_CHECK_OUT = "checkout"


    const val FOODTRUCK_TYPE_NON_REGULAR = "NONREGULAR"
    const val FOODTRUCK_TYPE_REGULAR = "REGULAR"
    const val FOODTRUCK_TYPE_HOMEVISIT = "HOMEVISIT"

    /**
     * Sync
     */
    const val SYNC_TYPE_TRANSACTION = "TRANSACTION"
    const val SYNC_STATUS_NOT_SYNC = 0
    const val SYNC_STATUS_QUEUED = 1
    const val SYNC_STATUS_SYNCED = 2
    const val SYNC_STATUS_MAL_DATA = -1 // data failed processed

    /**
     * Payment name
     * used for comparation to get icon (bad practice)!!
     */
    const val PAYMENT_METHOD_GOPAY = "GOPAY"
    const val PAYMENT_METHOD_OVO = "OVO"
    const val PAYMENT_METHOD_DANA = "DANA"
    const val PAYMENT_METHOD_LINKAJA = "LINKAJA"
    const val PAYMENT_METHOD_SHOPEPAY = "SHOPEEPAY"

    /**
     * Payment transaction type
     */
    const val TRANSACTION_TYPE_ORDER = "ORDER"
    const val TRANSACTION_TYPE_VISIT = "VISIT"

    /**
     * Payment status :
     * - unpaid
     * - paid
     */
    const val PAYMENT_STATUS_UNPAID = "unpaid"
    const val PAYMENT_STATUS_PAID = "paid"
    const val PAYMENT_STATUS_PENDING = "PENDING"

    /**
     * Food truck status
     */
    const val FOOD_TRUCK_STATUS_ONGOING = 2
    const val FOOD_TRUCK_STATUS_CHECK_IN = 3
    const val FOOD_TRUCK_STATUS_CHECK_OUT = 4

    const val TAX_TYPE_EXCLUSIVE = 0
    const val TAX_TYPE_INCLUSIVE = 1
    const val TAX_TYPE_EXCLUSIVE_DISPLAY = "Exc."
    const val TAX_TYPE_INCLUSIVE_DISPLAY = "Inc."

    //dataitem
    const val DATA_ITEM_STATUS_CALL_FOODTRUCK = "DATA_ITEM_STATUS_CALL_FOODTRUCK."
    const val DATA_ITEM_NOTIF = "DATA_ITEM_NOTIF."

}