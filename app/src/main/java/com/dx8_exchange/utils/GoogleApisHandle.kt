/*
 *  @copyright : ToXSL Technologies Pvt. Ltd. < www.toxsl.com >
 *   @author     : Shiv Charan Panjeta < shiv@toxsl.com >
 *  All Rights Reserved.
 *  Proprietary and confidential :  All information contained herein is, and remains
 *  the property of ToXSL Technologies Pvt. Ltd. and its partners.
 *  Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.dx8_exchange.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.*
import android.util.Log
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dx8_exchange.BuildConfig
import com.dx8_exchange.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import cz.msebera.android.httpclient.client.methods.HttpPost
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import kotlin.math.atan2
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin

class GoogleApisHandle {
    private var routeMap: GoogleMap? = null
    internal var context: Context? = null
    private var origin: LatLng? = null
    private var destination: LatLng? = null
    private var isAddLine: Boolean = false
    private val polyline: Polyline? = null
    private var totalDistance: Double? = null
    private var onPolyLineReceived: OnPolyLineReceived? = null

    private fun setAct(mAct: Context) {
        this.context = mAct
    }

    @Suppress("DEPRECATION")
    fun Geocoder.getAddress(
        latitude: Double,
        longitude: Double,
        address: (Address?) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }
            return
        }

        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch (e: Exception) {
            //will catch if there is an internet problem
            address(null)
        }
    }

    fun getAddress(lat: Double, lang: Double): String? {
        try {
            var fullAddress: String? = null
            Geocoder(context!!, Locale.getDefault()).getAddress(lat, lang) { addresses: Address? ->
                fullAddress = when {
                    addresses != null -> {
                        val address = addresses.getAddressLine(0)
                        val city = addresses.getAddressLine(1)
                        val country = addresses.getAddressLine(2)
                        val state = addresses.subLocality
                        address + ", " + city + ", " + (country ?: "")
                    }
                    else -> {
                        null
                    }
                }
            }
            return fullAddress
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
            return null
        }

    }


    fun decodeAddressFromLatLng(context: Context, latLng: LatLng, googleKey: String): String {
        var address = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            Geocoder(context, Locale.getDefault()).getAddress(
                latLng.latitude,
                latLng.longitude
            ) { addresses: Address? ->
                val json: JSONObject?
                when (addresses) {
                    null -> {
                        json =
                            getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&sensor=true&key=$googleKey")
                        try {
                            if (json!!.getJSONArray("results").length() > 0) {
                                address = json.getJSONArray("results").getJSONObject(0)
                                    .getString("formatted_address")
                            }
                        } catch (e: JSONException) {
                            if (BuildConfig.DEBUG) {
                                e.printStackTrace()
                            }
                        }

                    }
                    else -> {
                        address = when {
                            addresses.adminArea != null && addresses.countryName != null -> {
                                addresses.getAddressLine(0) + "," + addresses.locality + "," + (if (addresses.adminArea.equals(
                                        "Punjab",
                                        ignoreCase = true
                                    )
                                ) "Punjab" else addresses.adminArea) + "," + addresses.countryName // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            }
                            addresses.adminArea != null -> {
                                addresses.getAddressLine(0) + "," + addresses.locality + "," + (if (addresses.adminArea.equals(
                                        "Punjab",
                                        ignoreCase = true
                                    )
                                ) "Punjab" else addresses.adminArea) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            }
                            addresses.countryName != null -> {
                                addresses.getAddressLine(0) + "," + addresses.locality + "," + addresses.countryName // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            }
                            else -> {
                                addresses.getAddressLine(0) + "," + addresses.locality
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return address
    }

    fun getLatLngFromAddress(address: String, googleKey: String): LatLng? {
        val geocoder = Geocoder(context!!, Locale.getDefault())
        try {
            var latLng: List<Address>? = null
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocationName(
                    address, 10
                ) { p0 -> latLng = p0 }
            } else {
                @Suppress("DEPRECATION")
                latLng = geocoder.getFromLocationName(address, 10)
            }

            val location = latLng!![0]
            return LatLng(location.latitude, location.longitude)
        } catch (e: JSONException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        } catch (e: IOException) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        return null
    }

    private fun getJSONfromURL(url: String): JSONObject? {

        // initialize
        var `is`: InputStream? = null
        var result = ""
        var jObject: JSONObject? = null

        // http post
        try {
            val httpclient = HttpClientBuilder.create().build()
            val httpPost = HttpPost(url)
            val response = httpclient.execute(httpPost)
            val entity = response.entity
            `is` = entity.content

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("log_tag", "Error in http connection $e")
            }
        }

        // convert response to string
        try {
            val reader = BufferedReader(InputStreamReader(`is` !!, "iso-8859-1"), 8)
            val sb = StringBuilder()
            val line: String? = null
            while (true) {
                sb.append(line !! + "\n")
            }
            `is`.close()
            result = sb.toString()
        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e("log_tag", "Error converting result $e")
            }
        }

        // try parse the string to a JSON object
        try {
            jObject = JSONObject(result)
        } catch (e: JSONException) {
            if (BuildConfig.DEBUG) {
                Log.e("log_tag", "Error parsing data $e")
            }
        }

        return jObject
    }

    fun getLastKnownLocation(context: Context): Location? {
        val mLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = mLocationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_permission_not_specified),
                    Toast.LENGTH_LONG
                ).show()
                return bestLocation
            }

            var l: Location? = mLocationManager.getLastKnownLocation(provider)
            if (l == null) {
                mLocationManager.requestLocationUpdates(provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, object : LocationListener {
                    override fun onLocationChanged(p0: Location) {
                        TODO("Not yet implemented")
                    }

                })
                l = mLocationManager.getLastKnownLocation(provider)
            }
            if (l != null && (bestLocation == null || l.accuracy > bestLocation.accuracy)) {
                bestLocation = l
            }
        }
        if (bestLocation == null && ! isGPSEnabled(context)) {
            Toast.makeText(context, context.getString(R.string.gps_not_enabled), Toast.LENGTH_LONG).show()
        }

        return bestLocation
    }

    private fun isGPSEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun getDirectionsUrl(origin: LatLng, dest: LatLng, googleMap: GoogleMap, googleKey: String) {
        isAddLine = true
        val strOrigin = ("origin=" + origin.latitude + ","
                + origin.longitude)
        val strDest = "destination=" + dest.latitude + "," + dest.longitude
        val sensor = "sensor=false"
        val parameters = "$strOrigin&$strDest&$sensor"
        val output = "json"
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters&key=$googleKey"
        /*DownloadTask(origin, dest, googleMap).execute(url)*/
        val executorDownloadTask = Executors.newSingleThreadExecutor()
        val handlerDownloadTask = Handler(Looper.getMainLooper())
        executorDownloadTask.execute {
            /*doInBackground*/
            var data = ""
            try {
                data = downloadUrl(url)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.d("Background Task", e.toString())
                }
            }

            handlerDownloadTask.post {
                /*onPostExecute*/
                val executorParserTask = Executors.newSingleThreadExecutor()
                val handlerParserTask = Handler(Looper.getMainLooper())
                executorParserTask.execute {

                    val jObject: JSONObject
                    var routes: List<List<HashMap<String, String>>>? = null
                    try {
                        jObject = JSONObject(data)
                        val parser = DirectionsJSONParser()
                        routes = parser.parse(jObject)
                    } catch (e: Exception) {
                        if (BuildConfig.DEBUG) {
                            e.printStackTrace()
                        }
                    }

                    handlerParserTask.post {
                        var points: ArrayList<LatLng>? = null
                        var lineOptions = PolylineOptions()
                        if (routes == null) {
                            @Suppress("LABEL_NAME_CLASH")
                            return@post
                        }
                        if (routes.isEmpty()) {
                            return@post
                        }

                        for (i in routes.indices) {
                            points = ArrayList()
                            lineOptions = PolylineOptions()
                            val path = routes[i]
                            for (j in path.indices) {
                                val point = path[j]
                                if (j == 0) {
                                    val line = point["distance"]
                                    if (line != null) {
                                        val parts = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()
                                        val distance = java.lang.Double.parseDouble(parts[0].replace(",", "."))

                                        val dis = ceil(distance).toInt()
                                        totalDistance = distance
                                        if (onDistanceCalculated != null) {
                                            onDistanceCalculated!!.sendDistance(distance)
                                        }
                                    }
                                    continue

                                } else if (j == 1) {

                                    val duration = point["duration"]
                                    if (duration!!.contains("hours") && (duration.contains("mins") || duration
                                            .contains("min"))
                                    ) {

                                        val arr = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()
                                        var timeDur = 0
                                        for (k in arr.indices) {
                                            if (k == 0) {
                                                timeDur = Integer.parseInt(arr[k]) * 60
                                            }
                                            if (k == 2) {
                                                timeDur += Integer.parseInt(arr[k])
                                            }

                                        }

                                        //                            totalDuration = String.valueOf(timeDur);

                                    } else if (duration.contains("mins") || duration.contains("min")) {
                                        val words = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                                            .toTypedArray()
                                        //                            totalDuration = words[0];
                                    }
                                    continue
                                }

                                val lat = java.lang.Double.parseDouble(point["lat"]!!)
                                val lng = java.lang.Double.parseDouble(point["lng"]!!)
                                val position = LatLng(lat, lng)
                                points.add(position)
                            }

                            lineOptions.addAll(points)
                            lineOptions.width(5f)
                            lineOptions.color(Color.BLUE)
                        }
                        polyline?.remove()
                        if (routeMap != null && onPolyLineReceived != null) {
                            routeMap!!.addPolyline(lineOptions)
                            onPolyLineReceived!!.onPolyLineReceived(origin, destination, routeMap!!)
                            val builder = LatLngBounds.Builder()
                            builder.include(origin)
                            builder.include(destination!!)
                            val bounds = builder.build()
                            val padding = 10
                            val cu = CameraUpdateFactory.newLatLngBounds(
                                bounds,
                                padding
                            )
                            // routeMap.moveCamera(cu);
                            routeMap!!.animateCamera(cu)
                        }

                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        val iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)
            urlConnection = url.openConnection() as HttpURLConnection
            data = urlConnection.inputStream.bufferedReader().readText()
            return data

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {

                e.printStackTrace()
            }
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }

    fun rotateMarker(fromLat: Double, fromLong: Double, toLat: Double, toLong: Double, marker: Marker, handler: Handler) {
        val brng = bearingBetweenLocations(fromLat, fromLong, toLat, toLong)

        var rotation = marker.rotation

        if (rotation >= 360) {
            rotation %= 360
        } else if (rotation <= - 360) {
            rotation %= 360
            rotation += 360
        }

        var newAngle = marker.rotation.toDouble()
        if (brng - rotation in 0.0..180.0) {
            newAngle = marker.rotation + (brng - rotation)
        } else if (brng - rotation >= 0 && brng - rotation >= 180) {
            newAngle = marker.rotation - (360 - (brng - rotation))
        } else if (brng - rotation <= 0 && brng - rotation >= - 180) {
            newAngle = marker.rotation + brng - rotation
        } else if (brng - rotation <= 0 && brng - rotation <= - 180) {
            newAngle = marker.rotation + (360 - (rotation - brng))
        }


        val start = SystemClock.uptimeMillis()
        val startRotation = marker.rotation
        val toRotation = newAngle.toFloat()
        val duration: Long = 1500

        val interpolator = LinearInterpolator()
        handler.post(object : MyRunnable(start, interpolator, duration.toFloat(), toRotation, startRotation, marker, handler) {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)

                val rot = t * toRotation + (1 - t) * startRotation

                marker.rotation = if (- rot >= 180) {
                    rot / 2
                } else {
                    rot
                }
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16)
                }
            }
        })


        // marker.setRotation((float) newAngle)

    }

    private open inner class MyRunnable(var start: Long, var interpolator: Interpolator, var duration: Float, var toRotation: Float, var startRotation: Float, var marker: Marker, var handler: Handler) : Runnable {

        override fun run() {

        }
    }

    private fun bearingBetweenLocations(fromLat: Double, fromLong: Double, toLat: Double, toLong: Double): Double {

        val PI = 3.14159
        val lat1 = fromLat * PI / 180
        val long1 = fromLong * PI / 180
        val lat2 = toLat * PI / 180
        val long2 = toLong * PI / 180

        val dLon = long2 - long1

        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        var brng = atan2(y, x)

        brng = Math.toDegrees(brng)
        brng = (brng + 360) % 360

        return brng
    }

    private interface DistanceCalculated {

        fun sendDistance(distance: Double)
    }

    private inner class DownloadTask : AsyncTask<String, Void, String> {

        constructor(source: LatLng, dest: LatLng, map: GoogleMap) {

            origin = source
            destination = dest
            routeMap = map
        }

        constructor(source: LatLng, dest: LatLng, distanceCalculated: DistanceCalculated) {

            onDistanceCalculated = distanceCalculated
            origin = source
            destination = dest
        }


        override fun doInBackground(vararg url: String): String {
            var data = ""
            try {
                data = downloadUrl(url[0])
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    Log.d("Background Task", e.toString())
                }
            }

            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()
            parserTask.execute(result)

        }
    }

    private inner class ParserTask : AsyncTask<String, Int, List<List<HashMap<String, String>>>>() {
        override fun doInBackground(vararg jsonData: String): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                val parser = DirectionsJSONParser()
                routes = parser.parse(jObject)
            } catch (e: Exception) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
            }

            return routes
        }

        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            var points: ArrayList<LatLng>?
            var lineOptions = PolylineOptions()
            if (result == null) {
                return
            }
            if (result.isEmpty()) {
                return
            }

            for (i in result.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()
                val path = result[i]
                for (j in path.indices) {
                    val point = path[j]
                    if (j == 0) {
                        val line = point["distance"]
                        if (line != null) {
                            val parts = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            val distance = java.lang.Double.parseDouble(parts[0].replace(",", "."))

                            val dis = ceil(distance).toInt()
                            totalDistance = distance
                            if (onDistanceCalculated != null) {
                                onDistanceCalculated !!.sendDistance(distance)
                            }
                        }
                        continue

                    } else if (j == 1) {

                        val duration = point["duration"]
                        if (duration !!.contains("hours") && (duration.contains("mins") || duration
                                        .contains("min"))) {

                            val arr = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            var timeDur = 0
                            for (k in arr.indices) {
                                if (k == 0) {
                                    timeDur = Integer.parseInt(arr[k]) * 60
                                }
                                if (k == 2) {
                                    timeDur += Integer.parseInt(arr[k])
                                }

                            }

                            //                            totalDuration = String.valueOf(timeDur)

                        } else if (duration.contains("mins") || duration.contains("min")) {
                            val words = duration.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            //                            totalDuration = words[0]
                        }
                        continue
                    }

                    val lat = java.lang.Double.parseDouble(point["lat"] !!)
                    val lng = java.lang.Double.parseDouble(point["lng"] !!)
                    val position = LatLng(lat, lng)
                    points.add(position)
                }

                lineOptions.addAll(points)
                lineOptions.width(5f)
                lineOptions.color(Color.BLUE)
            }
            polyline?.remove()
            if (routeMap != null && onPolyLineReceived != null) {
                routeMap !!.addPolyline(lineOptions)
                onPolyLineReceived !!.onPolyLineReceived(origin, destination, routeMap !!)
                val builder = LatLngBounds.Builder()
                builder.include(origin !!)
                builder.include(destination !!)
                val bounds = builder.build()
                val padding = 10
                val cu = CameraUpdateFactory.newLatLngBounds(bounds,
                        padding)
                // routeMap.moveCamera(cu)
                routeMap !!.animateCamera(cu)
            }
        }
    }

    fun setPolyLineReceivedListener(onPolyLineReceived: OnPolyLineReceived) {
        this.onPolyLineReceived = onPolyLineReceived
    }

    interface OnPolyLineReceived {
        fun onPolyLineReceived(origin: LatLng?, destination: LatLng?, routeMap: GoogleMap)
    }

    companion object {
        private var mapUtils: GoogleApisHandle? = null
        private var onDistanceCalculated: DistanceCalculated? = null

        private const val MIN_TIME_BW_UPDATES: Long = 1000
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f

        fun getInstance(context: Context): GoogleApisHandle {
            if (mapUtils == null) {
                mapUtils = GoogleApisHandle()
            }
            mapUtils !!.setAct(context)
            return mapUtils !!
        }
    }
}