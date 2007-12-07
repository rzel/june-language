package june;

import java.io.*;

import june.JuneParser.*;
import june.engine.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class JuneCompiler {

	public void compile(Reader reader) {
		try {
			JuneParser parser =
					new JuneParser(new CommonTokenStream(new JuneLexer(
							new ANTLRReaderStream(reader))));
			parser.setTreeAdaptor(new JuneTreeAdaptor());
			script_return script = parser.script();
			june.Analyzer analyzer =
					new june.Analyzer(
							new CommonTreeNodeStream(script.getTree()));
			analyzer.script();
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
