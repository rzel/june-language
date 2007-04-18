package june.tree;

public class Do extends Inline {

	public Token doToken;

	/**
	 * If a block, is the expression the block or null?
	 */
	public Node expression;

	public Params params;

	public Filler spaceAfterParams;

	public Filler spaceBeforeParams;

	@Override
	public Iterable<Node> getKids() {
		return nonNull(
				doToken,
				spaceBeforeParams,
				params,
				spaceAfterParams,
				expression);
	}

}
