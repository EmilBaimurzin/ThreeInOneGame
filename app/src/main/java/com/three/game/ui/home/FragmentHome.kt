package com.three.game.ui.home

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.three.game.R
import com.three.game.core.library.GameFragment
import com.three.game.databinding.FragmentHomeBinding

class FragmentHome : GameFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exit.setOnClickListener {
            requireActivity().finish()
        }

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(),
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.fragmentMain, false, false)
                }
            })

        binding.play.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentChooseGame)
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}