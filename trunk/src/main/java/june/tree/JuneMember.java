package june.tree;

import java.lang.reflect.*;

public class JuneMember extends Entity implements Member {

	/**
	 * TODO This isn't going to work since we can't just make our own Class instances on demand.
	 */
	public JuneClass declaringClass;

	public Class<?> getDeclaringClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getModifiers() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getName() {
		return name;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

}
