package com.anahjanes.feature_weather.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun rememberLocationPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied:()-> Unit
): LocationPermissionHandler {

    val context = LocalContext.current

    var permissionDenied by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted =
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true ||
                    result[Manifest.permission.ACCESS_FINE_LOCATION] == true

        permissionDenied = !granted

        if (granted) {
            onPermissionGranted()
        }
        else
            onPermissionDenied()
    }

    fun requestPermission() {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            onPermissionGranted()
        } else {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    return LocationPermissionHandler(
        requestPermission = ::requestPermission,
        permissionDenied = permissionDenied
    )
}

data class LocationPermissionHandler(
    val requestPermission: () -> Unit,
    val permissionDenied: Boolean
)

