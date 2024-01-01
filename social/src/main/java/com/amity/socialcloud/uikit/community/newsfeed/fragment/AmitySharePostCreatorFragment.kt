package com.amity.socialcloud.uikit.community.newsfeed.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.amity.socialcloud.sdk.model.social.post.AmityPost
import com.amity.socialcloud.uikit.common.utils.AmityAlertDialogUtil
import com.amity.socialcloud.uikit.common.utils.AmityAndroidUtil
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.newsfeed.model.SharedPostData
import com.amity.socialcloud.uikit.community.newsfeed.util.AmityNewsFeedEvents
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_COMMUNITY_ID
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_POST_ID
import com.amity.socialcloud.uikit.community.utils.EXTRA_PARAM_SHARED_POST_DATA
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AmitySharePostCreatorFragment : AmityBaseCreatePostFragment() {

    override fun handlePostMenuItemClick() {
        view?.let(AmityAndroidUtil::hideKeyboard)
        createPost()
    }

    override fun setToolBarText() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getToolbarTitleForSharePost()
    }

    override fun getPostMenuText(): String {
        return getString(R.string.amity_share_caps)
    }

    override fun onClickNegativeButton() {

    }

    private fun getToolbarTitleForSharePost(): String {
        if (viewModel.community != null)
            return viewModel.community!!.getDisplayName()
        return getString(R.string.amity_my_timeline)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etPost.requestFocus()
    }

    private fun createPost() {
        if (isLoading) {
            return
        }
        isLoading = true
        updatePostMenu(false)
        val ekoPostSingle = viewModel.sharePost(binding.etPost.getTextCompose(),
            binding.etPost.getUserMentions(),viewModel.sharedPostData!!.postId)

        val disposable = ekoPostSingle
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { post ->
                viewModel.checkReviewingPost(
                    feedTye = post.getFeedType(),
                    showDialog = { showPendingPostsDialog(post) },
                    closePage = { handleCreatePostSuccessResponse(post) },
                )
            }
            .doOnError {
                updatePostMenu(true)
                isLoading = false
                showErrorMessage(it.message)
            }
            .subscribe()
        compositeDisposable.add(disposable)
    }
    
    private fun showPendingPostsDialog (post: AmityPost) {
        AmityAlertDialogUtil.showDialog(requireContext(),
            getString(R.string.amity_create_post_pending_post_title_dialog),
            getString(R.string.amity_create_post_pending_post_message_dialog),
            getString(R.string.amity_ok),
            negativeButton = null,
            cancelable = false
        ) { dialog, which ->
            AmityAlertDialogUtil.checkConfirmDialog(
                isPositive = which,
                confirmed = {
                    dialog.dismiss()
                    handleCreatePostSuccessResponse(post)
                })
        }
    }


    private fun handleCreatePostSuccessResponse(post: AmityPost) {
        val resultIntent = Intent("postCreation")
        resultIntent.putExtra(
            EXTRA_PARAM_POST_ID,
            post.getPostId()
        )
        activity?.setResult(Activity.RESULT_OK, resultIntent)
        AmityNewsFeedEvents.newPostCreated = true
        activity?.finish()
    }

    class Builder internal constructor() {
        private var sharedPostData: SharedPostData? = null

        fun build(): AmitySharePostCreatorFragment {
            return AmitySharePostCreatorFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_PARAM_SHARED_POST_DATA, this@Builder.sharedPostData)
                }
            }
        }

        internal fun onMyFeed(postId:String): Builder {
            //this.sharedPostId = postId
            return this
        }

        internal fun onCommunityFeed(sharedPostData: SharedPostData): Builder {
            this.sharedPostData = sharedPostData
            return this
        }

    }

    class PostTargetPicker internal constructor() {

        fun onMyFeed(postId: String): Builder {
            return Builder().onMyFeed(postId)
        }

        fun onCommunityFeed(sharedPostData: SharedPostData): Builder {
            return Builder().onCommunityFeed(sharedPostData)
        }


    }

    companion object {
        fun newInstance(): PostTargetPicker {
            return PostTargetPicker()
        }
    }
}