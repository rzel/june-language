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

assignment:
	^(('='|'+='|'-='|'*='|'/=') target=expression expression) {
		System.out.println("Assignment on " + $target.start);
	} |
	^(('++'|'--') expression)
;

block: ^(BLOCK statement+);

blockExpression: ^(('do'|DEF_EXPR) params? block);

call[JuneTree target]: ^(CALL ID args? expression? callPart*) {
	// TODO Include argument types and so on.
	engine.findEntities(target, $call.start, $ID.text);
};

callNew: ^('new' type? constructorArgs classContent?);

callPart: ^(CALL_PART ID args? expression?);

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

defPart: ID typeParams? ('?'|'*')? params?;

defStatement: ^('def' ('final'|'native'|'override')* ID typeParams? params? defPart* type? throwsClause? block?);

enumContent: ^(LIST enumItem*);

enumItem: ^(DECLARATION annotations ID);

enumStatement: ^('enum' ID enumContent);

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
	^('.' target=expression call[$target.start]) |
	^('.&' expression memberRef) |
	// TODO Need a 'this' object for the call arg.
	^(IMPLIED_THIS '.' call[null]) |
	^(IMPLIED_THIS '.&' memberRef) |
	^('[' expression expression*) |
	^('static' expression) |
	blockExpression |
	call[null] |
	callNew |
	collection |
	strings |
	NUMBER;

expressionPair: ^(PAIR expression expression);

importStatement: ^('import' 'advice'? ID+);

mainClass: ^(TYPE_DEF typeKind? typeParams? supers? classContent?);

map: ^(MAP (pair+ | expressionPair*));

memberRef: ^(MEMBER_REF ID typeArgs?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement:
	assignment | controlStatement | expression |
	^(LABEL ID (assignment | controlStatement | expression)) |
	^(DECLARATION annotations (classStatement | defStatement | enumStatement | varStatement))
;

string:
	LINE_STRING {$string.start.addEntity($LINE_STRING.text);} |
	POWER_STRING {$string.start.addEntity($POWER_STRING.text);} |
	RAW_STRING {$string.start.addEntity($RAW_STRING.text);}
;

strings: ^(STRINGS string+);

supers: ^('is' type+);

throwsClause: ^('throws' type+);

type: ^(TYPE_REF ID+ typeArgs? ('?'|'*')?) | ^(TYPE_REF ('do'|'def') typeArgs? type?);

typeArgs: ^(TYPE_ARGS type+);

typeParam: ^(TYPE_PARAM ID supers?);

typeParams: ^(TYPE_PARAMS typeParam+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^(('val'|'var') varDef expression?);

visibility: 'internal'|'protected'|'private'|'public' 'static'?;
