package com.amity.socialcloud.uikit.community.newsfeed.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.common.views.text.AmityExpandableTextView
import com.amity.socialcloud.uikit.common.linkpreview.AmityPreviewLinkView
import com.amity.socialcloud.uikit.community.R
import java.util.regex.Matcher
import java.util.regex.Pattern

class AmityPostItemTextViewHolder(itemView: View) : AmityPostContentViewHolder(itemView) {

    private val tvPost = itemView.findViewById<AmityExpandableTextView>(R.id.tvFeed)
    private val previewLink = itemView.findViewById<AmityPreviewLinkView>(R.id.viewLinkPreview)

    override fun bind(post: AmityPost) {
        setPostText(post, showFullContent)

        val firstURL = findFirstURL()
        previewLink.loadPreview(post, firstURL)
    }

    private fun findFirstURL(): String? {
        return tvPost.urls.firstOrNull()?.url
    }
}