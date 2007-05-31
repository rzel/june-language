package june.tree;

public class Entity {

	public String name;

	@Override
	public String toString() {
		return getClass().getName().replaceFirst("^.*\\.", "") + ": " + name;
	}

}
