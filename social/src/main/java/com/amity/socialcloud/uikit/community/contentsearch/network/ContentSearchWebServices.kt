package com.amity.socialcloud.uikit.community.contentsearch.network

import com.amity.socialcloud.uikit.community.contentsearch.data.ContentSearchRequest
import com.amity.socialcloud.uikit.community.contentsearch.data.ContentSearchResponse
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface ContentSearchWebServices {
    @POST("posts")
    fun search(@Body request: ContentSearchRequest, @Header("Authorization") bearer:String) : Flowable<ContentSearchResponse>
   }