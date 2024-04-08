package com.amity.socialcloud.uikit.community.newsfeed.adapter

import android.view.View
import androidx.core.view.isVisible
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.common.readableNumber
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityItemPostFooterPostEngagementBinding
import com.amity.socialcloud.uikit.community.newsfeed.events.PostEngagementClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.ReactionCountClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.model.AmityBasePostFooterItem
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import io.reactivex.rxjava3.subjects.PublishSubject
import com.amity.socialcloud.uikit.common.reactions.showReactionPopup
import com.amity.socialcloud.uikit.common.reactions.ReactionViewController
import com.amity.socialcloud.uikit.common.reactions.Reactions
import com.amity.socialcloud.uikit.common.reactions.getDrawable20

import com.amity.socialcloud.uikit.common.reactions.getReactionByName

class AmityPostFooterPostEngagementViewHolder(
	private val binding: AmityItemPostFooterPostEngagementBinding,
	private val postEngagementClickPublisher: PublishSubject<PostEngagementClickEvent>,
	private val reactionCountClickPublisher: PublishSubject<ReactionCountClickEvent>,
	private val isHasSharedPost: Boolean,
) : AmityPostFooterViewHolder(binding.root) {

	private lateinit var mutableReactionsMap: MutableMap<String, Int>
	private var myReaction: Reactions? = null

	override fun bind(data: AmityBasePostFooterItem, position: Int) {
		binding.executePendingBindings()
		val postEngagementData = data as AmityBasePostFooterItem.POST_ENGAGEMENT
		mutableReactionsMap = data.post.getReactionMap().toMutableMap()
		postEngagementData.post.getMyReactions().let {
			myReaction = if (it.isEmpty()) null else getReactionByName(it[it.lastIndex])
		}

		setNumberOfReactions(postEngagementData.post.getReactionCount())
		val isReactedByMe = postEngagementData.post.getMyReactions().isNotEmpty()
		setUpReactView(isReactedByMe,
			postEngagementData.post.getReactionCount(),
			postEngagementData.post)
		setNumberOfComments(postEngagementData.post.getCommentCount())
		setReadOnlyMode(postEngagementData.isReadOnly)
		setShareOption(postEngagementData.post)
		setCommentListener(postEngagementData.post)
		setReactionCountListener(postEngagementData.post)
		binding.separator.visibility = if (isHasSharedPost) View.GONE else View.VISIBLE
	}

	private fun setCommentListener(post: AmityPost) {
		binding.cbComment.setOnClickListener {
			postEngagementClickPublisher.onNext(PostEngagementClickEvent.Comment(post))
		}
		binding.tvNumberOfComments.setOnClickListener {
			postEngagementClickPublisher.onNext(PostEngagementClickEvent.Comment(post))
		}
	}

	private fun setReactionCountListener(post: AmityPost) {
		binding.tvNumberOfReactionsView.setOnClickListener {
			reactionCountClickPublisher.onNext(ReactionCountClickEvent.Post(post))
		}
	}

	private fun setNumberOfComments(commentCount: Int) {
		binding.tvNumberOfComments.visibility = if (commentCount > 0) View.VISIBLE else View.GONE
		binding.tvNumberOfComments.text =
			binding.root.resources.getQuantityString(R.plurals.amity_feed_number_of_comments,
				commentCount,
				commentCount)
	}

	private fun setNumberOfReactions(reactionCount: Int) {
		binding.tvNumberOfReactionsView.visibility = if (reactionCount > 0) View.VISIBLE else {
			View.GONE
		}
		if (reactionCount<=0) return
		binding.tvNumberOfReactions.text = reactionCount.readableNumber()
		mutableReactionsMap.toList().filter { it.second != 0 }.sortedBy { it.second }.map { it.first }
			.apply {
				showTopThreeReactions(this)
			}
	}

	private fun setReactClickListener(isReactedByMe: Boolean, reactionCount: Int, post: AmityPost) {
		val convertedValue = !isReactedByMe
		binding.cbLike.setOnClickListener {
			if (isReactedByMe) {
				myReaction ?: return@setOnClickListener
				onSelectOrUnSelectReact(convertedValue, reactionCount, post, myReaction!!)
			} else {
				showReactionPopup(it,false) { react ->
					onSelectOrUnSelectReact(convertedValue, reactionCount, post, react)
				}
			}
		}
	}

	private fun onSelectOrUnSelectReact(convertedValue: Boolean,
	                                    reactionCount: Int,
	                                    post: AmityPost,
	                                    react: Reactions) {
		var displayReactionCount = reactionCount + 1
		var reactionEvent = PostEngagementClickEvent.Reaction(post, true, react)
		myReaction = react
		if (!convertedValue) {
			myReaction = null
			displayReactionCount = Math.max(reactionCount - 1, 0)
			reactionEvent = PostEngagementClickEvent.Reaction(post, false, react)
		}
		addOrRemoveReact(convertedValue,react)
		postEngagementClickPublisher.onNext(reactionEvent)
		setNumberOfReactions(displayReactionCount)
		setUpReactView(convertedValue, displayReactionCount, post, react)
	}

	private fun setUpReactView(isReactedByMe: Boolean,
	                           reactionCount: Int,
	                           post: AmityPost,
	                           react: Reactions? = null) {
		ReactionViewController().refreshReactView(isReactedByMe, post.getMyReactions(), react,binding.cbLike)
		setReactClickListener(isReactedByMe, reactionCount, post)
	}


	private fun addOrRemoveReact(isToAdd: Boolean, react: Reactions) {
		if (isToAdd){
			if (mutableReactionsMap.containsKey(react.reactName)){
				mutableReactionsMap[react.reactName]?.let {
					mutableReactionsMap[react.reactName] = (it+1)
				}
			}else{
				mutableReactionsMap[react.reactName] = 1
			}
		}else{
			mutableReactionsMap[react.reactName] = Math.max((mutableReactionsMap[react.reactName]?:1)-1, 0)
		}
	}

	private fun showTopThreeReactions(reactionsStr: List<String>) {
		binding.firstReactionIv.isVisible = false
		binding.secondReactionIv.isVisible = false
		binding.thirdReactionIv.isVisible = false
		for (i in 0 until reactionsStr.size.coerceAtMost(2)) {
			getReactionByName(reactionsStr[i])?.let {
				when (i) {
					0 -> {
						binding.firstReactionIv.apply {
							setImageDrawable(it.getDrawable20(context))
							isVisible = true
						}
					}

					1 -> {
						binding.secondReactionIv.apply {
							setImageDrawable(it.getDrawable20(context))
							isVisible = true
						}
					}

					2 -> {
						binding.thirdReactionIv.apply {
							setImageDrawable(it.getDrawable20(context))
							isVisible = true
						}
					}

					else -> {}
				}
			}
		}
	}

	private fun setReadOnlyMode(isReadOnly: Boolean) {
		binding.readOnly = isReadOnly
	}

	private fun setShareOption(post: AmityPost) {
		val target = post.getTarget()
		binding.cbShare.setOnClickListener {
			postEngagementClickPublisher.onNext(PostEngagementClickEvent.Sharing(post))
		}
		when (target) {
			is AmityPost.Target.USER -> {
				if (target.getUser()?.getUserId() == AmityCoreClient.getUserId()) {
					if (AmitySocialUISettings.postSharingSettings.myFeedPostSharingTarget.isNotEmpty()) {
						binding.cbShare.visibility = View.VISIBLE
					} else {
						binding.cbShare.visibility = View.GONE
					}
				} else {
					if (AmitySocialUISettings.postSharingSettings.userFeedPostSharingTarget.isNotEmpty()) {
						binding.cbShare.visibility = View.VISIBLE
					} else {
						binding.cbShare.visibility = View.GONE
					}
				}
			}

			is AmityPost.Target.COMMUNITY -> {
				if (target.getCommunity()?.isPublic() != false) {
					if (AmitySocialUISettings.postSharingSettings.publicCommunityPostSharingTarget.isNotEmpty()) {
						binding.cbShare.visibility = View.VISIBLE
					} else {
						binding.cbShare.visibility = View.GONE
					}
				} else {
					if (AmitySocialUISettings.postSharingSettings.privateCommunityPostSharingTarget.isNotEmpty()) {
						binding.cbShare.visibility = View.VISIBLE
					} else {
						binding.cbShare.visibility = View.GONE
					}
				}
			}

			else -> {}
		}
	}

}