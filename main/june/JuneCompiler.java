package june;

import java.io.*;

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
			// Parse the source into a tree.
			JuneParser parser =
					new JuneParser(new CommonTokenStream(new JuneLexer(
							new ANTLRReaderStream(reader))));
			parser.setTreeAdaptor(new JuneTreeAdaptor());
			JuneTree tree = (JuneTree)parser.script().getTree();
			// List the symbols defined for each scope.
			june.SymbolDefLister symbolDefLister =
					new june.SymbolDefLister(new CommonTreeNodeStream(tree));
			symbolDefLister.script();
			assignBlocks(tree, null);
			// TODO Gather up symbol IDs across all source files before proceeding.
			// Determine the entities and types referred to.
			june.Analyzer analyzer = new june.Analyzer(new CommonTreeNodeStream(tree));
			analyzer.script();
			// TODO Generate bytecode.
			// TODO Supply result via a ClassLoader or on disk or in memory or something.
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

}
