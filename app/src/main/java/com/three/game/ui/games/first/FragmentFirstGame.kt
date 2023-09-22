package com.three.game.ui.games.first

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.three.game.R
import com.three.game.core.library.GameFragment
import com.three.game.databinding.FragmentFirstGameBinding
import com.three.game.domain.other.SP
import com.three.game.ui.other.CallbackViewModel
import io.github.hyuwah.draggableviewlib.DraggableListener
import io.github.hyuwah.draggableviewlib.DraggableView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentFirstGame :
    GameFragment<FragmentFirstGameBinding>(FragmentFirstGameBinding::inflate) {
    private val viewModel: FirstGameViewModel by viewModels()
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    private val sp by lazy {
        SP(requireContext())
    }
    private lateinit var playerPlaneView: DraggableView<ImageView>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayerView()
        viewLifecycleOwner

        binding.menu.setOnClickListener {
            findNavController().popBackStack()
        }

        callbackViewModel.callback = {
            viewModel.pauseState = false
            viewModel.start(
                dpToPx(80),
                dpToPx(120),
                xy.y.toInt(),
                xy.x.toInt(),
                dpToPx(10),
                dpToPx(30),
                binding.player.width
            )
        }

        binding.pause.setOnClickListener {
            viewModel.stop()
            viewModel.pauseState = true
            findNavController().navigate(R.id.action_fragmentFirstGame_to_dialogPause)
        }

        viewModel.endCallback = {
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.stop()
                viewModel.gameState = false

                if (sp.getBestScore(1) < viewModel.score.value) {
                    sp.setBestScore(1, viewModel.score.value)
                }
                findNavController().navigate(
                    FragmentFirstGameDirections.actionFragmentFirstGameToFragmentFinalResult(
                        1,
                        viewModel.score.value
                    )
                )
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bullets.collect {
                    binding.bulletsLayout.removeAllViews()
                    it.forEach { bullet ->
                        val bulletView = ImageView(requireContext())
                        bulletView.layoutParams = ViewGroup.LayoutParams(dpToPx(10), dpToPx(30))
                        bulletView.setImageResource(R.drawable.game01_bullet)
                        bulletView.x = bullet.x
                        bulletView.y = bullet.y
                        binding.bulletsLayout.addView(bulletView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.enemies.collect {
                    binding.enemyLayout.removeAllViews()
                    it.forEach { enemy ->
                        val enemyView = ImageView(requireContext())
                        enemyView.layoutParams = ViewGroup.LayoutParams(dpToPx(120), dpToPx(80))
                        enemyView.setImageResource(R.drawable.game01_enemy)
                        enemyView.x = enemy.x
                        enemyView.y = enemy.y
                        binding.enemyLayout.addView(enemyView)
                    }
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.score.collect {
                    binding.score.text = it.toString()
                }
            }
        }

        lifecycleScope.launch {
            delay(10)
            if (viewModel.playerXY.x == 0f) {
                viewModel.setPlayerXY(
                    ((xy.x / 2) - (binding.player.width / 2)),
                    xy.y - dpToPx(180).toFloat()
                )
                playerPlaneView.getView().x = viewModel.playerXY.x
                playerPlaneView.getView().y = viewModel.playerXY.y
            }
            if (viewModel.gameState && !viewModel.pauseState) {
                viewModel.start(
                    dpToPx(80),
                    dpToPx(120),
                    xy.y.toInt(),
                    xy.x.toInt(),
                    dpToPx(10),
                    dpToPx(30),
                    binding.player.width
                )
            }
        }
    }

    private fun setupPlayerView() {
        playerPlaneView = DraggableView.Builder(binding.player)
            .setListener(object : DraggableListener {
                override fun onPositionChanged(view: View) {
                    viewModel.setPlayerXY(x = view.x, y = view.y)
                }
            })
            .build()
        playerPlaneView.getView().x = viewModel.playerXY.x
        playerPlaneView.getView().y = viewModel.playerXY.y
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }
}