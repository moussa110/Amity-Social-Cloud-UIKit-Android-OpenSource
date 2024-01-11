package com.amity.socialcloud.uikit.community.newsfeed.popup

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.amity.socialcloud.uikit.common.utils.animateScaleClick
import com.amity.socialcloud.uikit.community.databinding.AmityPopupReactionsBinding
import com.amity.socialcloud.uikit.community.newsfeed.viewcontroller.Reactions


fun showReactionPopup(view: View,isFromComment:Boolean=false,listener:(Reactions)->Unit) {
	val context = view.context
	val dialogBinding = AmityPopupReactionsBinding.inflate(LayoutInflater.from(context))
	val wid = LinearLayout.LayoutParams.WRAP_CONTENT
	val high = LinearLayout.LayoutParams.WRAP_CONTENT
	val focus = true
	val popupWindow = PopupWindow(dialogBinding.root, wid, high, focus)

	popupWindow.showAsDropDown(view, 0, ((if (isFromComment)-4 else -5 ) * view.height), Gravity.CENTER)
	dialogBinding.apply {
		likeIv.setOnClickListener {
			it.animateScaleClick {
				listener.invoke(Reactions.LIKE)
				popupWindow.dismiss()
			}
		}
		loveIv.setOnClickListener {
			it.animateScaleClick {
				listener.invoke(Reactions.LOVE)
				popupWindow.dismiss()
			}
		}
		wowIv.setOnClickListener {
			it.animateScaleClick {
				listener.invoke(Reactions.WOW)
				popupWindow.dismiss()
			}
		}
		angryIv.setOnClickListener {
			it.animateScaleClick {
				listener.invoke(Reactions.ANGRY)
				popupWindow.dismiss()
			}
		}

		sadIv.setOnClickListener {
			it.animateScaleClick {
				listener.invoke(Reactions.SAD)
				popupWindow.dismiss()
			}
		}
	}
}
