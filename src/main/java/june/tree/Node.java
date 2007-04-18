package june.tree;

import java.util.*;

public abstract class Node {

	/**
	 * Helper for building iterables of kids. It skips null values.
	 * 
	 * @return
	 */
	protected static List<Node> nonNull(Node... kids) {
		List<Node> list = new ArrayList<Node>();
		for (Node kid: kids) {
			if (kid != null) {
				list.add(kid);
			}
		}
		return list;
	}

	public Parent parent;

	/**
	 * Errors (and warnings? - good for IDEs if at least common?) for this node.
	 */
	public List<?> problems;

	private void buildString(StringBuilder builder, String indent) {
		indent += "  ";
		builder.append(getClass().getName().replaceFirst("^.*\\.", ""));
		builder.append(":");
		boolean firstToken = true;
		for (Node kid: getKids()) {
			if (kid instanceof Token) {
				if (firstToken) {
					builder.append("\n");
					builder.append(indent);
					firstToken = false;
				} else {
					builder.append(", ");
				}
				builder.append(kid);
			} else {
				builder.append("\n");
				builder.append(indent);
				kid.buildString(builder, indent);
				firstToken = true;
			}
		}
	}

	public abstract Iterable<Node> getKids();

	public int lineBegin() {
		// TODO
		return 0;
	}

	public int lineEnd() {
		// TODO
		return 0;
	}

	public int offsetBegin() {
		// TODO
		return 0;
	}

	public int offsetEnd() {
		// TODO
		return 0;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		buildString(builder, "");
		return builder.toString();
	}

}
