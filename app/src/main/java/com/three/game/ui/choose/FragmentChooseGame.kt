package com.three.game.ui.choose

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.three.game.R
import com.three.game.databinding.FragmentChooseGameBinding
import com.three.game.ui.other.ViewBindingFragment

class FragmentChooseGame: ViewBindingFragment<FragmentChooseGameBinding>(FragmentChooseGameBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            game01.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentChooseGame_to_fragmentFirstGame)
            }
            game02.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentChooseGame_to_fragmentSecondGame)
            }
            game03.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentChooseGame_to_fragmentThirdGame)
            }
        }
    }
}