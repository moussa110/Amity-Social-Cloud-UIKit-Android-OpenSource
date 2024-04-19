package com.amity.socialcloud.uikit.chat.messages.viewHolder

import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionMetadataGetter
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionee
import com.amity.socialcloud.sdk.model.chat.message.AmityMessage
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.chat.databinding.AmityItemTextMessageReceiverBinding
import com.amity.socialcloud.uikit.chat.databinding.AmityPopupMsgReportBinding
import com.amity.socialcloud.uikit.chat.messages.popUp.AmityPopUp
import com.amity.socialcloud.uikit.chat.messages.viewModel.AmityTextMessageViewModel
import com.amity.socialcloud.uikit.common.components.AmityLongPressListener
import com.amity.socialcloud.uikit.common.model.AmityEventIdentifier
import com.amity.socialcloud.uikit.common.reactions.ReactionsViews
import timber.log.Timber
import java.util.regex.Matcher
import java.util.regex.Pattern

class AmityTextMsgReceiverViewHolder(
    itemView: View,
    private val itemViewModel: AmityTextMessageViewModel,
    context: Context
) : AmitySelectableMessageViewHolder(itemView, itemViewModel, context), AmityLongPressListener {

    private val binding: AmityItemTextMessageReceiverBinding? = DataBindingUtil.bind(itemView)
    private var popUp: AmityPopUp? = null

    init {
        binding?.vmTextMessage = itemViewModel
        binding?.lonPressListener = this
        addViewModelListener()
    }

    private fun getReactViews(): ReactionsViews? {
        binding?.apply {
            return ReactionsViews(
                topThreeReactionsView.parentView,topThreeReactionsView.firstReactionIv,
                topThreeReactionsView.secondReactionIv,
                topThreeReactionsView.thirdReactionIv,
                topThreeReactionsView.tvNumberOfReactions,
                addReactionView.addReactIv
            )
        }
        return null
    }

    private fun addViewModelListener() {
        itemViewModel.onAmityEventReceived += { event ->
            when (event.type) {
                AmityEventIdentifier.DISMISS_POPUP -> popUp?.dismiss()
                else -> {
                }
            }
        }
    }

    override fun setMessageData(item: AmityMessage) {
        val text = getHighlightTextUserMentionsAndHashtags(item)
        itemViewModel.text.set(text)
        itemViewModel.reactionHelper?.setReactionView(getReactViews())
        onReactionImageClicked()
    }

    private fun getHighlightTextUserMentionsAndHashtags(message: AmityMessage): SpannableString {
        val text = (message.getData() as? AmityMessage.Data.TEXT)?.getText() ?: ""
        val spannable = SpannableString("$text ")
        setMentionsClickableSpannable(spannable, message)
        // setHashtagClickable(spannable,text)
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
                val clickableSpan = ForegroundColorSpan(Color.BLUE)
                spannable.setSpan(clickableSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            } catch (exception: IndexOutOfBoundsException) {
                Timber.e("AmityPostContentViewHolder", "hashtags text user mentions crashes")
            }
        }
    }

    private fun setMentionsClickableSpannable(spannable: SpannableString, message: AmityMessage) {
        if (spannable.isNotEmpty() && message.getMetadata() != null) {
            val mentionUserIds = message.getMentionees().map { (it as? AmityMentionee.USER)?.getUserId() }
            val mentionedUsers = AmityMentionMetadataGetter(message.getMetadata()!!).getMentionedUsers()
            val mentions = mentionedUsers.filter { mentionUserIds.contains(it.getUserId()) }
            mentions.forEach { mentionUserItem ->
                try {
                    // val clickableSpan = AmityMentionClickableSpan(mentionUserItem.getUserId()){clickableSpanClicked = true}
                    val clickableSpan = object : ClickableSpan(){
                        override fun onClick(p0: View) {
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            super.updateDrawState(ds)
                            ds.isUnderlineText = false
                            ds.color = ds.linkColor
                        }

                    }
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



    override fun showPopUp() {
        popUp = AmityPopUp()
        val anchor: View = itemView.findViewById(R.id.tvMessageIncoming)
        val inflater: LayoutInflater = LayoutInflater.from(anchor.context)
        val binding: AmityPopupMsgReportBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.amity_popup_msg_report, null, true
        )
        binding.viewModel = itemViewModel
        if (itemViewModel.amityMessage?.isFlaggedByMe() == true) {
            binding.reportText.setText(R.string.amity_unreport)
        } else {
            binding.reportText.setText(R.string.amity_report)
        }
        popUp?.showPopUp(binding.root, anchor, itemViewModel, AmityPopUp.PopUpGravity.START)
    }

    override fun onLongPress() {
        itemViewModel.onLongPress(absoluteAdapterPosition)
    }
}