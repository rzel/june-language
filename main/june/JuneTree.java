package june;

import java.util.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class JuneTree extends CommonTree {

	/**
	 * The enclosing block of code that defines the scope for this node.
	 */
	public JuneTree block;

	/**
	 * If this is a block, then non-null, a map from IDs to nodes.
	 */
	public Map<String, Set<JuneTree>> symbols;

	/**
	 * TODO Need expected types and given types as separate lists (and dependent types for overloaded potential method matches?).
	 */
	public Class<?> type;

	public JuneTree(Token payload) {
		super(payload);
	}

	public void addSymbol(String id, JuneTree node) {
		Set<JuneTree> nodes = symbols.get(id);
		if (nodes == null) {
			nodes = new HashSet<JuneTree>();
			symbols.put(id, nodes);
		}
		nodes.add(node);
	}

	@SuppressWarnings("unchecked")
	public Iterable<JuneTree> getChildren() {
		return children == null ? Collections.emptyList() : children;
	}

	public boolean isBlock() {
		return symbols != null;
	}

}
