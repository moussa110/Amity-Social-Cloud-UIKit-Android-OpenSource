package com.amity.socialcloud.uikit.community.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.uikit.common.base.AmityFragmentStateAdapter
import com.amity.socialcloud.uikit.common.model.AmityEventIdentifier
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.AmityFragmentCommunityHomePageBinding
import com.amity.socialcloud.uikit.community.explore.fragments.AmityCommunityExplorerFragment
import com.amity.socialcloud.uikit.community.mycommunity.fragment.AmityMyCommunityFragment
import com.amity.socialcloud.uikit.community.newsfeed.fragment.AmityNewsFeedFragment
import com.amity.socialcloud.uikit.community.profile.fragment.AmityUserProfilePageFragment
import com.amity.socialcloud.uikit.community.search.AmityUserSearchFragment
import com.amity.socialcloud.uikit.community.setting.AmityCommunitySearchFragment
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit


class AmityCommunityHomePageFragment : Fragment() {
	private lateinit var fragmentStateAdapter: AmityFragmentStateAdapter
	private lateinit var globalSearchStateAdapter: AmityFragmentStateAdapter
	private lateinit var binding: AmityFragmentCommunityHomePageBinding
	private var isNavigatedToExplore = false
	private val viewModel: AmityCommunityHomeViewModel by activityViewModels()
	private var textChangeDisposable: Disposable? = null
	private val textChangeSubject: PublishSubject<String> = PublishSubject.create()
	private val searchString = ObservableField("")

	override fun onCreateView(inflater: LayoutInflater,
	                          container: ViewGroup?,
	                          savedInstanceState: Bundle?): View {
		binding = DataBindingUtil.inflate(inflater,
			R.layout.amity_fragment_community_home_page,
			container,
			false)
		binding.viewModel = viewModel
		binding.tabLayout.apply {
			disableSwipe()
			setOffscreenPageLimit(1)
		}
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setHasOptionsMenu(true)
		initTabLayout()
		setUpSearchTabLayout()
		addViewModelListeners()
		subscribeTextChangeEvents()
	}


	override fun onDestroyView() {
		super.onDestroyView()
		if (textChangeDisposable?.isDisposed == false) {
			textChangeDisposable?.dispose()
		}
	}

	fun initTabLayout() {
		fragmentStateAdapter =
			AmityFragmentStateAdapter(childFragmentManager, requireActivity().lifecycle)
		globalSearchStateAdapter =
			AmityFragmentStateAdapter(childFragmentManager, requireActivity().lifecycle)
		fragmentStateAdapter.setFragmentList(arrayListOf(AmityFragmentStateAdapter.AmityPagerModel(
			getString(R.string.amity_title_news_feed),
                    getNewsFeedFragment()
                ),
                AmityFragmentStateAdapter.AmityPagerModel(
                    getString(R.string.amity_title_explore),
                    getExploreFragment()
                ),
                AmityFragmentStateAdapter.AmityPagerModel(
                    getString(R.string.amity_title_my_communities),
                    getMyCommunityFragment()
                )
            )
        )
        binding.tabLayout.setAdapter(fragmentStateAdapter)
	}

	private fun getExploreFragment(): Fragment {
		return AmityCommunityExplorerFragment.newInstance().build()
	}

	private fun getMyProfileFragment(): Fragment {
		return AmityUserProfilePageFragment.newInstance(AmityCoreClient.getUserId())
			.build(requireActivity())
	}

	private fun getNewsFeedFragment(): Fragment {
		return AmityNewsFeedFragment.newInstance().build()
	}

    private fun getMyCommunityFragment(): Fragment {
        return AmityMyCommunityFragment.newInstance().build()
    }

	private fun addViewModelListeners() {
		viewModel.showExploreLiveData.observe(viewLifecycleOwner) {
			if (isNavigatedToExplore) return@observe
			if (it.first != 0) {
				if (it.second) {
					isNavigatedToExplore = true
					binding.tabLayout.switchTab(1)
				} else binding.tabLayout.switchTab(0)
			}
		}

		viewModel.onAmityEventReceived += { event ->
			when (event.type) {
				AmityEventIdentifier.EXPLORE_COMMUNITY -> {
					//searchMenuItem.expandActionView()
					binding.tabLayout.switchTab(1)
				}

				else -> {
					binding.tabLayout.switchTab(0)
				}
			}
		}
	}

	private fun setUpSearchTabLayout() {
		globalSearchStateAdapter.setFragmentList(arrayListOf(AmityFragmentStateAdapter.AmityPagerModel(getString(R.string.amity_communities),
			AmityCommunitySearchFragment.newInstance(searchString)
				.build(requireActivity() as AppCompatActivity)),
			AmityFragmentStateAdapter.AmityPagerModel(getString(R.string.amity_accounts),
				AmityUserSearchFragment.newInstance(searchString)
					.build(requireActivity() as AppCompatActivity))))
		binding.globalSearchTabLayout.setAdapter(globalSearchStateAdapter)
	}

	private fun subscribeTextChangeEvents() {
		viewModel.searchQueryLiveData.observe(viewLifecycleOwner) {
			textChangeSubject.onNext(it)
		}
		textChangeDisposable = textChangeSubject.debounce(300, TimeUnit.MILLISECONDS).map {
			if (searchString.get() != it) {
				searchString.set(it)
			}
			viewModel.emptySearchString.set(it.isEmpty())
		}.subscribe()
	}

	class Builder internal constructor() {
		fun build(): AmityCommunityHomePageFragment {
			return AmityCommunityHomePageFragment()
		}
	}

	companion object {

		fun newInstance(): Builder {
			return Builder()
		}
	}
}