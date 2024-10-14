@file:OptIn(ExperimentalPermissionsApi::class)

package bme.vik.diplomathesis.feature.main

import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import bme.vik.diplomathesis.ui.common.HandleLifecycleEvents
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    HandleLifecycleEvents(
        onResume = {
            viewModel.onEvent(MainUserEvent.CheckCurrentUser)
        }
    )

    if (!state.isLoading) {
        if(state.currentUser == null){
            viewModel.onEvent(MainUserEvent.SignInAnonymously)
        }
    }

    val permissionStates = rememberMultiplePermissionsState(
        permissions = getPermissionsList()
    )

    PermissionHandler(
        permissionStates = permissionStates,
        onPermissionsGranted = {
            //viewModel.onEvent(MainUserEvent.StartServices)
        }
    )
}

@Composable
private fun PermissionHandler(
    permissionStates: MultiplePermissionsState,
    onPermissionsGranted: () -> Unit,
) {
    Log.d("Permissions", permissionStates.permissions.toString())

    val context = LocalContext.current
    val grantedPermissions = permissionStates.permissions.filter { it.status.isGranted }
    val deniedPermissions = permissionStates.permissions.filter { !it.status.isGranted }

    if (grantedPermissions.isNotEmpty()) {
        onPermissionsGranted()
        Log.d("Granted Permissions", grantedPermissions.joinToString { it.permission })
    }

    Column {
        if (deniedPermissions.isNotEmpty()) {
            Log.d("Denied Permissions", deniedPermissions.joinToString { it.permission })

            Column {
                Text(text = "Denied permissions")

                deniedPermissions.forEach { permission ->
                    Text(text = permission.permission)
                }
            }
        }

        if (permissionStates.allPermissionsGranted) {
            Text("All permissions granted")
            onPermissionsGranted()
        } else {
            Column {
                val message = if (permissionStates.shouldShowRationale) {
                    "Please grant the required permissions."
                } else {
                    "Permissions are required for this feature. Please grant the permissions."
                }
                Text(message)
                Button(onClick = { permissionStates.launchMultiplePermissionRequest() }) {
                    Text("Grant permissions")
                }

                Button(onClick = {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }) {
                    Text("Open Settings")
                }
            }
        }
    }
}


private fun getPermissionsList(): List<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        listOf(
            READ_PHONE_STATE,
            READ_CALL_LOG,
            QUERY_ALL_PACKAGES,
            PACKAGE_USAGE_STATS,
            INTERNET,
            READ_EXTERNAL_STORAGE,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            ACCESS_BACKGROUND_LOCATION,
            READ_PRECISE_PHONE_STATE
        )
    } else {
        listOf(
            READ_PHONE_STATE,
            READ_CALL_LOG,
            PACKAGE_USAGE_STATS,
            INTERNET,
            READ_EXTERNAL_STORAGE,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
        )
    }
}

