@file:OptIn(ExperimentalPermissionsApi::class)

package bme.vik.diplomathesis.feature.main

import android.Manifest.permission.*
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Column(
        Modifier
            .background(Color(0xFF1a1a24))
            .padding(20.dp)
    ) {
        LoggerHomeHeader()
        Text(text = "Monitored metrics", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 10.dp), fontSize = 20.sp,)

        state.logging.forEach {
            it.loggingMetrics.forEach { metric ->
                MetricItem(icon = metric.icon, value = metric.title)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        PermissionHandler(
            permissionStates = permissionStates,
            onPermissionsGranted = {
                //viewModel.onEvent(MainUserEvent.StartServices)
            }
        )
    }
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

    Column(
        Modifier
            .fillMaxSize()
    ) {
        if (deniedPermissions.isNotEmpty()) {
            Log.d("Denied Permissions", deniedPermissions.joinToString { it.permission })

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(text = "Denied permissions", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 10.dp), fontSize = 20.sp)
                }
            }

            deniedPermissions.forEach { permission ->
                Text(text = permission.permission, color = Color.White)
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
                Text(message, color = Color.White)
                
                Spacer(modifier = Modifier.weight(1f))
                
                PrimaryButton(onClick = {
                    permissionStates.launchMultiplePermissionRequest()
                },text = "Grant permissions"
                )

                PrimaryButton(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(intent) },
                    text = "Open Settings"
                )
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

@Composable
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        content = {
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        },
        onClick = onClick,
        containerColor = Color(0xFF6947f6),
        contentColor = Color.White,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(10.dp)
    )
}

@Composable
fun MetricItem(
    icon: ImageVector,
    value: String,
    color: Color = Color(0xFF232232)
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(color, RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF707382) ,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = value,
            color = Color(0xFF707382) ,
        )
    }
}


@Composable
fun LoggerHomeHeader() {
    Column(
        Modifier
            .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
    ) {
        Text(
            "Logger",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}