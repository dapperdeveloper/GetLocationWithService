package com.dapper.matrixtestinkotlin.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.dapper.matrixtestinkotlin.databinding.FragmentSettingsBinding
import com.dapper.matrixtestinkotlin.service.LocationService
import com.dapper.matrixtestinkotlin.utils.Constants.Companion.REQUEST_CHECK_SETTINGS
import com.dapper.matrixtestinkotlin.utils.Sessions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class Settings : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    lateinit var sessions: Sessions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater)

        sessions = Sessions(context)

        //Check Service is running or Not if service is not running then uncheck the switch
        binding.startService.isChecked = sessions.isServiceStarted()

        switchChangeListener()
        return binding.root
    }

    private fun switchChangeListener(){


        binding.startService.setOnCheckedChangeListener { buttonView, isChecked ->
            Log.e("sjhfsjkfsfsf", "location switch service is clicked $isChecked")
            if (isChecked) {
                checkPermissions()
                binding.startService.text = "SERVICE STARTED"
                sessions.setServiceStarted(true)
            } else {
                binding.startService.text = "START SERVICE"
                sessions.setServiceStarted(false)
                stopLocationService()
            }
        }


    }

    private fun checkPermissions() {
        Dexter.withContext(activity)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        if (GPSstatusCheck()) {
                            settingsrequest()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Please allow all permission",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                }
            }).check()
    }




    fun GPSstatusCheck(): Boolean {
    val manager:LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private val TAG = "MarketMarketers"

    private var mSettingsClient: SettingsClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null

    companion object{
        const val UPDATE_INTERVAL_IN_MILLISECONDS=10000
        const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS=5000
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun settingsrequest() {
        mSettingsClient = activity?.let { LocationServices.getSettingsClient(it) }
        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = UPDATE_INTERVAL_IN_MILLISECONDS.toLong()
        mLocationRequest?.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS.toLong()
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()


        activity?.let {
            mSettingsClient
                ?.checkLocationSettings(mLocationSettingsRequest)
                ?.addOnSuccessListener(it) {
                    Log.i(TAG, "All location settings are satisfied.")
                    startLocationService()
                }
                ?.addOnFailureListener(activity!!) { e ->
                    when ((e as ApiException).statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i(
                                TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings "
                            )
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                            } catch (sie: SendIntentException) {
                                Log.i(TAG, "PendingIntent unable to execute request.")
                            }
                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings."
                            Log.e(TAG, errorMessage)
                            Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                startLocationService()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "location enabl cancled" + data!!.extras)
            }
        }
    }

    private fun startLocationService() {
        Log.e("sdfjdskfs", "calling location service")
        if (!foregroundServiceRunning()) {
            val serviceIntent = Intent(activity, LocationService::class.java)
            serviceIntent.putExtra("is_stop", false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity?.let { ContextCompat.startForegroundService(it, serviceIntent) }
            } else {
                activity?.startService(serviceIntent)
            }
            sessions.setServiceStarted(true)
        }
    }

    fun foregroundServiceRunning(): Boolean {
        val activityManager =
            activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun stopLocationService() {
        val serviceIntent = Intent(activity, LocationService::class.java)
        serviceIntent.putExtra("is_stop", true)
        activity?.stopService(serviceIntent)
    }
}