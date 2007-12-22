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
	 * The set of possible entities to which this node might refer, such as the
	 * variable, method, or class.
	 */
	public Set<Object> entities;

	/**
	 * If this is a block, then non-null, a map from IDs to nodes.
	 */
	public Map<String, Set<JuneTree>> symbols;

	/**
	 * TODO Need expected types and given types as separate lists (and dependent
	 * types for overloaded potential method matches?).
	 */
	public Class<?> type;

	public JuneTree(Token payload) {
		super(payload);
	}

	public void addEntity(Object entity) {
		if (entities == null) {
			entities = new HashSet<Object>();
		}
		entities.add(entity);
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

	public void findEntities(JuneTree target, String id) {
		System.out.println("findEntities for " + id + " on " + target);
		if (target == null) {
			// Search the scope.
			JuneTree block = isBlock() ? this : this.block;
			while (block != null) {
				Set<JuneTree> entities = block.symbols.get(id);
				if (entities != null) {
					System.out.println(entities);
				}
				block = block.block;
			}
		} else {
			// Search the target.
		}
	}

	public boolean isBlock() {
		return symbols != null;
	}

}
