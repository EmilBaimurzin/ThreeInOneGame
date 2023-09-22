package com.three.game.ui.games.third

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.three.game.core.library.GameViewModel
import com.three.game.core.library.XYIMpl
import com.three.game.core.library.l
import com.three.game.core.library.random
import com.three.game.domain.third_game.Symbol
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThirdGameViewModel: GameViewModel() {
    private val _symbols = MutableStateFlow<List<Symbol>>(emptyList())
    val symbols = _symbols.asStateFlow()

    private val _trigger = MutableStateFlow<Boolean>(true)
    val trigger = _trigger.asStateFlow()

    private val _lives = MutableLiveData(3)
    val lives: LiveData<Int> = _lives

    private val _scores = MutableLiveData(0)
    val scores: LiveData<Int> = _scores

    private var isSpawning = false

    init {
        _playerXY.postValue(XYIMpl(0f, 0f))
    }

    fun start(
        symbolSize: Int,
        maxX: Int,
        generationTime: Long,
        fallDelay: Long,
        maxY: Int,
        playerWidth: Int,
        playerHeight: Int,
        distance: Int
    ) {
        isSpawning = false
        gameScope = CoroutineScope(Dispatchers.Default)
        generateSymbols(symbolSize, maxX, generationTime)
        letItemsFall(maxY, symbolSize, playerWidth, playerHeight)
    }

    private fun generateSymbols(symbolSize: Int, maxX: Int, generationTime: Long) {
        gameScope.launch {
            while (true) {
                delay(generationTime)
                isSpawning = true
                val currentList = _symbols.value.toMutableList()
                val value = 1 random 7
                currentList.add(
                    Symbol(
                        y = 0f - symbolSize,
                        x = (0 random (maxX - symbolSize)).toFloat(),
                        value
                    )
                )
                _symbols.value = currentList
                isSpawning = false
            }
        }
    }

    private fun letItemsFall(
        maxY: Int,
        symbolSize: Int,
        playerWidth: Int,
        playerHeight: Int,
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isSpawning) {
                    val currentList = _symbols.value
                    val newList = mutableListOf<Symbol>()
                    currentList.forEach { obj ->
                        if (obj.y <= maxY) {
                            val objX = obj.x.toInt()..obj.x.toInt() + symbolSize
                            val objY = obj.y.toInt()..obj.y.toInt() + symbolSize
                            val playerX =
                                _playerXY.value!!.x.toInt().._playerXY.value!!.x.toInt() + playerWidth
                            val playerY =
                                _playerXY.value!!.y.toInt().._playerXY.value!!.y.toInt() + playerHeight
                            if (playerX.any { it in objX } && playerY.any { it in objY }) {
                                if (obj.value == 7) {
                                    _lives.postValue(_lives.value!! - 1)
                                } else {
                                    _scores.postValue(_scores.value!! + 1)
                                }
                            } else {
                                obj.y = obj.y + 10
                                obj.x = obj.x
                                newList.add(obj)
                            }
                        }
                    }
                    _symbols.value = newList
                    _trigger.value = !_trigger.value
                }
            }
        }
    }

    fun initPlayer(x: Int, y: Int, playerWidth: Int, playerHeight: Int) {
        _playerXY.postValue(
            XYIMpl(
                x = x / 2 - (playerWidth.toFloat() / 2),
                y = y - playerHeight.toFloat()
            )
        )
    }

    fun playerMoveLeft(limit: Float) {
        if (_playerXY.value!!.x - 4f > limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x - 4, _playerXY.value!!.y))
        }
    }

    fun playerMoveRight(limit: Float) {
        if (_playerXY.value!!.x + 4f < limit) {
            _playerXY.postValue(XYIMpl(_playerXY.value!!.x + 4, _playerXY.value!!.y))
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}