package com.three.game.ui.other

import androidx.lifecycle.ViewModel

class CallbackViewModel: ViewModel() {
    var callback: (()->Unit)? = null
}