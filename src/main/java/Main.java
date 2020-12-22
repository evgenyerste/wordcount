import java.io.FileNotFoundException;

public class Main {
    public static void main(final String[] args) throws FileNotFoundException {
        final InputProvider inputProvider = new InputProvider(new InputReaderImpl());
        final WordCounter.Result wordCount =
                WordCounter.countWords(inputProvider.getInput(args), inputProvider.getStopWords());

        System.out.println(
                "Number of words: " + wordCount.getOverallCount() + " , unique: " + wordCount.getUniqueCount());
    }
}
