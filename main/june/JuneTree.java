package june;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class JuneTree extends CommonTree {

	/**
	 * The enclosing block of code that defines the scope for this node.
	 */
	public JuneTree block;

	/**
	 * TODO Need expected types and given types as separate lists (and dependent
	 * types for overloaded potential method matches?).
	 */
	public Class<?> type;

	public JuneTree(Token payload) {
		super(payload);
	}

}
