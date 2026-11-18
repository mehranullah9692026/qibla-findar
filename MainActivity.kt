package com.qibla.noor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QiblaNoorTheme { App() }
        }
    }
}

@Composable
fun QiblaNoorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1B6B4A),
            secondary = Color(0xFFC9A227),
            background = Color(0xFFF7FBF8),
            surface = Color(0xFFF7FBF8)
        ),
        content = content
    )
}

object LocationHelper {
    fun has(ctx: Context): Boolean =
        ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun fetch(ctx: Context, onResult: (Location) -> Unit) {
        if (!has(ctx)) return
        val lm = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val last = lm.getProviders(true)
            .mapNotNull { p -> runCatching { lm.getLastKnownLocation(p) }.getOrNull() }
            .maxByOrNull { it.time }
        if (last != null) onResult(last)

        val provider = when {
            lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
            lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
            else -> return
        }
        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                onResult(location)
                lm.removeUpdates(this)
            }

            @Suppress("OVERRIDE_DEPRECATION")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        runCatching {
            lm.requestLocationUpdates(provider, 0L, 0f, listener, Looper.getMainLooper())
        }
    }
}

@Composable
fun App() {
    val ctx = LocalContext.current
    val location = remember { mutableStateOf<Location?>(null) }
    var hasPerm by remember { mutableStateOf(LocationHelper.has(ctx)) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { hasPerm = LocationHelper.has(ctx) }

    LaunchedEffect(hasPerm) {
        if (hasPerm) LocationHelper.fetch(ctx) { location.value = it }
    }

    var tab by rememberSaveable { mutableIntStateOf(0) }
    val onAllow = {
        launcher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        Unit
    }
    val tabs = listOf(
        "🕌" to R.string.tab_prayer,
        "🧭" to R.string.tab_qibla,
        "📿" to R.string.tab_tasbeeh,
        "🤲" to R.string.tab_azkar,
        "⚙️" to R.string.tab_more
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { i, item ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Text(item.first) },
                        label = { Text(stringResource(item.second), maxLines = 1) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            when (tab) {
                0 -> PrayerScreen(location.value, hasPerm, onAllow)
                1 -> QiblaScreen(location.value, hasPerm, onAllow)
                2 -> TasbeehScreen()
                3 -> AzkarScreen()
                else -> MoreScreen()
            }
        }
    }
}
