import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordCountMain {

    List<String> stopwords;

    public WordCountMain() throws IOException {
        readStopwords("stopwords.txt");
    }

    public static void main(String[] args) throws IOException {
        BufferedReader input;
        if (args.length == 1) {
            // read input from file
            input = new BufferedReader(new FileReader(args[0]));
        } else {
            // get input from terminal
            System.out.print("Enter text: ");
            input = new BufferedReader(new InputStreamReader(System.in));
        }
        WordCountMain app = new WordCountMain();
        List<String> lines = app.readAllLines(input);
        System.out.printf("Number of words %d%n", app.wordCount(lines));
    }

    List<String> readAllLines(BufferedReader reader) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines;
    }

    void readStopwords(String filename) throws IOException {
        File stopwordsFile = new File(filename);
        BufferedReader input = new BufferedReader(new FileReader(stopwordsFile));
        stopwords = readAllLines(input);
    }

    int wordCount(List<String> lines) {
        int count = 0;
        for (String line : lines) {
            String[] parts = line.split(" ");
            for (String part : parts) {
                if (part.matches("[a-zA-Z]+") && !stopwords.contains(part)) {
                    count++;
                }
            }
        }
        return count;
    }
}
