package sk.linhard.wc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;

public class Main {

	private static final Charset UTF_8 = Charset.forName("UTF-8");

	public static void main(String[] args) {
		try {
			System.out.print("Enter text: ");
			Reader inputReader = createInputReader();
			WordCounter wordCounter = new WordCounter(inputReader);
			int count = wordCounter.count();
			System.out.print("Number of words: " + count);
		} catch (Throwable e) {
			System.err.println("ERROR: " + e.getMessage());
		}
	}

	private static Reader createInputReader() throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, UTF_8));
		return new StringReader(reader.readLine());
	}
}
