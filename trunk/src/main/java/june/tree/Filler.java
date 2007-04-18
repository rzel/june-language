package june.tree;

import java.util.*;

/**
 * Represents a list of meaningless nodes. This could include whitespace, endlines, comments, and errors.
 */
public class Filler extends Inline {

	public List<Node> kids = new ArrayList<Node>();

	@Override
	public Iterable<Node> getKids() {
		return new ArrayList<Node>(kids);
	}

}
