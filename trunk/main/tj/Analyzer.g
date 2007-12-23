tree grammar Analyzer;

options {
	ASTLabelType = JuneTree;
	tokenVocab = June;
}

@header {
	package tj;
}

@members {
	JuneEngine engine;
}

script: ^(SCRIPT importStatement* mainClass);

args: expression*;

block: content?;

blockExpression: ^(('do'|DEF_EXPR) params? block);

call[JuneTree target]: ^(CALL ID callArgs? blockExpression?) {
	// TODO Include argument types and so on.
	engine.findEntities(target, $call.start, $ID.text);
};

callArgs: ^(ARGS args);

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID typeParams? classContent?);

collection:	^(LIST expression*) | ^(MAP pair*);

content: ^(BLOCK statement+);

defStatement: ^('def' ID typeParams? params? type? block?);

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
	^(('.'|'?.') target=expression call[$target.start]) |
	call[null] |
	collection |
	string {
		/*$expression.start.addEntity($string.text);*/
	} |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? typeParams? classContent?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: expression|classStatement|defStatement|varStatement;

string: POWER_STRING | RAW_STRING;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(TYPE_ARGS type+);

typeParam: ^(TYPE_PARAM ID);

typeParams: ^(TYPE_PARAMS typeParam+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^('var' varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
