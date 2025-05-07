import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    println("Welcome to the Terminal Game!")
    print("Enter the number of players: ")
    val numPlayers = scanner.nextInt()

    // Validate the number of players
    if (numPlayers <= 0) {
        println("Invalid number of players. Exiting the game.")
        return
    }

    // Store player names
    val playerNames = mutableListOf<String>()

    // Loop to get player names
    for (i in 1..numPlayers) {
        print("Enter the name of player $i: ")
        val name = scanner.next()
        playerNames.add(name)
    }

    // Game loop (example)
    var gameRunning = true
    while (gameRunning) {
        for (player in playerNames) {
            println("It's $player's turn!")

            // Example action
            print("Enter 'end' to stop the game or anything else to continue: ")
            val action = scanner.next()

            if (action.equals("end", ignoreCase = true)) {
                gameRunning = false
                break
            }

            // Add game logic here for each player's turn
            println("$player performed their action!")
        }
    }

    println("Game Over. Thanks for playing!")
}