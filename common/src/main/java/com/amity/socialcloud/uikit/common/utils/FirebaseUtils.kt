package com.amity.socialcloud.uikit.common.utils

import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.socialMetaTagParameters

object FirebaseConstants {
	const val TAG="firebase_tag"
	const val PREFIX = "https://korakings.page.link"
	const val SHARE_POST_LINK = "https://www.korakings.com"
}

	fun generateSharingLink(
		deepLink: Uri,
		previewImageLink: Uri,
		getShareableLink: (String) -> Unit = {},
	) {

		FirebaseDynamicLinks.getInstance().createDynamicLink().run {
			link = deepLink
			domainUriPrefix = FirebaseConstants.PREFIX
			// Pass your preview Image Link here;
			socialMetaTagParameters {
				imageUrl = previewImageLink
				title = "Kora-Kings app"
				description = "show content via kora-kings app"
			}
			// Required
			androidParameters {
				build()
			}
			// Finally
			buildShortDynamicLink()
		}.also {
			it.addOnSuccessListener { dynamicLink ->
				getShareableLink.invoke(dynamicLink.shortLink.toString())
			}
			it.addOnFailureListener { exception ->
				Log.d(FirebaseConstants.TAG, "generateSharingLink: ${exception.message}")
			}
		}
	}
