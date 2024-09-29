package com.example.weatherforcast.map
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.weatherforcast.R
import com.example.weatherforcast.db.PreferencesManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var autoCompleteFragment: AutocompleteSupportFragment
    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        preferencesManager = PreferencesManager(requireContext())
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "ApiKey")
        }

        autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))

        autoCompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                val latLng = place.latLng
                if (latLng != null) {
                    lifecycleScope.launch {
                        mapsViewModel.emitSearchLocation(latLng)
                    }
                }
            }

            override fun onError(status: Status) {
                Toast.makeText(requireContext(), "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
            }
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        lifecycleScope.launch {
            mapsViewModel.searchQueryFlow.collect { latLng ->
                updateMapLocation(latLng)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val defaultLocation = LatLng(30.0444, 31.2357)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 5f))

        mMap.setOnMapClickListener { latLng ->
            updateMapLocation(latLng)
            Navigation.findNavController(requireView()).navigate(R.id.action_mapsFragment_to_homeFragment)
        }
    }

    private fun updateMapLocation(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Selected Location")
                .draggable(false)
                .visible(true)
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))

        preferencesManager.saveSelectedLocation(latLng.latitude.toString(), latLng.longitude.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity?)?.supportActionBar?.show()

    }
}
