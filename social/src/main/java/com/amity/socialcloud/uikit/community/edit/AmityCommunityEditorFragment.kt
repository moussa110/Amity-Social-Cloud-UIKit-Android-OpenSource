package com.amity.socialcloud.uikit.community.edit

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.amity.socialcloud.sdk.model.social.community.AmityCommunity
import com.amity.socialcloud.uikit.common.utils.AmityConstants.APP_FANTASY_CATEGORY_ID
import com.amity.socialcloud.uikit.common.utils.AmityConstants.APP_PREDICTION_CATEGORY_ID
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.ui.view.AmityCommunityCreateBaseFragment
import com.amity.socialcloud.uikit.community.ui.viewModel.AmityCreateCommunityViewModel
import com.bumptech.glide.Glide
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class AmityCommunityEditorFragment : AmityCommunityCreateBaseFragment() {


	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		loadProfile()
	}

	private fun loadProfile() {
		disposable.add(viewModel.getCommunityDetail().subscribeOn(Schedulers.io())
			.observeOn(AndroidSchedulers.mainThread()).doOnNext {

				viewModel.setCommunityDetails(it)
				if (it.getCategoryIds().contains(APP_FANTASY_CATEGORY_ID) ||  it.getCategoryIds().contains(APP_PREDICTION_CATEGORY_ID)) {
					disableCategory()
				}
				renderAvatar()
			}.doOnError {

			}.subscribe())
	}

	private fun disableChangeVisibility() {
		binding.apply {
			rbPublic.setOnClickListener(null)
			rbPublic.isClickable = false
			rbPublic.isActivated = false
			ivGlobe.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(requireActivity(),
				R.color.yellowInActiveColor))
			tvPublic.setTextColor(ContextCompat.getColor(requireActivity(),
				R.color.fb_gray_placeholder))
			tvPublicDescription.setTextColor(ContextCompat.getColor(requireActivity(),
				R.color.fb_gray_placeholder))
		}
	}

	private fun disableCategory() {
		binding.apply {
			categoryArrow.setOnClickListener(null)
			category.setOnClickListener(null)
			binding.tvCategory.text = getString(R.string.amity_category)
			binding.tvCategory.setTextColor(ContextCompat.getColor(requireActivity(), R.color.fb_gray_placeholder))
		}
	}


	override fun renderAvatar() {
		if (viewModel.avatarUrl.get().isNullOrEmpty()) {
			super.renderAvatar()
		} else {
			Glide.with(requireContext()).load(viewModel.avatarUrl.get()).centerCrop()
				.into(binding.ccAvatar)
		}
	}

	class Builder internal constructor() {
		private lateinit var communityId: String

		fun build(activity: AppCompatActivity): AmityCommunityEditorFragment {
			val fragment = AmityCommunityEditorFragment()
			fragment.viewModel =
				ViewModelProvider(activity).get(AmityCreateCommunityViewModel::class.java)
			fragment.viewModel.communityId.set(communityId)
			fragment.viewModel.savedCommunityId = communityId
			return fragment
		}

		internal fun communityId(communityId: String): Builder {
			this.communityId = communityId
			return this
		}

	}

	companion object {

		fun newInstance(communityId: String): Builder {
			return Builder().communityId(communityId)
		}

		@Deprecated("Use communityId instead")
		fun newInstance(community: AmityCommunity): Builder {
			return Builder().communityId(community.getCommunityId())
		}
	}
}