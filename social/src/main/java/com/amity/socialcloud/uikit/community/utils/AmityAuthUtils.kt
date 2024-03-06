package com.amity.socialcloud.uikit.community.utils

import android.content.Context
import com.amity.socialcloud.sdk.api.core.AmityCoreClient
import com.amity.socialcloud.sdk.core.session.AccessTokenRenewal
import com.amity.socialcloud.sdk.core.session.model.SessionState
import com.amity.socialcloud.sdk.model.core.session.SessionHandler
import com.amity.socialcloud.uikit.common.utils.SharedPrefsUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

fun loginUserInAmity(context: Context,userId: String, name: String, isDone: () -> Unit){
	observeSessionState { isLoggedIn->
		if (isLoggedIn) isDone()
		else {
			if (SharedPrefsUtils(context).isUserLoginBefore()){
				AmityCoreClient.login(userId, object : SessionHandler {
					override fun sessionWillRenewAccessToken(renewal: AccessTokenRenewal) {}
				}).build().submit().subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread()).doOnComplete {
						isDone()
					}.doOnError {
						isDone()
					}.subscribe()
			}else{
				AmityCoreClient.login(userId, object : SessionHandler {
					override fun sessionWillRenewAccessToken(renewal: AccessTokenRenewal) {}
				}).displayName(name).build().submit().subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread()).doOnComplete {
						isDone()
						SharedPrefsUtils(context).setIsUserLogin(true)
					}.doOnError {
						isDone()
					}.subscribe()
			}
		}
	}

}

private fun observeSessionState(isLoggedInListener:(Boolean)->Unit) {
	AmityCoreClient.observeSessionState().subscribeOn(Schedulers.io())
		.observeOn(AndroidSchedulers.mainThread()).doOnNext { sessionState: SessionState ->
			if (sessionState is SessionState.Established) isLoggedInListener(true)
			else isLoggedInListener(false)
		}.doOnError {
			isLoggedInListener(false)
		}.subscribe()
}