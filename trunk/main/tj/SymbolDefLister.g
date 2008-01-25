tree grammar SymbolDefLister;

options {
	ASTLabelType = JuneTree;
	tokenVocab = June;
}

scope Scope {
	JuneTree block;
}

@header {
	package tj;

	import java.util.*;
	import org.antlr.runtime.BitSet;
}

@members {

	private void putOuterSymbol(String id, JuneTree node) {
		addSymbol((Scope_scope)Scope_stack.get(Scope_stack.size() - 2), id, node);
	}

	private void addSymbol(String id, JuneTree node) {
		addSymbol((Scope_scope)Scope_stack.peek(), id, node);
	}

	private void addSymbol(Scope_scope scope, String id, JuneTree node) {
		scope.block.addSymbol(id, node);
	}

	private void startBlock(JuneTree node) {
		((Scope_scope)Scope_stack.peek()).block = node;
		((Scope_scope)Scope_stack.peek()).block.symbols = new HashMap<String, Set<JuneTree>>();
	}

}

script
	scope Scope;
	@init {
		startBlock($script.start);
	}
	@after {
		//Log.info("Script with " + $Scope::block.symbols);
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
		//Log.info("Block with " + $Scope::block.symbols);
	}
:
	^(BLOCK fluff*)
;

blockExpression
	scope Scope;
	@init {
		startBlock($blockExpression.start);
	}
	@after {
		//Log.info("Block expression with " + $Scope::block.symbols);
	}
:
	^(('do'|DEF_EXPR) fluff*)
;

classDef
	scope Scope;
	@init {
		startBlock($classDef.start);
	}
	@after {
		//Log.info("Class with " + $Scope::block.symbols);
	}
:
	^(TYPE_DEF modifier* typeKind? ID fluff*) {
		putOuterSymbol($ID == null ? null : $ID.text, $classDef.start);
	}
;

label: ^(LABEL ID fluff*) {
	addSymbol($ID.text, $label.start);
};

fluff:
	^(~(BLOCK|'do'|DEF_EXPR|LABEL|PARAM|TYPE_DEF|TYPE_PARAM|'var') fluff*) |
	block | blockExpression | classDef | label | param | typeParam | varStatement
;

modifier: 'final'|'native'|'override';

param: ^(PARAM ('var'|'val')? ID .*) {
	addSymbol($ID.text, $param.start);
};

typeKind: 'annotation'|'aspect'|'class'|'def'|'enum'|'interface'|'role';

typeParam: ^(TYPE_PARAM ID .*) {
	addSymbol($ID.text, $typeParam.start);
};

typeParams: ^(TYPE_PARAMS typeParam+);

varStatement: ^(('val'|'var') ID .*) {
	addSymbol($ID.text, $varStatement.start);
};
