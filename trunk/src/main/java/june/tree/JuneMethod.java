package june.tree;

import java.lang.reflect.*;
import java.util.*;

import org.objectweb.asm.Type;

/**
 * Represents both normal methods and constructors.
 */
public class JuneMethod extends JuneMember implements GenericDeclaration {

	public enum MethodType {
		CONSTRUCTOR, METHOD
	}

	public List<JuneType> argTypes = new ArrayList<JuneType>();

	@Override
	public String getDescriptor() {
		// TODO Seems like a lot of work. Probably should cache the results.
		Type[] asmTypes = new Type[argTypes.size()];
		int index = 0;
		for (JuneType juneType: argTypes) {
			asmTypes[index] = ((JuneClass)juneType).toAsmType();
			index++;
		}
		return Type
				.getMethodDescriptor(((JuneClass)type).toAsmType(), asmTypes);
	}

	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return super.toString() + " with " + argTypes;
	}

}
