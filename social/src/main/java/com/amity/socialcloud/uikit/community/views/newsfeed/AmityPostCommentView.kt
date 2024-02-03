package com.amity.socialcloud.uikit.community.views.newsfeed

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionMetadataGetter
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionee
import com.amity.socialcloud.sdk.model.core.file.AmityImage
import com.amity.socialcloud.sdk.model.core.role.AmityRoles
import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.comment.AmityComment
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.common.readableFeedPostTime
import com.amity.socialcloud.uikit.common.common.readableNumber
import com.amity.socialcloud.uikit.common.utils.AmityConstants
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityItemCommentNewsFeedBinding
import com.amity.socialcloud.uikit.community.newsfeed.events.CommentContentClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.CommentEngagementClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.CommentOptionClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.ReactionCountClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityMentionClickableSpan
import com.amity.socialcloud.uikit.community.newsfeed.popup.showReactionPopup
import com.amity.socialcloud.uikit.common.utils.ReactionViewController
import com.amity.socialcloud.uikit.common.utils.Reactions
import com.amity.socialcloud.uikit.common.utils.getDrawable20
import com.amity.socialcloud.uikit.common.utils.getReactionByName
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber

class AmityPostCommentView : ConstraintLayout {

	private lateinit var binding: AmityItemCommentNewsFeedBinding
	private var userClickPublisher = PublishSubject.create<AmityUser>()
	private var commentContentClickPublisher = PublishSubject.create<CommentContentClickEvent>()
	private var commentEngagementClickPublisher = PublishSubject.create<CommentEngagementClickEvent>()
	private var commentOptionClickPublisher = PublishSubject.create<CommentOptionClickEvent>()
	private var reactionCountClickPublisher = PublishSubject.create<ReactionCountClickEvent>()
	private lateinit var mutableReactionsMap: MutableMap<String, Int>
	private var myReaction: Reactions? = null

