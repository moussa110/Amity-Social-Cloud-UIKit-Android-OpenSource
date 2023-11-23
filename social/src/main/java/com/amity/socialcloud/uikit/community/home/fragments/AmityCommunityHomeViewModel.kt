package com.amity.socialcloud.uikit.community.home.fragments

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import com.amity.socialcloud.uikit.common.base.AmityBaseViewModel

class AmityCommunityHomeViewModel : AmityBaseViewModel() {
	private var isTanHandled=false
	var isSearchMode = ObservableBoolean(false)
	val emptySearchString = ObservableBoolean(true)

	val showExploreLiveData = MutableLiveData<Pair<Int, Boolean>>()

	fun showExplore(): Boolean {
		if (isTanHandled) return false
		isTanHandled = true
		showExploreLiveData.value = Pair(1, true)
	return true
	}

	fun showNewsFeed(): Boolean {
		if (isTanHandled) return false
		showExploreLiveData.value = Pair(1, false)
		isTanHandled = true
		return true
	}
}