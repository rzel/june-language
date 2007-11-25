package june.engine;

import java.io.*;

import june.*;
import june.JuneParser.*;
import junit.framework.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class AntlrTest extends TestCase {

	public void testParse() {
		try {
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
