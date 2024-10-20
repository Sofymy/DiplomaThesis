package bme.vik.diplomathesis.domain.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import bme.vik.diplomathesis.MainActivity
import bme.vik.diplomathesis.data.repository.MainRepository
import bme.vik.diplomathesis.domain.utils.ServiceUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.timerTask


@AndroidEntryPoint
class LocationService : Service() {

    @Inject
    lateinit var mainRepository: MainRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationRequest()
        setupLocationCallback()
        checkLocationPermissions()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        ServiceUtils.createNotificationChannel(this)
        ServiceUtils.startForegroundService(this, input)
        ServiceUtils.scheduleTask(5000) { getLocation() }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun setupLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = 5000 // 5 seconds
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val location: Location? = locationResult.lastLocation
                // Handle location updates if needed
            }
        }
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ServiceUtils.stopTimer()

        stopForeground(true)
        stopSelf()
    }
}
