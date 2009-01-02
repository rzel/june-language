package fathom;

import java.io.*;

import org.testng.annotations.*;

import rio.*;

public class RioTest {

	/**
	 * TODO Delete once I either get the TestNG plugin or switch to JUnit.
	 */
	public static void main(String... args) {
		new RioTest().parser();
	}

	@Test
	public void parser() {
		String helloSource = readHello();
		System.out.println(helloSource);
		Reader reader = new StringReader(helloSource);
		new Engine().run(reader);
	}

	private String readHello() {
		try {
			StringBuilder builder = new StringBuilder();
			InputStream stream = getClass().getResourceAsStream("hello.rio");
			try {
				Reader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
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

}
