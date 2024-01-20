package com.amity.socialcloud.uikit.common.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amity.socialcloud.uikit.common.R
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.common.components.AmityToolBar
import com.amity.socialcloud.uikit.common.components.AmityToolBarClickListener
import kotlin.math.roundToInt


fun Fragment.setActionBarRightDrawable(imageResource: Int, listener: () -> Unit) {
	try {
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
	}catch (e:Exception){
		e.printStackTrace()
	}
}

fun Fragment.setActionBarRightText(text: String, listener: () -> Unit) {
	try {
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
	}catch (e:Exception){
		e.printStackTrace()
	}
}

fun Fragment.setActionBarRightText(text: String) {
	try {
		getAmityActionBar()?.setRightString(text)
	}catch (e:Exception){
		e.printStackTrace()
	}
}

fun Fragment.setActionBarRightTextEnabled(enabled:Boolean) {
	try {
		getAmityActionBar()?.setRightStringActive(enabled)
	}catch (e:Exception){
		e.printStackTrace()
	}
}

fun Fragment.setRightActionBarClickListener(listener: () -> Unit){
	try {
		getAmityActionBar()?.setClickListener(object : AmityToolBarClickListener {
			override fun rightIconClick() {
				listener.invoke()
			}

			override fun leftIconClick() {
				requireActivity().onBackPressed()
			}
		})
	}catch (e:Exception){
		e.printStackTrace()
	}

}

fun Fragment.getAmityActionBar(): AmityToolBar? {
	return try {
		(requireActivity() as AmityBaseToolbarFragmentContainerActivity).getToolBar()
	}catch (e:Exception){
		e.printStackTrace()
		null
	}
}


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
	value: Float = 0.5f,
	duration: Long = 150,
	endListener: (() -> Unit)? = null
) {
	animate().scaleX(value).scaleY(value)
		.setDuration(duration).withEndAction {
			endListener?.invoke()
		}
}


fun View.animateScaleIn(
	value: Float = 1f,
	duration: Long = 150,
	endListener: (() -> Unit)? = null
) {
	animate().scaleX(value).scaleY(value)
		.setDuration(duration).withEndAction {
			endListener?.invoke()
		}
}

fun Int.pxToDp(context: Context): Int {
	val displayMetrics: DisplayMetrics = context.resources.displayMetrics
	return (this / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}