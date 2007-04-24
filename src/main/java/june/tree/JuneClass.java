package june.tree;

import java.lang.reflect.*;
import java.util.*;

public class JuneClass extends JuneType implements GenericDeclaration {

	public enum ClassType {
		ANNOTATION, CLASS, INTERFACE, ROLE
	}

	/**
	 * TODO Instead have a map by name of lists of members?
	 */
	public List<JuneMethod> methods = new ArrayList<JuneMethod>();

	public JuneMember getMember(Signature signature) {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
