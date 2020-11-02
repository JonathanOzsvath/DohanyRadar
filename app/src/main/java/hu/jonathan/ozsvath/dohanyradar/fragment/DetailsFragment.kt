package hu.jonathan.ozsvath.dohanyradar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import hu.jonathan.ozsvath.dohanyradar.R
import kotlinx.android.synthetic.main.fragment_details.*
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_ID = "id"

class DetailsFragment : Fragment() {

    private val TAG = "DetailsFragmentTag"
    private var shopId: Int? = null
    private val baseUrl = "https://dohanyradar.codevisionkft.hu/tobbacoshop/"

    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            shopId = it.getInt(ARG_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnGoogleMaps = activity!!.findViewById<View>(R.id.btnGoogleMaps)
        btnGoogleMaps.visibility = View.INVISIBLE

        requestQueue = Volley.newRequestQueue(context)

        val imageRequest = ImageRequest(
            "$baseUrl$shopId/image",
            { response ->
                detailsImageView.setImageBitmap(response)
            }, 0, 0, ImageView.ScaleType.CENTER_CROP, null,
            { error ->
                Toast.makeText(
                    context,
                    error.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        imageRequest.tag = TAG

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, baseUrl + shopId, null,
            { response ->
                detailsName.text = response.getString("name")
                detailsAddress.text = response.getString("address")

                val openOurArray = response.getJSONArray("openHours")
                var openStr = ""
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val formatOut = SimpleDateFormat("HH:mm")

                for (i in 0 until openOurArray.length()) {

                    val openDate: Date = format.parse(
                        response.getJSONArray("openHours")
                            .getJSONObject(i).getString("openTime")
                    )
                    val closeDate: Date = format.parse(
                        response.getJSONArray("openHours")
                            .getJSONObject(i).getString("closeTime")
                    )

                    openStr += "${formatOut.format(openDate)} - ${formatOut.format(closeDate)}\n"
                }
                detailsOpeningHour.text = openStr

                if (response.getBoolean("isOpen")) {
                    detailsIsOpen.text = "Nyitva"
                } else {
                    val calendar: Calendar = Calendar.getInstance()
                    val day: Int = calendar.get(Calendar.DAY_OF_WEEK)

                    val closeDate: Date = format.parse(
                        response.getJSONArray("openHours")
                            .getJSONObject(day - 1).getString("closeTime")
                    )

                    detailsIsOpen.text = "Zárva - Nyitás ${formatOut.format(closeDate)}"
                }

                detailsDescription.text = response.getString("description")

                detailsSpinner.visibility = View.INVISIBLE
            },
            { error ->
                Toast.makeText(
                    context,
                    error.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                detailsSpinner.visibility = View.INVISIBLE
            }
        )
        jsonObjectRequest.tag = TAG
        requestQueue?.add(imageRequest)
        requestQueue?.add(jsonObjectRequest)
    }

    override fun onStop() {
        super.onStop()
        requestQueue?.cancelAll(TAG)
    }

    companion object {
        @JvmStatic
        fun newInstance(shopId: Int) =
            DetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ID, shopId)
                }
            }
    }
}