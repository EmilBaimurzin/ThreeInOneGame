package com.three.game.domain.third_game

import com.three.game.core.library.XY

data class Symbol(
    override var y: Float,
    override var x: Float,
    val value: Int
) : XY