package com.amity.socialcloud.uikit.community.views.comment

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StyleRes
import com.amity.socialcloud.uikit.common.common.views.AmityStyle
import com.amity.socialcloud.uikit.community.R

class AmityCommentComposeViewStyle : AmityStyle {
    var backgroundColor: Int = -1
    var textColor: Int = -1
    var hintTextColor: Int = -1
    var padding: Int = -1
    var hint: Int = -1

    init {
        backgroundColor = getColor(R.color.amityColorBlack)
        textColor = getColor(R.color.amityColorBase)
        hintTextColor = getColor(R.color.fb_gray)
        hint = R.string.amity_post_comment_hint
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context)
    constructor(context: Context, @StyleRes style: Int) : super(context)
}