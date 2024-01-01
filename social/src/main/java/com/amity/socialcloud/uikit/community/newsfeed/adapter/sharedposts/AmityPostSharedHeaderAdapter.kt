package com.amity.socialcloud.uikit.community.newsfeed.adapter.sharedposts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostHeaderItem
import io.reactivex.rxjava3.subjects.PublishSubject

class AmityPostSharedHeaderAdapter(private val userClickPublisher: PublishSubject<AmityUser>,
                                   private val communityClickPublisher: PublishSubject<AmityCommunity>,
                                   var sharedViewListener: ((View) -> Unit)? = null) :
	RecyclerView.Adapter<AmityPostSharedHeaderViewHolder>() {

	val list: ArrayList<AmityBasePostHeaderItem> = arrayListOf()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmityPostSharedHeaderViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.amity_item_base_shared_post_header, parent, false)
		return AmityPostSharedHeaderViewHolder(view, userClickPublisher, communityClickPublisher)
	}

	override fun onBindViewHolder(holder: AmityPostSharedHeaderViewHolder, position: Int) {
		holder.sharedViewListener = sharedViewListener
		holder.bind(list[position])
	}

	override fun getItemViewType(position: Int): Int {
		return R.layout.amity_item_base_shared_post_header
	}

	override fun getItemCount(): Int {
		return list.size
	}

	fun submitList(newList: List<AmityBasePostHeaderItem>) {
		setItems(newList, DiffCallback(list, newList))
	}

	fun setItems(listItems: List<AmityBasePostHeaderItem>, diffCallBack: DiffUtil.Callback) {
		val diffResult = DiffUtil.calculateDiff(diffCallBack)
		list.clear()
		list.addAll(listItems)
		diffResult.dispatchUpdatesTo(this)
	}

	class DiffCallback(private val oldList: List<AmityBasePostHeaderItem>,
	                   private val newList: List<AmityBasePostHeaderItem>) : DiffUtil.Callback() {

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			return oldList[oldItemPosition].post.getPostId() == newList[newItemPosition].post.getPostId()
		}

		override fun getOldListSize(): Int = oldList.size

		override fun getNewListSize(): Int = newList.size

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			val oldItem = oldList[oldItemPosition]
			val newItem = newList[newItemPosition]
			return oldItem == newItem && oldItem.post.getCreator()
				?.getDisplayName() == newItem.post.getCreator()
				?.getDisplayName() && oldItem.post.getCreator()?.getAvatar()
				?.getUrl() == newItem.post.getCreator()?.getAvatar()?.getUrl()
		}
	}

}
