package com.amity.socialcloud.uikit.community.newsfeed.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream

@Parcelize
data class SharedPostData(private var fileName: String? = null,
                          var postId: String,
                          var communityId: String? = null,
                          var isTargetPostHasSharedPost: Boolean = false) : Parcelable {

	fun setBitmap(bitmap: Bitmap, context: Context) {
		fileName = createImageFromBitmap(bitmap, context)
	}

	private fun createImageFromBitmap(bitmap: Bitmap, context: Context): String? {
		context.apply {
			var fileName: String? = "shared_post.png"
			try {
				val bytes = ByteArrayOutputStream()
				bitmap.compress(Bitmap.CompressFormat.PNG, 80, bytes)
				val fo: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)
				fo.write(bytes.toByteArray())
				fo.close()
			} catch (e: java.lang.Exception) {
				e.printStackTrace()
				fileName = null
			}
			return fileName
		}
	}

	fun getBitmap(context: Context): Bitmap? {
		return BitmapFactory.decodeStream(context.openFileInput("shared_post.png"))
	}

}
