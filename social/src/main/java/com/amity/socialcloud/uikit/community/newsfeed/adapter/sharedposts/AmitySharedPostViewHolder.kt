package com.amity.socialcloud.uikit.community.newsfeed.adapter.sharedposts

import android.view.View
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.base.AmityBaseRecyclerViewAdapter
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.newsfeed.adapter.AmityPostContentAdapter
import com.amity.socialcloud.uikit.community.newsfeed.adapter.AmityPostDummyAdapter
import com.amity.socialcloud.uikit.community.newsfeed.adapter.AmityPostFooterAdapter
import com.amity.socialcloud.uikit.community.newsfeed.adapter.AmityPostHeaderAdapter
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

class AmitySharedPostViewHolder(itemView: View,
                                private val userClickPublisher: PublishSubject<AmityUser>,
                                private val communityClickPublisher: PublishSubject<AmityCommunity>,
                                private val postContentClickPublisher: PublishSubject<PostContentClickEvent>,
                                private val pollVoteClickPublisher: PublishSubject<PollVoteClickEvent>) :
	RecyclerView.ViewHolder(itemView), AmityBaseRecyclerViewAdapter.IBinder<AmityBasePostItem> {


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


}