package com.amity.socialcloud.uikit.community.contentsearch.data

data class ContentSearchRequest(
    val query: Query,
    var sort: List<Sort?>? = null
) {
    data class Query(
        var categoryId: String? = null,
        var createdAt: CreatedAt? = null,
        var flagCount: FlagCount? = null,
        var haghtagList: List<String?>? = null,
        var hasFlaggedChildren: Boolean? = null,
        var hasFlaggedComment: Boolean? = null,
        var lastActivity: String? = null,
        var mentionees: List<String?>? = null,
        var metadata: Metadata? = null,
        var postedUserId: String? = null,
        var publicSearch: Boolean = false,
        var reactionsCount: ReactionsCount? = null,
        var sharedCount: SharedCount? = null,
        var tags: List<String?>? = null,
        var targetId: List<String?>? = null,
        var targetType: String? = null,
        var text: String? = null,
        var updatedAt: UpdatedAt? = null
    ) {
        data class CreatedAt(
            var gt: String? = null
        )

        data class FlagCount(
            var gt: Int? = null
        )

        data class Metadata(
            var Field: String? = null
        )

        data class ReactionsCount(
            var gt: Int? = null,
            var lt: Int? = null
        )

        data class SharedCount(
            var gt: Int? = null,
            var lt: Int? = null
        )

        data class UpdatedAt(
            var gt: String? = null
        )
    }

    data class Sort(
        var createdAt: CreatedAt? = null
    ) {
        data class CreatedAt(
            var order: String? = null
        )
    }
}