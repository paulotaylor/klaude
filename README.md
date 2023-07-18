<div align="center">

# Klaude - Kotlin Client for Claude from Anthropic
  [![Maven Central](https://img.shields.io/maven-central/v/com.klaude/klaude?color=blue&label=Download)](https://central.sonatype.com/namespace/com.klaude)
  [![License](https://img.shields.io/github/license/paulotaylor/klaude)](https://github.com/paulotaylor/klaude/blob/main/LICENSE)

A community-maintained easy-to-use Java/Kotlin Claude API
</div>

## Documentation
* [Claude API](https://docs.anthropic.com/claude/docs)

## Installation
For Kotlin DSL (`build.gradle.kts`), add this to your dependencies block:
```kotlin
dependencies {
    implementation("com.klaude:klaude:0.0.1")
}
```
For Maven projects, add this to your `pom.xml` file in the `<dependencies>` block:
```xml
<dependency>
    <groupId>com.klaude</groupId>
    <artifactId>klaude</artifactId>
    <version>0.0.1</version>
</dependency>
```
See the [maven repository](https://central.sonatype.com/artifact/com.klaude/klaude/0.0.1) for gradle/ant/etc.


## Working Example
This is a basic working example. 
```kotlin
fun translateToEnglish() {

    val klaudeClient = KlaudeClient.Builder()
        .key("YOUR_API_KEY")
        .model("claude-2")
        .maxTokensToSample(1000)
        .build()
    val text = "Olá, tudo bem?"
    klaudeClient.complete("Translate to English: $text") { result ->
        println(result ?: "[empty]")
    }
}

fun chat() {
    val scan = Scanner(System.`in`)

    val klaude = KlaudeClient.Builder()
        .model("claude-2")
        .key("YOUR_API_KEY")
        .maxTokensToSample(500)
        .build()

    val messages = mutableListOf(KlaudeMessage("You are a helpful assistant", KlaudeMessageType.USER))

    println("Start chatting, type 'exit' or 'quit' to finish:\n")

    while (true) {

        println("Human:")

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
                println("Assistant:\n${reply.trim()}")
            }
        }
    }
    println("DONE")
}

```

## License
Klaude is an open-sourced software licensed under the [MIT License](https://github.com/paulotaylor/klaude/blob/master/LICENSE).
**This is an unofficial library, and is not affiliated with Anthropic**.
