package com.amity.socialcloud.uikit.common.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE

import android.content.SharedPreferences




class SharedPrefsUtils (context: Context){
	object Keys{
		const val openNewsFeed="openNewsFeed"
	}

	private var sharedPreferences: SharedPreferences = context.getSharedPreferences("AMITY_PREFS", MODE_PRIVATE)
	private var editor: SharedPreferences.Editor = sharedPreferences.edit()

	fun setBooleanValue(key: String?, value: Boolean) {
		editor.putBoolean(key, value)
		editor.commit()
	}

	fun getBooleanValue(key: String?, defaultValue: Boolean): Boolean {
		return sharedPreferences.getBoolean(key, defaultValue)
	}
}