package june.tree;

import java.lang.reflect.*;

/**
 * Represents both normal methods and constructors.
 */
public class JuneMethod extends JuneMember implements GenericDeclaration {

	public enum MethodType {
		CONSTRUCTOR, METHOD
	}

	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
