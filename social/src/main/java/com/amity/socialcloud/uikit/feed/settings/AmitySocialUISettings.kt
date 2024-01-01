package com.amity.socialcloud.uikit.social

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.utils.FirebaseConstants
import com.amity.socialcloud.uikit.common.utils.generateSharingLink
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityGlobalCommunityClickListener
import com.amity.socialcloud.uikit.community.newsfeed.listener.AmityGlobalUserClickListener
import com.amity.socialcloud.uikit.feed.settings.AmityDefaultPostViewHolders
import com.amity.socialcloud.uikit.feed.settings.AmityPostRenderer
import com.amity.socialcloud.uikit.feed.settings.AmityPostShareClickListener
import com.amity.socialcloud.uikit.feed.settings.AmityPostSharingSettings

object AmitySocialUISettings {

	var postShareClickListener: AmityPostShareClickListener = object : AmityPostShareClickListener {
		override fun shareToExternal(context: Context, post: AmityPost) {
			// generate a link
			generateSharingLink("${FirebaseConstants.SHARE_POST_LINK}/post/${post.getPostId()}".toUri(),
				"https://i.ibb.co/KGcHxMP/kora-kings-logo.png".toUri()) {
				val share = Intent.createChooser(Intent().apply {
					action = Intent.ACTION_SEND
					type = "text/plain"
					putExtra(Intent.EXTRA_SUBJECT, "share post")
					putExtra(Intent.EXTRA_TEXT, it)
				}, "share via")
				context.startActivity(share)
			}
		}
	}

	var postSharingSettings = AmityPostSharingSettings()

	var globalUserClickListener: AmityGlobalUserClickListener =
		object : AmityGlobalUserClickListener {}

	internal var globalCommunityClickListener: AmityGlobalCommunityClickListener =
		object : AmityGlobalCommunityClickListener {}

	private var postViewHolders: MutableMap<String, AmityPostRenderer> =
		AmityDefaultPostViewHolders.getDefaultMap()

	fun registerPostRenderers(renderers: List<AmityPostRenderer>) {
		renderers.forEach { renderer ->
			postViewHolders[renderer.getDataType()] = renderer
		}
	}

	internal fun getViewHolder(dataType: String): AmityPostRenderer {
		return postViewHolders[dataType] ?: AmityDefaultPostViewHolders.unknownViewHolder
	}

	internal fun getViewHolder(viewType: Int): AmityPostRenderer {
		for (viewHolder in postViewHolders.values.toList()) {
			if (viewType == viewHolder.getDataType().hashCode()) {
				return viewHolder
			}
		}
		return AmityDefaultPostViewHolders.unknownViewHolder
	}

}