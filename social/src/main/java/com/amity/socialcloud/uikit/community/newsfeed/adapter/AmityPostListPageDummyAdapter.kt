package com.amity.socialcloud.uikit.community.newsfeed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.amity.socialcloud.uikit.community.databinding.AmityItemPostDummyBinding
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem

class AmityPostListPageDummyAdapter() : PagingDataAdapter<AmityBasePostItem, AmityPostDummyViewHolder>(POST_COMPARATOR) {

	override fun onCreateViewHolder(
		parent: ViewGroup,
		viewType: Int
	): AmityPostDummyViewHolder {
		val itemBinding = AmityItemPostDummyBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return AmityPostDummyViewHolder(itemBinding)
	}

	override fun onBindViewHolder(holder: AmityPostDummyViewHolder, position: Int) {
		// do nothing
	}

	companion object {
		val POST_COMPARATOR = object : DiffUtil.ItemCallback<AmityBasePostItem>() {
			override fun areItemsTheSame(oldItem: AmityBasePostItem,
			                             newItem: AmityBasePostItem): Boolean {
				return oldItem.post.getPostId() == newItem.post.getPostId()
			}


			override fun areContentsTheSame(oldItem: AmityBasePostItem,
			                                newItem: AmityBasePostItem): Boolean {
				// TODO: 1/8/23 need to add more fields check
				return oldItem.post.getPostId() == newItem.post.getPostId() && oldItem.post.getEditedAt() == newItem.post.getEditedAt() && oldItem.post.isDeleted() == newItem.post.isDeleted()
			}

		}
	}
}