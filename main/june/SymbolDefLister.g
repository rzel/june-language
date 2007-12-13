tree grammar SymbolDefLister;

options {
	ASTLabelType = JuneTree;
	tokenVocab = June;
}

scope Scope {
	Map<String, JuneTree> symbols;
}

@header {
	package june;

	import java.util.Map;
	import java.util.HashMap;
}

script: fluff*;

block
	scope Scope;
	@init {
		$Scope::symbols = new HashMap<String, JuneTree>();
	}
	@after {
		System.out.println("Block with " + $Scope::symbols);
	}
:
	^(BLOCK fluff*)
;

classDef: ^(TYPE_DEF typeKind? ID? fluff*) {
	$Scope::symbols.put($ID.text, $classDef.start);
};

defStatement: ^('def' ID params? type? block?) {
	$Scope::symbols.put($ID.text, $defStatement.start);
};

fluff: ^(~(BLOCK|'def'|PARAM|PARAMS|TYPE_DEF|'var') fluff*) | block | classDef | defStatement | param | params | varStatement;

param: ^(PARAM ID .*) {
	$Scope::symbols.put($ID.text, $param.start);
};

params
	scope Scope;
	@init {
		$Scope::symbols = new HashMap<String, JuneTree>();
	}
	@after {
		// TODO Really need to get these into the scope of the method block.
		System.out.println("Params with " + $Scope::symbols);
	}
:
	^(PARAMS param+)
;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

types: ^(TYPES type+);

varStatement: ^('var' ID .*) {
	$Scope::symbols.put($ID.text, $varStatement.start);
};
