package fathom;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class RioTreeAdaptor extends CommonTreeAdaptor {

	@Override
	public RioTree create(Token payload) {
		// TODO Make different classes for different token types?
		return new RioTree(payload);
	}

}
