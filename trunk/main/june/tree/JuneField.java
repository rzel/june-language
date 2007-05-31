package june.tree;

import org.objectweb.asm.*;

public class JuneField extends JuneMember {

	@Override
	public String getDescriptor() {
		return Type
				.getObjectType(((JuneClass)type).internalName)
				.getDescriptor();
	}

}
