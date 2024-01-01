package com.amity.socialcloud.uikit.feed.settings

class AmityPostSharingSettings {
    var privateCommunityPostSharingTarget = listOf<AmityPostSharingTarget>()

    var publicCommunityPostSharingTarget = listOf(
        AmityPostSharingTarget.OriginFeed,
        AmityPostSharingTarget.MyFeed,
        AmityPostSharingTarget.PublicCommunity,
        AmityPostSharingTarget.PrivateCommunity,
        AmityPostSharingTarget.External
    )
    var myFeedPostSharingTarget = listOf(
        AmityPostSharingTarget.OriginFeed,
        AmityPostSharingTarget.MyFeed,
        AmityPostSharingTarget.PublicCommunity,
        AmityPostSharingTarget.PrivateCommunity,
        AmityPostSharingTarget.External
    )
    var userFeedPostSharingTarget = listOf(
        AmityPostSharingTarget.OriginFeed,
        AmityPostSharingTarget.MyFeed,
        AmityPostSharingTarget.PublicCommunity,
        AmityPostSharingTarget.PrivateCommunity
    )

}