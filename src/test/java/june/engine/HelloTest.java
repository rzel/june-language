package june.engine;

import java.io.*;
import java.util.*;

import june.tree.*;
import junit.framework.*;

public class HelloTest extends TestCase {

	private String readHello() {
		try {
			StringBuilder builder = new StringBuilder();
			InputStream stream = getClass().getResourceAsStream("hello.june");
			try {
				Reader reader =
						new BufferedReader(new InputStreamReader(
								stream,
								"UTF-8"));
				char[] buffer = new char[4096];
				READ: while (true) {
					int count = reader.read(buffer);
					if (count == -1) {
						break READ;
					}
					builder.append(buffer, 0, count);
				}
			} finally {
				stream.close();
			}
			return builder.toString();
		} catch (Exception e) {
			throw Helper.throwAny(e);
		}
	}

	public void testParser() {
		String helloSource = readHello();
		Script script = null;
		for (int i = 0; i < 100; i++) {
			Parser parser = new Parser();
			script = parser.parse(helloSource);
		}
		System.out.println(script);
		new Analyzer().analyze(script);
	}

	// public void testRunner() {
	// String helloSource = readHello();
	// Runner runner = new Runner();
	// runner.run(helloSource);
	// }

	public void testTokenizer() {
		String helloSource = readHello();
		Tokenizer tokenizer = new Tokenizer();
		List<Token> tokens = tokenizer.tokenize(helloSource);
		for (Token token: tokens) {
			// System.out.println(token);
			// Avoid disuse warning for now:
			token.toString();
		}
	}

}
