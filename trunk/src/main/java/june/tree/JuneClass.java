package june.tree;

import java.lang.reflect.*;
import java.util.*;

public class JuneClass extends JuneType implements GenericDeclaration {

	public enum ClassType {
		ANNOTATION, ASPECT, CLASS, INTERFACE, ROLE
	}

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
		JuneMember member = null;
		Set<JuneMember> candidates = members.get(usage.name);
		if (candidates != null) {
			for (JuneMember candidate: candidates) {
				// TODO Actually check. Do we need a distance metric or something fancier?
				return candidate;
			}
		}
		return member;
	}

	public TypeVariable<?>[] getTypeParameters() {
		// TODO Auto-generated method stub
		return null;
	}

}
