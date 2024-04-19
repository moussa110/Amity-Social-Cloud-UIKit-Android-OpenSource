package com.amity.socialcloud.uikit.chat.compose.live.composer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amity.socialcloud.sdk.helper.core.mention.AmityMentionMetadata
import com.amity.socialcloud.sdk.model.core.error.AmityError
import com.amity.socialcloud.sdk.model.core.error.AmityException
import com.amity.socialcloud.uikit.chat.compose.R
import com.amity.socialcloud.uikit.chat.compose.live.mention.AmityMentionSuggestion
import com.amity.socialcloud.uikit.common.ui.base.AmityBaseComponent
import com.amity.socialcloud.uikit.common.ui.base.AmityBaseElement
import com.amity.socialcloud.uikit.common.ui.scope.AmityComposePageScope
import com.amity.socialcloud.uikit.common.ui.theme.AmityTheme
import com.amity.socialcloud.uikit.common.utils.getValue


@Composable
fun AmityLiveChatMessageComposeBar(
    modifier: Modifier = Modifier,
    pageScope: AmityComposePageScope? = null,
    viewModel: AmityLiveChatPageViewModel,
) {
    var messageText by remember { mutableStateOf("") }
    val isTextValid by remember {
        derivedStateOf { messageText.isNotEmpty() }
    }
    var shouldClearText by remember { mutableStateOf(false) }

    var shouldShowSuggestion by remember { mutableStateOf(false) }
    var queryToken by remember { mutableStateOf("") }

    var selectedUserToMention by remember { mutableStateOf<AmityMentionSuggestion?>(null) }
    var mentionedUsers by remember { mutableStateOf<List<AmityMentionMetadata>>(emptyList()) }

    var isReplyingToMessage by remember { mutableStateOf(false) }
    
    val parentMessage by remember { viewModel.replyTo }
    
    fun onDismissParent() { viewModel.dismissReplyMessage() }
    
    val isFetching by remember {
        viewModel.isFetching
    }

    LaunchedEffect(parentMessage) {
        isReplyingToMessage = parentMessage != null
    }
    
    if(!isFetching) {
        
        AmityBaseComponent(
            pageScope = pageScope,
            componentId = "message_composer"
        ) {
            Column(
                modifier = modifier.fillMaxWidth()
            ) {
                
                if (parentMessage != null && isReplyingToMessage) {
                    AmityMessageComposeReplyLabel(
                        parentMessage = parentMessage,
                    ) {
                        isReplyingToMessage = false
                        viewModel.dismissReplyMessage()
                    }
                }
                
                if (shouldShowSuggestion) {
                    AmityMentionSuggestionView(
                        modifier = modifier,
                        keyword = queryToken,
                        viewModel = viewModel
                    ) {
                        selectedUserToMention = it
                        shouldShowSuggestion = false
                    }
                }
                
                HorizontalDivider(
                    color = Color(0xFF292B32) //AmityTheme.colors.divider
                )
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    AmityBaseElement(
                        elementId = "message_composer_text_field",
                        componentScope = getComponentScope()
                    ) {
                        val maxChar: Int = try {
                            getComponentScope().getConfig().getValue("message_limit").toInt()
                        } catch (e: Exception) {
                            10000
                        }
                        val hint: String = try {
                            getComponentScope().getConfig().getValue("placeholder_text")
                        } catch (e: Exception) {
                            "Write a message"
                        }
                        AmityMessageMentionTextField(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("message_composer_text_field"),
                            maxChar = maxChar,
                            hint = hint,
                            maxLines = 5,
                            addedMention = selectedUserToMention,
                            shouldClearText = shouldClearText,
                            onValueChange = {
                                messageText = it
                            },
                            onMentionAdded = {
                                selectedUserToMention = null
                            },
                            onQueryToken = {
                                queryToken = it ?: ""
                                shouldShowSuggestion = (it != null)
                            },
                            onMention = {
                                mentionedUsers = it
                            }
                        )
                    }
                    
                    Button(
                        onClick = {
                            if (messageText.isBlank()) {
                                return@Button
                            }
                            shouldClearText = true
                            viewModel.createMessage(
                                parentId = parentMessage?.getMessageId(),
                                text = messageText.trim(),
                                mentionMetadata = mentionedUsers,
                                onSuccess = {
                                    messageText = ""
                                    selectedUserToMention = null
                                    mentionedUsers = emptyList()
                                    isReplyingToMessage = false
                                    shouldClearText = false
                                    onDismissParent()
                                },
                                onError = { exception ->
                                    messageText = ""
                                    mentionedUsers = emptyList()
                                    shouldClearText = false
                                    selectedUserToMention = null
                                    isReplyingToMessage = false
                                    onDismissParent()
                                    
                                    val errorMessage: String = if (exception is AmityException) {
                                        when (AmityError.from(exception.code)) {
                                            AmityError.BAN_WORD_FOUND -> "Your message wasn't sent as it contained a blocked word."
                                            AmityError.LINK_NOT_ALLOWED -> "Your message wasn't sent as it contained a link that's not allowed."
                                            else -> "unknown error"
                                        }
                                    } else {
                                        "unknown error"
                                    }
                                    getComponentScope().showSnackbar(
                                        message = errorMessage
                                    )
                                }
                            )
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AmityTheme.colors.primary,
                            contentColor = AmityTheme.colors.primaryShade4,
                            disabledContainerColor = AmityTheme.colors.primaryShade2,
                            disabledContentColor = AmityTheme.colors.primaryShade4,
                        ),
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isTextValid) {},
                        contentPadding = PaddingValues(1.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.amity_arrow_upward),
                            contentDescription = "Send",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AmityMessageComposerPreview() {
    AmityLiveChatMessageComposeBar(
        viewModel = AmityLiveChatPageViewModel("")
    )
}