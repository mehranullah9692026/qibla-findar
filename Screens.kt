@file:OptIn(ExperimentalMaterial3Api::class)

package com.qibla.noor

import android.content.Context
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.util.TimeZone

private const val DEFAULT_LAT = 31.5204 // Lahore
private const val DEFAULT_LON = 74.3587

@Composable
private fun isUrdu(): Boolean = LocalConfiguration.current.locales[0].language == "ur"

// ---------------------------------------------------------------- Prayer
@Composable
fun PrayerScreen(loc: Location?, hasPerm: Boolean, onAllow: () -> Unit) {
    val lat = loc?.latitude ?: DEFAULT_LAT
    val lon = loc?.longitude ?: DEFAULT_LON
    var method by rememberSaveable { mutableIntStateOf(0) } // 0 Karachi, 1 MWL
    var hanafi by rememberSaveable { mutableStateOf(true) }

    val today = LocalDate.now()
    val now = LocalDateTime.now()
    val tz = TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 3600000.0
    val ishaAngle = if (method == 0) 18.0 else 17.0
    val t = PrayerCalc.compute(
        today.year, today.monthValue, today.dayOfMonth,
        lat, lon, tz, 18.0, ishaAngle, if (hanafi) 2.0 else 1.0
    )

    val names = listOf(R.string.fajr, R.string.sunrise, R.string.dhuhr, R.string.asr, R.string.maghrib, R.string.isha)
    val values = listOf(t.fajr, t.sunrise, t.dhuhr, t.asr, t.maghrib, t.isha)
    val nowH = now.hour + now.minute / 60.0
    val prayerIdx = listOf(0, 2, 3, 4, 5)
    val nextIdx = prayerIdx.firstOrNull { !values[it].isNaN() && values[it] > nowH }

    val hijri = HijrahDate.now()
    val months = stringArrayResource(R.array.hijri_months)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(
                R.string.hijri_date,
                hijri.get(ChronoField.DAY_OF_MONTH),
                months[hijri.get(ChronoField.MONTH_OF_YEAR) - 1],
                hijri.get(ChronoField.YEAR)
            ),
            style = MaterialTheme.typography.titleMedium
        )

        val nextText = if (nextIdx != null) {
            stringResource(R.string.next_prayer, stringResource(names[nextIdx]), PrayerCalc.format(values[nextIdx]))
        } else {
            stringResource(R.string.next_prayer_tomorrow, stringResource(names[0]), PrayerCalc.format(values[0]))
        }
        Card(Modifier.fillMaxWidth()) {
            Text(nextText, Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
        }

        names.forEachIndexed { i, n ->
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(stringResource(n), style = MaterialTheme.typography.titleMedium)
                    Text(PrayerCalc.format(values[i]), style = MaterialTheme.typography.titleMedium)
                }
            }
        }

        Text(stringResource(R.string.method), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = method == 0, onClick = { method = 0 }, label = { Text(stringResource(R.string.method_karachi)) })
            FilterChip(selected = method == 1, onClick = { method = 1 }, label = { Text(stringResource(R.string.method_mwl)) })
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Switch(checked = hanafi, onCheckedChange = { hanafi = it })
            Text(stringResource(R.string.asr_hanafi))
        }

        if (loc == null) {
            Text(stringResource(R.string.location_default), style = MaterialTheme.typography.bodyMedium)
            if (!hasPerm) Button(onClick = onAllow) { Text(stringResource(R.string.allow_location)) }
        } else {
            Text(stringResource(R.string.location_ok), style = MaterialTheme.typography.bodySmall)
        }
    }
}

