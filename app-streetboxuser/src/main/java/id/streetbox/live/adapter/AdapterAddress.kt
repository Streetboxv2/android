package id.streetbox.live.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.zeepos.models.response.DataAddress
import com.zeepos.utilities.showView
import id.streetbox.live.R
import id.streetbox.live.ui.onclick.OnClickItemAny
import id.streetbox.live.ui.onclick.OnClickItemSelect
import kotlinx.android.synthetic.main.layout_item_list_address.view.*

class AdapterAddress(
    var from: String?,
    val dataAddress: MutableList<DataAddress> = arrayListOf(),
    val onClickItemAny: OnClickItemAny,
    val onClickItemSelect: OnClickItemSelect,
    val onClickSelectItemChecked: OnClickSelectItemChecked
) : RecyclerView.Adapter<AdapterAddress.ViewHolder>() {
    private var selectedPosition = -1

    class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_list_address, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataAddress.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataAddress[position]
        holder.apply {
            itemView.apply {
                tvItemNameAddress.text = item.addressName
                tvItemPesonAddress.text = item.person
                tvItemAddress.text = item.address
                tvItemPhoneAddress.text = item.phone

                if (from.equals("homevisit", ignoreCase = true)) {
                    showView(btnChoiceItem)
                }

                if (item.primary) {
                    showView(tvStatusPrimaryItem)
                    tvStatusPrimaryItem.text = "Utama"
                }

                btnChoiceItem.setOnClickListener {
                    onClickSelectItemChecked.onChecked(item)
                }

                btnChangeAddress.setOnClickListener {
                    onClickItemSelect.clickItem(item)
                }

                btnSetPrimary.setOnClickListener {
                    onClickItemSelect.clickItemMarkAsAddress(item)
                }

                btnDeleteAddress.setOnClickListener {
                    onClickItemSelect.clickItemDelete(item)
                }

                itemView.setOnClickListener {
                    onClickItemAny.clickItem(item)
                }

//                btnMoreAddress.setOnClickListener {
//                    val popupMenu = PopupMenu(btnMoreAddress.context, itemView)
//                    popupMenu.inflate(R.menu.menu_option_address)
//                    popupMenu.show()
//
//                    popupMenu.setOnMenuItemClickListener {
//                        when (it.itemId) {
////                            R.id.ic_mark_address -> {
////                                onClickItemSelect.clickItemMarkAsAddress(item)
////                                return@setOnMenuItemClickListener true
////                            }
//                            R.id.ic_delete_address -> {
//                                onClickItemSelect.clickItemDelete(item)
//                                return@setOnMenuItemClickListener true
//                            }
//                        }
//                        return@setOnMenuItemClickListener false
//                    }
//                }
            }
        }
    }
}


interface OnClickSelectItemChecked {
    fun onChecked(any: Any)
}
