package com.three.game.ui.games.second

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.three.game.domain.second_game.PairsRepository
import com.three.game.domain.second_game.PairsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SecondGameViewModel : ViewModel() {
    private val repository = PairsRepository()
    private var gameScope = CoroutineScope(Dispatchers.Default)
    var gameState = true
    var winCallback: (() -> Unit)? = null
    var pauseState = false
    private val _list = MutableLiveData(
        repository.generateList(36)
    )
    val list: LiveData<List<PairsItem>> = _list

    private var bonusPoints = 0

    private val _timer = MutableLiveData(60)
    val timer: LiveData<Int> = _timer

    private val _points = MutableLiveData(0)
    val points: LiveData<Int> = _points

    fun startTimer() {
        gameScope = CoroutineScope(Dispatchers.Default)
        gameScope.launch {
            while (true) {
                delay(1000)
                _timer.postValue(_timer.value!! - 1)
            }
        }
    }

    fun stopTimer() {
        gameScope.cancel()
    }

    fun openItem(index: Int) {
        viewModelScope.launch {
            val newList = _list.value!!
            newList[index].openAnimation = true
            _list.postValue(newList)
            delay(410)
            newList[index].openAnimation = false
            newList[index].isOpened = true
            newList[index].lastOpened = true
            val filteredList = newList.filter { it.lastOpened }
            if (filteredList.size == 2) {
                val item1 = filteredList[0]
                val item2 = filteredList[1]
                if (item1.value == item2.value) {
                    _points.postValue(_points.value!! + 1 + bonusPoints)
                    bonusPoints += 1
                    newList.map {
                        it.lastOpened = false
                    }
                    _list.postValue(newList)
                } else {
                    bonusPoints = 0

                    newList[newList.indexOf(item1)].closeAnimation = true
                    newList[newList.indexOf(item1)].lastOpened = false

                    newList[newList.indexOf(item2)].closeAnimation = true
                    newList[newList.indexOf(item2)].lastOpened = false

                    _list.postValue(newList)
                    delay(410)
                    newList[newList.indexOf(item1)].closeAnimation = false
                    newList[newList.indexOf(item1)].isOpened = false

                    newList[newList.indexOf(item2)].closeAnimation = false
                    newList[newList.indexOf(item2)].isOpened = false

                    _list.postValue(newList)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameScope.cancel()
    }
}