package bme.vik.diplomathesis

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import bme.vik.diplomathesis.feature.main.MainScreen
import bme.vik.diplomathesis.ui.theme.DiplomaThesisTheme
import bme.vik.diplomathesis.domain.receiver.PowerConnectionReceiver
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DiplomaThesisTheme {
                MainScreen()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val receiver = PowerConnectionReceiver ()
        val ifilter = IntentFilter()
        ifilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED)
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        registerReceiver(receiver, ifilter)

    }
}