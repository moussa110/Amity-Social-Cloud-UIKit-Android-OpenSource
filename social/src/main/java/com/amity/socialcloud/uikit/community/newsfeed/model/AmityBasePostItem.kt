package com.amity.socialcloud.uikit.community.newsfeed.model

import com.amity.socialcloud.sdk.model.core.user.AmityUser
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

data class AmityBasePostItem(val post: AmityPost,
                             val headerItems: List<AmityBasePostHeaderItem>,
                             val contentItems: List<AmityBasePostContentItem>,
                             val footerItems: List<AmityBasePostFooterItem>,
                             var sharedPost: AmityBasePostItem? = null,
                             var sharedUpdatedListener: ((AmityBasePostItem)->Unit)?=null
)

