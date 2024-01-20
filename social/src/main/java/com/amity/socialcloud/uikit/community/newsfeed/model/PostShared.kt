package com.amity.socialcloud.uikit.community.newsfeed.model

import android.os.Parcelable
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharedPost(var id: String="",
                      var newPostTextChars:Int=0,
                      var sharedPostTextChars:Int=0,
                      var sharedPostContent: SharedPostsType?=null,
                      var childrenCount: Int=0) : Parcelable

enum class SharedPostsType {
	TEXT, POLL, IMAGE, VIDEO, FILE, LIVE
}