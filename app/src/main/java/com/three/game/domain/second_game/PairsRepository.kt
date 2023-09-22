package com.three.game.domain.second_game

class PairsRepository {
    fun generateList(amount: Int): List<PairsItem> {
        val listToReturn = mutableListOf<PairsItem>()

        repeat(6) {
            repeat(6) { ind ->
                listToReturn.add(PairsItem(value = ind + 1))
            }
        }

        listToReturn.shuffle()
        return listToReturn
    }
}