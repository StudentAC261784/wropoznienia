package com.example.wropoznienia

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.opencsv.CSVReader
import java.io.*


class FileRead {

    fun readCsvFile(
        context: Context,
        file: File,
        vehicleMap: HashMap<String, Marker>,
        stopMap: HashMap<String, Marker>,
        googleMap: GoogleMap,
        enteredText: String,
        callback: (HashMap<String, Marker>) -> Unit
    ) {
        var vehicleMapCopy = HashMap<String, Marker>()
        vehicleMapCopy.putAll(vehicleMap)
        var csvLines = ""
        try {
            val fileInputStream = FileInputStream(file)
            val reader = CSVReader(InputStreamReader(fileInputStream))
            var nextLine: Array<String>?
            nextLine = reader.readNext()
            while (reader.readNext().also { nextLine = it } != null) {
                // nextLine[] is an array of values from the line
                csvLines = nextLine!!.joinToString(separator = ",")
                try {
                    vehicleMapCopy = addVehicleToMap(vehicleMapCopy, stopMap, csvLines, googleMap, context, enteredText)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Different error, maybe can't add rat?", Toast.LENGTH_SHORT).show()
                }
            }

            reader.close()
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show()
        }
        // Invoke the callback with the updated map
        callback(vehicleMapCopy)
    }


    private fun addVehicleToMap(vehicleMap: HashMap<String, Marker>, stopMap: HashMap<String, Marker>,mpkLine: String, googleMap: GoogleMap, context: Context, enteredText: String): HashMap<String, Marker> {
        val values = mpkLine.split(",")
        var delayMessage = " nie wiem ile :D"
        val transportMpkPosition = LatLng(values[4].toDouble(), values[5].toDouble())
        if (values[8] != "NP") {
            delayMessage = " Opóźnienie:" + values[12] + "s"
        }
        if (vehicleMap.containsKey(values[0])) {
            vehicleMap[values[0]]?.position = transportMpkPosition
            vehicleMap[values[0]]?.snippet = "Kierunek: " + values[3] + delayMessage
            vehicleMap[values[0]]?.tag = values[6] + "&" + values[8] + "&" + values[12]
        } else {
            val markerName: Marker = googleMap.addMarker(
                MarkerOptions()
                    .position(transportMpkPosition)
                    .title("Szczur - linia " + values[2])
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarker))
                    .snippet("Kierunek: " + values[3] + delayMessage))
            markerName.tag = values[6] + "&" + values[8] + "&" + values[12]
            vehicleMap[values[0]] = markerName
        }
        if (enteredText != "") {
            if (!values[0].contains(enteredText+"_")) {
                if (vehicleMap.containsKey(values[0])) {
                    vehicleMap[values[0]]?.isVisible = false
                }
            } else {
                vehicleMap[values[0]]?.isVisible = true
                val stopsId = values[6].split("/")
                for (stopId in stopsId) {
                    stopMap[stopId]?.isVisible = true
                }
            }
        } else {
            vehicleMap[values[0]]?.isVisible = true
            for ((id, stop) in stopMap) {
                stop.isVisible = false
            }
        }
        return vehicleMap
    }

    fun readTxtFile(
        context: Context,
        file: File,
        stopMap: HashMap<String, Marker>,
        googleMap: GoogleMap,
        enteredText: String,
        callback: (HashMap<String, Marker>) -> Unit
    ) {
        var stopMapCopy = HashMap<String, Marker>()
        stopMapCopy.putAll(stopMap)
        try {
            val fileInputStream = FileInputStream(file)
            val reader = BufferedReader(InputStreamReader(fileInputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                try {
                    stopMapCopy = addStopToMap(stopMapCopy, line!!, googleMap, context, enteredText)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Different error, maybe can't add cheese?", Toast.LENGTH_SHORT).show()
                }
            }
            reader.close()
            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show()
        }
        // Invoke the callback with the updated map
        callback(stopMapCopy)
    }

    private fun addStopToMap(stopMap: HashMap<String, Marker>, mpkLine: String, googleMap: GoogleMap, context: Context, enteredText: String): HashMap<String, Marker> {
        val values = mpkLine.split(",")
        val stopPosition = LatLng(values[3].toDouble(), values[4].toDouble())
        val markerName: Marker = googleMap.addMarker(
            MarkerOptions()
                .position(stopPosition)
                .title(values[2])
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mymarker2)))
        markerName.tag = values[0]
        stopMap[values[0]] = markerName
        stopMap[values[0]]?.isVisible = false
        return stopMap
    }
}