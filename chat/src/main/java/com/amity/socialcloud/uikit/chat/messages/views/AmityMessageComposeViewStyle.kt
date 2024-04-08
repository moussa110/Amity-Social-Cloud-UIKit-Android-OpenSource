package com.amity.socialcloud.uikit.chat.messages.views

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.StyleRes
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.common.common.views.AmityStyle

class AmityMessageComposeViewStyle : AmityStyle {
    var backgroundColor: Int = -1
    var textColor: Int = -1
    var hintTextColor: Int = -1
    var padding: Int = -1
    var hint: Int = -1

    init {
        backgroundColor = getColor(R.color.transparent_100)
        textColor = getColor(R.color.yellowColor)
        hintTextColor = getColor(R.color.fb_text_light_gray)
        hint = R.string.amity_type_message
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context)
    constructor(context: Context, @StyleRes style: Int) : super(context)
}