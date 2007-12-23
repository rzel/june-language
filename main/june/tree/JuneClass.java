package june.tree;

import java.lang.reflect.*;
import java.util.*;

import org.objectweb.asm.Type;

/**
 * All types in June are represented by Java classes (except for arrays?). The nullable indicator is used to distinguish primitives as needed. Um, then how do we represent NonNull annotations on
 * (e.g.) Integer types for future Java versions? Have to deal with that later.
 */
public class JuneClass extends JuneType implements GenericDeclaration {

	public enum ClassType {
		ANNOTATION, ASPECT, CLASS, INTERFACE, ROLE
	}

	public JunePackage $package;

	/**
	 * With '$' or '.'? And what about the very base name (with neither)?
	 */
	public String baseName;

	public String internalName;

	public boolean loaded;

	public Map<String, Set<JuneMember>> members =
			new HashMap<String, Set<JuneMember>>();

	public void addMember(JuneMember member) {
		Set<JuneMember> members = this.members.get(member.name);
		if (members == null) {
			members = new HashSet<JuneMember>();
			this.members.put(member.name, members);
		}
		members.add(member);
	}

	/**
	 * Finds the best match of members (fields or methods) if any for the given signature.
	 */
	public JuneMember getMember(Usage usage) {
		JuneMember best = null;
		Set<JuneMember> candidates = members.get(usage.name);
		if (candidates != null) {
			for (JuneMember candidate: candidates) {
				// TODO Varargs of all arrays and lists (and sets and other iterables?). Or no varargs in June?
				// TODO Actually check. Do we need a distance metric or something multidimensional?
				if (usage.argTypes == null) {
					if (candidate instanceof JuneField) {
						// Done deal.
						return candidate;
					} else if (candidate instanceof JuneMethod) {
						if (((JuneMethod)candidate).argTypes.isEmpty()) {
							best = candidate;
						}
					}
				} else if (candidate instanceof JuneMethod) {
					// TODO Check for hierarchy matches up and down. June allows automatic downcasting.
					// TODO Prefer upcasts to downcasts? Or call such cases ambiguous?
					if (((JuneMethod)candidate).argTypes.equals(usage.argTypes)) {
						// Done deal.
						return candidate;
					}
				}
			}
		}
		return best;
	}

	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	Type toAsmType() {
		if (internalName.equals("java/lang/Void")) {
			return Type.VOID_TYPE;
		} else {
			return Type.getObjectType(internalName);
		}
	}

}
