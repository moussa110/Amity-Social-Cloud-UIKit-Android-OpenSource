package com.amity.socialcloud.uikit.community.newsfeed.adapter

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.base.AmityBaseRecyclerViewAdapter
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.newsfeed.adapter.sharedposts.AmitySharedPostListAdapter
import com.amity.socialcloud.uikit.community.newsfeed.events.CommentContentClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.CommentEngagementClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.CommentOptionClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PollVoteClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostContentClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostEngagementClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostOptionClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostReviewClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.ReactionCountClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem
import com.amity.socialcloud.uikit.community.newsfeed.view.AmityBasePostView
import io.reactivex.rxjava3.subjects.PublishSubject

class AmityPostViewHolder(itemView: View,
                          private val userClickPublisher: PublishSubject<AmityUser>,
                          private val communityClickPublisher: PublishSubject<AmityCommunity>,
                          private val postEngagementClickPublisher: PublishSubject<PostEngagementClickEvent>,
                          private val postContentClickPublisher: PublishSubject<PostContentClickEvent>,
                          private val postOptionClickPublisher: PublishSubject<PostOptionClickEvent>,
                          private val postReviewClickPublisher: PublishSubject<PostReviewClickEvent>,
                          private val pollVoteClickPublisher: PublishSubject<PollVoteClickEvent>,
                          private val commentEngagementClickPublisher: PublishSubject<CommentEngagementClickEvent>,
                          private val commentContentClickPublisher: PublishSubject<CommentContentClickEvent>,
                          private val commentOptionClickPublisher: PublishSubject<CommentOptionClickEvent>,
                          private val reactionCountClickPublisher: PublishSubject<ReactionCountClickEvent>,
                          var sharedViewListener: ((Pair<View, View>) -> Unit)? = null) :
	RecyclerView.ViewHolder(itemView), AmityBaseRecyclerViewAdapter.IBinder<AmityBasePostItem> {


	private val headerAdapter = AmityPostHeaderAdapter(userClickPublisher, communityClickPublisher, postOptionClickPublisher)
	private val contentAdapter = AmityPostContentAdapter(postContentClickPublisher, pollVoteClickPublisher)

	/*
	ConcatAdapter with headerAdapter contentAdapter and footerAdapter
	has an issue with re-rendering footer's post engagement on re-binding a viewHolder.

	Adding a dummyAdapter is the hotfix.

	To remove the dummyAdapter after finding the proper solution
 */

	private val dummyAdapter = AmityPostDummyAdapter()
	private val footerAdapter = AmityPostFooterAdapter(userClickPublisher,
		postEngagementClickPublisher,
		postReviewClickPublisher,
		commentContentClickPublisher,
		commentEngagementClickPublisher,
		commentOptionClickPublisher,
		reactionCountClickPublisher)


	private var concatAdapter: ConcatAdapter? = null
	private var sharedPostAdapter = AmitySharedPostListAdapter(
		userClickPublisher,
		communityClickPublisher,
		postContentClickPublisher,
		pollVoteClickPublisher)
	//private val sharedContentAdapter = AmityShared(postContentClickPublisher, pollVoteClickPublisher)


	override fun bind(data: AmityBasePostItem?, position: Int) {
		if (data == null) {
			return
		}
		initBasePostRv(data)
		initAdapters(data)
	}

	private fun initAdapters(data: AmityBasePostItem) {
		// get view to save it in map to share as bitmap
		var headerView: View? = null
		var contentView: View? = null
		headerAdapter.submitList(data.headerItems)
		headerAdapter.sharedViewListener = {
			headerView = it
			contentView?.let {
				sharedViewListener?.invoke(Pair(headerView!!, contentView!!))
			}
		}
		contentAdapter.submitList(data.contentItems)
		contentAdapter.sharedViewListener = {
			contentView = it
			headerView?.let {
				sharedViewListener?.invoke(Pair(headerView!!, contentView!!))
			}
		}
		dummyAdapter.setItems(listOf())
		footerAdapter.submitList(data.footerItems, data.sharedPost != null)

		data.sharedPost.let {
			if (it == null) {
				sharedPostAdapter.submitList(listOf())
			} else {
				sharedPostAdapter.submitList(listOf(it))
			}
		}
	}

	private fun initBasePostRv(data: AmityBasePostItem) {
		val basePostView = itemView.findViewById<AmityBasePostView>(R.id.basePostView)
//            To check if special config needed
//            val config = ConcatAdapter.Config.Builder().apply {
//                this.setIsolateViewTypes(false)
//            }.build()

		concatAdapter = if (data.sharedPost == null) ConcatAdapter(headerAdapter, contentAdapter, dummyAdapter, footerAdapter)
		else ConcatAdapter(headerAdapter, contentAdapter, sharedPostAdapter, footerAdapter)
		basePostView.layoutManager = LinearLayoutManager(this.itemView.context)
		basePostView.adapter = concatAdapter
	}
}