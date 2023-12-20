package com.amity.socialcloud.uikit.community.newsfeed.util

import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.utils.AmityConstants

fun AmityPost.isTargetingModerator(): Boolean {
	val roles = (getTarget() as? AmityPost.Target.COMMUNITY)?.getCreatorMember()?.getRoles()
	return roles?.any {
		it == AmityConstants.MODERATOR_ROLE || it == AmityConstants.COMMUNITY_MODERATOR_ROLE
	} ?: false
}

fun AmityPost.isTargetingOwnFeed() {
	val target = getTarget()
	val isTargetingOwnFeed = if (target is AmityPost.Target.USER) (target.getUser()?.getUserId()
		?: "") == AmityCoreClient.getUserId() else false
}
