package com.amity.socialcloud.uikit.chat.messages.adapter

import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.chat.messages.model.AmityUserMention
import com.amity.socialcloud.uikit.common.base.AmityBaseRecyclerViewPagingDataAdapter

class AmityUserMentionAdapter :
    AmityBaseRecyclerViewPagingDataAdapter<AmityUser>(UserDiffUtil()),
    AmityUserMentionViewHolder.AmityUserMentionListener {



    private var listener: AmityUserMentionAdapterListener? = null

    override fun getLayoutId(position: Int, obj: AmityUser?) = R.layout.amity_item_user_mention

    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return AmityUserMentionViewHolder(view, this)
    }

    override fun onClickUserMention(userMention: AmityUserMention) {
        listener?.onClickUserMention(userMention)
    }

    fun setListener(listener: AmityUserMentionAdapterListener) {
        this.listener = listener
    }

    interface AmityUserMentionAdapterListener {
        fun onClickUserMention(userMention: AmityUserMention)
    }
}

class UserDiffUtil : DiffUtil.ItemCallback<AmityUser>() {

    override fun areItemsTheSame(oldItem: AmityUser, newItem: AmityUser): Boolean {
        return oldItem.getUserId() == newItem.getUserId()
    }

    override fun areContentsTheSame(oldItem: AmityUser, newItem: AmityUser): Boolean {
        return oldItem.getDisplayName() == newItem.getDisplayName()
                && oldItem.getAvatar()?.getUrl() == newItem.getAvatar()?.getUrl()
                && oldItem.getAvatarCustomUrl() == newItem.getAvatarCustomUrl()
                && oldItem.isGlobalBan() == newItem.isGlobalBan()
    }
}