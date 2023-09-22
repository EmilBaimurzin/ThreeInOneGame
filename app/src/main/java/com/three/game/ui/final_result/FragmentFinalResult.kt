package com.three.game.ui.final_result

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.three.game.R
import com.three.game.databinding.FragmentFinalResultBinding
import com.three.game.domain.other.SP
import com.three.game.ui.other.ViewBindingFragment

class FragmentFinalResult :
    ViewBindingFragment<FragmentFinalResultBinding>(FragmentFinalResultBinding::inflate) {
    private val args: FragmentFinalResultArgs by navArgs()
    private val sp by lazy {
        SP(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.bestScore.text = sp.getBestScore(args.game).toString()
        binding.score.text = args.score.toString()

        binding.menu.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }

        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentChooseGame, false, false)
            when (args.game) {
                1 -> findNavController().navigate(R.id.action_fragmentChooseGame_to_fragmentFirstGame)
                2 -> findNavController().navigate(R.id.action_fragmentChooseGame_to_fragmentSecondGame)
                else -> findNavController().navigate(R.id.action_fragmentChooseGame_to_fragmentThirdGame)
            }
        }

        binding.root.setBackgroundResource(
            when (args.game) {
                1 -> R.drawable.background04
                2 -> R.drawable.background07
                else -> R.drawable.background10
            }
        )

        binding.game1.isVisible = args.game == 1
        binding.game2.isVisible = args.game == 2
        binding.game3.isVisible = args.game == 3
    }
}