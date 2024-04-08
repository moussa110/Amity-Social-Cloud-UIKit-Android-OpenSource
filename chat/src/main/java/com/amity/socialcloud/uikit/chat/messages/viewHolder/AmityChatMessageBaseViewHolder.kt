package com.amity.socialcloud.uikit.chat.messages.viewHolder

import android.content.Context
import android.content.DialogInterface
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.model.chat.message.AmityMessage
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.chat.messages.viewModel.AmityChatMessageBaseViewModel
import com.amity.socialcloud.uikit.common.utils.AmityAlertDialogUtil
import com.amity.socialcloud.uikit.common.utils.AmityDateUtils
import com.amity.socialcloud.uikit.common.reactions.Reactions
import com.amity.socialcloud.uikit.common.reactions.ReactionsViews
import com.amity.socialcloud.uikit.common.reactions.showReactionPopup
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class AmityChatMessageBaseViewHolder(itemView: View,
                                              val itemBaseViewModel: AmityChatMessageBaseViewModel) :
	RecyclerView.ViewHolder(itemView) {
	abstract fun getContext(): Context
	abstract fun setMessage(message: AmityMessage)

	fun setItem(item: AmityMessage?) {
		itemBaseViewModel.amityMessage = item
		itemBaseViewModel.msgTime.set(item?.getCreatedAt()?.toString("hh:mm a"))
		itemBaseViewModel.editedAt.set(item?.getEditedAt()?.toString("hh:mm a"))
		itemBaseViewModel.msgDate.set(AmityDateUtils.getRelativeDate(item?.getCreatedAt()?.millis
			?: 0))
		if (itemBaseViewModel.isDeleted.get() != item?.isDeleted()) {
			itemBaseViewModel.isDeleted.set(item?.isDeleted() ?: false)
		}
		itemBaseViewModel.isFailed.set(item?.getState() == AmityMessage.State.FAILED)
		if (item != null) {
			itemBaseViewModel.sender.set(getSenderName(item))
			itemBaseViewModel.isSelf.set(item.getCreatorId() == AmityCoreClient.getUserId())
			itemBaseViewModel.isEdited.set(item.isEdited())
			setMessage(item)
		}

	}

	open fun onReactionImageClicked() {
		itemBaseViewModel.reactionHelper?.let { helper ->
			helper.getSelectReactionImage()?.setOnClickListener { v ->
				if (helper.myReaction == null) showReactionPopup(v, true) { react ->
					helper.addReact(react)
					addReact(react)
				} else {
					removeReact(helper.myReaction!!)
					helper.removeReact()
				}
			}

			helper.getTopThreeReactionsParentView()?.setOnClickListener {

			}
		}
	}

	private fun getSenderName(item: AmityMessage): String {
		return if (item.getCreatorId() == AmityCoreClient.getUserId()) {
			"ME"
		} else {
			item.getCreator()?.getDisplayName()
				?: itemView.context.getString(R.string.amity_anonymous)
		}
	}

	private fun addReact(reaction: Reactions) {
		itemBaseViewModel.addReaction(reaction)?.let {
			it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.doOnComplete {}.doOnError {
					showReactionFailedDialog()
				}.subscribe()
		}
	}

	private fun removeReact(reaction: Reactions) {
		itemBaseViewModel.removeReaction(reaction)?.let {
			it.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
				.doOnComplete {}.doOnError {
					showReactionFailedDialog()
				}.subscribe()
		}
	}

	private fun showReactionFailedDialog() {
		AmityAlertDialogUtil.showDialog(getContext(),
			getContext().getString(R.string.amity_unable_to_edit_reaction),
			getContext().getString(R.string.amity_try_again),
			getContext().getString(R.string.amity_ok),
			null) { dialog, which ->
			if (which == DialogInterface.BUTTON_POSITIVE) {
				dialog.cancel()
			}
		}
	}


}