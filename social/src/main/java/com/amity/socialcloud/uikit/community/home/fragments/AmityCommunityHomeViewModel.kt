package com.amity.socialcloud.uikit.community.home.fragments

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amity.socialcloud.uikit.common.base.AmityBaseViewModel

class AmityCommunityHomeViewModel : AmityBaseViewModel() {


	var isSearchMode = ObservableBoolean(false)
	val emptySearchString = ObservableBoolean(true)
	val showExploreLiveData = MutableLiveData<Pair<Int, Boolean>>()

	private  var _searchQueryLiveData = MutableLiveData("")
	val searchQueryLiveData:LiveData<String> get() = _searchQueryLiveData

	fun showExplore() {
		showExploreLiveData.value = Pair(1, true)
	}

	fun showNewsFeed() {
		showExploreLiveData.value = Pair(1, false)
	}

	enum class ActionBarAction{
		BACK,SEARCH,NONE
	}

	fun search(query:String){
		_searchQueryLiveData.value = query
	}
}