package com.amity.socialcloud.uikit.community.newsfeed.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.amity.socialcloud.sdk.api.chat.AmityChatClient
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.model.core.reaction.AmityReactionMap
import com.amity.socialcloud.sdk.model.core.reaction.AmityReactionReferenceType

private const val SAVED_REFERENCE_TYPE = "SAVED_REFERENCE_TYPE"
private const val SAVED_REFERENCE_ID = "SAVED_REFERENCE_ID"

class AmityReactionListViewModel(private val savedState: SavedStateHandle) : ViewModel() {

	init {
		savedState.get<AmityReactionReferenceType>(SAVED_REFERENCE_TYPE)?.let { referenceType = it }
		savedState.get<String>(SAVED_REFERENCE_ID)?.let { referenceId = it }
	}

	var referenceType: AmityReactionReferenceType = AmityReactionReferenceType.POST
		set(value) {
			savedState.set(SAVED_REFERENCE_TYPE, value)
			field = value
		}

	var referenceId: String = ""
		set(value) {
			savedState.set(SAVED_REFERENCE_ID, value)
			field = value
		}

	var allReactionsCount: Int = 0
	private lateinit var reactionsMap: AmityReactionMap

	fun getReactionsSorted(): List<Pair<String, Int>> {
		val entries = reactionsMap.toList()
		return entries.sortedByDescending { it.second }.filter { it.second != 0 }
	}

	fun getPostDetails(): Int {
		return when (referenceType) {
			AmityReactionReferenceType.POST -> {
				AmitySocialClient.newPostRepository().getPost(referenceId).map {
					allReactionsCount = it.getReactionCount()
					reactionsMap = it.getReactionMap()
					return@map 0
				}.blockingFirst(0)

			}

			AmityReactionReferenceType.COMMENT -> {
				AmitySocialClient.newCommentRepository().getComment(referenceId).map {
					allReactionsCount = it.getReactionCount()
					reactionsMap = it.getReactionMap()
					return@map 0
				}.blockingFirst(0)
			}

			AmityReactionReferenceType.MESSAGE -> {
				AmityChatClient.newMessageRepository().getMessage(referenceId).map {
					allReactionsCount = it.getReactionCount()
					reactionsMap = it.getReactionMap()
					return@map 0
				}.blockingFirst(0)
			}
		}
	}
}