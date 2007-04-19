package june.engine;

import june.tree.*;

import static june.tree.TokenType.*;

public class Analyzer {

	// TODO Resolve names. Find higher-level bugs. More.

	// TODO Pass in a Resolver! (Into the constructor?)

	public void analyze(Script script) {
		for (Node kid: script.getKids()) {
			if (kid instanceof Block) {
				block(kid);
			}
		}
	}

	private void block(Node block) {
		for (Node kid: block.getKids()) {
			if (kid instanceof Expression) {
				expression((Expression)kid);
			}
		}
	}

	private void call(Call call, Node context) {
		for (Node kid: call.getKids()) {
			if (kid instanceof Token) {
				Token token = (Token)kid;
				if (token.type == ID) {
					System.out.println("call " + token + " at " + context);
					// TODO Search through context to find what this is.
					// TODO Do we need to know the arg types first?
				}
			}
		}
	}

	private void expression(Expression expression) {
		Node context = expression.parent;
		for (Node kid: expression.getKids()) {
			if (kid instanceof Call) {
				call((Call)kid, context);
				// TODO This should be any prior expression node (not just statement nor call) in a series of dot drills.
				context = kid;
			}
		}
	}

}
