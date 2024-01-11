package com.amity.socialcloud.uikit.community.newsfeed.viewcontroller

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.amity.socialcloud.uikit.community.R

class ReactionViewController {
	fun refreshReactView(isReactedByMe: Boolean,
	                             myReactions: List<String>,
	                             react: Reactions? = null,
	                             cbLike: AppCompatTextView) {
		when (react
			?: if (isReactedByMe && myReactions.isNotEmpty()) getReactionByName(myReactions[myReactions.lastIndex]) else Reactions.LIKE) {
			Reactions.LOVE -> handleLoveReaction(isReactedByMe,cbLike)
			Reactions.WOW -> handleWowReaction(isReactedByMe,cbLike)
			Reactions.ANGRY -> handleAngryReaction(isReactedByMe,cbLike)
			Reactions.SAD -> handleSadReaction(isReactedByMe,cbLike)
			else -> handleLikeReaction(isReactedByMe,cbLike)
		}
	}

	private fun handleLikeReaction(isSelected: Boolean = true,textView: AppCompatTextView) {
		textView.apply {
			val iconDrawable = ContextCompat.getDrawable(context,
				if (isSelected) R.drawable.amity_btn_liked_pressed else R.drawable.amity_btn_like_normal)
			setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable, null, null, null)
			setTextColor(ContextCompat.getColor(context,
				if (isSelected) R.color.yellowColor else R.color.fb_text_gray))
			setText(if (isSelected) R.string.amity_liked else R.string.amity_like)
		}
	}

	private fun handleLoveReaction(isSelected: Boolean = true,textView: AppCompatTextView) {
		textView.apply {
			setText(if (isSelected) R.string.amity_loved else R.string.amity_like)
			val iconDrawable = ContextCompat.getDrawable(context,
				if (isSelected) R.drawable.love_20 else R.drawable.amity_btn_like_normal)
			setTextColor(ContextCompat.getColor(context,
				if (isSelected) R.color.redColor else R.color.fb_text_gray))
			setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable, null, null, null)
		}
	}

	private fun handleWowReaction(isSelected: Boolean = true,textView: AppCompatTextView) {
		textView.apply {
			setText(if (isSelected) R.string.amity_wow else R.string.amity_like)
			val iconDrawable = ContextCompat.getDrawable(context,
				if (isSelected) R.drawable.wow_20 else R.drawable.amity_btn_like_normal)
			setTextColor(ContextCompat.getColor(context,
				if (isSelected) R.color.yellowColor else R.color.fb_text_gray))
			setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable, null, null, null)
		}
	}

	private fun handleSadReaction(isSelected: Boolean = true,textView: AppCompatTextView) {
		textView.apply {
			setText(if (isSelected) R.string.amity_sad else R.string.amity_like)
			val iconDrawable = ContextCompat.getDrawable(context,
				if (isSelected) R.drawable.sad_20 else R.drawable.amity_btn_like_normal)
			setTextColor(ContextCompat.getColor(context,
				if (isSelected) R.color.yellowColor else R.color.fb_text_gray))
			setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable, null, null, null)
		}
	}

	private fun handleAngryReaction(isSelected: Boolean = true,textView: AppCompatTextView) {
		textView.apply {
			setText(if (isSelected) R.string.amity_angry else R.string.amity_like)
			val iconDrawable = ContextCompat.getDrawable(context,
				if (isSelected) R.drawable.angry_20 else R.drawable.amity_btn_like_normal)
			setTextColor(ContextCompat.getColor(context,
				if (isSelected) R.color.redColor else R.color.fb_text_gray))
			setCompoundDrawablesRelativeWithIntrinsicBounds(iconDrawable, null, null, null)
		}
	}
}

enum class Reactions(val reactName: String) {
	LIKE("like"), LOVE("love"), WOW("wow"), ANGRY("angry"), SAD("sad")
}

fun getReactionByName(name: String) = Reactions.values().find { it.reactName == name }

fun Reactions.getDrawable20(context: Context): Drawable? {
	return when(this){
		Reactions.LIKE -> ContextCompat.getDrawable(context, R.drawable.amity_ic_circle_like)
		Reactions.LOVE -> ContextCompat.getDrawable(context, R.drawable.love_20)
		Reactions.WOW -> ContextCompat.getDrawable(context, R.drawable.wow_20)
		Reactions.ANGRY -> ContextCompat.getDrawable(context, R.drawable.angry_20)
		Reactions.SAD -> ContextCompat.getDrawable(context, R.drawable.sad_20)
	}
}