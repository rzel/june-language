package june.tree;

import java.lang.reflect.*;

/**
 * TODO Just fields and methods? I'd like to keep inner classes separately, I think.
 */
public abstract class JuneMember extends Entity implements Member {

	/**
	 * TODO This isn't going to work since we can't just make our own Class instances on demand.
	 */
	public JuneClass declaringClass;

	/**
	 * The type of a field or the return type of a method.
	 */
	public JuneType type;

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

	@Override
	public String toString() {
		return super.toString() + " in " + declaringClass;
	}

}
