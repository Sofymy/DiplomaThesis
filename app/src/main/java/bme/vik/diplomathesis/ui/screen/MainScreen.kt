package bme.vik.diplomathesis.ui.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import bme.vik.diplomathesis.viewmodel.MainViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
){
    if(Firebase.auth.currentUser == null){
        viewModel.signInAnonymously()
    }

    val context = LocalContext.current
    requestPermissions(context, viewModel)

    DisposableEffect(viewModel) {
        viewModel.addListener()
        onDispose { viewModel.removeListener() }
    }
}

fun requestPermissions(context: Context, viewModel: MainViewModel) {
    if (context.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_PHONE_STATE
            )
        } == PackageManager.PERMISSION_GRANTED || context.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.READ_LOGS
            )
        } == PackageManager.PERMISSION_GRANTED || context.let {
            ContextCompat.checkSelfPermission(
                it,
                Manifest.permission.PACKAGE_USAGE_STATS
            )
        } == PackageManager.PERMISSION_GRANTED
    ) {
        Log.d("ttttt", "starts")
        viewModel.startService()
        return
    }

    // 2. If if a permission rationale dialog should be shown
    if (ActivityCompat.shouldShowRequestPermissionRationale(
            context as Activity,
            Manifest.permission.READ_PHONE_STATE
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            Manifest.permission.READ_LOGS
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            context,
            Manifest.permission.PACKAGE_USAGE_STATS
        )
    ) {
        Log.d("ttttt", "dialog")
        return
    }

    // 3. Otherwise, request permission
    ActivityCompat.requestPermissions(
        context,
        arrayOf(
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_LOGS,
            Manifest.permission.PACKAGE_USAGE_STATS
        ),
        1
    )
    Log.d("ttttt", "vege")

}

