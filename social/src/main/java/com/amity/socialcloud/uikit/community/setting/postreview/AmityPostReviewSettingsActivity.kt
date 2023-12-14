package com.amity.socialcloud.uikit.community.setting.postreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.amity.socialcloud.uikit.common.base.AmityBaseActivity
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.common.components.AmityToolBarClickListener
import com.amity.socialcloud.uikit.common.utils.setKoraKingsTransparentBackground
import com.amity.socialcloud.uikit.community.BR
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityActivityPostReviewSettingsBinding

class AmityPostReviewSettingsActivity :
    AmityBaseToolbarFragmentContainerActivity(),
    AmityToolBarClickListener {

    companion object {
        private const val COMMUNITY_ID = "COMMUNITY_ID"

        fun newIntent(context: Context, communityId: String): Intent =
            Intent(context, AmityPostReviewSettingsActivity::class.java).apply {
                putExtra(COMMUNITY_ID, communityId)
            }

    }

    override fun initToolbar() {
        getToolBar()?.apply {
            setLeftDrawable(ContextCompat.getDrawable(this@AmityPostReviewSettingsActivity, R.drawable.amity_ic_arrow_back))

            val titleToolbar = getString(R.string.amity_post_review)
            setLeftString(titleToolbar)
        }
    }

    override fun getContentFragment(): Fragment {
        return AmityPostReviewSettingsFragment.newInstance(intent.getStringExtra(COMMUNITY_ID)!!).build(this)
    }
}