package june.tree;

import java.lang.reflect.*;
import java.util.*;

/**
 * Represents the information available (name, argument types, ...) to identify a particular entity (var, method, class, ...).
 */
public class Usage {

	/**
	 * Empty list for no args (e.g., "run()"). Null for unspecified (e.g., "run").
	 */
	public List<Type> argTypes;

	public String name;

}
