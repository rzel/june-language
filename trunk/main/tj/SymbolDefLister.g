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
		//System.out.println("Script with " + $Scope::block.symbols);
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
		//System.out.println("Block with " + $Scope::block.symbols);
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
		//System.out.println("Block expression with " + $Scope::block.symbols);
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
		//System.out.println("Class with " + $Scope::block.symbols);
	}
:
	^(TYPE_DEF typeKind? ID? fluff*) {
		putOuterSymbol($ID == null ? null : $ID.text, $classDef.start);
	} |
	^('enum' ID enumContent) {
		putOuterSymbol($ID.text, $classDef.start);
	}
;

defStatement
	scope Scope;
	@init {
		startBlock($defStatement.start);
	}
	@after {
		//System.out.println("Def ID with " + $Scope::block.symbols);
	}
:
	^('def' ('final'|'native'|'override')* ID fluff*) {
		putOuterSymbol($ID.text, $defStatement.start);
	}
;

enumContent
	scope Scope;
	@init {
		startBlock($enumContent.start);
	}
:
	^(LIST enumItem*)
;

enumItem: ^(DECLARATION fluff* ID) {
	addSymbol($ID.text, $enumItem.start);
};

label: ^(LABEL ID fluff*) {
	addSymbol($ID.text, $label.start);
};

fluff:
	^(~(BLOCK|'do'|DEF_EXPR|'def'|'enum'|LABEL|PARAM|TYPE_DEF|TYPE_PARAM|'var') fluff*) |
	block | blockExpression | classDef | defStatement | label | param | typeParam | varStatement
;

param: ^(PARAM ID .*) {
	addSymbol($ID.text, $param.start);
};

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role';

typeParam: ^(TYPE_PARAM ID .*) {
	addSymbol($ID.text, $typeParam.start);
};

typeParams: ^(TYPE_PARAMS typeParam+);

varStatement: ^(('val'|'var') ID .*) {
	addSymbol($ID.text, $varStatement.start);
};
