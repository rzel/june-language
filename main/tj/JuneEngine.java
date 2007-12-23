package tj;

import java.io.*;
import java.util.*;

import june.engine.*;
import june.tree.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class JuneEngine {

	private Map<String, Entity> globals = new HashMap<String, Entity>();

	private void assignBlocks(JuneTree tree, JuneTree block) {
		tree.block = block;
		if (tree.isBlock()) {
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
			JuneTree script = (JuneTree)parser.script().getTree();
			script.initScript();
			// List the symbols defined for each scope.
			SymbolDefLister symbolDefLister =
					new SymbolDefLister(new CommonTreeNodeStream(script));
			symbolDefLister.script();
			assignBlocks(script, null);
			// TODO Gather up symbol IDs across all source files before proceeding.
			// Determine the entities and types referred to.
			Analyzer analyzer = new Analyzer(new CommonTreeNodeStream(script));
			analyzer.engine = this;
			analyzer.script();
			// TODO Generate bytecode.
			// TODO Supply result via a ClassLoader or on disk or in memory or something.
		} catch (Exception e) {
			Helper.throwAny(e);
		}
	}

	void findEntities(JuneTree target, JuneTree node, String id) {
		System.out.println("findEntities2 for " + id + " on " + target);
		if (target == null) {
			// Search the scope.
			JuneTree block = node.isBlock() ? node : node.block;
			JuneTree script = null;
			while (block != null) {
				if (block.getType() == JuneParser.SCRIPT) {
					script = block;
				}
				Set<JuneTree> entities = block.symbols.get(id);
				if (entities != null) {
					System.out.println(entities);
				}
				block = block.block;
			}
			if (script != null) {
				// Need types for Usage.
				new Resolver.ImportResolver(script.imports, globals, null);
			}
			// TODO If no good matches still, then check imports, classes in this packages, and top level package names.
		} else {
			// Search the target.
		}
	}

}
