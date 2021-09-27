package com.zeepos.streetbox.ui.main.profile

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zeepos.streetbox.R

class ProfileAdapter(data: MutableList<Profile> = ArrayList()) :
    BaseQuickAdapter<Profile, BaseViewHolder>(R.layout.custom_list, data) {

    init {
        addChildClickViewIds(R.id.arrow)
    }

    override fun convert(holder: BaseViewHolder, item: Profile) {
    }

}