package com.cashngun
import kotlin.test.Test
import kotlin.test.assertEquals

class EndGameTest {
    @Test
    fun billsAndDiamonds() {
        val lootCards = mutableListOf(
            LootCard(LootCardType.CASH, 5000),
            LootCard(LootCardType.CASH, 20000),
            LootCard(LootCardType.DIAMOND, 5000),
        )
        val playerToTest = Player(name = "Player", lootCards = lootCards)
        val players = listOf(playerToTest)
        val result = calculateEndGameTotalForPlayer(playerToTest, players)
        assertEquals(5000 + 20000 + 5000 + DIAMOND_BONUS, result)
    }

    @Test
    fun paintings() {
        val lootCards = mutableListOf(
            LootCard(LootCardType.CASH, 5000),
            LootCard(LootCardType.CASH, 20000),
            LootCard(LootCardType.DIAMOND, 5000),
            LootCard(LootCardType.PAINTING),
            LootCard(LootCardType.PAINTING),
            LootCard(LootCardType.PAINTING),
        )
        val playerToTest = Player(name = "Player", lootCards = lootCards)
        val players = listOf(playerToTest)
        val result = calculateEndGameTotalForPlayer(playerToTest, players)
        assertEquals(5000 + 20000 + 5000 + 30000 + DIAMOND_BONUS, result)
    }

    @Test
    fun diamondBonus() {
        val lootCards = mutableListOf(
            LootCard(LootCardType.CASH, 5000),
            LootCard(LootCardType.DIAMOND, 5000),
            LootCard(LootCardType.DIAMOND, 10000),
            LootCard(LootCardType.PAINTING),
        )
        val playerToTest = Player(name = "Player", lootCards = lootCards)
        val otherPlayer = Player(name = "Other Player", lootCards =
            mutableListOf(
                LootCard(LootCardType.CASH, 5000),
                LootCard(LootCardType.CASH, 20000),
                LootCard(LootCardType.DIAMOND, 5000),
                LootCard(LootCardType.PAINTING),
            ))
        val players = listOf(playerToTest)
        val result = calculateEndGameTotalForPlayer(playerToTest, players)
        assertEquals(24000 + DIAMOND_BONUS, result)

        val result2 = calculateEndGameTotalForPlayer(otherPlayer, players)
        assertEquals(34000, result2)
    }
}
