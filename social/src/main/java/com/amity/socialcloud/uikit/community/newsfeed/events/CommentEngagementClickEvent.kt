package com.amity.socialcloud.uikit.community.newsfeed.events

import com.amity.socialcloud.sdk.model.social.comment.AmityComment
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.reactions.Reactions

sealed class CommentEngagementClickEvent {

    class Reaction(val comment: AmityComment, val isAdding: Boolean,val reactions: Reactions) : CommentEngagementClickEvent()

    class Reply(val comment: AmityComment, val post: AmityPost?) : CommentEngagementClickEvent()

}