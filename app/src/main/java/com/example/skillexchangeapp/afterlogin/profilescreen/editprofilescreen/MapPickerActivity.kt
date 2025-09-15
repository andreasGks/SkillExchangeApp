package com.example.skillexchangeapp.afterlogin.profilescreen.editprofilescreen

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.skillexchangeapp.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng


class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var selectedLatLng: LatLng? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_picker)

        Log.d("MAP_DEBUG", "onCreate called")

        // Permission check
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("MAP_DEBUG", "Permission not granted, requesting")
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("MAP_DEBUG", "Permission already granted, initializing map")
            initMap()
        }

        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
            Log.d("MAP_DEBUG", "Confirm location button clicked")
            selectedLatLng?.let {
                val resultIntent = Intent().apply {
                    putExtra("selected_lat", it.latitude)
                    putExtra("selected_lng", it.longitude)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    private fun initMap() {
        Log.d("MAP_DEBUG", "initMap called")

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment)

        if (mapFragment != null) {
            Log.d("MAP_DEBUG", "Map fragment found, getting map async")
            (mapFragment as SupportMapFragment).getMapAsync(this)
        } else {
            Log.e("MAP_DEBUG", "Map fragment is null")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("MAP_DEBUG", "onMapReady called")

        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            Log.d("MAP_DEBUG", "My location enabled")
        }

        val athensLatLng = LatLng(37.9838, 23.7275)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(athensLatLng, 12f))
        map.addMarker(MarkerOptions().position(athensLatLng).title("Athens, Greece"))

        map.setOnMapClickListener { latLng ->
            Log.d("MAP_DEBUG", "Map clicked at: $latLng")
            selectedLatLng = latLng
            map.clear()
            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))

            // Convert LatLng to address using Geocoder
            val geocoder = android.location.Geocoder(this)
            try {
                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addressList.isNullOrEmpty()) {
                    val address = addressList[0]
                    val city = address.locality
                    if (city != null) {
                        Log.d("MAP_DEBUG", "Selected City: $city")
                        Toast.makeText(this, "Selected City: $city", Toast.LENGTH_SHORT).show()

                        // Return city with LatLng
                        val resultIntent = Intent().apply {
                            putExtra("selected_lat", latLng.latitude)
                            putExtra("selected_lng", latLng.longitude)
                            putExtra("selected_city", city) // Passing city instead of full address
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                    } else {
                        Log.d("MAP_DEBUG", "No city found for this location")
                        Toast.makeText(this, "No city found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.d("MAP_DEBUG", "No address found for this location")
                    Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("MAP_DEBUG", "Geocoder error: ${e.localizedMessage}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("MAP_DEBUG", "onRequestPermissionsResult called with requestCode: $requestCode")

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MAP_DEBUG", "Permission granted after request")
                initMap()
            } else {
                Log.d("MAP_DEBUG", "Permission denied after request")
                Toast.makeText(
                    this,
                    "Location permission is required to show the map.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
}





//class MapPickerActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var map: GoogleMap
//    private var selectedLatLng: LatLng? = null
//
//    private val LOCATION_PERMISSION_REQUEST_CODE = 101
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_map_picker)
//
//        Log.d("MAP_DEBUG", "onCreate called")
//
//        // Permission check
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            Log.d("MAP_DEBUG", "Permission not granted, requesting")
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            Log.d("MAP_DEBUG", "Permission already granted, initializing map")
//            initMap()
//        }
//
//        findViewById<Button>(R.id.confirmLocationButton).setOnClickListener {
//            selectedLatLng?.let {
//                val resultIntent = Intent().apply {
//                    putExtra("selected_lat", it.latitude)
//                    putExtra("selected_lng", it.longitude)
//                }
//                setResult(Activity.RESULT_OK, resultIntent)
//                finish()
//            }
//        }
//    }
//
//    private fun initMap() {
//        Log.d("MAP_DEBUG", "initMap called")
//
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map_fragment)
//
//        if (mapFragment == null) {
//            Log.d("MAP_DEBUG", "mapFragment is null")
//        } else {
//            Log.d("MAP_DEBUG", "mapFragment is NOT null")
//            (mapFragment as SupportMapFragment).getMapAsync(this)
//        }
//
//    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        Log.d("MAP_DEBUG", "onMapReady called")
//
//        map = googleMap
//        map.uiSettings.isZoomControlsEnabled = true
//
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
//            == PackageManager.PERMISSION_GRANTED
//        ) {
//            map.isMyLocationEnabled = true
//        }
//
//        val athensLatLng = LatLng(37.9838, 23.7275)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(athensLatLng, 12f))
//        map.addMarker(MarkerOptions().position(athensLatLng).title("Athens, Greece"))
//
//        map.setOnMapClickListener { latLng ->
//            selectedLatLng = latLng
//            map.clear()
//
//            // Add marker
//            map.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
//
//            // Convert LatLng to address using Geocoder
//            val geocoder = android.location.Geocoder(this)
//            try {
//                val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//                if (!addressList.isNullOrEmpty()) {
//                    val address = addressList[0]
//                    val city = address.locality  // Extract city if available
//                    if (city != null) {
//                        Log.d("MAP_DEBUG", "Selected City: $city")
//
//                        // Optional: show the city somewhere (e.g., in a Toast)
//                        Toast.makeText(this, "Selected City: $city", Toast.LENGTH_SHORT).show()
//
//                        // Set the result with city
//                        val resultIntent = Intent().apply {
//                            putExtra("selected_lat", latLng.latitude)
//                            putExtra("selected_lng", latLng.longitude)
//                            putExtra("selected_city", city) // Passing city instead of full address
//                        }
//                        setResult(Activity.RESULT_OK, resultIntent)
//
//                    } else {
//                        Log.d("MAP_DEBUG", "No city found for this location.")
//                        Toast.makeText(this, "No city found", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    Log.d("MAP_DEBUG", "No address found for this location.")
//                    Toast.makeText(this, "No address found", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                Log.e("MAP_DEBUG", "Geocoder error: ${e.localizedMessage}")
//            }
//        }
//
//
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        Log.d("MAP_DEBUG", "onRequestPermissionsResult called with requestCode: $requestCode")
//
//        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.d("MAP_DEBUG", "Permission granted after request")
//                initMap()
//            } else {
//                Log.d("MAP_DEBUG", "Permission denied after request")
//                Toast.makeText(
//                    this,
//                    "Location permission is required to show the map.",
//                    Toast.LENGTH_SHORT
//                ).show()
//                finish()
//            }
//        }
//    }
//}
