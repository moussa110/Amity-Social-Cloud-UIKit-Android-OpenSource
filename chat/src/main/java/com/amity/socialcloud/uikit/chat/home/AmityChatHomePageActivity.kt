package com.amity.socialcloud.uikit.chat.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.amity.socialcloud.sdk.api.chat.AmityChatClient
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.model.core.unread.UserUnread
import com.amity.socialcloud.uikit.chat.BR
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.chat.databinding.AmityActivityChatHomeBinding
import com.amity.socialcloud.uikit.chat.home.fragment.AmityChatHomePageFragment
import com.amity.socialcloud.uikit.common.base.AmityBaseActivity

class AmityChatHomePageActivity :
	AmityBaseActivity<AmityActivityChatHomeBinding, AmityChatHomePageViewModel>() {

	private val mViewModel: AmityChatHomePageViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		initializeFragment()
		AmityCoreClient.startUnreadSync()
	}

	private fun initializeFragment() {
		val chatHomeFragment = AmityChatHomePageFragment.newInstance().build(this)
		val transaction = supportFragmentManager.beginTransaction()
		transaction.replace(R.id.chatHomeContainer, chatHomeFragment)
		transaction.addToBackStack(null)
		transaction.commit()
	}

	override fun onBackPressed() {
		this.finish()
	}

	override fun getLayoutId(): Int = R.layout.amity_activity_chat_home

	override fun getViewModel(): AmityChatHomePageViewModel = mViewModel

	override fun getBindingVariable(): Int = BR.viewModel

	override fun onDestroy() {
		super.onDestroy()
	}

}