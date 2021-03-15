import arguments.api.Arguments
import arguments.api.ArgumentsParseUseCase
import arguments.domain.DefaultArgumentsParseUseCase
import stopwords.domain.TextInputReadStopWordsGetUseCase
import text.input.api.InvalidInputException
import text.input.commandline.CommandLineTextInputReadUseCase
import text.input.file.FileTextInputReadUseCase
import text.output.api.TextOutputWriteUseCase
import text.output.commandline.CommandLineTextOutputWriteUseCase
import text.split.api.TextSplitUseCase
import text.split.domain.CompositeTextSplitUseCase
import text.split.domain.TextByDashSplitUseCase
import text.split.domain.TextByNewLineSplitUseCase
import text.split.domain.TextByWhitespaceSplitUseCase
import token.count.api.TokensCountUseCase
import token.count.domain.AllTokensCountUseCase
import token.filter.api.TokensFilterUseCase
import token.filter.domain.CompositeTokensFilterUseCase
import token.filter.domain.NonStopWordTokensFilterUseCase
import token.filter.domain.UniqueTokensFilterUseCase
import token.filter.domain.ValidWordTokensFilterUseCase
import word.count.api.WordsCountGetUseCase
import word.count.domain.ConfigurableWordsCountGetUseCase
import word.input.api.WordsInputUseCase
import word.input.commandline.CommandLineWordsInputUseCase
import word.input.file.FileWordsInputUseCase

private val argumentsParseUseCase: ArgumentsParseUseCase = DefaultArgumentsParseUseCase()

private val outputWriteUseCase: TextOutputWriteUseCase = CommandLineTextOutputWriteUseCase()

private val textSplitUseCase: TextSplitUseCase = CompositeTextSplitUseCase(
    listOf(
        TextByWhitespaceSplitUseCase(),
        TextByDashSplitUseCase()
    )
)

private val tokensCountUseCase: TokensCountUseCase = AllTokensCountUseCase()

private val validWordTokensFilterUseCase: TokensFilterUseCase = ValidWordTokensFilterUseCase()

private val nonStopWordTokensFilterUseCase: TokensFilterUseCase = NonStopWordTokensFilterUseCase(
    stopWordsGetUseCase = TextInputReadStopWordsGetUseCase(
        textInputReadUseCase = FileTextInputReadUseCase("stopwords.txt"),
        textSplitUseCase = TextByNewLineSplitUseCase()
    )
)

private val wordsCountGetUseCase: WordsCountGetUseCase = ConfigurableWordsCountGetUseCase(
    textSplitUseCase = textSplitUseCase,
    tokensFilterUseCase = CompositeTokensFilterUseCase(
        listOf(
            validWordTokensFilterUseCase,
            nonStopWordTokensFilterUseCase
        )
    ),
    tokensCountUseCase = tokensCountUseCase
)

private val uniqueWordsCountGetUseCase: WordsCountGetUseCase = ConfigurableWordsCountGetUseCase(
    textSplitUseCase = textSplitUseCase,
    tokensFilterUseCase = CompositeTokensFilterUseCase(
        listOf(
            validWordTokensFilterUseCase,
            nonStopWordTokensFilterUseCase,
            UniqueTokensFilterUseCase()
        )
    ),
    tokensCountUseCase = tokensCountUseCase
)

private fun wordsInputUseCase(arguments: Arguments): WordsInputUseCase = arguments.textFilePath?.let { path ->
    FileWordsInputUseCase(FileTextInputReadUseCase(path))
} ?: CommandLineWordsInputUseCase(
    textInputReadUseCase = CommandLineTextInputReadUseCase(),
    textOutputWriteUseCase = outputWriteUseCase
)

fun main(args: Array<String>) {

    val arguments = argumentsParseUseCase.parseArguments(args)
    val wordsInputUseCase: WordsInputUseCase = wordsInputUseCase(arguments)

    try {
        val wordsInput = wordsInputUseCase.getInput()

        val result = wordsCountGetUseCase.getWordCount(wordsInput)
        val uniqueResult = uniqueWordsCountGetUseCase.getWordCount(wordsInput)

        outputWriteUseCase.writeText("Number of words: $result, unique: $uniqueResult\n")
    } catch (e: InvalidInputException) {
        outputWriteUseCase.writeText(e.message ?: "Unknown input error")
    }
}