package com.qibla.noor

import java.util.Locale
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/** All values are local clock hours (e.g. 13.5 = 1:30 PM). NaN if the sun never reaches that angle. */
class Times(
    val fajr: Double,
    val sunrise: Double,
    val dhuhr: Double,
    val asr: Double,
    val maghrib: Double,
    val isha: Double
)

object PrayerCalc {
    private class Sun(val decl: Double, val eqt: Double)

    private fun rad(d: Double) = Math.toRadians(d)
    private fun deg(r: Double) = Math.toDegrees(r)
    private fun fix(a: Double, b: Double) = a - b * floor(a / b)

    private fun julian(year: Int, month: Int, day: Int): Double {
        var y = year
        var m = month
        if (m <= 2) {
            y -= 1
            m += 12
        }
        val a = floor(y / 100.0)
        val b = 2 - a + floor(a / 4.0)
        return floor(365.25 * (y + 4716)) + floor(30.6001 * (m + 1)) + day + b - 1524.5
    }

    private fun sunPosition(jd: Double): Sun {
        val d = jd - 2451545.0
        val g = fix(357.529 + 0.98560028 * d, 360.0)
        val q = fix(280.459 + 0.98564736 * d, 360.0)
        val l = fix(q + 1.915 * sin(rad(g)) + 0.020 * sin(rad(2 * g)), 360.0)
        val e = 23.439 - 0.00000036 * d
        val ra = deg(atan2(cos(rad(e)) * sin(rad(l)), cos(rad(l)))) / 15.0
        val eqt = q / 15.0 - fix(ra, 24.0)
        val decl = deg(asin(sin(rad(e)) * sin(rad(l))))
        return Sun(decl, eqt)
    }

    fun compute(
        year: Int, month: Int, day: Int,
        lat: Double, lon: Double, tzHours: Double,
        fajrAngle: Double, ishaAngle: Double, asrFactor: Double
    ): Times {
        val jd = julian(year, month, day) - lon / (15.0 * 24.0)
        val sun = sunPosition(jd + 0.5)
        val noon = fix(12.0 - sun.eqt, 24.0)
        val adj = tzHours - lon / 15.0

        fun angleTime(angle: Double, before: Boolean): Double {
            val x = (-sin(rad(angle)) - sin(rad(sun.decl)) * sin(rad(lat))) /
                (cos(rad(sun.decl)) * cos(rad(lat)))
            val t = deg(acos(x)) / 15.0 // NaN when |x| > 1
            return noon + (if (before) -t else t)
        }

        val asrAngle = -deg(atan(1.0 / (asrFactor + tan(rad(abs(lat - sun.decl))))))

        return Times(
            fajr = angleTime(fajrAngle, true) + adj,
            sunrise = angleTime(0.833, true) + adj,
            dhuhr = noon + adj,
            asr = angleTime(asrAngle, false) + adj,
            maghrib = angleTime(0.833, false) + adj,
            isha = angleTime(ishaAngle, false) + adj
        )
    }

    fun format(hours: Double): String {
        if (hours.isNaN()) return "--:--"
        val total = Math.round((((hours % 24.0) + 24.0) % 24.0) * 60.0).toInt() % 1440
        val hh = total / 60
        val mm = total % 60
        val h12 = if (hh % 12 == 0) 12 else hh % 12
        return String.format(Locale.US, "%d:%02d %s", h12, mm, if (hh < 12) "AM" else "PM")
    }
}
