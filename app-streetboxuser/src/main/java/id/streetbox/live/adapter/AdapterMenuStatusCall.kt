package id.streetbox.live.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import id.streetbox.live.R
import id.streetbox.live.adapter.AdapterMenuStatusCall.MyHolder
import id.streetbox.live.ui.onclick.OnClickItemAny
import kotlinx.android.synthetic.main.layout_item_menu_status_call.view.*

class AdapterMenuStatusCall(val listMenu: MutableList<String>, val onClickItemAny: OnClickItemAny) :
    RecyclerView.Adapter<MyHolder>() {

    var selectPosition = -1

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_menu_status_call, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return listMenu.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.apply {
            itemView.apply {
                tvStatusCallItem.text = listMenu[position]

                itemView.setOnClickListener {
                    selectPosition = position
                    notifyDataSetChanged()
                }

                if (selectPosition == position) {
                    onClickItemAny.clickItem(listMenu[position])
                    clStatusCallItem.setBackgroundResource(R.drawable.bg_selector_tab_green)
                    tvStatusCallItem.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.white
                        )
                    )
                } else {
                    clStatusCallItem.setBackgroundResource(R.drawable.bg_selector_tab_green_muda)
                    tvStatusCallItem.setTextColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.black
                        )
                    )
                }
            }

        }
    }
}