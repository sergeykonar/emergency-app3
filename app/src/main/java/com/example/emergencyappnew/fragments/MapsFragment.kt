package com.example.emergencyappnew.fragments

import android.R.attr
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.emergencyappnew.R
import com.example.emergencyappnew.api.RetroClient
import com.example.emergencyappnew.util.DirectionHelper

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.utsman.samplegooglemapsdirection.kotlin.model.DirectionResponses
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.R.attr.path
import android.graphics.Color
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions


class MapsFragment : Fragment() {

    private lateinit var map: GoogleMap
    private val mTag = MapsFragment::class.java.canonicalName
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        map = googleMap
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
        mapFragment.getMapAsync(callback)
    }

    override fun onResume() {
        super.onResume()
        val api = RetroClient().getRetrofitService()
        val call = api.getDirection("Moscow", "Minsk", "AIzaSyAsq3BJ6YxppGX07J-TP-AfkQdpRX7YNpA")
        call.enqueue(object : Callback<DirectionResponses> {
            override fun onResponse(
                call: Call<DirectionResponses>,
                response: Response<DirectionResponses>
            ) {
                if(response.isSuccessful){
                    val options = PolylineOptions()
                    val res = response.body()
                    for(route in res?.routes!!){
                        for(leg in route?.legs!!){
                            for(step in leg?.steps!!){
                                val line = step?.polyline
                                options.color(Color.RED)
                                options.width(10f)
                                val list = line?.points!!.decodePoly()
                                options.addAll(list)
                            }
                        }
                    }
                    map.addPolyline(options)
                    Log.e(mTag, response.body().toString())


                }else{
                    Log.e(mTag, response.message().toString())
                }
            }

            override fun onFailure(call: Call<DirectionResponses>, t: Throwable) {
                Log.e("deed", t.message.toString())
            }

        })
    }

    fun String.decodePoly(): MutableList<LatLng> {
        if (this.isEmpty()) return mutableListOf()
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = this.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = this[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = this[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }
}