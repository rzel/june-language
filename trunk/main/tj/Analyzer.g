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

script: ^(SCRIPT packageStatement? importStatement* statement*);

annotation: ^('@' type args?) {
	Log.info("Annotation " + $type.start);
};

annotations: annotation*;

args: ^(ARGS expression* pair*);

assignment:
	^(('='|'+='|'-='|'*='|'/=') target=expression expression) {
		Log.info("Assignment on " + $target.start);
	} |
	^(('++'|'--') expression)
;

block: ^(BLOCK '^'? statement*);

blockExpression: ^(('do'|DEF_EXPR) params? block);

call[JuneTree target]: ^(CALL ID args? expression? callPart*) {
	// TODO Include argument types and so on.
	engine.findEntities(target, $call.start, $ID.text);
};

callNew: ^('new' type? args block?);

callPart: ^(CALL_PART ID args? expression?);

collection:	^(LIST expression*) | map;

controlStatement:
	^('return' expression) |
	^('throw' expression) |
	^('break' ID? expression?) |
	^('continue' ID? expression?) |
	^('redo' ID?);

defPart: ID typeParams? ('?'|'*')? params?;

defStatement: ^(DEF modifier* typeKind ID typeParams? params? defPart* type? throwsClause? block?);

expression:
	^('!' expression) |
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
	^('&' memberRef) |
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

map: ^(MAP (pair+ | expressionPair*));

memberRef: ^(MEMBER_REF ID typeArgs?);

modifier: 'final'|'native'|'override';

packageStatement: ^('package' ID+);

pair: ^(PAIR ID expression);

param: ^(PARAM ('var'|'val')? varDef);

params: ^(PARAMS param*);

statement:
	assignment | controlStatement | expression |
	^(LABEL ID (assignment | controlStatement | expression)) |
	^(DECLARATION annotations (defStatement | varStatement)) |
	visibility
;

string:
	LINE_STRING {$string.start.addEntity($LINE_STRING.text);} |
	POWER_STRING {$string.start.addEntity($POWER_STRING.text);} |
	RAW_STRING {$string.start.addEntity($RAW_STRING.text);}
;

strings: ^(STRINGS string+);

throwsClause: ^('throws' type+);

type:
	^(TYPE_REF ID+ typeArgs? ('?'|'*')?) |
	^(TYPE_REF ('do'|'def') '?'? typeArgs? type?) |
	^(TYPE_AND type+);

typeArgs: ^(TYPE_ARGS type+);

typeParam: ^(TYPE_PARAM ID type?);

typeParams: ^(TYPE_PARAMS typeParam+);

typeKind: 'annotation'|'aspect'|'class'|'def'|'enum'|'interface'|'role';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^(('val'|'var') varDef expression?);

visibility: ('internal'|'protected'|'private'|'public') 'static'?;
