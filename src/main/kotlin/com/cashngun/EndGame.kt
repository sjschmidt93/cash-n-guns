package com.cashngun

fun determineWinner(players: List<Player>): Player {
    val playerScores = players.associateWith {
        calculateEndGameTotalForPlayer(it, players)
    }

    val maxScore = playerScores.values.maxOrNull() ?: 0
    return playerScores.filter { it.value == maxScore }.keys.first()
}

fun calculateEndGameTotalForPlayer(player: Player, players: List<Player>): Int {
    val lootCards = player.lootCards
    val paintings = lootCards.filter { it.type == LootCardType.PAINTING }
    val billsAndDiamondsSum = lootCards.sumOf { it.value ?: 0 }
    val paintingSum = paintingValueMap.getValue(paintings.size)
    val allOtherPlayers = players.filter { it != player }
    val shouldAwardDiamondBonus = allOtherPlayers.all { player.countDiamondCards() > it.countDiamondCards() }
    val diamondBonus = if (shouldAwardDiamondBonus) DIAMOND_BONUS else 0
    return billsAndDiamondsSum + paintingSum + diamondBonus
}

const val DIAMOND_BONUS = 60000

fun Player.countDiamondCards(): Int {
    return this.lootCards.count { it.type == LootCardType.DIAMOND }
}

val paintingValueMap = mapOf(
    0 to 0,
    1 to 4000,
    2 to 12000,
    3 to 30000,
    4 to 60000,
    5 to 100000,
    6 to 150000,
    7 to 200000,
    8 to 300000,
    9 to 400000,
    10 to 500000,
)
