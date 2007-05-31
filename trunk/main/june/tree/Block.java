package june.tree;

public class Block extends Parent {

	/**
	 * The class defined by this block. Some blocks are phantom classes that don't really become Java classes, but they still get seen as classes during some phases of compilation.
	 */
	public JuneClass $class = new JuneClass();

	/**
	 * The method represented by the top level code in the class. TODO We need to make it a member (probably other than the constructor - just that it's called by the constructor if for actual
	 * classes) of $class.
	 */
	public JuneMethod method = new JuneMethod();

}
