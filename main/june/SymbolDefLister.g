tree grammar SymbolDefLister;

options {
	ASTLabelType = JuneTree;
	tokenVocab = June;
}

scope Scope {
	JuneTree block;
}

@header {
	package june;

	import java.util.*;
	import org.antlr.runtime.BitSet;
}

@members {

	private void putSymbol(String id, JuneTree node) {
		((Scope_scope)Scope_stack.peek()).block.symbols.put(id, node);
	}

	private void startBlock(JuneTree node) {
		((Scope_scope)Scope_stack.peek()).block = node;
		((Scope_scope)Scope_stack.peek()).block.symbols = new HashMap<String, JuneTree>();
	}

}

script
	scope Scope;
	@init {
		startBlock($script.start);
	}
	@after {
		System.out.println("Script with " + $Scope::block.symbols);
	}
:
	fluff*
;

block
	scope Scope;
	@init {
		startBlock($block.start);
	}
	@after {
		System.out.println("Block with " + $Scope::block.symbols);
	}
:
	^(BLOCK fluff*)
;

classDef: ^(TYPE_DEF typeKind? ID? fluff*) {
	putSymbol($ID == null ? null : $ID.text, $classDef.start);
};

defStatement: ^('def' ID params? type? block?) {
	putSymbol($ID.text, $defStatement.start);
};

fluff: ^(~(BLOCK|'def'|PARAM|PARAMS|TYPE_DEF|'var') fluff*) | block | classDef | defStatement | param | params | varStatement;

param: ^(PARAM ID .*) {
	putSymbol($ID.text, $param.start);
};

params
	scope Scope;
	@init {
		startBlock($params.start);
	}
	@after {
		// TODO Really need to get these into the scope of the method block.
		System.out.println("Params with " + $Scope::block.symbols);
	}
:
	^(PARAMS param+)
;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

types: ^(TYPES type+);

varStatement: ^('var' ID .*) {
	putSymbol($ID.text, $varStatement.start);
};
