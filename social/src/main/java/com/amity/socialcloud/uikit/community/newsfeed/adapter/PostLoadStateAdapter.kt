package com.amity.socialcloud.uikit.community.newsfeed.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amity.socialcloud.uikit.community.R
import com.amity.socialcloud.uikit.community.databinding.PoastLoadStateViewBinding

class PostLoadStateAdapter() : LoadStateAdapter<PostLoadStateAdapter.LoadStateViewHolder>() {

	override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) = holder.bindItem(loadState)

	override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
		return LoadStateViewHolder(
			LayoutInflater.from(parent.context)
				.inflate(R.layout.poast_load_state_view, parent, false)
		)
	}

	class LoadStateViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
		val binding:PoastLoadStateViewBinding = PoastLoadStateViewBinding.bind(view)
		fun bindItem(loadState: LoadState) {
			binding.apply {
				shimmerLayout.startShimmer()
				/*loadStateRetry.isVisible = loadState !is LoadState.Loading
				loadStateErrorMessage.isVisible = loadState !is LoadState.Loading
				loadStateProgress.isVisible = loadState is LoadState.Loading
				if (loadState is LoadState.Error){
					loadStateErrorMessage.text = loadState.error.localizedMessage
				}

				loadStateRetry.setOnClickListener {
					retry.invoke()
				}*/
			}
		}
	}
}