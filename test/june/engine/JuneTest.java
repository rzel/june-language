package june.engine;

import java.io.*;

import org.testng.annotations.*;

import tj.*;

public class JuneTest {

	@Test
	public void parse() {
		try {
			// TODO How to fail on errors?
			InputStream stream = getClass().getClassLoader().getResourceAsStream("june/Array.june");
			try {
				new JuneEngine().compile(new InputStreamReader(stream));
			} finally {
				stream.close();
			}
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
