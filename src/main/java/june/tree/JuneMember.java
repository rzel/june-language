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

	private int modifiers;

	/**
	 * The type of a field or the return type of a method.
	 */
	public JuneType type;

	public Class<?> getDeclaringClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract String getDescriptor();

	public int getModifiers() {
		return modifiers;
	}

	public String getName() {
		return name;
	}

	public boolean isStatic() {
		return (modifiers & Modifier.STATIC) != 0;
	}

	public boolean isSynthetic() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setStatic(boolean $static) {
		if ($static) {
			modifiers |= Modifier.STATIC;
		} else {
			modifiers &= ~Modifier.STATIC;
		}
	}

	@Override
	public String toString() {
		return super.toString() + " in " + declaringClass;
	}

}
