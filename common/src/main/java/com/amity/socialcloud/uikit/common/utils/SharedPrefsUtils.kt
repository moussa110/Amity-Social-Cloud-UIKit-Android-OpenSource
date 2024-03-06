package com.amity.socialcloud.uikit.common.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences
import com.amity.socialcloud.uikit.common.utils.SharedPrefsUtils.Keys.IS_AMITY_USER_LOGGED_IN


class SharedPrefsUtils (context: Context){
	object Keys{
		const val openNewsFeed="openNewsFeed"
		const val IS_AMITY_USER_LOGGED_IN = "isAmityUserLogging"
	}

	private var sharedPreferences: SharedPreferences = context.getSharedPreferences("AMITY_PREFS", MODE_PRIVATE)
	private var editor: SharedPreferences.Editor = sharedPreferences.edit()

	private fun setBooleanValue(key: String?, value: Boolean) {
		editor.putBoolean(key, value)
		editor.commit()
	}

	private fun getBooleanValue(key: String?, defaultValue: Boolean): Boolean {
		return sharedPreferences.getBoolean(key, defaultValue)
	}

	fun isUserLoginBefore(): Boolean {
		return getBooleanValue(IS_AMITY_USER_LOGGED_IN,false)
	}

	fun setIsUserLogin(isLogin:Boolean){
		setBooleanValue(IS_AMITY_USER_LOGGED_IN,isLogin)
	}

}