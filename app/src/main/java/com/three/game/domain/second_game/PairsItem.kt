package com.three.game.domain.second_game

import java.util.Random

data class PairsItem(
    val id: Int = Random().nextInt(),
    val value: Int,
    var isOpened: Boolean = false,
    var lastOpened: Boolean = false,
    var openAnimation: Boolean = false,
    var closeAnimation: Boolean = false
)