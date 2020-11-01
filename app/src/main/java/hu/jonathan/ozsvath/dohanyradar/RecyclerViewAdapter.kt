package hu.jonathan.ozsvath.dohanyradar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_view_item.view.*

class RecyclerViewAdapter(
    private val recyclerViewList: List<RecyclerViewItem>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        var itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)

        return RecyclerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = recyclerViewList[position]

        holder.name.text = currentItem.name
        holder.address.text = currentItem.address
        if (currentItem.is_open) {
            holder.isOpen.setImageResource(R.drawable.ic_check_box_positive)
        } else {
            holder.isOpen.setImageResource(R.drawable.ic_icheck_box_negative)
        }
    }

    override fun getItemCount() = recyclerViewList.size

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val name: TextView = itemView.name
        val address: TextView = itemView.address
        val isOpen: ImageView = itemView.is_open

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}