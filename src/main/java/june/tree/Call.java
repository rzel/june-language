package june.tree;

/**
 * Any kind of reference (to var or class or ...) really. It could even be a method call.
 */
public class Call extends Expression {

	public Entity entity;

	/**
	 * Whether this call is free standing (e.g. "call") or tied explicitly (e.g., "something.call").
	 */
	public boolean open;

}
