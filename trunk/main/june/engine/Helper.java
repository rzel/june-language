package june.engine;

public class Helper {

	public static RuntimeException throwAny(Throwable throwable) {
		if (throwable instanceof Error) {
			throw (Error)throwable;
		} if (throwable instanceof RuntimeException) {
			throw (RuntimeException)throwable;
		} else {
			throw new RuntimeException(throwable);
		}
	}

	public static void newInstance(Class<?> class_) {
		try {
			class_.newInstance();
		} catch (Exception e) {
			throwAny(e);
		}
	}

}