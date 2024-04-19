package com.amity.socialcloud.uikit.chat.messages.viewModel

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.amity.socialcloud.sdk.model.chat.message.AmityMessage
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.common.base.AmityBaseViewModel
import com.amity.socialcloud.uikit.common.reactions.ReactionHelper
import com.amity.socialcloud.uikit.common.reactions.Reactions
import io.reactivex.rxjava3.core.Completable

open class AmityChatMessageBaseViewModel : AmityBaseViewModel() {
	var reactionHelper:ReactionHelper?=null
	val isSelf = ObservableBoolean(false)
	val sender = ObservableField("")
	val msgTime = ObservableField("")
	val msgDate = ObservableField("")
	val isDateVisible = ObservableBoolean(false)
	val isSenderVisible = ObservableBoolean(false)
	var amityMessage: AmityMessage? = null
		set(value) {
			initReactionHelper(value)
			field = value
		}
	val isDeleted = ObservableBoolean(false)
	val editedAt = ObservableField("")
	val isEdited = ObservableBoolean(false)
	val dateFillColor = ObservableField(R.color.fb_gray_placeholder)
	val isFailed = ObservableBoolean(false)
	val isToHideReact = ObservableBoolean(true)
	val isRepliedMessage = ObservableBoolean(false)

	private fun initReactionHelper(amityMessage: AmityMessage?){
		amityMessage?.let {
			reactionHelper = ReactionHelper(
				reactionsCount = it.getReactionCount(),
				mutableReactionsMap = it.getReactionMap().toMutableMap(),
				myReactionsList = it.getMyReactions(),

			)
		}
	}

	fun deleteMessage(): Completable? {
		return amityMessage?.delete()
	}

	fun addReaction(reaction: Reactions): Completable? {
		return amityMessage?.react()?.addReaction(reaction.reactName)
	}

	fun removeReaction(reaction: Reactions): Completable? {
		return amityMessage?.react()?.removeReaction(reaction.reactName)
	}


}