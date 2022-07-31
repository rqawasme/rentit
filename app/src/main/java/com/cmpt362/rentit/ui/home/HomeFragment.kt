package com.cmpt362.rentit.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cmpt362.rentit.databinding.FragmentHomeBinding
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HomeFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var  markerOptions: MarkerOptions
    private val PERMISSION_REQUEST_CODE = 0
    private lateinit var locationManager: LocationManager
    private var isCenter = false

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        markerOptions = MarkerOptions()
        checkPermission()
    }


    fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        else
            initLocationManager()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) initLocationManager()
        }
    }

    @SuppressLint("MissingPermission")
    fun initLocationManager() {
        try {
            locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if(location != null)
                onLocationChanged(location)
            locationManager.requestLocationUpdates(provider, 0, 0f, this)
        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
        val lat = location.latitude
        val lng = location.longitude
        val latlng = LatLng(lat, lng)

        if (!isCenter) {
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 13f)
            mMap.animateCamera(cameraUpdate)
            markerOptions.position(latlng)
            mMap.addMarker(markerOptions)
            isCenter = true
        }
    }
}