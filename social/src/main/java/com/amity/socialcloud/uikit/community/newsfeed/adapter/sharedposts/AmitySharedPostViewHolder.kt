package com.amity.socialcloud.uikit.community.newsfeed.adapter.sharedposts

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.base.AmityBaseRecyclerViewAdapter
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityItemBaseSharedPostBinding
import com.amity.socialcloud.uikit.community.newsfeed.adapter.AmityPostContentAdapter
import com.amity.socialcloud.uikit.community.newsfeed.events.PollVoteClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostContentClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem
import com.amity.socialcloud.uikit.community.newsfeed.view.AmityBasePostView
import com.amity.socialcloud.uikit.community.newsfeed.viewcontroller.SharedPostsViewController
import com.amity.socialcloud.uikit.community.utils.getSharedPostId
import io.reactivex.rxjava3.subjects.PublishSubject

class AmitySharedPostViewHolder(itemView: View,
                                private val userClickPublisher: PublishSubject<AmityUser>,
                                private val communityClickPublisher: PublishSubject<AmityCommunity>,
                                private val postContentClickPublisher: PublishSubject<PostContentClickEvent>,
                                private val pollVoteClickPublisher: PublishSubject<PollVoteClickEvent>) :
	RecyclerView.ViewHolder(itemView), AmityBaseRecyclerViewAdapter.IBinder<AmityBasePostItem> {

	private val binding = AmityItemBaseSharedPostBinding.bind(itemView)
	private val contentAdapter = AmityPostContentAdapter(postContentClickPublisher, pollVoteClickPublisher)
	private var concatAdapter: ConcatAdapter? = null
	private var sharedHeaderAdapter = AmityPostSharedHeaderAdapter(userClickPublisher, communityClickPublisher)


	override fun bind(data: AmityBasePostItem?, position: Int) {
		if (data == null) {
			return
		}
		val basePostView = itemView.findViewById<AmityBasePostView>(R.id.basePostView)
		if (concatAdapter == null) {
//            To check if special config needed
//            val config = ConcatAdapter.Config.Builder().apply {
//                this.setIsolateViewTypes(false)
//            }.build()

			concatAdapter = ConcatAdapter(sharedHeaderAdapter, contentAdapter)
			basePostView.layoutManager = LinearLayoutManager(this.itemView.context)
			basePostView.adapter = concatAdapter
		}
		sharedHeaderAdapter.submitList(data.headerItems)
		contentAdapter.submitList(data.contentItems)
	}

	private fun showShimmerView() {
		hidePostDeletedView()
		binding.shimmerView.apply {
			root.isVisible = true
			shimmerLayout.startShimmer()
		}
	}

	private fun hideShimmerView() {
		binding.shimmerView.apply {
			root.isVisible = false
			shimmerLayout.stopShimmer()
		}
	}

	fun loadSharePost(sharedPostId: String,position: Int,listener:(AmityBasePostItem)->Unit) {
		showShimmerView()
		SharedPostsViewController(sharedPostId){
			if(it == null) showPostDeletedView()
			else {
				listener(it)
				loadItem(it, position)
			}
		}
	}

	private fun showPostDeletedView(){
		hideShimmerView()
		binding.errorView.apply {
			root.isVisible = true
		}
	}

	private fun hidePostDeletedView(){
		binding.errorView.apply {
			root.isVisible = false
		}
	}

	fun loadItem(item: AmityBasePostItem,
	                     position: Int) {
		binding.apply {
			bind(item, position)
			handleCardsView(item)
			hideShimmerView()
			hidePostDeletedView()
		}
	}

	private fun handleCardsView(item: AmityBasePostItem) {
		binding.apply {
			sharedMultiableTimeCard.let {
				it.visibility = if (item.post.getSharedPostId() != null) View.VISIBLE else View.INVISIBLE
				it.setOnClickListener {
					postContentClickPublisher.onNext(PostContentClickEvent.Text(item.post))
				}
			}
			topCardView.setOnClickListener {
				postContentClickPublisher.onNext(PostContentClickEvent.Text(item.post))
			}
		}
	}


}