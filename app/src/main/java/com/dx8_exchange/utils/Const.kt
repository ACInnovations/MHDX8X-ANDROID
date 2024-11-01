/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 *   @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *  All Rights Reserved.
 *  Proprietary and confidential :  All information contained herein is, and remains
 *  the property of ToXSL Technologies Pvt. Ltd. and its partners.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.dx8_exchange.utils

object Const {

    const val WALLET = 999
    const val SERVER_REMOTE_URL = "https://prod.dx8x.com/api/" // "https://dx8-exchange.toxsl.in/api/"
    const val IMAGE_BASE_URL = "https://prod.dx8x.com/" //"https://dx8-exchange.toxsl.in"

    const val ANDROID = "1"

    const val PLAY_SERVICES_RESOLUTION_REQUEST = 1234
    const val API_CALL_DELAY = 60000

    const val SPLASH_TIMEOUT = 3000
    const val PERMISSION_ID = 99
    const val DATE_CHECK = "2099-03-15"
    const val STATUS_OK = 200
    const val CRYPTO_LIST = 1
    const val ONE = 0
    const val TWO = 1
    const val THREE = 2
    const val FOUR = 3
    const val FIVE = 4
    const val LOGIN = 4
    const val REGISTER = 5

    const val SEND = 1
    const val RECEIVE = 2
    const val BUY = 3
    const val SWAP = 4

    const val SCAN_AND_SEND = 5
    const val SWAP_AND_SEND = 6
    const val TRANSAK = 115
    const val PURCHASE_METHOD = 116
    const val IMAGE_CODE = 1234


    const val TRENDS = 118
    const val REMOVE_WATCHLIST = 119
    const val DRAWER = 12
    const val PROFILE = 13
    const val PASSWORD_LENGTH = 8
    const val TOP_MOVER = 123
    const val TAB_VIEW_CLICK = 0
    const val NEWS_ITEM_CLICK = 5
    const val ITEM_CLICK = 6
    const val SAVE_CRYPTO = 7

    const val TERMS_AND_CONDITION = 1
    const val PRIVACY_POLICY = 3
    const val ABOUT_US = 2
    const val GET_HELP = 4

    const val TYPE_DEFAULT = "DEFAULT"
    const val CRYPTO_DATA = "crypto_data"
    const val SAVE_CRYPTO_WATCHLIST = "crypto_watchlist"
    const val SAVE_CRYPTO_LIST = "crypto_list"
    const val SAVE_MY_CRYPTO_LIST = "my_crypto_list"
    const val SAVE_RECENT_SEARCH_CRYPTO = "recent_search_crypto_list"
    const val LINK_TOKEN = "link_token"
    const val DATA = "data"
    const val URL = "url"

    object PrefConst {
        const val FOREGROUND = "foreground"
        const val USER_DATA = "user_data"
        const val DEVICE_TOKEN = "device_token"
        const val NOT_FIRST_TIME = "not_first_time"
    }

    object Drawable {
        const val TOP = 1
        const val END = 2
        const val BOTTOM = 3
        const val START = 0
    }

    object Authentication {
        const val API_LOGIN = "login/"
        const val GENERATE_OTP = "generate-otp/"
        const val VARIFY_OTP = "verify-otp/"
        const val API_LOGOUT = "logout/"
        const val API_CHECK = "check-user/"
        const val RESEND_OTP = "resend-otp/"
        const val FORGOT_PASSWORD = "resend-otp/"
        const val EDIT_PROFILE = "edit-profile/"
        const val SET_DID = "set-did/"
    }
    object symbol{
        const val space = '-'
    }
    const val CONTACT_US = "contact-support/"
    const val DELETE_USER = "delete-user/"
    const val FLAT_PAGES = "flat-pages/"

    const val PNG = ".png"
    const val SOURCE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val TARGET_FORMAT = "MMM dd, yyyy"
    const val TARGET_FORMAT_DETAIL = "MMM dd, hh:mm a"
    const val URL_WYRE_SUCCESS = "https://www.sendwyre.com/success"
    const val URL_WYRE_FAILURE = "https://www.sendwyre.com/failure"
    const val URL_TRANSAK_SUCCESS = "https://prod.dx8x.com/orders"

    object CryptoApi{
        var BASE_URL = "https://pro-api.coinmarketcap.com/"
        const val LOGO_URL = "https://s2.coinmarketcap.com/static/img/coins/64x64/"

        const val API_TRENDING_CRYPTO = "v1/cryptocurrency/listings/latest"
        const val API_TOP_MOVER_CRYPTO = "v1/cryptocurrency/trending/most-visited"
        const val API_CRYPTO_INFO = "v2/cryptocurrency/info"
        const val API_CRYPTO_HISTORICAL_DATA = "v2/cryptocurrency/ohlcv/historical"
        const val API_CRYPTO_MARKET = "v2/cryptocurrency/market-pairs/latest"

        const val BASE_URL_NEWS = "https://api.coinmarketcap.com/content/v3/"
        const val API_CRYPTO_NEWS = "news/aggregated"
        const val API_ALL_NEWS = "news"
    }

    object Payment{
        const val GENERATE_BANK_TOKEN = "generate-bank-token/"
        const val GET_PERM_TOKEN = "get-permanent-token/"
        const val ADD_ACCOUNT = "add-bank-details/"
        const val ADD_ACCOUNT_SUCCESS = "add-bank-success/"
        const val GET_ACCOUNTS = "bank-accounts-list/"
        const val GET_WALLET = "wallet/"
        const val GET_WALLETS = "wallets/"
        const val GET_ORDERS = "transactions/"
        const val GENERATE_CHECKOUT_URL = "generate-checkout-url/"

    }
    object Orders{
        const val GET_ORDER = "order/"
        const val GET_ORDERS = "orders/"
        const val GET_TRANSACTIONS = "transactions/"
    }
    object Venly{
        const val CREATE_WALLET = "newwallet/"
        const val VERIFY_ADDRESS = "verifyaddress/"
        const val PREVIEW_TOKEN_TRANSFER = "preview-token-transfer/"
        const val EXECUTE_TOKEN_TRANSFER = "execute-token-transfer/"
        const val GET_TOKEN_HISTORY = "get-token-history/"
        const val GET_USERS_HISTORY = "get-users-history/"
        //swap
        const val GET_TOKEN_PAIRS = "get-token-pairs/"
        const val GET_EXCHANGE_ESTIMATE = "get-exchange-rate/"
        const val EXECUTE_TOKEN_SWAP = "execute-token-swap/"
    }
    object Graph{
        const val ONE_DAY = 0
        const val SEVEN_DAY = 1
        const val THIRTY_DAY = 2
        const val SIXTY_DAY = 3
        const val NINETY_DAY = 4
        const val ONE_YEAR = 5
    }// mh
    //9c22397d-7041-4614-b175-b1a517ce65ed
//    6786afc4-dfb3-49c1-a5c3-e63a3fe5beab
    //wallet address:0x86349020e9394b2BE1b1262531B0C3335fc32F20
      const val TRANSAK_URL = "https://global.transak.com?apiKey=9c22397d-7041-4614-b175-b1a517ce65ed&environment=PRODUCTION&themeColor=2575fc" +
            "&redirectURL=https://prod.dx8x.com/orders" +
            "&hideMenu=true&walletAddress="
}

