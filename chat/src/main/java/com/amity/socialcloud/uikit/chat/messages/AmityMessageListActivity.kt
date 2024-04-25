package com.amity.socialcloud.uikit.chat.messages

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.amity.socialcloud.uikit.chat.R
import com.amity.socialcloud.uikit.chat.compose.live.AmityLiveChatFragment
import com.amity.socialcloud.uikit.chat.messages.composebar.AmityChatRoomComposeBar
import com.amity.socialcloud.uikit.chat.messages.fragment.AmityChatRoomFragment
import com.amity.socialcloud.uikit.common.utils.AmityThemeUtil

class AmityMessageListActivity : AppCompatActivity() {
    private lateinit var channelId: String

    companion object {
        private const val INTENT_CHANNEL_ID = "channelID"
        private const val INTENT_IS_TEXT_ONLY = "isTextOnly"
        private const val INTENT_IS_FROM_KK = "isFromKK"


        fun newIntent(context: Context, channelId: String, isTextOnly: Boolean = false): Intent {
            return Intent(context, AmityMessageListActivity::class.java).apply {
                putExtra(INTENT_CHANNEL_ID, channelId)
                putExtra(INTENT_IS_TEXT_ONLY, isTextOnly)
            }
        }

        fun newIntentKK(context: Context, channelId: String): Intent {
            return Intent(context, AmityMessageListActivity::class.java).apply {
                putExtra(INTENT_CHANNEL_ID, channelId)
                putExtra(INTENT_IS_TEXT_ONLY, false)
                putExtra(INTENT_IS_FROM_KK, true)
            }
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AmityThemeUtil.setCurrentTheme(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.amity_activity_chat)
        channelId = intent.getStringExtra(INTENT_CHANNEL_ID) ?: ""
        initializeFragment()
    }

    @OptIn(UnstableApi::class)
    private fun initializeFragment() {
        //val composeFragment = AmityLiveChatFragment.newInstance(channelId).build()
        val isTextOnly = intent.getBooleanExtra(INTENT_IS_TEXT_ONLY, false)
        val composebar = if (isTextOnly) AmityChatRoomComposeBar.TEXT else AmityChatRoomComposeBar.DEFAULT

        val messageListFragment = AmityChatRoomFragment.newInstance(channelId)
            .enableChatToolbar(true)
            .enableConnectionBar(true)
            .composeBar(composebar)
            .isFromKK(intent.getBooleanExtra(INTENT_IS_FROM_KK,false))
            .build(this)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.messageListContainer, messageListFragment)
        transaction.commit()
    }
}