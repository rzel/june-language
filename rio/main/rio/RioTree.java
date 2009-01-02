package rio;

import java.util.*;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class RioTree extends CommonTree {

	/**
	 * The enclosing block of code that defines the scope for this node.
	 */
	public RioTree block;

	/**
	 * If this is a block, then non-null, a map from IDs to nodes.
	 */
	public Map<String, Set<RioTree>> symbols;

	public RioTree(Token payload) {
		super(payload);
	}

}
