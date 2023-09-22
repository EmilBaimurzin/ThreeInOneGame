package com.three.game.ui.games.third

import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.three.game.R
import com.three.game.core.library.GameFragment
import com.three.game.databinding.FragmentThirdGameBinding
import com.three.game.domain.other.SP
import com.three.game.ui.other.CallbackViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentThirdGame: GameFragment<FragmentThirdGameBinding>(FragmentThirdGameBinding::inflate) {
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    private val sp by lazy { SP(requireContext()) }
    private val viewModel: ThirdGameViewModel by viewModels()
    private var moveScope = CoroutineScope(Dispatchers.Default)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackViewModel.callback = {
            viewModel.pauseState = false
            viewModel.start(
                dpToPx(70),
                xy.x.toInt(),
                1200L,
                10L,
                xy.y.toInt(),
                binding.player.width,
                binding.player.height,
                distance = 8
            )
        }

        binding.pause.setOnClickListener {
            viewModel.stop()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentThirdGame_to_dialogPause)
        }

        binding.menu.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.player.apply {
                x = it.x
                y = it.y
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.score.text = it.toString()
        }

        viewModel.lives.observe(viewLifecycleOwner) {
            binding.livesLayout.removeAllViews()
            repeat(it) {
                val heartView = ImageView(requireContext())
                heartView.layoutParams = LinearLayout.LayoutParams(dpToPx(25), dpToPx(25)).apply {
                    marginStart = dpToPx(3)
                    marginEnd = dpToPx(3)
                }
                heartView.setImageResource(R.drawable.game03_live)
                binding.livesLayout.addView(heartView)
            }

            if (it == 0 && viewModel.gameState) {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.gameState = false
                    viewModel.stop()
                    if (sp.getBestScore(3) < viewModel.scores.value!!) {
                        sp.setBestScore(3, viewModel.scores.value!!)
                    }
                    findNavController().navigate(
                        FragmentThirdGameDirections.actionFragmentThirdGameToFragmentFinalResult(
                            3,
                            viewModel.scores.value!!
                        )
                    )
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.trigger.collect {
                    binding.symbolsLayout.removeAllViews()
                    viewModel.symbols.value.forEach { symbol ->
                        val symbolView = ImageView(requireContext())
                        symbolView.layoutParams =
                            ViewGroup.LayoutParams(dpToPx(70), dpToPx(70))
                        val img = when (symbol.value) {
                            1 -> R.drawable.game03_symbol01
                            2 -> R.drawable.game03_symbol02
                            3 -> R.drawable.game03_symbol03
                            4 -> R.drawable.game03_symbol04
                            5 -> R.drawable.game03_symbol05
                            6 -> R.drawable.game03_symbol06
                            else -> R.drawable.game03_enemy
                        }
                        symbolView.setImageResource(img)
                        symbolView.x = symbol.x
                        symbolView.y = symbol.y
                        binding.symbolsLayout.addView(symbolView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            delay(10)
            if (viewModel.playerXY.value!!.y == 0f) {
                viewModel.initPlayer(xy.x.toInt(), xy.y.toInt(), binding.player.width, binding.player.height + dpToPx(20))
            }

            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(70),
                    xy.x.toInt(),
                    1200,
                    10L,
                    xy.y.toInt(),
                    binding.player.width,
                    binding.player.height,
                    distance = 8
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setButtons()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtons() {
        binding.root.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            if (motionEvent.x > xy.x / 2) {
                                viewModel.playerMoveRight((xy.x - binding.player.width).toFloat())
                                delay(2)
                            } else {
                                viewModel.playerMoveLeft(0f)
                                delay(2)
                            }
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            if (motionEvent.x > xy.x / 2) {
                                viewModel.playerMoveRight((xy.x - binding.player.width).toFloat())
                                delay(2)
                            } else {
                                viewModel.playerMoveLeft(0f)
                                delay(2)
                            }
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}