	constructor(context: Context) : super(context) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init()
	}

	constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,
		attrs,
		defStyleAttr) {
		init()
	}

	private fun init() {
		val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
		binding =
			DataBindingUtil.inflate(inflater, R.layout.amity_item_comment_news_feed, this, true)
	}

	fun setComment(comment: AmityComment, post: AmityPost? = null, isReadOnly: Boolean? = false) {
		binding.avatarUrl = comment.getCreator()?.getAvatar()?.getUrl(AmityImage.Size.SMALL)
		binding.tvUserName.text =
			comment.getCreator()?.getDisplayName() ?: context.getString(R.string.amity_anonymous)
		binding.tvCommentTime.text = comment.getCreatedAt().millis.readableFeedPostTime(context)
		binding.edited = comment.isEdited()
		binding.isReplyComment = !comment.getParentId().isNullOrEmpty()

		val banIcon = if (comment.getCreator()?.isGlobalBan() == true) {
			ContextCompat.getDrawable(context, R.drawable.amity_ic_ban)
		} else {
			null
		}
		binding.tvUserName.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
			null,
			banIcon,
			null)
		mutableReactionsMap = comment.getReactionMap().toMutableMap()
		comment.getMyReactions().let {
			myReaction = if (it.isEmpty()) null else getReactionByName(it[it.lastIndex])
		}
		setText(comment)
		renderModBadge(comment)
		val isReactedByMe = comment.getMyReactions().isNotEmpty()
		setUpReactView(isReactedByMe, comment.getReactionCount(), comment)
		setReadOnlyMode(isReadOnly!!)
		setViewListeners(comment, post)
	}

	private fun renderModBadge(comment: AmityComment) {
		val roles =
			(comment.getTarget() as? AmityComment.Target.COMMUNITY)?.getCreatorMember()?.getRoles()
		if (isCommunityModerator(roles)) {
			binding.tvCommentBy.visibility = View.VISIBLE
		} else {
			binding.tvCommentBy.visibility = View.GONE
		}
	}

	private fun isCommunityModerator(roles: AmityRoles?): Boolean {
		return roles?.any {
			it == AmityConstants.MODERATOR_ROLE || it == AmityConstants.COMMUNITY_MODERATOR_ROLE
		} ?: false
	}

	private fun setViewListeners(comment: AmityComment, post: AmityPost?) {

		binding.ivAvatar.setOnClickListener {
			comment.getCreator()?.let {
				userClickPublisher.onNext(it)
			}
		}
		binding.tvUserName.setOnClickListener {
			comment.getCreator()?.let {
				userClickPublisher.onNext(it)
			}
		}

		binding.tvPostComment.setOnClickListener {
			if (binding.tvPostComment.isReadMoreClicked()) {
				binding.tvPostComment.showCompleteText()
			} else {
				commentContentClickPublisher.onNext(CommentContentClickEvent.Text(comment, post))
			}
		}

		binding.layoutCommentItem.setOnClickListener {
			commentContentClickPublisher.onNext(CommentContentClickEvent.Text(comment, post))
		}

		binding.reply.setOnClickListener {
			commentEngagementClickPublisher.onNext(CommentEngagementClickEvent.Reply(comment, post))
		}

		binding.btnCommentAction.setOnClickListener {
			commentOptionClickPublisher.onNext(CommentOptionClickEvent(comment))
		}

		binding.tvNumberOfReactions.setOnClickListener {
			reactionCountClickPublisher.onNext(ReactionCountClickEvent.Comment(comment))
		}
	}

	private fun setUpReactView(isReactedByMe: Boolean,
	                           reactionCount: Int,
	                           comment: AmityComment,
	                           react: Reactions? = null) {
		ReactionViewController().refreshReactView(isReactedByMe, comment.getMyReactions(), react,binding.cbLike)
		setReactClickListener(isReactedByMe, reactionCount, comment)
	}



	private fun setReactClickListener(isReactedByMe: Boolean,
	                                  reactionCount: Int,
	                                  comment: AmityComment) {
		val convertedValue = !isReactedByMe
		binding.cbLike.setOnClickListener {

			if (isReactedByMe) {
				myReaction ?: return@setOnClickListener
				onSelectOrUnSelectReact(convertedValue, reactionCount, comment, myReaction!!)
			} else {
				showReactionPopup(it,true) { react ->
					onSelectOrUnSelectReact(convertedValue, reactionCount, comment, react)
				}
			}
		}
	}

	private fun onSelectOrUnSelectReact(convertedValue: Boolean,
	                                    reactionCount: Int,
	                                    comment: AmityComment,
	                                    react: Reactions) {

		var displayReactionCount = reactionCount + 1
		var reactionEvent = CommentEngagementClickEvent.Reaction(comment, true,react)
		myReaction = react
		if (!convertedValue) {
			myReaction = null
			displayReactionCount = Math.max(reactionCount - 1, 0)
			reactionEvent = CommentEngagementClickEvent.Reaction(comment, false,react)
		}
		addOrRemoveReact(convertedValue, react)
		commentEngagementClickPublisher.onNext(reactionEvent)
		setNumberOfReactions(displayReactionCount)
		setUpReactView(convertedValue, displayReactionCount, comment,react)
	}

	private fun addOrRemoveReact(isToAdd: Boolean, react: Reactions) {
		if (isToAdd) {
			if (mutableReactionsMap.containsKey(react.reactName)) {
				mutableReactionsMap[react.reactName]?.let {
					mutableReactionsMap[react.reactName] = (it + 1)
				}
			} else {
				mutableReactionsMap[react.reactName] = 1
			}
		} else {
			mutableReactionsMap[react.reactName] =
				Math.max((mutableReactionsMap[react.reactName] ?: 1) - 1, 0)
		}
	}


	fun setEventPublishers(userClickPublisher: PublishSubject<AmityUser>,
	                       commentContentClickPublisher: PublishSubject<CommentContentClickEvent>,
	                       commentEngagementClickPublisher: PublishSubject<CommentEngagementClickEvent>,
	                       commentOptionClickPublisher: PublishSubject<CommentOptionClickEvent>,
	                       reactionCountClickPublisher: PublishSubject<ReactionCountClickEvent>) {
		this.userClickPublisher = userClickPublisher
		this.commentContentClickPublisher = commentContentClickPublisher
		this.commentEngagementClickPublisher = commentEngagementClickPublisher
		this.commentOptionClickPublisher = commentOptionClickPublisher
		this.reactionCountClickPublisher = reactionCountClickPublisher
	}

	private fun handleBottomSpace() {
		binding.addBottomSpace = binding.readOnly != null && binding.readOnly!!
	}

	private fun setReadOnlyMode(isReadOnly: Boolean) {
		binding.readOnly = isReadOnly
		handleBottomSpace()
	}

	private fun setText(comment: AmityComment) {
		binding.tvPostComment.text = getHighlightTextUserMentions(comment)
		setNumberOfReactions(comment.getReactionCount())
	}

	private fun setNumberOfReactions(reactionCount: Int) {
		binding.tvNumberOfReactionsView.visibility = if (reactionCount > 0) View.VISIBLE else {
			View.GONE
		}
		if (reactionCount <= 0) return
		binding.tvNumberOfReactions.text = reactionCount.readableNumber()
		mutableReactionsMap.toList().filter { it.second != 0 }.sortedBy { it.second }
			.map { it.first }.apply {
				showTopThreeReactions(this)
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


	private fun getHighlightTextUserMentions(comment: AmityComment): SpannableString {
		val commentText = (comment.getData() as? AmityComment.Data.TEXT)?.getText() ?: ""
		val spannable = SpannableString(commentText)
		if (spannable.isNotEmpty() && comment.getMetadata() != null) {
			val mentionUserIds =
				comment.getMentionees().map { (it as? AmityMentionee.USER)?.getUserId() }
			val mentionedUsers =
				AmityMentionMetadataGetter(comment.getMetadata()!!).getMentionedUsers()
			val mentions = mentionedUsers.filter { mentionUserIds.contains(it.getUserId()) }
			mentions.forEach { mentionUserItem ->
				try {
					spannable.setSpan(AmityMentionClickableSpan(mentionUserItem.getUserId()),
						mentionUserItem.getIndex(),
						mentionUserItem.getIndex().plus(mentionUserItem.getLength()).inc(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
				} catch (exception: IndexOutOfBoundsException) {
					Timber.e("AmityPostCommentView", "Highlight text user mentions crashes")
				}
			}
		}
		return spannable
	}
}