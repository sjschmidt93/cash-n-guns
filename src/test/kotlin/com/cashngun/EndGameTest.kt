package com.cashngun
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

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
        val players = listOf(playerToTest, otherPlayer)
        val result = calculateEndGameTotalForPlayer(playerToTest, players)
        assertEquals(24000 + DIAMOND_BONUS, result)

        val result2 = calculateEndGameTotalForPlayer(otherPlayer, players)
        assertEquals(34000, result2)
    }

    @Test
    fun diamondBonusWithTie() {
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
                LootCard(LootCardType.DIAMOND, 5000),
                LootCard(LootCardType.PAINTING),
            ))
        val players = listOf(playerToTest, otherPlayer)
        val result = calculateEndGameTotalForPlayer(playerToTest, players)
        assertEquals(24000, result)

        val result2 = calculateEndGameTotalForPlayer(otherPlayer, players)
        assertEquals(39000, result2)
    }

    @Test
    fun figureOutWhoWon() {
        val lootCards = mutableListOf(
            LootCard(LootCardType.CASH, 5000),
            LootCard(LootCardType.DIAMOND, 5000),
            LootCard(LootCardType.DIAMOND, 10000),
            LootCard(LootCardType.PAINTING),
        )
        val playerToTest = Player(name = "Player 1", lootCards = lootCards)
        val otherPlayer = Player(name = "Player 2", lootCards =
            mutableListOf(
                LootCard(LootCardType.CASH, 5000),
                LootCard(LootCardType.CASH, 20000),
                LootCard(LootCardType.DIAMOND, 5000),
                LootCard(LootCardType.PAINTING),
            ))
        val players = listOf(playerToTest, otherPlayer)
        val result = determineWinner(players)
        assertEquals("Player 1", result?.name)
    }

    @Test
    fun handleTies() {
        val lootCards = mutableListOf(
            LootCard(LootCardType.CASH, 5000),
            LootCard(LootCardType.DIAMOND, 5000),
            LootCard(LootCardType.DIAMOND, 10000),
            LootCard(LootCardType.PAINTING),
        )
        val playerToTest = Player(name = "Player 1", lootCards = lootCards)
        val otherPlayer = Player(name = "Player 2", lootCards =
            mutableListOf(
                LootCard(LootCardType.CASH, 5000),
                LootCard(LootCardType.DIAMOND, 5000),
                LootCard(LootCardType.DIAMOND, 10000),
                LootCard(LootCardType.PAINTING),
            ))
        val players = listOf(playerToTest, otherPlayer)
        val result = determineWinner(players)
        assertNull(result)
    }
}
