package june.tree;

import java.lang.reflect.*;
import java.util.*;

/**
 * Represents both normal methods and constructors.
 */
public class JuneMethod extends JuneMember implements GenericDeclaration {

	public enum MethodType {
		CONSTRUCTOR, METHOD
	}

	public List<JuneType> argTypes = new ArrayList<JuneType>();

	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
