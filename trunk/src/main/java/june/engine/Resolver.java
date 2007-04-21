package june.engine;

import june.tree.*;

public abstract class Resolver {

	// TODO Keep abstract enough that it could apply other than with Java classpaths?

	// TODO How do we represent a signified entity (method, field, property, class, package)? Just saying "Object" for now.

	public Resolver parent;

	protected abstract Entity findCurrentEntity(Signature signature);

	public Entity findEntity(Signature signature) {
		Entity entity = findCurrentEntity(signature);
		if (entity == null) {
			entity = parent.findEntity(signature);
		}
		return entity;
	}

}
