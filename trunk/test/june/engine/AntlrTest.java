package june.engine;

import java.io.*;

import june.*;
import june.JuneParser.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.testng.annotations.*;

public class AntlrTest {

	@Test
	public void parse() {
		try {
			// TODO How to fail on errors?
			InputStream stream = getClass().getResourceAsStream(
					"antlr_test.june");
			script_return script = new JuneParser(new CommonTokenStream(
					new JuneLexer(new ANTLRInputStream(stream)))).script();
			june.Analyzer analyzer = new june.Analyzer(
					new CommonTreeNodeStream(script.getTree()));
			analyzer.script();
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
