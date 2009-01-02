package rio;

import java.io.*;

import org.antlr.runtime.*;

public class Engine {

	public void run(Reader reader) {
		try {
			RioParser parser = new RioParser(new CommonTokenStream(new RioLexer(new ANTLRReaderStream(reader))));
			parser.setTreeAdaptor(new RioTreeAdaptor());
			RioTree tree = (RioTree)parser.script().getTree();
			// TODO Run it.
			System.out.println(tree);
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
