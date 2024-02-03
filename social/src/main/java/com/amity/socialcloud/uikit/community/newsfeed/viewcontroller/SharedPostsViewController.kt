package com.amity.socialcloud.uikit.community.newsfeed.viewcontroller

import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.model.social.feed.AmityFeedType
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostContentItem
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostHeaderItem
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem
import com.amity.socialcloud.uikit.feed.settings.AmityDefaultPostViewHolders
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class SharedPostsViewController(private val postId: String,
                                private var listener: ((AmityBasePostItem?) -> Unit)? = null) {

	init {
		getSharedPostData()
	}

	private var disposable: Disposable? = null
	private fun getSharedPostData() {
		disposable =
			AmitySocialClient.newPostRepository().getPost(postId).map { createPostItem(it) }
				.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.doOnNext { sharedPost ->
					sendSharedPostResult(sharedPost)
				}.doOnError {
					sendSharedPostResult(null)
				}.subscribe()
	}

	private fun sendSharedPostResult(sharedPost: AmityBasePostItem?) {
		if (sharedPost != null && sharedPost.post.isDeleted()) listener?.invoke(null)
		else listener?.invoke(sharedPost)
		disposable?.dispose()
		listener = null
		disposable = null
	}

	private fun createPostItem(post: AmityPost): AmityBasePostItem {
		return AmityBasePostItem(post, createPostHeaderItems(post), createPostContentItems(post))
	}

	private fun createPostHeaderItems(post: AmityPost): List<AmityBasePostHeaderItem> {
		val showHeader = AmitySocialUISettings.getViewHolder(getDataType(post)).enableHeader()
		return if (showHeader) {
			listOf(AmityBasePostHeaderItem(post = post,
				showTarget = false,
				showOptions = shouldShowPostOptions(post)))
		} else {
			emptyList()
		}
	}

	private fun getDataType(post: AmityPost): String {
		return if (post.getChildren().isNotEmpty()) {
			when (post.getChildren().first().getData()) {
				is AmityPost.Data.IMAGE -> {
					AmityDefaultPostViewHolders.imageViewHolder.getDataType()
				}

				is AmityPost.Data.FILE -> {
					AmityDefaultPostViewHolders.fileViewHolder.getDataType()
				}

				is AmityPost.Data.VIDEO -> {
					AmityDefaultPostViewHolders.videoViewHolder.getDataType()
				}

				is AmityPost.Data.POLL -> {
					AmityDefaultPostViewHolders.pollViewHolder.getDataType()
				}

				is AmityPost.Data.LIVE_STREAM -> {
					AmityDefaultPostViewHolders.livestreamViewHolder.getDataType()
				}

				else -> {
					AmityDefaultPostViewHolders.textViewHolder.getDataType()
				}
			}
		} else {
			when (post.getData()) {
				is AmityPost.Data.TEXT -> {
					AmityDefaultPostViewHolders.textViewHolder.getDataType()
				}

				is AmityPost.Data.CUSTOM -> {
					(post.getData() as AmityPost.Data.CUSTOM).getDataType()
				}

				else -> {
					AmityDefaultPostViewHolders.unknownViewHolder.getDataType()
				}
			}
		}
	}


	private fun createPostContentItems(post: AmityPost): List<AmityBasePostContentItem> {
		return listOf(AmityBasePostContentItem(post))
	}

	private fun shouldShowPostOptions(post: AmityPost): Boolean {
		return (post.getFeedType() == AmityFeedType.PUBLISHED && !isPostReadOnly(post)) || (post.getFeedType() == AmityFeedType.REVIEWING && post.getCreatorId() == AmityCoreClient.getUserId())
	}

	private fun isPostReadOnly(post: AmityPost): Boolean {
		val target = post.getTarget()
		if (target is AmityPost.Target.COMMUNITY) {
			val community = target.getCommunity()
			return community?.isJoined() == false
		}
		return false
	}

}