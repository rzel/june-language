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

	private void putOuterSymbol(String id, JuneTree node) {
		putSymbol((Scope_scope)Scope_stack.get(Scope_stack.size() - 2), id, node);
	}

	private void putSymbol(String id, JuneTree node) {
		putSymbol((Scope_scope)Scope_stack.peek(), id, node);
	}

	private void putSymbol(Scope_scope scope, String id, JuneTree node) {
		scope.block.symbols.put(id, node);
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

blockExpression
	scope Scope;
	@init {
		startBlock($blockExpression.start);
	}
	@after {
		System.out.println("Block with " + $Scope::block.symbols);
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
		System.out.println("Class with " + $Scope::block.symbols);
	}
:
	^(TYPE_DEF typeKind? ID? fluff*) {
		putOuterSymbol($ID == null ? null : $ID.text, $classDef.start);
	}
;

defStatement
	scope Scope;
	@init {
		startBlock($defStatement.start);
	}
	@after {
		System.out.println("Def ID with " + $Scope::block.symbols);
	}
:
	^('def' ID fluff*) {
		putOuterSymbol($ID.text, $defStatement.start);
	}
;

fluff: ^(~('do'|DEF_EXPR|'def'|PARAM|TYPE_DEF|'var') fluff*) | blockExpression | classDef | defStatement | param | varStatement;

param: ^(PARAM ID .*) {
	putSymbol($ID.text, $param.start);
};

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varStatement: ^('var' ID .*) {
	putSymbol($ID.text, $varStatement.start);
};
