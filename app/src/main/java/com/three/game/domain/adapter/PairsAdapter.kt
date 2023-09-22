package com.three.game.domain.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.three.game.R
import com.three.game.databinding.ItemPairBinding
import com.three.game.domain.second_game.PairsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PairsAdapter(private var onItemClick: ((Int) -> Unit)? = null) :
    RecyclerView.Adapter<PairsViewHolder>() {
    var items = mutableListOf<PairsItem>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PairsViewHolder {
        return PairsViewHolder(
            ItemPairBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PairsViewHolder, position: Int) {
        holder.bind(items[position])
        holder.onItemClick = onItemClick
    }
}

class PairsViewHolder(private val binding: ItemPairBinding) :
    RecyclerView.ViewHolder(binding.root) {
    var onItemClick: ((Int) -> Unit)? = null
    fun bind(item: PairsItem) {
        binding.apply {
            val img = when (item.value) {
                1 -> R.drawable.game02_symbol01
                2 -> R.drawable.game02_symbol02
                3 -> R.drawable.game02_symbol03
                4 -> R.drawable.game02_symbol04
                5 -> R.drawable.game02_symbol05
                else -> R.drawable.game02_symbol06
            }
            if (item.isOpened) {
                itemImg.setImageResource(img)
            } else {
                itemImg.setImageResource(R.drawable.game02_symbol_closed)
            }
            if (item.openAnimation) {
                flipImage(binding.root, img, R.drawable.game02_symbol_closed)
            }

            if (item.closeAnimation) {
                flipImage(binding.root, null, R.drawable.game02_symbol_closed)
            }

            binding.root.setOnClickListener {
                if (!item.openAnimation && !item.closeAnimation && !item.isOpened) {
                    onItemClick?.invoke(adapterPosition)
                }
            }
        }
    }

    private fun flipImage(
        view: View,
        @DrawableRes img: Int?,
        @DrawableRes imgBox: Int,
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            delay(200)
            if (img != null) {
                binding.itemImg.setImageResource(img)
            } else {
                binding.itemImg.setImageResource(imgBox)
            }
        }
        val animatorSet = AnimatorSet()
        val rotateAnimator = ObjectAnimator.ofFloat(view, "rotationY", 0f, 180f)
        rotateAnimator.duration = 400

        val scaleXAnimator = ValueAnimator.ofFloat(1f, -1f)
        scaleXAnimator.addUpdateListener { animator ->
            val scale = animator.animatedValue as Float
            view.scaleX = scale
        }
        scaleXAnimator.duration = 400

        animatorSet.playTogether(rotateAnimator, scaleXAnimator)
        animatorSet.start()
    }

}