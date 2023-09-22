package com.three.game.ui.games.second

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.three.game.R
import com.three.game.core.library.GameFragment
import com.three.game.databinding.FragmentSecondGameBinding
import com.three.game.domain.adapter.PairsAdapter
import com.three.game.domain.other.SP
import com.three.game.ui.other.CallbackViewModel

class FragmentSecondGame: GameFragment<FragmentSecondGameBinding>(FragmentSecondGameBinding::inflate) {
    private lateinit var pairsAdapter: PairsAdapter
    private val sp by lazy {
        SP(requireContext())
    }
    private val viewModel: SecondGameViewModel by viewModels()
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()

        binding.menu.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.winCallback = {
            end()
        }

        callbackViewModel.callback = {
            viewModel.pauseState = false
            viewModel.startTimer()
        }

        binding.pause.setOnClickListener {
            viewModel.pauseState = true
            viewModel.stopTimer()
            findNavController().navigate(R.id.action_fragmentSecondGame_to_dialogPause)
        }

        binding.menu.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        viewModel.list.observe(viewLifecycleOwner) {
            pairsAdapter.items = it.toMutableList()
            pairsAdapter.notifyDataSetChanged()
        }
        viewModel.timer.observe(viewLifecycleOwner) { totalSecs ->
            val minutes = (totalSecs % 3600) / 60;
            val seconds = totalSecs % 60;

            binding.time.text = String.format("%02d:%02d", minutes, seconds)

            if (totalSecs == 0 && viewModel.gameState && !viewModel.pauseState) {
                end()
            }
        }

        viewModel.points.observe(viewLifecycleOwner) {
            binding.score.text = it.toString()
        }

        if (viewModel.gameState && !viewModel.pauseState) {
            viewModel.startTimer()
        }

    }

    private fun end() {
        viewModel.stopTimer()
        viewModel.gameState = false
        if (sp.getBestScore(2) < viewModel.points.value!!) {
            sp.setBestScore(2, viewModel.points.value!!)
        }
        findNavController().navigate(
            FragmentSecondGameDirections.actionFragmentSecondGameToFragmentFinalResult(2, viewModel.points.value!!)
        )
    }

    private fun initAdapter() {
        pairsAdapter = PairsAdapter {
            if (viewModel.list.value!!.find { it.closeAnimation } == null && viewModel.list.value!!.find { it.openAnimation } == null) {
                viewModel.openItem(it)
            }
        }
        with(binding.gameRV) {
            adapter = pairsAdapter
            layoutManager = GridLayoutManager(requireContext(), 6).also {
                it.orientation = GridLayoutManager.VERTICAL
            }
            setHasFixedSize(true)
            itemAnimator = null
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopTimer()
    }
}