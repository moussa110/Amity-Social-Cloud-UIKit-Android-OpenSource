package com.amity.socialcloud.uikit.community.newsfeed.adapter

import android.text.Spannable
import android.text.SpannableString
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionMetadataGetter
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionee
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.common.views.text.AmityExpandableTextView
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.newsfeed.events.PollVoteClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.events.PostContentClickEvent
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityHashtagClickableSpan
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityMentionClickableSpan
import io.reactivex.rxjava3.subjects.PublishSubject
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

open class AmityPostContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    internal var postContentClickPublisher = PublishSubject.create<PostContentClickEvent>()
    internal var pollVoteClickPublisher = PublishSubject.create<PollVoteClickEvent>()
    internal var showFullContent = false
    private var clickableSpanClicked = false

    open fun bind(post: AmityPost) {

    }

    internal fun setPostText(data: AmityPost, showCompleteText: Boolean) {
        val tvPost = itemView.findViewById<AmityExpandableTextView>(R.id.tvFeed)
        setPostTextToTextView(tvPost, data, showCompleteText)
    }

    internal fun setPostTextToTextView(tvPost: AmityExpandableTextView, data: AmityPost, showCompleteText: Boolean) {
        tvPost.text = getHighlightTextUserMentionsAndHashtags(data)
        //tvPost.text = addClickablePartForHashtags(tvPost.text.toString())
        if (showCompleteText) {
            tvPost.showCompleteText()
            tvPost.tag = tvPost.getVisibleLineCount()
        }

        if (tvPost.tag != tvPost.getVisibleLineCount()) {
            tvPost.forceLayout()
            tvPost.tag = tvPost.getVisibleLineCount()
        }

        tvPost.isVisible = tvPost.text.isNotEmpty()

        tvPost.setExpandOnlyOnReadMoreClick(true)

        itemView.setOnClickListener {
            postContentClickPublisher.onNext(PostContentClickEvent.Text(data))
           /* if (tvPost.isReadMoreClicked()) {
                tvPost.showCompleteText()
                tvPost.tag = tvPost.getVisibleLineCount()
            } else {
                postContentClickPublisher.onNext(PostContentClickEvent.Text(data))
            }*/
        }

        tvPost.setOnClickListener {
            if (!clickableSpanClicked) postContentClickPublisher.onNext(PostContentClickEvent.Text(data))
            clickableSpanClicked = false
            /* if (tvPost.isReadMoreClicked()) {
				 tvPost.showCompleteText()
				 tvPost.tag = tvPost.getVisibleLineCount()
			 } else {
				 postContentClickPublisher.onNext(PostContentClickEvent.Text(data))
			 }*/
        }
    }


    private fun getHighlightTextUserMentionsAndHashtags(post: AmityPost): SpannableString {
        val postText = (post.getData() as? AmityPost.Data.TEXT)?.getText() ?: ""
        val spannable = SpannableString("$postText ")
        setMentionsClickableSpannable(spannable,post)
        setHashtagClickable(spannable,postText)
        return spannable
    }

    private fun setHashtagClickable(spannable: SpannableString, postText: String) {
        val pattern: Pattern = Pattern.compile("#\\w+")
        val matcher: Matcher = pattern.matcher(postText)
        while (matcher.find()) {
            val hashtag: String = matcher.group()
            val start: Int = postText.indexOf(hashtag)
            val end: Int = start + hashtag.length
            try {
                val clickableSpan = AmityHashtagClickableSpan(hashtag = hashtag){clickableSpanClicked = true}
                spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } catch (exception: IndexOutOfBoundsException) {
                Timber.e("AmityPostContentViewHolder", "hashtags text user mentions crashes")
            }
        }
    }

    private fun setMentionsClickableSpannable(spannable: SpannableString, post: AmityPost){
        if (spannable.isNotEmpty() && post.getMetadata() != null) {
            val mentionUserIds = post.getMentionees().map { (it as? AmityMentionee.USER)?.getUserId() }
            val mentionedUsers = AmityMentionMetadataGetter(post.getMetadata()!!).getMentionedUsers()
            val mentions = mentionedUsers.filter { mentionUserIds.contains(it.getUserId()) }
            mentions.forEach { mentionUserItem ->
                try {
                    val clickableSpan = AmityMentionClickableSpan(mentionUserItem.getUserId()){clickableSpanClicked = true}
                    spannable.setSpan(clickableSpan,
                        mentionUserItem.getIndex(),
                        mentionUserItem.getIndex().plus(mentionUserItem.getLength()).inc(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                } catch (exception: IndexOutOfBoundsException) {
                    Timber.e("AmityPostContentViewHolder", "Highlight text user mentions crashes")
                }
            }
        }
    }
}