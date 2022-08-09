package com.cmpt362.rentit.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.cmpt362.rentit.Constants
import com.cmpt362.rentit.R
import com.cmpt362.rentit.db.Listing
import com.cmpt362.rentit.ui.home.MapDialog.Companion.MAP_DIALOG_DESCRIPTION_KEY
import com.cmpt362.rentit.ui.home.MapDialog.Companion.MAP_DIALOG_ID_KEY
import com.cmpt362.rentit.ui.home.MapDialog.Companion.MAP_DIALOG_NAME_KEY
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener {
    private lateinit var mMap: GoogleMap
    private lateinit var  markerOptions: MarkerOptions
    private val PERMISSION_REQUEST_CODE = 0
    private lateinit var locationManager: LocationManager
    private lateinit var supportMapFragment: SupportMapFragment
    private lateinit var database: DatabaseReference
    private var listings = ArrayList<Listing>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        supportMapFragment.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        markerOptions = MarkerOptions()
        mMap.setOnMarkerClickListener(this)
        checkPermission()
    }


    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        else {
            val currentLocation = moveCameraToYou()
            getListingsNearYou(currentLocation)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val currentLocation = moveCameraToYou()
                getListingsNearYou(currentLocation)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun moveCameraToYou(): Location? {
        try {
            locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(provider!!)
            if(location != null) {
                val lat = location.latitude
                val lng = location.longitude
                val latlng = LatLng(lat, lng)
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 12f)
                mMap.animateCamera(cameraUpdate)
                return location
            } else {
                val sfu = Location(provider)
                sfu.latitude = 49.276765
                sfu.longitude = -122.917957
                val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(sfu.latitude, sfu.longitude), 12f)
                mMap.animateCamera(cameraUpdate)
                return sfu
            }
        } catch (_e: SecurityException) {
            println("DEBUG: encountered an error $_e")
        }
        return null
    }

    private fun getListingsNearYou(currentLocation: Location?) {
        database = Firebase.database.getReference(Constants.LISTINGS_TABLE_NAME)
        database.get().addOnSuccessListener {
            listings.clear()
            if (it.hasChildren()){
                it.children.forEach{ _listing ->
                    val id = _listing.child("listingID").getValue(String::class.java)!!
                    val type = _listing.child("type").getValue(String::class.java)
                    val name = _listing.child("name").getValue(String::class.java)
                    val price = _listing.child("price").getValue(Double::class.java)
                    val description = _listing.child("description").getValue(String::class.java)
                    val postUserID = _listing.child("postUserID").getValue(String::class.java)?: "-1"
                    val renterUserID = _listing.child("renterUserID").getValue(String::class.java)?: "-1"
                    val available = _listing.child("available").getValue(Boolean::class.java)?: false
                    val locationString = _listing.child("location").getValue(String::class.java)
                    val locationType = object : TypeToken<LatLng>() {}.type
                    val location: LatLng = Gson().fromJson(locationString, locationType)
                    val listing = Listing(id, type, name, price, description, postUserID, renterUserID, available, locationString)
                    listings.add(listing)
                    if (available){
                        if(currentLocation != null && locationString != "") {
                            markerOptions.title(name)
                            markerOptions.snippet(id)
                            markerOptions.position(location)
                            markerOptions.icon(
                                BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN))
                            mMap.addMarker(markerOptions)
                        }
                    }
                }
            }
        }.addOnFailureListener {
            println("DEBUG: failure loading data: $it")
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val bundle = Bundle()
        val dialog = MapDialog()
        bundle.putString(MAP_DIALOG_NAME_KEY, marker.title)
        bundle.putString(MAP_DIALOG_ID_KEY, marker.snippet)
        bundle.putString(MAP_DIALOG_DESCRIPTION_KEY, marker.title)
        dialog.arguments = bundle
        dialog.show(parentFragmentManager, "mapDialogTag")
        marker.hideInfoWindow()
        return true
    }

}