package com.amity.socialcloud.uikit.chat.messages.viewModel

import android.text.SpannableString
import androidx.databinding.ObservableField
import com.amity.socialcloud.uikit.chat.R

class AmityTextMessageViewModel : AmitySelectableMessageViewModel() {

    val text = ObservableField<SpannableString>()
    val senderFillColor = ObservableField<Int>(R.color.amityColorPrimary)
    val replyFillColor = ObservableField<Int>(R.color.grayOpacity)
    val receiverFillColor = ObservableField<Int>(R.color.amityMessageBubbleInverse)
    val reactionFillColor = ObservableField<Int>(R.color.grayDark)
}