package com.three.game.ui.games.first

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.three.game.core.library.XY
import com.three.game.core.library.XYIMpl
import com.three.game.core.library.random
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirstGameViewModel : ViewModel() {
    var gameState = true
    var pauseState = false

    private var gameScope = CoroutineScope(Dispatchers.Default)

    private val _bullets = MutableStateFlow<List<XYIMpl>>(emptyList())
    val bullets = _bullets.asStateFlow()

    private val _enemies = MutableStateFlow<List<XYIMpl>>(emptyList())
    val enemies = _enemies.asStateFlow()

    private val _score = MutableStateFlow<Int>(0)
    val score = _score.asStateFlow()

    var playerXY = XYIMpl(0f, 0f)

    fun setPlayerXY(x: Float, y: Float) {
        playerXY = XYIMpl(x, y)
    }

    var isSpawning = false
    var isSpawningBullet = false

    var endCallback: (() -> Unit)? = null

    fun stop() {
        gameScope.cancel()
    }

    fun start(
        enemyWidth: Int,
        enemyHeight: Int,
        maxY: Int,
        maxX: Int,
        bulletWidth: Int,
        bulletHeight: Int,
        playerWidth: Int
    ) {
        gameScope = CoroutineScope(Dispatchers.Default)
        generateEnemies(enemyWidth, enemyHeight, maxX)
        letEnemiesMove(maxY, bulletWidth, bulletHeight, enemyWidth, enemyHeight)
        generateBullets(bulletWidth, bulletHeight, playerWidth)
        letBulletsMove()
    }

    private fun generateEnemies(
        enemyWidth: Int,
        enemyHeight: Int,
        maxX: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(1000)
                isSpawning = true
                val newEnemies = _enemies.value.toMutableList()
                newEnemies.add(XYIMpl((0 random (maxX - enemyWidth)).toFloat(), 0f - enemyHeight))
                _enemies.value = (newEnemies)
                isSpawning = false
            }
        }
    }

    private fun generateBullets(
        bulletWidth: Int,
        bulletHeight: Int,
        playerWidth: Int
    ) {
        gameScope.launch {
            while (true) {
                delay(800)
                isSpawningBullet = true
                val newList = _bullets.value.toMutableList()
                val x = playerXY.x + ((playerWidth - bulletWidth) / 2)
                val y = playerXY.y - bulletHeight
                newList.add(XYIMpl(x, y))
                _bullets.value = (newList)
                isSpawningBullet = false
            }
        }
    }

    private fun letEnemiesMove(
        maxY: Int,
        bulletWidth: Int,
        bulletHeight: Int,
        enemyWidth: Int,
        enemyHeight: Int,
    ) {
        gameScope.launch {
            while (true) {
                delay(16)
                if (!isSpawning) {
                    val currentList = _enemies.value
                    val newList = mutableListOf<XYIMpl>()
                    currentList.forEach { enemy ->
                        if (enemy.y <= maxY) {
                            val currentBullets = _bullets.value
                            var isShot = false
                            currentBullets.forEach { bullet ->
                                val objX = bullet.x.toInt()..bullet.x.toInt() + bulletWidth
                                val objY = bullet.y.toInt()..bullet.y.toInt() + bulletHeight

                                val enemyX = enemy.x.toInt()..enemy.x.toInt() + enemyWidth
                                val enemyY = enemy.y.toInt()..enemy.y.toInt() + enemyHeight

                                if (objX.any { it in enemyX } && objY.any { it in enemyY } && !isShot) {
                                    isShot = true
                                }
                            }
                            if (!isShot) {
                                newList.add(XYIMpl(enemy.x, enemy.y + 20))
                            } else {
                                _score.value = _score.value + 1
                            }
                        } else {
                            endCallback?.invoke()
                        }
                    }
                    _enemies.value = (newList)
                }
            }
        }
    }

    private fun letBulletsMove() {
        gameScope.launch {
            while (true) {
                delay(15)
                if (!isSpawningBullet) {
                    _bullets.value = (moveSomethingUp(_bullets.value!!.toMutableList() as MutableList<XY>).toList() as List<XYIMpl>)
                }
            }
        }
    }

    private suspend fun moveSomethingUp(
        oldList: MutableList<XY>,
    ): MutableList<XY> {
        return suspendCoroutine { cn ->
            val newList = mutableListOf<XY>()
            oldList.forEach { obj ->
                if (obj.y >= 0) {
                    newList.add(XYIMpl(obj.x, obj.y - 15))
                }
            }
            cn.resume(newList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameScope.cancel()
    }
}