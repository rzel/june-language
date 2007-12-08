tree grammar Analyzer;

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

script
	scope Scope;
	@init {
		$Scope::symbols = new HashMap<String, JuneTree>();
	}
	@after {
		System.out.println($Scope::symbols);
	}
:
	^(SCRIPT importStatement* mainClass)
;

args: ^(ARGS expression*);

block: content?;

call: ^(CALL ID args? block?);

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID classContent?);

collection:	^(LIST expression*) | ^(MAP pair*);

content: ^(BLOCK statement+);

defStatement: ^('def' ID params? type? block?) {
	$Scope::symbols.put($ID.text, $defStatement.start);
};

expression:
	^('&&' expression expression) |
	^('||' expression expression) |
	^('==' expression expression) |
	^('!=' expression expression) |
	^('<' expression expression) |
	^('<=' expression expression) |
	^('>' expression expression) |
	^('>=' expression expression) |
	^('+' expression expression) |
	^('-' expression expression) |
	^('*' expression expression) |
	^('/' expression expression) |
	^(('.'|'?.') expression call) |
	call |
	collection |
	string {$expression.start.type = String.class;} |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? classContent?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: expression|classStatement|defStatement|varStatement;

string: RAW_STRING | POWER_STRING;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(PARAMS type+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^('var' varDef expression?) {
	$Scope::symbols.put($varDef.start.getText(), $varStatement.start);
};

visibility: 'internal'|'protected'|'private'|'public';
