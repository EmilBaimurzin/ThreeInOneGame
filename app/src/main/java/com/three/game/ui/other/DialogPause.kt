package com.three.game.ui.other

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.three.game.R
import com.three.game.core.library.ViewBindingDialog
import com.three.game.databinding.DialogPauseBinding

class DialogPause: ViewBindingDialog<DialogPauseBinding>(DialogPauseBinding::inflate) {
    private val callbackViewModel: CallbackViewModel by activityViewModels()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack()
                callbackViewModel.callback?.invoke()
                true
            } else {
                false
            }
        }

        binding.play.setOnClickListener {
            findNavController().popBackStack()
            callbackViewModel.callback?.invoke()
        }
    }
}