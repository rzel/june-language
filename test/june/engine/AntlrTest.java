package june.engine;

import java.io.*;

import june.*;

import org.testng.annotations.*;

public class AntlrTest {

	@Test
	public void parse() {
		try {
			// TODO How to fail on errors?
			InputStream stream = getClass().getResourceAsStream("antlr_test.june");
			try {
				new JuneCompiler().compile(new InputStreamReader(stream));
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
