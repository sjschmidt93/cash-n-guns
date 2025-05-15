package com.cashngun

data class Player(
  val name: String,

  var wounds: Int = 0,

  var numClickCards: Int = 5,
  var numBangCards: Int = 3,

  var isGodFather: Boolean = false,
  var playerPosition: PlayerPosition = PlayerPosition.STANDING,

  val lootCards: MutableList<LootCard> = mutableListOf(),

  var specialPower: SpecialPower? = null
)

fun Player.isAlive(): Boolean {
  val maxWounds = if (this.specialPower == SpecialPower.UNBREAKABLE) 5 else 3
  return this.wounds <= maxWounds
}

enum class BulletCard {
  CLICK, BANG, INVALID
}

enum class GameMode {
  AUTOMATIC, REAL
}