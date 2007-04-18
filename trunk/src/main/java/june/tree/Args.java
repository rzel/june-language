package june.tree;

import java.util.*;

/**
 * Represents an args list calling a method.
 */
public class Args extends Inline {

	public List<Arg> argItems = new ArrayList<Arg>();

	public Token closeParen;

	public Token openParen;

	@Override
	public Iterable<Node> getKids() {
		List<Node> kids = nonNull(openParen);
		kids.addAll(argItems);
		kids.addAll(nonNull(closeParen));
		return kids;
	}

}
