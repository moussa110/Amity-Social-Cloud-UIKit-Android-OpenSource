package com.amity.socialcloud.uikit.community.newsfeed.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmityPostTargetPickerFragment
import com.amity.socialcloud.uikit.community.newsfeed.model.SharedPostData
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_POST_ID
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_SHARED_POST_DATA
import com.amity.socialcloud.uikit.community.utils.parcelable

class AmitySharePostTargetPickerActivity : AmityBaseToolbarFragmentContainerActivity() {

	override fun getContentFragment(): Fragment {
		return intent?.parcelable<SharedPostData>(EXTRA_PARAM_SHARED_POST_DATA)?.let {
			AmityPostTargetPickerFragment.newInstance().buildForSharePost(it)
		} ?: kotlin.run {
			Fragment()
		}
	}

	override fun initToolbar() {
		getToolBar()?.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.amity_ic_cross))
		getToolBar()?.setLeftString(getString(R.string.amity_share_to))
	}

	override fun leftIconClick() {
		this.finish()
	}

	class AmitySharePostActivityContract : ActivityResultContract<SharedPostData, String?>() {
		override fun createIntent(context: Context, sharedPostData: SharedPostData): Intent {
			return Intent(context, AmitySharePostTargetPickerActivity::class.java).apply {
				putExtra(EXTRA_PARAM_SHARED_POST_DATA, sharedPostData)
			}
		}

		override fun parseResult(resultCode: Int, intent: Intent?): String? {
			val data = intent?.getStringExtra(EXTRA_PARAM_POST_ID)
			return if (resultCode == Activity.RESULT_OK && data != null) data
			else null
		}
	}
}