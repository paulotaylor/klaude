import com.klaude.KlaudeClient
import com.klaude.KlaudeMessage
import com.klaude.KlaudeMessageType
import io.github.cdimascio.dotenv.dotenv
import java.util.*

// Colors for pretty formatting
const val RESET = "\u001b[0m"
const val BLACK = "\u001b[0;30m"
const val RED = "\u001b[0;31m"
const val GREEN = "\u001b[0;32m"
const val YELLOW = "\u001b[0;33m"
const val BLUE = "\u001b[0;34m"
const val PURPLE = "\u001b[0;35m"
const val CYAN = "\u001b[0;36m"
const val WHITE = "\u001b[0;37m"

fun main() {
    val scanner = Scanner(System.`in`)

    // Print out the menu of options
    println("""
            ${GREEN}Please select one of the options below by typing a number.
                1. Completion
                2. Chat
        """.trimIndent()
    )

    when (scanner.nextLine().trim()) {
        "1" -> doCompletion()
        "2" -> doChat()
        else -> System.err.println("Invalid option")
    }
}

fun doCompletion() {
    val scan = Scanner(System.`in`)
    println(YELLOW + "Enter completion: ")
    val input = scan.nextLine()

    val key = dotenv()["CLAUDE_KEY"]

    val klaude = KlaudeClient.Builder()
        .model("claude-2")
        .key(key)
        .maxTokensToSample(500)
        .build()

    klaude.complete(input) {
        println(it)
    }
}
fun doChat() {
    val scan = Scanner(System.`in`)

    val key = dotenv()["CLAUDE_KEY"]

    val klaude = KlaudeClient.Builder()
        .model("claude-2")
        .key(key)
        .maxTokensToSample(500)
        .build()

    val messages = mutableListOf(KlaudeMessage("You are a helpful assistant", KlaudeMessageType.USER))

    println(YELLOW + "Start chatting, type 'exit' or 'quit' to finish:\n")

    while (true) {

        println(GREEN + "Human:")

        val input = scan.nextLine().trim()

        if (input.isBlank()) {
            continue
        }

        if (input == "exit" || input == "quit") {
            break
        }
        messages.add(KlaudeMessage(input, KlaudeMessageType.USER))

        klaude.chat(messages) { reply ->
            if (!reply.isNullOrBlank()) {
                messages.add(KlaudeMessage(reply.trim(), KlaudeMessageType.ASSISTANT))
                println(BLUE + "Assistant:\n${reply.trim()}")
            }
        }
    }
    println(RED + "DONE")

}