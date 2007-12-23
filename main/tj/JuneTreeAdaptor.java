package tj;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;

public class JuneTreeAdaptor extends CommonTreeAdaptor {

	@Override
	public JuneTree create(Token payload) {
		// TODO Make different classes for different token types?
		return new JuneTree(payload);
	}

}
