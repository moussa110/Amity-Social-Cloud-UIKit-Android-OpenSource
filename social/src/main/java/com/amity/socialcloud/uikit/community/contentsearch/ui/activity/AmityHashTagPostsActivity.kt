package com.amity.socialcloud.uikit.community.contentsearch.ui.activity

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.contentsearch.ui.fragment.AmityHashtagsSearchFragment

class AmityHashTagPostsActivity : AmityBaseToolbarFragmentContainerActivity() {

	companion object {
		const val EXTRA_PARAM_HASHTAG = "PARAMS_HASHTAG"
		var hashtag:String?=null
		fun newIntent(context: Context, hashtagQuery: String): Intent {
			hashtag = hashtagQuery
			return Intent(context, AmityHashTagPostsActivity::class.java).apply {
				putExtra(EXTRA_PARAM_HASHTAG,hashtagQuery)
			}
		}
	}

	override fun onStop() {
		super.onStop()
		hashtag = null
	}

	override fun initToolbar() {
		getToolBar()?.setLeftDrawable(
			ContextCompat.getDrawable(this, R.drawable.amity_ic_arrow_back)
		)
		getToolBar()?.setLeftString(getString(R.string.amity_hashtag))
	}

	override fun getContentFragment(): Fragment {
		intent.getStringExtra(EXTRA_PARAM_HASHTAG)?.let {
			return AmityHashtagsSearchFragment.newInstance().build(it)
		}
		return AmityHashtagsSearchFragment.newInstance().build("")
	}
}