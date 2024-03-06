package com.amity.socialcloud.uikit.community.newsfeed.listener

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import com.amity.socialcloud.uikit.community.utils.AmityCommunityNavigation

class AmityMentionClickableSpan(private val userId: String,private val listener:(()->Unit)?=null) : ClickableSpan() {

    override fun onClick(widget: View) {
        listener?.invoke()
        AmityCommunityNavigation
            .navigateToUserProfile(widget.context, userId)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}