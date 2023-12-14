package com.amity.socialcloud.uikit.common.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.amity.socialcloud.uikit.common.R
import com.amity.socialcloud.uikit.common.components.AmityToolBar
import com.amity.socialcloud.uikit.common.components.AmityToolBarClickListener
import com.amity.socialcloud.uikit.common.databinding.AmityActivityBaseToolbarFragmentContainerBinding
import com.amity.socialcloud.uikit.common.utils.setKoraKingsTransparentBackground
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity

abstract class AmityBaseToolbarFragmentContainerActivity : RxAppCompatActivity(),
        AmityToolBarClickListener {
    lateinit var binding: AmityActivityBaseToolbarFragmentContainerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.amity_activity_base_toolbar_fragment_container)
        if (savedInstanceState == null) {
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            val fragment = getContentFragment()
            fragmentTransaction.replace(R.id.fragmentContainer, fragment)
            fragmentTransaction.commit()
        }
        setupBackground()

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        setSupportActionBar(binding.toolbar)
        binding.toolbar?.setClickListener(this)

        initToolbar()
    }

    fun getToolBar(): AmityToolBar? {
        return binding.toolbar
    }


    fun showToolbarDivider() {
        //binding.divider.visibility = View.VISIBLE
    }

    abstract fun initToolbar()

    private fun setupBackground(){
        setKoraKingsTransparentBackground(binding.root,binding.fragmentContainer)
    }

    abstract fun getContentFragment(): Fragment

    override fun leftIconClick() {
        onBackPressed()
    }

    override fun rightIconClick() {

    }
}