package june.engine;

import java.io.*;

import org.testng.annotations.*;

import tj.*;

public class JuneTest {

	@Test
	public void parse() {
		try {
			// TODO How to fail on errors?
			for (int i = 0; i < 1; i++) {
				InputStream stream = getClass().getClassLoader().getResourceAsStream("june/Core.june");
				try {
					new JuneEngine().compile(new BufferedReader(new InputStreamReader(stream)));
				} finally {
					stream.close();
				}
			}
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
