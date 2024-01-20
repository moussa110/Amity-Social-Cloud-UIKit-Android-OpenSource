package com.amity.socialcloud.uikit.community.newsfeed.adapter.sharedposts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.common.toDp
import com.amity.socialcloud.uikit.common.common.toPx
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityItemBaseSharedPostBinding
import com.amity.socialcloud.uikit.community.newsfeed.events.PollVoteClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostContentClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem
import com.amity.socialcloud.uikit.community.utils.getSharedPostId
import io.reactivex.rxjava3.subjects.PublishSubject

class AmitySharedPostListAdapter(private val userClickPublisher: PublishSubject<AmityUser>,
                                 private val communityClickPublisher: PublishSubject<AmityCommunity>,
                                 private val postContentClickPublisher: PublishSubject<PostContentClickEvent>,
                                 private val pollVoteClickPublisher: PublishSubject<PollVoteClickEvent>,
) : RecyclerView.Adapter<AmitySharedPostViewHolder>() {
	private val list: ArrayList<AmityBasePostItem?> = arrayListOf()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmitySharedPostViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.amity_item_base_shared_post, parent, false)

		return AmitySharedPostViewHolder(view,
			userClickPublisher,
			communityClickPublisher,
			postContentClickPublisher,
			pollVoteClickPublisher)
	}

	override fun getItemCount(): Int {
		return list.size
	}

	override fun onBindViewHolder(holder: AmitySharedPostViewHolder, position: Int) {
		val item = list[position]
		val binding = AmityItemBaseSharedPostBinding.bind(holder.itemView)
		binding.apply {
			if (item == null){
				parentView.updateLayoutParams {
					height = 300.toPx()
				}
				shimmerView.apply {
					root.isVisible = true
					shimmerLayout.startShimmer()
				}
			}else{
				parentView.updateLayoutParams {
					height = WRAP_CONTENT
				}
				shimmerView.apply {
					root.isVisible = false
					shimmerLayout.stopShimmer()
				}

				sharedMultiableTimeCard.let {
					it.visibility = if (item.post.getSharedPostId() != null) View.VISIBLE else View.INVISIBLE
				}
				holder.bind(item, position)
			}
		}

	}

	fun submitList(newList: List<AmityBasePostItem?>) {
		setItems(newList, DiffCallback(list, newList))
	}

	fun setItems(listItems: List<AmityBasePostItem?>, diffCallBack: DiffUtil.Callback) {
		val diffResult = DiffUtil.calculateDiff(diffCallBack)
		list.clear()
		list.addAll(listItems)
		diffResult.dispatchUpdatesTo(this)
	}

	class DiffCallback(
		private val oldList: List<AmityBasePostItem?>,
		private val newList: List<AmityBasePostItem?>
	) : DiffUtil.Callback() {

		override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			val oldItem = oldList[oldItemPosition]
			val newItem = newList[newItemPosition]
			return oldItem?.post?.getPostId() == newItem?.post?.getPostId()

		}

		override fun getOldListSize(): Int = oldList.size

		override fun getNewListSize(): Int = newList.size

		override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
			val oldItem = oldList[oldItemPosition]
			val newItem = newList[newItemPosition]
			return oldItem?.post?.getPostId() == newItem?.post?.getPostId() &&
					oldItem?.post?.getEditedAt() == newItem?.post?.getEditedAt() &&
					oldItem?.post?.isDeleted() == newItem?.post?.isDeleted()
		}
	}
}