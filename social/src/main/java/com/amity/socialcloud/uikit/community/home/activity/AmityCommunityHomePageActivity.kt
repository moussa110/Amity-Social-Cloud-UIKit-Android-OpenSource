package com.amity.socialcloud.uikit.community.home.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import com.amity.socialcloud.uikit.common.utils.AmityAndroidUtil
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityActivityCommunityHomeBinding
import com.amity.socialcloud.uikit.community.home.fragments.AmityCommunityHomePageFragment
import com.amity.socialcloud.uikit.community.home.fragments.AmityCommunityHomeViewModel


class AmityCommunityHomePageActivity : AppCompatActivity() {


	private val binding: AmityActivityCommunityHomeBinding by lazy {
		AmityActivityCommunityHomeBinding.inflate(layoutInflater)
	}

	private val viewModel: AmityCommunityHomeViewModel by viewModels()


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(binding.root)
		initToolbar()
		loadFragment()
		initActions()
		handleSearchView()
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
		val searchEditText = binding.homeSearchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
		searchEditText.background = ContextCompat.getDrawable(this, R.drawable.rounded_gray_serarch_bg)
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
		// binding.communityHomeToolbar.setLeftString(getString(R.string.amity_community))
	}

	private fun loadFragment() {
		val fragmentManager = supportFragmentManager
		val fragmentTransaction = fragmentManager.beginTransaction()
		val fragment = AmityCommunityHomePageFragment.newInstance().build()
		fragmentTransaction.replace(R.id.fragmentContainer, fragment)
		fragmentTransaction.commit()
	}



}