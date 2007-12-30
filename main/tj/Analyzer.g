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

annotation: ^('@' type constructorArgs?) {
	System.out.println("Annotation " + $type.start);
};

annotations: annotation*;

args: ^(ARGS expression*);

assignment: ^('=' target=expression expression) {
	System.out.println("Assignment on " + $target.start);
};

block: ^(BLOCK statement+);

blockExpression: ^(('do'|DEF_EXPR) params? block);

call[JuneTree target]: ^(CALL ID args? expression? callPart*) {
	// TODO Include argument types and so on.
	engine.findEntities(target, $call.start, $ID.text);
};

callNew: ^('new' type? constructorArgs classContent?);

callPart: ^(CALL_PART ID args? blockExpression?);

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID typeParams? supers? classContent?);

collection:	^(LIST expression*) | map;

constructorArgs: ^(ARGS expression* pair*);

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
	^('.&' expression memberRef) |
	^('[' expression expression*) |
	^('static' expression) |
	blockExpression |
	call[null] |
	callNew |
	collection |
	strings |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? typeParams? supers? classContent?);

map: ^(MAP pair*);

memberRef: ^(MEMBER_REF ID typeArgs?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement:
	assignment | controlStatement | expression |
	^(LABEL ID (assignment | controlStatement | expression)) |
	^(DECLARATION annotations (classStatement | defStatement | varStatement))
;

string:
	LINE_STRING {$string.start.addEntity($LINE_STRING.text);} |
	POWER_STRING |
	RAW_STRING {$string.start.addEntity($RAW_STRING.text);}
;

strings: ^(STRINGS string+);

supers: ^('is' type+);

throwsClause: ^('throws' type+);

type: ^(TYPE_REF ID+ typeArgs? ('?'|'*')?);

typeArgs: ^(TYPE_ARGS type+);

typeParam: ^(TYPE_PARAM ID supers?);

typeParams: ^(TYPE_PARAMS typeParam+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^(('val'|'var') varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
