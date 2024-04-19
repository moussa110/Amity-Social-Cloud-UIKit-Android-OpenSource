package com.amity.socialcloud.uikit.chat.messages.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.amity.socialcloud.sdk.model.chat.member.AmityChannelMember
import com.amity.socialcloud.uikit.chat.databinding.AmityItemUserMentionBinding
import com.amity.socialcloud.uikit.chat.messages.model.AmityUserMention

class AmityUserMentionPagingDataAdapter :
    PagingDataAdapter<AmityChannelMember, AmityUserMentionPagingDataViewHolder>(
        ChannelMemberDiffUtil()
    ),
    AmityUserMentionViewHolder.AmityUserMentionListener {

    private var listener: AmityUserMentionViewHolder.AmityUserMentionListener? = null

    override fun onClickUserMention(userMention: AmityUserMention) {
        listener?.onClickUserMention(userMention)
    }

    override fun onBindViewHolder(holder: AmityUserMentionPagingDataViewHolder, position: Int) {
        holder.bind(getItem(position)?.getUser())
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AmityUserMentionPagingDataViewHolder {
        val itemBinding = AmityItemUserMentionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AmityUserMentionPagingDataViewHolder(itemBinding, this)
    }

    fun setListener(listener: AmityUserMentionViewHolder.AmityUserMentionListener) {
        this.listener = listener
    }
}

class ChannelMemberDiffUtil : DiffUtil.ItemCallback<AmityChannelMember>() {

    override fun areItemsTheSame(oldItem: AmityChannelMember, newItem: AmityChannelMember): Boolean {
        return oldItem.getUserId() == newItem.getUserId()
    }

    override fun areContentsTheSame(oldItem: AmityChannelMember, newItem: AmityChannelMember): Boolean {
        return oldItem.getUser()?.getDisplayName() == newItem.getUser()?.getDisplayName()
                && oldItem.getUser()?.getAvatar()?.getUrl() == newItem.getUser()?.getAvatar()?.getUrl()
                && oldItem.getUser()?.getAvatarCustomUrl() == newItem.getUser()?.getAvatarCustomUrl()
                && oldItem.getUser()?.isGlobalBan() == newItem.getUser()?.isGlobalBan()
    }
}