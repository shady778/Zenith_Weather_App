package com.example.zenith.data.location

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationProvider (context: Context) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    fun fetchLocation(): Flow<Pair<Double, Double>> = callbackFlow {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    trySend(Pair(location.latitude, location.longitude))
                } else {
                    fusedLocationClient.lastLocation.addOnSuccessListener { lastLoc ->
                        if (lastLoc != null) trySend(Pair(lastLoc.latitude, lastLoc.longitude))
                        else close()
                    }.addOnFailureListener { close(it) }
                }
            }
            .addOnFailureListener { exception ->
                close(exception)
            }
        awaitClose { }
    }
}
