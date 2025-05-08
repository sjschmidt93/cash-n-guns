package com.cashngun

fun main() {
  val lootDeck = INITIAL_DECK
  val numberOfPlayers = 2

  val players = listOf(
    Player("Frank"),
    Player("Dan")
  )

  while(!lootDeck.isEmpty()) {
    val lootForThisRound = getLootForThisRound(lootDeck)

    playersChooseBulletCards()
  }
}
