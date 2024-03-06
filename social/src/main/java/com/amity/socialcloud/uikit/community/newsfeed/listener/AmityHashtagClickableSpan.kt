package com.amity.socialcloud.uikit.community.newsfeed.listener

import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import com.amity.socialcloud.uikit.community.utils.AmityCommunityNavigation

open class AmityHashtagClickableSpan(private val hashtag: String,private val listener:()->Unit) : ClickableSpan() {


    override fun onClick(widget: View) {
        listener()
        AmityCommunityNavigation.navigateToHashtagSearch(widget.context, hashtag)
    }

    override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = false
    }
}