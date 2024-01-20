package com.amity.socialcloud.uikit.community.utils

import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.community.newsfeed.model.SharedPost
import com.google.gson.Gson
import com.google.gson.JsonElement

import com.google.gson.JsonObject


enum class NewsFeedMetaDataKeys(val key: String) {
	IS_POST_PINNED("isPostPinned"),
	SHARED_POST_ID("sharedPostId")
}

fun AmityPost.getSharedPostId():String?{
	getMetadata()?.let {
		return it.get(NewsFeedMetaDataKeys.SHARED_POST_ID.key)?.asString
	}
	return null
}


/*fun AmityPost.getSharedPost(): SharedPost? {
	getMetadata()?.let {
		 it.getAsJsonObject(NewsFeedMetaDataKeys.SHARED_POST_KEY.key)?.let {sharedPost->
			 try {
				 return Gson().fromJson(sharedPost, SharedPost::class.java)
			 }
			 catch (e:Exception){
				 e.printStackTrace()
				 return null
			 }
		}
	}
	return null
}*/


fun mergeJsonObjects(jsonObjects: List<JsonObject>): JsonObject {
	val mergedJson = JsonObject()
	jsonObjects.forEach{ jsonObj: JsonObject ->
		val entrySet = jsonObj.entrySet()
		entrySet.forEach{ (key, value): Map.Entry<String?, JsonElement?> ->
			mergedJson.add(key, value)
		}
	}
	return mergedJson
}