// ---------------------------------------------------------------- Qibla
@Composable
fun QiblaScreen(loc: Location?, hasPerm: Boolean, onAllow: () -> Unit) {
    val ctx = LocalContext.current
    val lat = loc?.latitude ?: DEFAULT_LAT
    val lon = loc?.longitude ?: DEFAULT_LON
    val qibla = QiblaMath.bearing(lat, lon)
    val distance = QiblaMath.distanceKm(lat, lon)
    val declination = remember(lat, lon) {
        GeomagneticField(lat.toFloat(), lon.toFloat(), 0f, System.currentTimeMillis()).declination
    }

    var azimuth by remember { mutableFloatStateOf(0f) }
    var hasSensor by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val sm = ctx.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensor == null) {
            hasSensor = false
            onDispose { }
        } else {
            val listener = object : SensorEventListener {
                private val r = FloatArray(9)
                private val o = FloatArray(3)
                override fun onSensorChanged(e: SensorEvent) {
                    SensorManager.getRotationMatrixFromVector(r, e.values)
                    SensorManager.getOrientation(r, o)
                    val az = (Math.toDegrees(o[0].toDouble()).toFloat() + 360f) % 360f
                    var diff = az - azimuth
                    while (diff > 180f) diff -= 360f
                    while (diff < -180f) diff += 360f
                    azimuth = (azimuth + 0.15f * diff + 360f) % 360f
                }

                override fun onAccuracyChanged(s: Sensor?, accuracy: Int) {}
            }
            sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            onDispose { sm.unregisterListener(listener) }
        }
    }

    val heading = azimuth + declination
    val needle = (((qibla.toFloat() - heading) % 360f) + 360f) % 360f
    val aligned = needle < 5f || needle > 355f
    val needleColor = if (aligned) Color(0xFF1B6B4A) else Color(0xFFC9A227)

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(R.string.qibla_direction, qibla.toInt()),
            style = MaterialTheme.typography.titleLarge
        )
        Text(stringResource(R.string.distance_kaaba, distance.toInt()))

        Box(Modifier.size(280.dp), contentAlignment = Alignment.Center) {
            Canvas(Modifier.fillMaxSize()) {
                val c = center
                val r = size.minDimension / 2f
                drawCircle(Color(0xFFE3F1EA), radius = r)
                drawCircle(Color(0xFF1B6B4A), radius = r, style = Stroke(width = 6f))
                drawLine(Color.Gray, Offset(c.x, c.y - r), Offset(c.x, c.y - r + 24f), strokeWidth = 6f)
                rotate(degrees = needle, pivot = c) {
                    val p = Path().apply {
                        moveTo(c.x, c.y - r * 0.8f)
                        lineTo(c.x - 22f, c.y)
                        lineTo(c.x + 22f, c.y)
                        close()
                    }
                    drawPath(p, needleColor)
                    drawLine(needleColor, c, Offset(c.x, c.y + r * 0.4f), strokeWidth = 10f)
                }
            }
            Text("N", Modifier.align(Alignment.TopCenter).padding(top = 28.dp), color = Color.Gray)
        }

        if (aligned && hasSensor) {
            Text(stringResource(R.string.qibla_aligned), color = Color(0xFF1B6B4A), style = MaterialTheme.typography.titleMedium)
        }
        Text(
            stringResource(if (hasSensor) R.string.calibrate_hint else R.string.no_sensor),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        if (loc == null) {
            Text(stringResource(R.string.location_default), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
            if (!hasPerm) Button(onClick = onAllow) { Text(stringResource(R.string.allow_location)) }
        }
    }
}

// ---------------------------------------------------------------- Tasbeeh
@Composable
fun TasbeehScreen() {
    var count by rememberSaveable { mutableIntStateOf(0) }
    var target by rememberSaveable { mutableIntStateOf(33) }
    val inRound = count % target

    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Text("$count", fontSize = 72.sp, color = MaterialTheme.colorScheme.primary)
        Text(stringResource(R.string.rounds, count / target))
        LinearProgressIndicator(
            progress = { inRound / target.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )
        Button(onClick = { count++ }, modifier = Modifier.size(200.dp), shape = CircleShape) {
            Text(stringResource(R.string.tap_to_count), textAlign = TextAlign.Center)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(33, 99, 100).forEach { v ->
                FilterChip(selected = target == v, onClick = { target = v }, label = { Text("$v") })
            }
        }
        OutlinedButton(onClick = { count = 0 }) { Text(stringResource(R.string.reset)) }
    }
}

// ---------------------------------------------------------------- Azkar
@Composable
fun AzkarScreen() {
    val ur = isUrdu()
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(AZKAR) { z ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(z.arabic, fontSize = 26.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    Text(if (ur) z.urdu else z.english, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ---------------------------------------------------------------- More
@Composable
fun MoreScreen() {
    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(stringResource(R.string.language), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
                }) { Text("English") }
                OutlinedButton(onClick = {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("ur"))
                }) { Text("اردو") }
            }
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.about), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(R.string.about_text), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.privacy_text), style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.names_title), style = MaterialTheme.typography.titleMedium)
        }
        itemsIndexed(NAMES) { i, s ->
            val parts = s.split("|")
            Card(Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${i + 1}. ${parts[1]}", style = MaterialTheme.typography.bodyMedium)
                    Text(parts[0], fontSize = 22.sp)
                }
            }
        }
    }
}
