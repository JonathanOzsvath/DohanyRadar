package hu.jonathan.ozsvath.dohanyradar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import hu.jonathan.ozsvath.dohanyradar.R
import hu.jonathan.ozsvath.dohanyradar.RecyclerViewItem


class MapsFragment : Fragment() {

    private lateinit var recyclerViewItemList: ArrayList<RecyclerViewItem>

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
        for (item in recyclerViewItemList) {
            val latLng = LatLng(item.latitude, item.longitude)
            if (item.is_open) {
                var marker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title(item.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                )
                marker.tag = item.id
            } else {
                var marker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title(item.name)
                )
                marker.tag = item.id
            }
        }

        val budapest = LatLng(47.4813602, 18.9902189)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(budapest, 10F))

        googleMap.setOnMarkerClickListener { marker ->
            fragmentManager!!
                .beginTransaction()
                .replace(
                    R.id.fragment_holder,
                    DetailsFragment.newInstance(marker.tag as Int),
                    null
                )
                .addToBackStack(null)
                .commit()
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recyclerViewItemList =
                it.getParcelableArrayList<RecyclerViewItem>("recyclerViewItemList") as ArrayList<RecyclerViewItem>
        }
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
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val btnGoogleMaps = activity!!.findViewById<View>(R.id.btnGoogleMaps)
        btnGoogleMaps.visibility = View.INVISIBLE
        val btnShopList = activity!!.findViewById<View>(R.id.btnShopList)
        btnShopList.visibility = View.VISIBLE
    }


    override fun onStop() {
        super.onStop()
        val btnGoogleMaps = activity!!.findViewById<View>(R.id.btnGoogleMaps)
        btnGoogleMaps.visibility = View.VISIBLE
        val btnShopList = activity!!.findViewById<View>(R.id.btnShopList)
        btnShopList.visibility = View.INVISIBLE
    }
}