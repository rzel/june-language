package june;

import java.io.*;

import june.JuneParser.*;
import june.engine.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class JuneCompiler {

	private void assignBlocks(JuneTree tree, JuneTree block) {
		tree.block = block;
		if (tree.isBlock()) {
			System.out.println(tree);
			block = tree;
		}
		for (JuneTree child: tree.getChildren()) {
			assignBlocks(child, block);
		}
	}

	public void compile(Reader reader) {
		try {
			JuneParser parser =
					new JuneParser(new CommonTokenStream(new JuneLexer(
							new ANTLRReaderStream(reader))));
			parser.setTreeAdaptor(new JuneTreeAdaptor());
			script_return script = parser.script();
			JuneTree tree = (JuneTree)script.getTree();
			june.SymbolDefLister symbolDefLister =
					new june.SymbolDefLister(new CommonTreeNodeStream(tree));
			symbolDefLister.script();
			assignBlocks(tree, null);
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
