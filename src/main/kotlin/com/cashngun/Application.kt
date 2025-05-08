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
  var roundNumber = 1

  var players = mutableListOf(
    Player(name = "Frank", isGodFather = true),
    Player(name = "Dan"),
    Player(name = "Steven"),
    Player(name = "Erik")
  )

  while(!lootDeck.isEmpty()) {
    printRoundAndGodFather(roundNumber, players)

    val lootForThisRound = getLootForThisRound(lootDeck)

    val bulletCards = playersChooseBulletCards(players)
    println("")

    val playersPointingGuns = playersPointGuns(players)

    val playerToRedirect = godFatherPrivilege(players)
    println("")

    playerToRedirect.let {
      redirectPlayer(playerToRedirect, playersPointingGuns)
    }

    val playerPositions = courage(players).toMutableList();
    println("")

    players = resolvePointing(bulletCards, playersPointingGuns, playerPositions, players)

    collectLoot(players, lootForThisRound)

    // End of round
    changeGodFather(players)
    roundNumber++
  }

  val winner = determineWinner(players)
  println()
  println("The winner is: ${winner.name}!")
}

fun collectLoot(players: MutableList<Player>, lootForThisRound: List<LootCard>) {
  players.forEach { player ->
    
  }
}

fun resolvePointing(
  bulletCards: List<BulletCard>,
  playersPointingGuns: MutableList<Pair<Player, Player>>,
  playerPositions: MutableList<PlayerPosition>,
  players: MutableList<Player>
) : MutableList<Player> {
  println("Step 6: Card effects")

  playersPointingGuns.forEachIndexed { idx, pair ->
    val playerPointing = pair.first
    val playerPointingBulletCard = bulletCards[idx]
    val playerBeingPointedAt = pair.second
    val playerBeingPointedAtPosition = playerPositions[idx]

    println("${playerPointing.name} fires a ${playerPointingBulletCard} at ${playerBeingPointedAt.name} who is ${playerBeingPointedAtPosition}")

    if (playerBeingPointedAtPosition == PlayerPosition.LAYING_DOWN) {

    } else {

      if (playerPointingBulletCard == BulletCard.BANG) {
        println("${playerBeingPointedAt.name} loses 1 health, their health is now ${playerBeingPointedAt.health}")
        playerBeingPointedAt.health--
        playerPositions[idx] = PlayerPosition.LAYING_DOWN
      }
    }
    println("")
  }

  players.forEach { player ->
    if (player.health <= 0) {
      println("${player.name} is dead")
    }
  }

  return players.filter { player -> player.health > 0 }.toMutableList()
}

fun playersPointGuns(players: List<Player>): MutableList<Pair<Player, Player>> {
  println("Step 3: Hold-Up")
  val playerPairs = players.map { player ->
    println("\n${player.name}: Choose a player to point your gun at:")

    val otherPlayers = players.filterNot { p -> p.name == player.name }
    
    otherPlayers.forEachIndexed { index, it ->
      println("${index + 1}) ${it.name}")
    }

    val playerToPointAtPrompt = getChoice(otherPlayers.size)
    
    val playerToPointAt = playerToPointAtPrompt - 1
    println("${player.name} points their gun at ${otherPlayers[playerToPointAt].name}")

    Pair(player, otherPlayers[playerToPointAt])
  }

  println("")

  return playerPairs.toMutableList()
}

fun godFatherPrivilege(players: List<Player>): Player? {
  println("Step 4: Godfather's Privilege")
  val godfather = players.first{it.isGodFather}
  val otherPlayers = players.filterNot { it.isGodFather }
  println("\n${godfather.name}: Order another player to change target:")
  otherPlayers.forEachIndexed { index, it ->
    println("${index + 1}) ${it.name}")
  }
  println("${otherPlayers.size + 1}) Nobody")
  val choice = getChoice(otherPlayers.size + 1)
  return if (choice < otherPlayers.size) {
    println("Godfather chose ${otherPlayers[choice].name}")
     otherPlayers[choice]
  } else {
    println("Godfather chose nobody")
    null
  }
}

fun redirectPlayer(player: Player?, playerPairs: MutableList<Pair<Player, Player>>) {
  player?.let {
    val currentTarget = playerPairs.first {it.first == player}.second
    val newTargetChoices = playerPairs.map { it.first }.filterNot {it == currentTarget || it == player}

    newTargetChoices.forEachIndexed { i, p -> println("${i + 1}) ${p.name}") }

    val choice = getChoice(newTargetChoices.size) - 1

    val indexToRemove = playerPairs.indexOfFirst { it.first == player }
    playerPairs.removeAt(indexToRemove)
    playerPairs.add(Pair(player, newTargetChoices[choice]))
    println("${player.name} redirects and points their gun at ${newTargetChoices[choice].name}")
    println("")
  }
}

enum class PlayerPosition {
  LAYING_DOWN,
  STANDING
}

fun courage(players: MutableList<Player>): List<PlayerPosition> {
  println("Step 5: Courage")
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
  println("Step 2: Choice of the Bullet card")
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

