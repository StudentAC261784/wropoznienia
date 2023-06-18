package com.example.wropoznienia

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private lateinit var textInputDialog: AlertDialog
    private var enteredText = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initTextInputDialog()
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(51.1256586, 17.006079), 12.0f))
        var vehicleMap = HashMap<String, Marker>()
        val fileDownload = FileDownload()
        val context = this

//        fileDownload.getFromFirestore(vehicleList, googleMap, db)
//
//        GlobalScope.launch {
//            while (isActive) {
//                delay(10_000)
//                runOnUiThread {
//                    vehicleList = fileDownload.updateMarkerDataOnce(vehicleList, googleMap, db)
//                }
//            }
//        }
        val btnAdd: ImageButton = findViewById(R.id.btnAdd)
        btnAdd.setOnClickListener {
            textInputDialog.show()
        }


        fileDownload.downloadFile(vehicleMap, googleMap, application, context, enteredText) { updatedVehicleMap ->
            vehicleMap = updatedVehicleMap
        }

        GlobalScope.launch {
            while (isActive) {
                delay(5_000)
                runOnUiThread {
                    fileDownload.downloadFile(vehicleMap, googleMap, application, context, enteredText) { updatedVehicleMap ->
                        vehicleMap = updatedVehicleMap
                    }
                }
            }
        }
    }
    public override fun onResume() {
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
    }

    private fun initTextInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Text")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            enteredText = input.text.toString()
            // Do something with the entered text
            // For example, you can display it in a TextView or perform some other action
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        textInputDialog = builder.create()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest = ArrayList<String>()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

//    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
//        val permissionsToRequest = ArrayList<String>()
//        for (permission in permissions) {
//            if (ContextCompat.checkSelfPermission(this, permission)
//                != PackageManager.PERMISSION_GRANTED
//            ) {
//                // Permission is not granted
//                permissionsToRequest.add(permission)
//            }
//        }
//        if (permissionsToRequest.size > 0) {
//            ActivityCompat.requestPermissions(
//                this,
//                permissionsToRequest.toTypedArray(),
//                REQUEST_PERMISSIONS_REQUEST_CODE
//            )
//        }
//    }
}