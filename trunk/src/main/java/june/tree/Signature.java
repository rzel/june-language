package june.tree;

import java.lang.reflect.*;
import java.util.*;

/**
 * Represents the information available to identify a particular entity (var, method, class, ...).
 */
public class Signature {

	/**
	 * Empty list for no args (e.g., "run()"). Null for unspecified (e.g., "run").
	 */
	public List<Type> argTypes;

	public String name;

}
