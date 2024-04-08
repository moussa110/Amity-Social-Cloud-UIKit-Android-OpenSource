package com.amity.socialcloud.uikit.common.reactions

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.amity.socialcloud.uikit.common.R
import com.amity.socialcloud.uikit.common.common.readableNumber
import com.amity.socialcloud.uikit.common.reactions.Reactions.*

data class ReactionHelper(var reactionsCount: Int,
                          val myReactionsList: List<String>,
                          var myReaction: Reactions? = null,
                          val mutableReactionsMap: MutableMap<String, Int>,
                          var topThreeReactions: List<String>? = null,
                          private var reactionsViews: ReactionsViews? = null) {
	init {
		updateData()
		myReactionsList.let {
			myReaction = if (it.isEmpty()) null else getReactionByName(it[it.lastIndex])
		}
	}

	fun setReactionView(reactionsViews: ReactionsViews?) {
		reactionsViews?.let {
			this.reactionsViews = it
			updateReactionForImage(it.selectReactIv)
			updateTopThreeReactions(it.topThreeParentView, it.iv1, it.iv2, it.iv3)
			setNumberOfReactions(it.countTv)
		}
	}

	fun getSelectReactionImage() = reactionsViews?.selectReactIv

	fun getTopThreeReactionsParentView() = reactionsViews?.topThreeParentView

	private fun updateData() {
		topThreeReactions =
			mutableReactionsMap.toList().filter { it.second != 0 }.sortedBy { it.second }
				.map { it.first }.take(3)
	}

	fun addReact(reaction: Reactions) {
		myReaction = reaction
		reactionsCount++
		if (mutableReactionsMap.containsKey(reaction.reactName)) {
			mutableReactionsMap[reaction.reactName]?.let {
				mutableReactionsMap[reaction.reactName] = (it + 1)
			}
		} else {
			mutableReactionsMap[reaction.reactName] = 1
		}
		updateData()
		reactionsViews?.let {
			updateReactionForImage(it.selectReactIv)
			updateTopThreeReactions(it.topThreeParentView, it.iv1, it.iv2, it.iv3)
			setNumberOfReactions(it.countTv)
		}
	}

	fun removeReact() {
		if (myReaction == null) return
		mutableReactionsMap[myReaction!!.reactName] =
			Math.max((mutableReactionsMap[myReaction!!.reactName] ?: 1) - 1, 0)
		reactionsCount--
		myReaction = null
		updateData()
		reactionsViews?.let {
			updateReactionForImage(it.selectReactIv)
			updateTopThreeReactions(it.topThreeParentView, it.iv1, it.iv2, it.iv3)
			setNumberOfReactions(it.countTv)
		}
	}

	private fun setNumberOfReactions(tv: TextView) {
		tv.isVisible = reactionsCount > 1
		if (reactionsCount <= 1) return
		tv.text = reactionsCount.readableNumber()
		updateData()
	}

	private fun updateTopThreeReactions(parentView: View,
	                                    iv1: ImageView,
	                                    iv2: ImageView,
	                                    iv3: ImageView) {
		parentView.isVisible = false
		iv1.isVisible = false
		iv2.isVisible = false
		iv3.isVisible = false

		if (reactionsCount < 2) return

		topThreeReactions?.forEachIndexed { index, str ->
			getReactionByName(str)?.let { reaction ->
				when (index) {
					0 -> {
						parentView.isVisible = true
						iv1.apply {
							setImageDrawable(reaction.getDrawable20(context))
							isVisible = true
						}
					}

					1 -> {
						iv2.apply {
							setImageDrawable(reaction.getDrawable20(context))
							isVisible = true
						}
					}

					2 -> {
						iv3.apply {
							setImageDrawable(reaction.getDrawable20(context))
							isVisible = true
						}
					}
				}
			}
		}
	}

	private fun updateReactionForImage(imageView: ImageView) {
		val context = imageView.context
		val react = if (myReaction == null) R.drawable.baseline_add_reaction_24
		else when (myReaction!!) {
			LIKE -> R.drawable.amity_ic_circle_like_20
			LOVE -> R.drawable.love_20
			WOW -> R.drawable.wow_20
			ANGRY -> R.drawable.angry_20
			SAD -> R.drawable.sad_20
		}
		imageView.setImageDrawable(ContextCompat.getDrawable(context, react))
	}
}

data class ReactionsViews(val topThreeParentView: View,
                          val iv1: ImageView,
                          val iv2: ImageView,
                          val iv3: ImageView,
                          val countTv: TextView,
                          val selectReactIv: ImageView)