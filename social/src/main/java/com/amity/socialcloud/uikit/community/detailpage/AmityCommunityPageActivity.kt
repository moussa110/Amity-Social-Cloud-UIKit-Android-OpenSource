package com.amity.socialcloud.uikit.community.detailpage

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.community.R

class AmityCommunityPageActivity :
    AmityBaseToolbarFragmentContainerActivity() {

    companion object {
        private const val COMMUNITY = "COMMUNITY"
        private const val IS_CREATE_COMMUNITY = "IS_CREATE_COMMUNITY"
        private const val COMMUNITY_ID = "COMMUNITY_ID"

        fun newIntent(context: Context, community: AmityCommunity, isCreateCommunity: Boolean = false): Intent {
            return Intent(context, AmityCommunityPageActivity::class.java).apply {
                putExtra(COMMUNITY, community)
                putExtra(IS_CREATE_COMMUNITY, isCreateCommunity)
            }
        }

        fun newIntent(context: Context, communityId:String): Intent {
            return Intent(context, AmityCommunityPageActivity::class.java).apply {
                putExtra(COMMUNITY_ID, communityId)
            }
        }
    }

    override fun initToolbar() {
        val isFromContest = intent?.extras?.getString(COMMUNITY_ID) != null

        getToolBar()?.setLeftDrawable(
            ContextCompat.getDrawable(
                this,
                  R.drawable.amity_ic_arrow_back
            ),if (isFromContest) R.color.yellowColor else null
        )
    }

    override fun getContentFragment(): Fragment {
        intent?.extras?.getString(COMMUNITY_ID)?.let {
            return AmityCommunityPageFragment
                .newInstance(it)
                .createCommunitySuccess(intent?.extras?.getBoolean(IS_CREATE_COMMUNITY) ?: false)
                .build(this)
        } ?: run {
            val amityCommunity: AmityCommunity = intent?.extras?.getParcelable(COMMUNITY)!!
            return AmityCommunityPageFragment
                .newInstance(amityCommunity)
                .createCommunitySuccess(intent?.extras?.getBoolean(IS_CREATE_COMMUNITY) ?: false)
                .build(this)
        }
    }
}