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

call[JuneTree target]: ^(CALL ID callArgs? expression? callPart*) {
	// TODO Include argument types and so on.
	engine.findEntities(target, $call.start, $ID.text);
};

callArgs: ^(ARGS args);

callPart: ^(CALL_PART ID callArgs? blockExpression?);

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID typeParams? supers? classContent?);

collection:	^(LIST expression*) | ^(MAP pair*);

content: ^(BLOCK statement+);

defStatement: ^('def' ID typeParams? params? type? throwsClause? block?);

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
	blockExpression |
	call[null] |
	collection |
	string |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? typeParams? supers? classContent?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: expression|classStatement|defStatement|varStatement;

string: POWER_STRING | RAW_STRING {$string.start.addEntity($RAW_STRING.text);};

supers: ^('is' type+);

throwsClause: ^('throws' type+);

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(TYPE_ARGS type+);

typeParam: ^(TYPE_PARAM ID supers?);

typeParams: ^(TYPE_PARAMS typeParam+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^('var' varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
