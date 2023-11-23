package com.amity.socialcloud.uikit.community.utils

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.single
import java.math.RoundingMode
import java.text.DecimalFormat

fun Long.formatFollowers(): String? {
    if ((this / 1000) > 1) {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        var formattedNumber = this.toDouble() / 1000;
        return df.format(formattedNumber).toString() + "K";
    }
    return this.toString()
}

fun Int.formatFollowers(): String? {
    if ((this / 1000) > 1) {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        var formattedNumber = this.toDouble() / 1000;
        return df.format(formattedNumber).toString() + "K";
    }
    return this.toString()
}

@Suppress("UNCHECKED_CAST")
suspend fun <T : Any> PagingData<T>.toList(): List<T> {
    val flow = PagingData::class.java.getDeclaredField("flow").apply {
        isAccessible = true
    }.get(this) as Flow<Any?>
    val pageEventInsert = flow.single()
    val pageEventInsertClass = Class.forName("androidx.paging.PageEvent\$Insert")
    val pagesField = pageEventInsertClass.getDeclaredField("pages").apply {
        isAccessible = true
    }
    val pages = pagesField.get(pageEventInsert) as List<Any?>
    val transformablePageDataField =
        Class.forName("androidx.paging.TransformablePage").getDeclaredField("data").apply {
            isAccessible = true
        }
    val listItems =
        pages.flatMap { transformablePageDataField.get(it) as List<*> }
    return listItems as List<T>
}