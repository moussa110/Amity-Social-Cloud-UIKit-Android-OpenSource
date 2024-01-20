package com.amity.socialcloud.uikit.community.newsfeed.viewmodel

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.community.newsfeed.adapter.AmityPostListAdapter
import com.amity.socialcloud.uikit.community.newsfeed.events.*
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityCommunityClickListener
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityUserClickListener
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostContentItem
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostFooterItem
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostHeaderItem
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostItem
import com.amity.socialcloud.uikit.community.newsfeed.viewcontroller.getReactionByName
import com.amity.socialcloud.uikit.community.utils.getSharedPostId
import com.amity.socialcloud.uikit.feed.settings.AmityDefaultPostViewHolders
import com.amity.socialcloud.uikit.feed.settings.AmityPostShareClickListener
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*

abstract class AmityFeedViewModel : ViewModel(), UserViewModel, PostViewModel, CommentViewModel,
	PermissionViewModel {
	lateinit var userClickListener: AmityUserClickListener
	lateinit var communityClickListener: AmityCommunityClickListener
	lateinit var postShareClickListener: AmityPostShareClickListener
	internal var feedLoadStatePublisher = PublishSubject.create<AmityFeedLoadStateEvent>()
	internal var feedRefreshEvents = Flowable.never<AmityFeedRefreshEvent>()
	private val userClickPublisher = PublishSubject.create<AmityUser>()
	private val communityClickPublisher = PublishSubject.create<AmityCommunity>()
	private val postEngagementClickPublisher = PublishSubject.create<PostEngagementClickEvent>()
	private val postContentClickPublisher = PublishSubject.create<PostContentClickEvent>()
	private val postOptionClickPublisher = PublishSubject.create<PostOptionClickEvent>()
	private val postReviewClickPublisher = PublishSubject.create<PostReviewClickEvent>()
	private val pollVoteClickPublisher = PublishSubject.create<PollVoteClickEvent>()
	private val commentEngagementClickPublisher =
		PublishSubject.create<CommentEngagementClickEvent>()
	private val commentContentClickPublisher = PublishSubject.create<CommentContentClickEvent>()
	private val commentOptionClickPublisher = PublishSubject.create<CommentOptionClickEvent>()
	private val reactionCountClickPublisher = PublishSubject.create<ReactionCountClickEvent>()

	val feedDisposable = UUID.randomUUID().toString()

	val postReactionEventMap = hashMapOf<String, PostEngagementClickEvent.Reaction>()
	val commentReactionEventMap: HashMap<String, CommentEngagementClickEvent.Reaction> = hashMapOf()

	abstract fun getFeed(onPageLoaded: (posts: PagingData<AmityBasePostItem>) -> Unit): Completable?

	fun createFeedAdapter(): AmityPostListAdapter {
		return AmityPostListAdapter(userClickPublisher,
			communityClickPublisher,
			postEngagementClickPublisher,
			postContentClickPublisher,
			postOptionClickPublisher,
			postReviewClickPublisher,
			pollVoteClickPublisher,
			commentEngagementClickPublisher,
			commentContentClickPublisher,
			commentOptionClickPublisher,
			reactionCountClickPublisher)
	}

	private val sharedPostsMap = mutableMapOf<String, AmityBasePostItem>()

	internal fun createPostItem(post: AmityPost,
	                            isFromSharedPost: Boolean = false): AmityBasePostItem {
		val basePost = AmityBasePostItem(post,
			createPostHeaderItems(post),
			createPostContentItems(post),
			createPostFooterItems(post))
		if (!isFromSharedPost) {
			post.getSharedPostId()?.let { sharedPostId ->
				if (sharedPostsMap.contains(sharedPostId)) {
					sharedPostsMap[sharedPostId].let {
						basePost.sharedPost = it
					}
				} else getSharedPostData(basePost)
			}
		}
		return basePost
	}

	private fun getSharedPostData(basePost: AmityBasePostItem) {
		if (sharedPostsMap.contains(basePost.post.getSharedPostId())) return
		AmitySocialClient.newPostRepository().getPost(basePost.post.getSharedPostId()!!)
			.map { createPostItem(it, true) }.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread()).doOnNext { sharedPost ->
				if (sharedPostsMap.contains(basePost.post.getSharedPostId())) return@doOnNext
				sharedPostsMap[basePost.post.getSharedPostId()!!] = sharedPost
				basePost.sharedPost = sharedPost
				basePost.sharedUpdatedListener?.invoke(sharedPost)
			}.subscribe()
	}

	open fun createPostHeaderItems(post: AmityPost): List<AmityBasePostHeaderItem> {
		val showHeader = AmitySocialUISettings.getViewHolder(getDataType(post)).enableHeader()
		return if (showHeader) {
			listOf(AmityBasePostHeaderItem(post = post,
				showTarget = false,
				showOptions = shouldShowPostOptions(post)))
		} else {
			emptyList()
		}
	}

	open fun createPostContentItems(post: AmityPost): List<AmityBasePostContentItem> {
		return listOf(AmityBasePostContentItem(post))
	}

	open fun createPostFooterItems(post: AmityPost): List<AmityBasePostFooterItem> {
		val footerItems = mutableListOf<AmityBasePostFooterItem>()
		val showFooter = AmitySocialUISettings.getViewHolder(getDataType(post)).enableFooter()
		if (showFooter) {
			val isReadOnly = isPostReadOnly(post)
			val engagement = AmityBasePostFooterItem.POST_ENGAGEMENT(post, isReadOnly)
			footerItems.add(engagement)
			if (post.getLatestComments().isNotEmpty()) {
				val previews = AmityBasePostFooterItem.COMMENT_PREVIEW(post, isReadOnly)
				footerItems.add(previews)
			}
		}
		return footerItems
	}

	internal fun getDataType(post: AmityPost): String {
		return if (!post.getChildren().isNullOrEmpty()) {
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

	fun getUserClickEvents(onReceivedEvent: (AmityUser) -> Unit): Completable {
		return userClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getCommunityClickEvents(onReceivedEvent: (AmityCommunity) -> Unit): Completable {
		return communityClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.ignoreElements()
	}

	fun getPostEngagementClickEvents(onReceivedEvent: (PostEngagementClickEvent) -> Unit): Completable {
		return postEngagementClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getReactionCountClickEvents(onReceivedEvent: (ReactionCountClickEvent) -> Unit): Completable {
		return reactionCountClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getPostContentClickEvents(onReceivedEvent: (PostContentClickEvent) -> Unit): Completable {
		return postContentClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getPostOptionClickEvents(onReceivedEvent: (PostOptionClickEvent) -> Unit): Completable {
		return postOptionClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getPostReviewClickEvents(onReceivedEvent: (PostReviewClickEvent) -> Unit): Completable {
		return postReviewClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getPollVoteClickEvents(onReceivedEvent: (PollVoteClickEvent) -> Completable): Completable {
		return pollVoteClickPublisher.toFlowable(BackpressureStrategy.BUFFER).flatMapCompletable {
			onReceivedEvent.invoke(it)
		}.doOnError {

		}
	}

	fun getCommentEngagementClickEvents(onReceivedEvent: (CommentEngagementClickEvent) -> Unit): Completable {
		return commentEngagementClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getCommentContentClickEvents(onReceivedEvent: (CommentContentClickEvent) -> Unit): Completable {
		return commentContentClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getCommentOptionClickEvents(onReceivedEvent: (CommentOptionClickEvent) -> Unit): Completable {
		return commentOptionClickPublisher.toFlowable(BackpressureStrategy.BUFFER)
			.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun getRefreshEvents(onReceivedEvent: (AmityFeedRefreshEvent) -> Unit): Completable {
		return feedRefreshEvents.subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread()).doOnNext {
				onReceivedEvent.invoke(it)
			}.doOnError {

			}.ignoreElements()
	}

	fun sendPendingReactions() {
		sendPostReactionRequests()
		sendCommentReactionRequests()
	}

	private fun sendPostReactionRequests() {
		val reactionEvents = postReactionEventMap.values
		reactionEvents.forEach {
			val isAdding = it.isAdding
			val isReactedByMe = it.post.getMyReactions().contains(it.reaction.reactName)
			if (isAdding && !isReactedByMe) {
				it.post.getMyReactions().forEach { react ->
					removePostReaction(post = it.post, getReactionByName(react)!!).subscribe()
				}
				addPostReaction(post = it.post, it.reaction).subscribe()

			} else if (!isAdding && isReactedByMe) {
				removePostReaction(post = it.post, it.reaction).subscribe()
			}
		}
		postReactionEventMap.clear()
	}

	private fun sendCommentReactionRequests() {
		val reactionEvents = commentReactionEventMap.values
		reactionEvents.forEach {
			val isAdding = it.isAdding
			val isReactedByMe = it.comment.getMyReactions().contains(it.reactions.reactName)
			if (isAdding && !isReactedByMe) {
				it.comment.getMyReactions().forEach { react ->
					removeCommentReaction(comment = it.comment,
						getReactionByName(react)!!).subscribe()
				}
				addCommentReaction(comment = it.comment, it.reactions).subscribe()
			} else if (!isAdding && isReactedByMe) {
				removeCommentReaction(comment = it.comment, it.reactions).subscribe()
			}
		}
		commentReactionEventMap.clear()
	}
}