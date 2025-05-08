package com.cashngun

import com.cashngun.GameMode

val mode = GameMode.AUTOMATIC

fun getModeAndChoice(gameMode: GameMode) = { totalChoices: Int -> 
  if (gameMode == GameMode.AUTOMATIC) {
    (1..totalChoices).random()
  } else {
    readLine()?.trim()?.toIntOrNull() ?: 1
  }
}

val getChoice = getModeAndChoice(mode)

fun main() {
  val mode = GameMode.AUTOMATIC
  val lootDeck = INITIAL_DECK
  val numberOfPlayers = 3
  var roundNumber = 1

  val players = listOf(
    Player(name = "Frank", isGodFather = true),
    Player(name = "Dan"),
    Player(name = "Steven")
  )

  while(!lootDeck.isEmpty()) {
    printRoundAndGodFather(roundNumber, players)

    val lootForThisRound = getLootForThisRound(lootDeck)

    val choices = playersChooseBulletCards(players)

    val playersPointingGuns = playersPointGuns(players)

    val playerPositions = courage(players)

    // End of round
    changeGodFather(players)
    roundNumber++
  }
}

fun playersPointGuns(players: List<Player>): List<Player> {
  val players = players.map { player ->
    println("\n${player.name}: Choose a player to point your gun at:")

    players.filterNot { p -> p.name == player.name }.forEachIndexed { index, player ->
      println("${index + 1}) ${player.name}")
    }

    val playerToPointAtPrompt = getChoice(players.size - 1)
    
    val playerToPointAt = playerToPointAtPrompt - 1
    println("${player.name} points their gun at ${players[playerToPointAt].name}")
    players[playerToPointAt]
  }

  println("")

  return players
}

enum class PlayerPosition {
  LAYING_DOWN,
  STANDING
}

fun courage(players: List<Player>): List<PlayerPosition> {
  return players.map {
    println("${it.name}: Lay down or stay standing")
    when (getChoice(2)) { 
      1 -> {
        println("${it.name} lays down")
        PlayerPosition.LAYING_DOWN
      }
      else -> {
        println("${it.name} stays standing")
        PlayerPosition.STANDING
      }
    }
  }
}

fun playersChooseBulletCards(players: List<Player>): List<BulletCard> {
  println("Choose your action:")
  println("1) Bullet")
  println("2) Click\n")
  
  return players.map {
    println("${it.name} choose your card")

    val chosenCard = when {
      it.numClickCards == 0 && it.numBangCards == 0 -> {
        println("You have no cards left, this should not happen")
        BulletCard.INVALID
      }

      it.numClickCards == 0 -> {
        println("You have no click cards, you must choose a bang card")
        it.numBangCards--
        BulletCard.BANG
      }

      it.numBangCards == 0 -> {
        println("You have no bang cards, you must choose a click card")
        it.numClickCards--
        BulletCard.CLICK
      }
      
      else -> {
        when (getChoice(2)) {
          1 -> {
            it.numBangCards--
            println(it.numBangCards)
            BulletCard.BANG
          }
          else -> {
            it.numClickCards--
            BulletCard.CLICK
          }
        }
      }
    }

    println("${it.name} chose ${chosenCard}\n")

    chosenCard
  }
}

fun printLootForThisRound(loot: List<LootCard>) {
  println("Loot for this round:")

  loot.forEach {
    when (it.type) {
      LootCardType.CASH -> println("• ${it.value} cash")
      LootCardType.PAINTING -> println("• Painting") 
      LootCardType.CLIP -> println("• Clip")
      LootCardType.DIAMOND -> println("• Diamond worth ${it.value}")
      LootCardType.FIRST_AID_KIT -> println("• First Aid Kit")
    }
  }

  println("")
}

fun printRoundAndGodFather(roundNumber: Int, players: List<Player>) {
  println("Round ${roundNumber}")
  println("Godfather: ${players.find { it.isGodFather }?.name}\n")
}

fun changeGodFather(players: List<Player>) {
  val godFather = players.indexOfFirst { it.isGodFather }
  val newGodFatherIndex = (godFather + 1) % players.size

  players[godFather].isGodFather = false
  players[newGodFatherIndex].isGodFather = true
}

