package com.dapper.matrixtestinkotlin.utils

import android.content.Context
import android.content.SharedPreferences

class Sessions {

    companion object {
        const val CURRENT_LATITUDE = "latitude"
        const val CURRENT_LONGITUDE = "longitude"
        const val IS_SERVICE_STARTED = "is_service_started"
        const val PREFER_NAME = "MatrixMarketers"
    }

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var _context: Context? = null
    var PRIVATE_MODE = 0

    constructor(_context: Context?) {
        this._context = _context
        pref = _context!!.getSharedPreferences(PREFER_NAME, PRIVATE_MODE)
        editor = pref?.edit()
    }

    fun setLocation(latitude: String?, longitude: String?) {
        editor!!.putString(CURRENT_LATITUDE, latitude)
        editor!!.putString(CURRENT_LONGITUDE, longitude)
        editor!!.commit()
    }
    fun setServiceStarted(isServiceStarted: Boolean): Boolean {
        editor!!.putBoolean(IS_SERVICE_STARTED, isServiceStarted)
        editor!!.commit()
        return true
    }
    fun isServiceStarted(): Boolean {
        return pref!!.getBoolean(IS_SERVICE_STARTED, false)
    }

    fun getCurrentLatitude(): String? {
        return pref!!.getString(CURRENT_LATITUDE, "")
    }

    fun getCurrentLongitude(): String? {
        return pref!!.getString(CURRENT_LONGITUDE, "")
    }

}