package com.cashngun
import kotlin.test.Test
import kotlin.test.assertEquals

class TheKidTest {

    @Test
    fun `the kid should pick last`() {
        val players = listOf(
            Player("Player 1", 100, 0, specialPower = SpecialPower.KID),
            Player("Player 2", 100, 0)
        )
        val playerPairs = playersPointGuns(players)
        assertEquals(playerPairs[1].first.specialPower, SpecialPower.KID)
    }
}
