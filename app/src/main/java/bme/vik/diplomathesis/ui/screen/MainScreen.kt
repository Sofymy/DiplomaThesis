@file:OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)

package bme.vik.diplomathesis.ui.screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.PACKAGE_USAGE_STATS
import android.Manifest.permission.READ_LOGS
import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_PRECISE_PHONE_STATE
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import bme.vik.diplomathesis.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
){
    if(Firebase.auth.currentUser == null){
        viewModel.signInAnonymously()
    }

    val context = LocalContext.current
    requestPermissions(context, viewModel)

    val permissionStates = rememberMultiplePermissionsState(
        listOf(
            PACKAGE_USAGE_STATS,
            READ_PHONE_STATE,
            READ_LOGS,
            ACCESS_COARSE_LOCATION,
            READ_PRECISE_PHONE_STATE,
            ACCESS_FINE_LOCATION
        )
    )

    if (permissionStates.allPermissionsGranted) {
        Text("All permission Granted")
        viewModel.startService()
    } else {
        Column {
            val textToShow = if (permissionStates.shouldShowRationale) {
                "Please grant the permission."
            } else {
                "Camera permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = {
                permissionStates.launchMultiplePermissionRequest()
                viewModel.startService()
            }) {
                Text("Grant permissions")
            }
        }
    }

    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose { viewModel.removeListener() }
    }
}

fun requestPermissions(context: Context,
                       viewModel: MainViewModel,
) {


}



