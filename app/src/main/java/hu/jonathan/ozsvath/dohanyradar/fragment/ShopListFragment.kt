package hu.jonathan.ozsvath.dohanyradar.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import hu.jonathan.ozsvath.dohanyradar.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_shop_list.*
import java.util.*
import kotlin.collections.ArrayList


//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class ShopListFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private val TAG = "ShopListFragment"
    private val DETAILS_TAG = "details"

    private val recyclerViewItemList = ArrayList<RecyclerViewItem>()
    private val displayRecyclerViewItemList = ArrayList<RecyclerViewItem>()
    private val recyclerViewAdapter = RecyclerViewAdapter(displayRecyclerViewItemList, this)
    private var requestQueue: RequestQueue? = null
    private val url = "https://dohanyradar.codevisionkft.hu/tobbacoshop/"

    private var param1: String? = null
    private var param2: String? = null

    lateinit var comm: Communicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
//        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shop_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        comm = activity as Communicator

        recycle_view.adapter = recyclerViewAdapter
        recycle_view.layoutManager = LinearLayoutManager(context)
        recycle_view.setHasFixedSize(true)

        if (recyclerViewItemList.isEmpty()) {
            requestQueue = Volley.newRequestQueue(context)

            val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
                Response.Listener { response ->
                    for (i in 0 until response.length()) {
                        val actualObject = response.getJSONObject(i)
                        val item = RecyclerViewItem(
                            actualObject.getInt("id"),
                            actualObject.getString("name"),
                            actualObject.getString("address"),
                            actualObject.getDouble("longitude"),
                            actualObject.getDouble("latitude"),
                            actualObject.getBoolean("isOpen")
                        )

                        recyclerViewItemList.add(item)
                    }

                    displayRecyclerViewItemList.addAll(recyclerViewItemList)

                    recyclerViewAdapter.notifyDataSetChanged()
                    activity!!.actionBar?.show()

                    comm.passDataCom(recyclerViewItemList)

                    spinner.visibility = View.INVISIBLE
                },
                Response.ErrorListener { error ->
                    Toast.makeText(
                        context,
                        error.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    spinner.visibility = View.INVISIBLE
                }
            )
            jsonObjectRequest.tag = TAG
            requestQueue?.add(jsonObjectRequest)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShopListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
        fun newInstance() =
            ShopListFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
            }
    }


    override fun onItemClick(position: Int) {
        fragmentManager!!
            .beginTransaction()
            .replace(
                R.id.fragment_holder,
                DetailsFragment.newInstance(recyclerViewItemList[position].id),
                DETAILS_TAG
            )
            .addToBackStack(TAG)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        val menuItem = menu.findItem(R.id.search)

        if (menuItem != null) {
            val searchView = menuItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        displayRecyclerViewItemList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        recyclerViewItemList.forEach {
                            if (it.name.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayRecyclerViewItemList.add(it)
                            }
                        }
                        recyclerViewAdapter.notifyDataSetChanged()
                    } else {
                        displayRecyclerViewItemList.clear()
                        displayRecyclerViewItemList.addAll(recyclerViewItemList)
                        recyclerViewAdapter.notifyDataSetChanged()
                    }

                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onStop() {
        super.onStop()
        requestQueue?.cancelAll(TAG)
    }
}