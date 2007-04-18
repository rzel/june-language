package june.tree;

public class Arg extends Inline {

	/**
	 * Space after the value.
	 */
	public Filler after;

	/**
	 * Space before the value.
	 */
	public Filler before;

	/**
	 * Preceding comma. Should be null for first arg.
	 */
	public Token comma;

	public Inline value;

	@Override
	public Iterable<Node> getKids() {
		return nonNull(comma, before, value, after);
	}

}
