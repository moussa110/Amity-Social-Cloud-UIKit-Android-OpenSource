package com.amity.socialcloud.uikit.community.newsfeed.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.api.social.community.AmityCommunityRepository
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.base.AmityBaseToolbarFragmentContainerActivity
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.explore.activity.EXTRA_PARAM_COMMUNITY
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmityPostCreatorFragment
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmitySharePostCreatorFragment
import com.amity.socialcloud.uikit.community.newsfeed.model.SharedPostData
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_POST_ID
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_SHARED_POST_DATA
import com.amity.socialcloud.uikit.community.utils.parcelable
import com.ekoapp.rxlifecycle.extension.untilLifecycleEnd
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AmityPostCreatorActivity : AmityBaseToolbarFragmentContainerActivity() {

    private var communityRepository: AmityCommunityRepository = AmitySocialClient.newCommunityRepository()

    override fun initToolbar() {
        var communityId = intent?.getStringExtra(EXTRA_PARAM_COMMUNITY)
        if (communityId == null){
            communityId = intent?.parcelable<SharedPostData>(EXTRA_PARAM_SHARED_POST_DATA)?.communityId
        }

        getToolBar()?.setLeftDrawable(ContextCompat.getDrawable(this, R.drawable.amity_ic_cross))
        if (communityId != null) {
            getCommunity(communityRepository,communityId)
        } else {
            getToolBar()?.setLeftString(getString(R.string.amity_my_timeline))
        }
    }
    fun getCommunity(communityRepository: AmityCommunityRepository, communityId: Any) {
        communityRepository
            .getCommunity(communityId = communityId.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { community: AmityCommunity ->
                var displayName = community.getDisplayName()
                getToolBar()?.setLeftString(displayName)
            }
            .untilLifecycleEnd(this)
            .subscribe()
    }


    override fun getContentFragment(): Fragment {
        //to share post
        intent?.parcelable<SharedPostData>(EXTRA_PARAM_SHARED_POST_DATA)?.let {
                return AmitySharePostCreatorFragment.newInstance()
                    .onCommunityFeed(it)
                    .build()

        }
        //to create post
        return intent?.getStringExtra(EXTRA_PARAM_COMMUNITY)?.let { communityId ->
            return AmityPostCreatorFragment.newInstance()
                    .onCommunityFeed(communityId)
                    .build()
        } ?: kotlin.run {
            AmityPostCreatorFragment.newInstance()
                    .onMyFeed()
                    .build()
        }
    }

    class AmityCreateCommunityPostActivityContract :
        ActivityResultContract<String?, String?>() {
        override fun createIntent(context: Context, communityId: String?): Intent {
            return Intent(context, AmityPostCreatorActivity::class.java).apply {
                putExtra(EXTRA_PARAM_COMMUNITY, communityId)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            val data = intent?.getStringExtra(EXTRA_PARAM_POST_ID)
            return if (resultCode == Activity.RESULT_OK && data != null) data
            else null
        }
    }

    class AmityCreateCommunitySharedPostActivityContract :
        ActivityResultContract<SharedPostData, String?>() {
        override fun createIntent(context: Context, sharedPostData: SharedPostData): Intent {
            return Intent(context, AmityPostCreatorActivity::class.java).apply {
                putExtra(EXTRA_PARAM_SHARED_POST_DATA,sharedPostData)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): String? {
            val data = intent?.getStringExtra(EXTRA_PARAM_POST_ID)
            return if (resultCode == Activity.RESULT_OK && data != null) data
            else null
        }
    }
}

