package com.cashngun

fun main() {
  val lootDeck = INITIAL_DECK
  val numberOfPlayers = 3

  val players = listOf(
    Player(name = "Frank"),
    Player(name = "Dan"),
    Player(name = "Steven")
  )

  while(!lootDeck.isEmpty()) {
    val lootForThisRound = getLootForThisRound(lootDeck)
    println(lootForThisRound)

    val choices = playersChooseBulletCards(players)
    println(choices)
  }
}

fun playersChooseBulletCards(players: List<Player>): List<BulletCard> {
  println("Choose your action:")
  println("1) Bullet")
  println("2) Click")
  
  val choices = players.map {
    println("${it.name} chose your card")
    when (readLine()?.trim()) {
      "1" -> BulletCard.BANG
      else -> BulletCard.CLICK
    }
  }
  return choices
}
