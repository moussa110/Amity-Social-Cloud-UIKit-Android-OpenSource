package com.amity.socialcloud.uikit.chat.messages.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.chat.databinding.AmityItemUserMentionBinding
import com.amity.socialcloud.uikit.chat.messages.model.AmityUserMention
import com.amity.socialcloud.uikit.common.base.AmityBaseRecyclerViewPagingDataAdapter

class AmityUserMentionViewHolder(itemView: View, private val listener: AmityUserMentionListener) :
    RecyclerView.ViewHolder(itemView), AmityBaseRecyclerViewPagingDataAdapter.Binder<AmityUser> {

    private val itemBiding: AmityItemUserMentionBinding? = DataBindingUtil.bind(itemView)

    override fun bind(data: AmityUser?, position: Int) {

        data?.let { userItem ->
            val banIcon = if (userItem.isGlobalBan()) {
                ContextCompat.getDrawable(itemView.context, R.drawable.amity_ic_ban)
            } else {
                null
            }
            itemBiding?.apply {
                user = userItem
                userMention = AmityUserMention(userItem)
                isGlobalBan = userItem.isGlobalBan()
                textviewDisplayname.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    banIcon,
                    null
                )
                clickListener = listener
            }
        }
    }

    interface AmityUserMentionListener {
        fun onClickUserMention(userMention: AmityUserMention)
    }

}