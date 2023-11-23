package com.amity.socialcloud.uikit.community.home.activity

import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.map
import com.amity.socialcloud.sdk.api.social.AmitySocialClient
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityActivityCommunityHomeBinding
import com.amity.socialcloud.uikit.community.home.fragments.AmityCommunityHomePageFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

class AmityCommunityHomePageActivity : AppCompatActivity() {

    private val binding : AmityActivityCommunityHomeBinding by lazy {
        AmityActivityCommunityHomeBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initToolbar()
        loadFragment()
    }

    private fun initToolbar() {
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(binding.communityHomeToolbar)
        binding.communityHomeToolbar.setLeftString(getString(R.string.amity_community))
    }

    private fun loadFragment() {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = AmityCommunityHomePageFragment.newInstance().build()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}