package com.dapper.matrixtestinkotlin.service

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.dapper.matrixtestinkotlin.MainActivity
import com.dapper.matrixtestinkotlin.utils.Sessions
import com.google.android.gms.location.*

class LocationService : Service() {

    private val TAG = "MatrixMarketer"
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    companion object{
    private const val UPDATE_INTERVAL = (1000 * 1000 /* 4 secs */).toLong()
    private const val FASTEST_INTERVAL = (1000 * 100 /* 2 sec */).toLong()
    var LastKnownLatitude = 0.0
        var LastKnownLongitude = 0.0
    }

    private var myLooper: Looper? = null
    private var IsStopped = false
    private var IsArreadyRunnig = false


    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            Log.d(TAG, "onLocationResult: got location result.")
            val location = locationResult.lastLocation
            Log.e(TAG, "location service call back IsStopped = $IsStopped")
            if (!IsStopped) {
                if (location != null) {
                    LastKnownLatitude = location.latitude
                    LastKnownLongitude = location.longitude
                    saveUserLocation(location.latitude, location.longitude)
                }
            } else {
                Log.d(TAG, "stopping fused location api")
                mFusedLocationClient?.removeLocationUpdates(this)
                stopSelf()
                return
            }
        }
    }
    private fun saveUserLocation(latitude: Double, longitude: Double) {
        val sessionManagers = Sessions(this@LocationService)
        sessionManagers.setLocation("" + latitude, "" + longitude)
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val notificationIntent = Intent(this, MainActivity::class.java)

        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT)
        }

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "matrix_marketers"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Matrix Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("First")
                .setContentText("")
                .setContentIntent(pendingIntent).build()
            startForeground(1, notification)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (!IsArreadyRunnig) {
            if (intent!!.getBooleanExtra("is_stop", true)) {
                this.stopSelf()
                IsStopped = true
                IsArreadyRunnig = false
            } else {
                val CHANNEL_ID = "matrix_001"
                val CHANNEL_NAME = "Matrix Channel"
                var channel: NotificationChannel? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                    )
                }
                val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager?.createNotificationChannel(channel!!)
                }
                var notification: Notification? = null
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = Notification.Builder(this, CHANNEL_ID)
                        .setContentTitle("")
                        .setContentText("")
                        .build()
                    this.startForeground(1, notification)
                }
                Log.d(TAG, "Else: called. $IsStopped")
                IsArreadyRunnig = true
                IsStopped = false
                getLocation()
            }
        }
        Log.d(TAG, "onStartCommand: called. $IsStopped")
        return START_NOT_STICKY
   }



    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)

        val restartServicePendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT
        )
        val alarmService = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmService[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000] =
            restartServicePendingIntent
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        IsStopped =true;
        Log.e(TAG, "service stopped")
    }

    private fun getLocation() {
        val mLocationRequestHighAccuracy = LocationRequest()
        mLocationRequestHighAccuracy.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequestHighAccuracy.interval = UPDATE_INTERVAL
        mLocationRequestHighAccuracy.fastestInterval = FASTEST_INTERVAL
        myLooper = Looper.myLooper()



        if (ActivityCompat.checkSelfPermission(
                this@LocationService,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "getLocation: stopping the location service. return")
            stopSelf()
            return
        }
        Log.d(TAG, "getLocation: getting location information. fetching")
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequestHighAccuracy,
            mLocationCallback,
            myLooper
        )

        mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
            saveUserLocation(location.latitude, location.longitude)
            Log.e(TAG, "location fetch success locatin is $location")
        }.addOnFailureListener { e ->
            Log.e(
                TAG,
                "location fetch error " + e.message
            )
        }

    }
}