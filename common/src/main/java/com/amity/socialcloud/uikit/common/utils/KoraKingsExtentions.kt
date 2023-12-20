package com.amity.socialcloud.uikit.common.utils

import android.app.Activity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amity.socialcloud.uikit.common.R
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.common.components.AmityToolBarClickListener

fun Fragment.setActionBarRightDrawable(imageResource: Int, listener: () -> Unit) {
	getAmityActionBar()?.let {
		it.setRightDrawable(ContextCompat.getDrawable(requireActivity(), imageResource))
		it.setClickListener(object : AmityToolBarClickListener {
			override fun rightIconClick() {
				listener.invoke()
			}

			override fun leftIconClick() {
				requireActivity().onBackPressed()
			}
		})
	}
}

fun Fragment.setActionBarRightText(text: String, listener: () -> Unit) {
	getAmityActionBar()?.let {
		it.setRightString(text)
		it.setClickListener(object : AmityToolBarClickListener {
			override fun rightIconClick() {
				listener.invoke()
			}

			override fun leftIconClick() {
				requireActivity().onBackPressed()
			}
		})
	}
}

fun Fragment.setActionBarRightText(text: String) {
	getAmityActionBar()?.setRightString(text)
}

fun Fragment.setActionBarRightTextEnabled(enabled:Boolean) {
	getAmityActionBar()?.setRightStringActive(enabled)
}

fun Fragment.setRightActionBarClickListener(listener: () -> Unit){
	getAmityActionBar()?.setClickListener(object : AmityToolBarClickListener {
		override fun rightIconClick() {
			listener.invoke()
		}

		override fun leftIconClick() {
			requireActivity().onBackPressed()
		}
	})
}

fun Fragment.getAmityActionBar() =
	(requireActivity() as AmityBaseToolbarFragmentContainerActivity).getToolBar()

fun Fragment.setActionBarLeftText(text: String){
	getAmityActionBar()?.setLeftString(text)
}

fun AppCompatActivity.setKoraKingsTransparentBackground(layoutView: View, frameLayout: FrameLayout) {
	layoutView.background = ContextCompat.getDrawable(this, R.drawable.auth_bg)
	frameLayout.setBackgroundColor(ContextCompat.getColor(this,
		R.color.fb_darker_gray_bg_transparent_15))
}

fun View.animateScaleClick(endListener: (() -> Unit)? = null) {
	animateScaleOut {
		animateScaleIn {
			endListener?.invoke()
		}
	}
}

fun View.animateScaleOut(
	value: Float = 0.7f,
	duration: Long = 100,
	endListener: (() -> Unit)? = null
) {
	animate().scaleX(value).scaleY(value)
		.setDuration(duration).withEndAction {
			endListener?.invoke()
		}
}


fun View.animateScaleIn(
	value: Float = 1f,
	duration: Long = 100,
	endListener: (() -> Unit)? = null
) {
	animate().scaleX(value).scaleY(value)
		.setDuration(duration).withEndAction {
			endListener?.invoke()
		}
}