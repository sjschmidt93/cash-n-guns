package com.cashngun

data class Player(
  val name: String,

  var health: Int = 3,

  var numClickCards: Int = 5,
  var numBangCards: Int = 3,

  var isGodFather: Boolean = false
)

enum class BulletCard {
  CLICK, BANG, INVALID
}

enum class GameMode {
  AUTOMATIC, REAL
}