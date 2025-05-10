package com.cashngun

data class Player(
  val name: String,

  var wounds: Int = 0,

  var numClickCards: Int = 5,
  var numBangCards: Int = 3,

  var isGodFather: Boolean = false,
  var playerPosition: PlayerPosition = PlayerPosition.STANDING,

  val lootCards: MutableList<LootCard> = mutableListOf(),
)

fun Player.isAlive(): Boolean {
  return this.wounds <= 3
}

enum class BulletCard {
  CLICK, BANG, INVALID
}

enum class GameMode {
  AUTOMATIC, REAL, TEST
}