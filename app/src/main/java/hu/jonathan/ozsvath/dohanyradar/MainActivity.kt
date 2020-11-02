package hu.jonathan.ozsvath.dohanyradar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.jonathan.ozsvath.dohanyradar.fragment.MapsFragment
import hu.jonathan.ozsvath.dohanyradar.fragment.ShopListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), Communicator {
    private val TAG = "MainActivityTag"
    private val SHOP_LIST_TAG = "ShopList"
    private val MAP_TAG = "Map"
    private lateinit var recyclerViewItemList: ArrayList<RecyclerViewItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, ShopListFragment.newInstance(), SHOP_LIST_TAG)
                .commit()
        }

        btnGoogleMaps.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelableArrayList(
                "recyclerViewItemList",
                ArrayList<RecyclerViewItem>(recyclerViewItemList)
            )

            val mapsFragment = MapsFragment()
            mapsFragment.arguments = bundle

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, mapsFragment, MAP_TAG)
                .addToBackStack(null)
                .commit()
        }

        btnShopList.setOnClickListener {
//            supportFragmentManager.popBackStack()
            var shopListFragment = supportFragmentManager.findFragmentByTag(SHOP_LIST_TAG)

            if (shopListFragment == null) {
                shopListFragment = ShopListFragment.newInstance()
            }

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_holder, shopListFragment, SHOP_LIST_TAG)
                .commit()
        }
    }

    override fun passDataCom(recyclerViewItemList: ArrayList<RecyclerViewItem>) {
        this.recyclerViewItemList = recyclerViewItemList
    }


}