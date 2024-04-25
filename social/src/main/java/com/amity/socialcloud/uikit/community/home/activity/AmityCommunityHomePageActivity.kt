package com.amity.socialcloud.uikit.community.home.activity

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.core.session.AccessTokenRenewal
import com.amity.socialcloud.sdk.model.core.session.SessionHandler
import com.amity.socialcloud.uikit.chat.home.AmityChatHomePageActivity
import com.amity.socialcloud.uikit.common.utils.AmityAndroidUtil
import com.amity.socialcloud.uikit.common.utils.SharedPrefsUtils
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityActivityCommunityHomeBinding
import com.amity.socialcloud.uikit.community.home.fragments.AmityCommunityHomePageFragment
import com.amity.socialcloud.uikit.community.home.fragments.AmityCommunityHomeViewModel
import com.amity.socialcloud.uikit.community.newsfeed.activity.AmityPostDetailsActivity
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_POST_ID
import com.amity.socialcloud.uikit.community.utils.loginUserInAmity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers


class AmityCommunityHomePageActivity : AppCompatActivity() {
	@SuppressLint("DiscouragedApi", "InternalInsetResource")
	private fun getStatusBarHeight(): Int {
		val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
		return if (resourceId > 0) {
			resources.getDimensionPixelSize(resourceId)
		} else {
			resources.getDimensionPixelSize(com.amity.socialcloud.uikit.common.R.dimen.amity_twenty_eight)
		}
	}

	private val binding: AmityActivityCommunityHomeBinding by lazy {
		AmityActivityCommunityHomeBinding.inflate(layoutInflater)
	}

	companion object {
		private const val USER_ID = "userId"
		private const val FULL_NAME = "fullName"


		fun createIntentToOpenPostDetails(context: Context, postId: String) =
			Intent(context, AmityCommunityHomePageActivity::class.java).apply {
				putExtra(EXTRA_PARAM_POST_ID, postId)
			}

		fun createIntentFromKoraKings(id: String, fullName: String, context: Context): Intent {
			Intent(context, AmityCommunityHomePageActivity::class.java).also {
				it.putExtra(USER_ID, id)
				it.putExtra(FULL_NAME, fullName)
				return it
			}
		}
	}

	private val viewModel: AmityCommunityHomeViewModel by viewModels()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		initToolbar()
		handleSearchView()
		initActions()
		intent.getStringExtra(USER_ID).let { userId ->
			if (userId != null) {
				binding.progressBar.isVisible = true
				val name = intent.getStringExtra(FULL_NAME)
				loginUserInAmity(this, userId, name!!) {
					setupViews()
				}
			} else loadFragment()
		}
	}


	private fun setupViews() {
		Handler(Looper.getMainLooper()).postDelayed({
			try {
				binding.fragmentContainer.background = null
			} catch (_: Exception) { }
		}, 1000)
		loadFragment()
		binding.progressBar.isVisible = false
	}

	private fun checkIfIsToOpenPostDetails() {
		intent?.getStringExtra(EXTRA_PARAM_POST_ID)?.let {
			AmityPostDetailsActivity.newIntent(this, it).also {
				startActivity(it)
				finish()
			}
		}
	}

	private fun initActions() {
		binding.apply {
			back.setOnClickListener {
				onBackPressedDispatcher.onBackPressed()
			}
		}
	}

	private fun handleSearchView() {
		viewModel.search("")
		val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
		val searchEditText =
			binding.homeSearchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
		searchEditText.background =
			ContextCompat.getDrawable(this, R.drawable.rounded_gray_serarch_bg)
		searchEditText.setTextColor(ContextCompat.getColor(this, R.color.amityColorPrimary))
		searchEditText.setHintTextColor(ContextCompat.getColor(this, R.color.fb_gray))
		searchEditText.setHint(R.string.amity_search)
		searchEditText.imeOptions = EditorInfo.IME_ACTION_NEXT
		searchEditText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
			override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
				if (actionId == EditorInfo.IME_ACTION_NEXT) {
					AmityAndroidUtil.hideKeyboard(searchEditText)
					return true
				}
				return false
			}
		})
		binding.homeSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
		val queryTextListener = object : SearchView.OnQueryTextListener {
			override fun onQueryTextChange(newText: String?): Boolean {
				newText?.let { viewModel.search(it) }
				return true
			}

			override fun onQueryTextSubmit(query: String?): Boolean {
				query?.let { viewModel.search(it) }
				return true
			}
		}

		binding.homeSearchView.setOnQueryTextListener(queryTextListener)
		binding.homeSearchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
			if (hasFocus) {
				viewModel.isSearchMode.set(true)
				binding.logoIv.visibility = View.INVISIBLE
				binding.back.visibility = View.INVISIBLE
			} else {
				viewModel.isSearchMode.set(false)
				binding.homeSearchView.onActionViewCollapsed()
				binding.logoIv.visibility = View.VISIBLE
				binding.back.visibility = View.VISIBLE
			}
		}
	}

	private fun initToolbar() {
		setSupportActionBar(binding.toolbar)
		binding.toolbar.setPadding(0, getStatusBarHeight(), 0, 0)
		// binding.communityHomeToolbar.setLeftString(getString(R.string.amity_community))
	}

	private var fragmentHome: AmityCommunityHomePageFragment? = null
	private fun loadFragment() {
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		fragmentHome = AmityCommunityHomePageFragment.newInstance().build()
		fragmentTransaction.replace(R.id.fragmentContainer, fragmentHome!!)
		fragmentTransaction.commit()
	}


}