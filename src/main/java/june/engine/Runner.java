package june.engine;

import june.tree.*;

import static june.engine.Helper.*;

public class Runner {

	/**
	 * Run the script with a default context.
	 */
	public void run(CharSequence source) {
		// TODO Run the analyzer, compile dependent files, and so on.
		Script script = new Parser().parse(source);
		Class<?> class_ = new Compiler().compile(script);
		newInstance(class_);
	}

}
