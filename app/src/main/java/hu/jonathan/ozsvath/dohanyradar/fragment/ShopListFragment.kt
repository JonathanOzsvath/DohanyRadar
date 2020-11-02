package hu.jonathan.ozsvath.dohanyradar.fragment

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import hu.jonathan.ozsvath.dohanyradar.Communicator
import hu.jonathan.ozsvath.dohanyradar.R
import hu.jonathan.ozsvath.dohanyradar.RecyclerViewAdapter
import hu.jonathan.ozsvath.dohanyradar.RecyclerViewItem
import kotlinx.android.synthetic.main.fragment_shop_list.*
import java.util.*
import kotlin.collections.ArrayList


class ShopListFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private val TAG = "ShopListFragment"
    private val DETAILS_TAG = "Details"
    private val ITEM_LIST = "recyclerViewItemList"

    private lateinit var recyclerViewItemList: ArrayList<RecyclerViewItem>
    private val displayRecyclerViewItemList = ArrayList<RecyclerViewItem>()
    private val recyclerViewAdapter = RecyclerViewAdapter(displayRecyclerViewItemList, this)
    private var requestQueue: RequestQueue? = null
    private val url = "https://dohanyradar.codevisionkft.hu/tobbacoshop/"

    lateinit var comm: Communicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.run {
            recyclerViewItemList =
                getParcelableArrayList<RecyclerViewItem>(ITEM_LIST) as ArrayList<RecyclerViewItem>
        }
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

        val btnGoogleMaps = activity!!.findViewById<View>(R.id.btnGoogleMaps)
        btnGoogleMaps.visibility = View.VISIBLE

        comm = activity as Communicator

        recycle_view.adapter = recyclerViewAdapter
        recycle_view.layoutManager = LinearLayoutManager(context)
        recycle_view.setHasFixedSize(true)

        if (!this::recyclerViewItemList.isInitialized) {
            recyclerViewItemList = arrayListOf()
        } else {
            displayRecyclerViewItemList.addAll(recyclerViewItemList)
            comm.passDataCom(recyclerViewItemList)
            recyclerViewAdapter.notifyDataSetChanged()
        }

        if (recyclerViewItemList.isEmpty()) {
            spinner.visibility = View.VISIBLE

            requestQueue = Volley.newRequestQueue(context)

            val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
                { response ->
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

                    val btnGoogleMaps = activity!!.findViewById<View>(R.id.btnGoogleMaps)
                    btnGoogleMaps.visibility = View.VISIBLE

                    spinner.visibility = View.INVISIBLE
                },
                { error ->
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
        @JvmStatic
        fun newInstance() =
            ShopListFragment()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putParcelableArrayList(
            ITEM_LIST,
            ArrayList<RecyclerViewItem>(recyclerViewItemList)
        )
    }


}