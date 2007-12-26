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

assignment: ^('=' target=expression expression) {
	System.out.println("Assignment on " + $target.start);
};

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

controlStatement:
	^('return' expression) |
	^('throw' expression) |
	^('break' ID? expression?) |
	^('continue' ID? expression?) |
	^('redo' ID?);

defStatement: ^('def' ('final'|'native'|'override')* ID typeParams? params? type? throwsClause? block?);

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
	^('[' expression expression*) |
	blockExpression |
	call[null] |
	collection |
	strings |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? typeParams? supers? classContent?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: assignment | classStatement | controlStatement | defStatement | expression | varStatement;

string:
	LINE_STRING {$string.start.addEntity($LINE_STRING.text);} |
	POWER_STRING |
	RAW_STRING {$string.start.addEntity($RAW_STRING.text);}
;

strings: ^(STRINGS string+);

supers: ^('is' type+);

throwsClause: ^('throws' type+);

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(TYPE_ARGS type+);

typeParam: ^(TYPE_PARAM ID supers?);

typeParams: ^(TYPE_PARAMS typeParam+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^(('val'|'var') varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
