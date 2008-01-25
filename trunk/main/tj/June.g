grammar June;

options {
	output = AST;
}

tokens {
	ARGS;
	BLOCK;
	CALL;
	CALL_PART;
	COMPARE;
	DECLARATION;
	DEF;
	DEF_EXPR;
	GET_AT;
	IMPLIED_THIS;
	LABEL;
	LIST;
	MAP;
	MEMBER_REF;
	PAIR;
	PARAM;
	PARAMS;
	SCRIPT;
	STRINGS;
	TYPE_AND;
	TYPE_ARGS;
	TYPE_PARAM;
	TYPE_PARAMS;
	TYPE_REF;
}

@header {
	package tj;
}

@lexer::header {
	package tj;
}

script: packageStatement? importStatement* content? -> ^(SCRIPT packageStatement? importStatement* content?);

addExpression
	:	multiplyExpression (('+'^|'-'^) multiplyExpression)*;

annotation: '@'^ typeNoDo args? eol*;

annotations: annotation*;

args: '(' EOL* argItems? ')' -> ^(ARGS argItems?);

argItems: expression (eoi expression)* (eoi pair)* eoi? | pair (eoi pair)* eoi?;

block: '{' '^'? EOL* content? '}' -> ^(BLOCK '^'? content?);

blockExpression:
	block -> ^('do' block) |
	'do'^ params? block |
	'def' params? block -> ^(DEF_EXPR params? block);

booleanExpression: compareExpression (('&&'^|'||'^) compareExpression)*;

call: ID args? blockExpression? callPart* ->
	^(CALL ID args? blockExpression? callPart*);

callNew: 'new'^ typeNoDo? args block?;

callPart: ID args? blockExpression? -> ^(CALL_PART ID args? blockExpression?);

collection: '[' EOL* argItems? ']' -> ^(LIST argItems?) | expressionMap;

compareExpression
	:	addExpression (('=='^|'!='^|'<'^|'<='^|'>'^|'>='^) addExpression)?;

content: statement (eol statement)* eol? -> statement+;

controlStatement:
	'return'^ expression |
	'throw'^ expression |
	'break'^ ID? (':'! expression)? |
	'continue'^ ID? (':'! expression)? |
	'redo'^ ID?;

defStatement:
	modifier* typeKind ID typeParams? params? defPart* typeSpec? throwsClause? block? ->
	^(DEF modifier* typeKind ID typeParams? params? defPart* typeSpec? throwsClause? block?);

defPart: ID typeParams? ('?'|'*')? params?;

eoi: (','|EOL) EOL* ->;

eol: (';'|EOL) EOL* ->;

expression
	:	booleanExpression;

// TODO How to guarantee a good left side? Fancy grammar or a check in the Analyzer?
expressionOrAssignment: expression (
	('='^|'+='^|'-='^|'*='^|'/='^) EOL!* expression |
	('++'^|'--'^)
)?;

expressionMap: '[' EOL* ':' EOL* (expressionPair (eoi expressionPair)* eoi?)? ']' -> ^(MAP expressionPair*);

expressionPair: expression ':' EOL* expression -> ^(PAIR expression+);

impliedThis:
	('.' call) -> ^(IMPLIED_THIS '.' call) |
	('.&' memberRef) -> ^(IMPLIED_THIS '.&' memberRef) |
	'&'^ memberRef;

importStatement
	:	'import'^ ('advice'?) ID ('.'! ID)* eol;

introExpression
	:	blockExpression | call | callNew | collection | ('('! expression ')'!) | NUMBER | strings;

items: EOL* (expression (eoi expression)* eoi?)? -> expression*;

memberExpression:
	(staticExpression | impliedThis)
	(('.'^ call) | ('['^ items ']'!) | ('.&'^ memberRef))*;

memberRef: ID ('(' typeArgs? ')')? -> ^(MEMBER_REF ID typeArgs?);

modifier: 'final'|'native'|'override';

multiplyExpression: notExpression (('*'^|'/'^) notExpression)*;

notExpression: '!'^? memberExpression;

packageStatement: 'package'^ ID ('.'! ID)* eol;

pair: ID ':' EOL* expression -> ^(PAIR ID expression); // ID or String (or Integer?)!

param: (v='var'|v='val')? varDef -> ^(PARAM $v? varDef);

params: '(' EOL* (param (eoi param)* eoi?)? ')' -> ^(PARAMS param*);

statement:
	controlStatement | expressionOrAssignment |
	ID ':' (
		controlStatement -> ^(LABEL ID controlStatement) |
		expressionOrAssignment -> ^(LABEL ID expressionOrAssignment)
	) |
	annotations (
		defStatement -> ^(DECLARATION annotations defStatement) |
		varStatement -> ^(DECLARATION annotations varStatement)
	) |
	visibility;

staticExpression: 'static'^ introExpression | introExpression;

string: LINE_STRING | POWER_STRING | RAW_STRING;

strings: string (EOL* string)* -> ^(STRINGS string+);

throwsClause: 'throws'^ type ('|'! type)*;

type:
	typeNoDo |
	(d='do'|d='def') '?'? ('(' typeArgs ')')? type? -> ^(TYPE_REF $d '?'? typeArgs? type?);

typeArgs: EOL* type (eoi type)* eoi? -> ^(TYPE_ARGS type+);

typeBasic: ID ('.' ID)* ('<' typeArgs '>')? (c='?'|c='*')? -> ^(TYPE_REF ID+ typeArgs? $c?);

typeKind: 'annotation' | 'class' | 'def' | 'enum' | 'interface' | 'role';

typeNoDo: typeBasic (('&' typeBasic)* -> ^(TYPE_AND typeBasic+));

typeParam: ID typeSpec? -> ^(TYPE_PARAM ID typeSpec?);

typeParams: '<' EOL* typeParam (eoi typeParam)* eoi? '>' -> ^(TYPE_PARAMS typeParam+);

typeSpec: ':'! type;

varDef	:	ID (c='?'|c='*') -> ID $c
	|	ID (':' EOL* type)? -> ID type?;

varStatement
	:	('val'^|'var'^) varDef ('='! EOL!* expression)?;

// TODO Should the visibility be grouping the statements?
visibility
	:	('internal'|'protected'|'private'|'public') 'static'? ':'!;

COMMENT: '#' (~('\r'|'\n'))* {skip();};

EOL	:	'\r'|('\r'? '\n');

ID	:	(LETTER|'$') (LETTER|DIGIT|'_')*;

LINE_STRING: '`' (~('\r'|'\n'))*;

NUMBER	:	DIGIT+;// ('.' DIGIT+)?;

// TODO So I do just have to parse this afterwards?
POWER_STRING: '"' (('\\'~('\r'|'\n'))|~('"'|'\r'|'\n'))* '"'?;

RAW_STRING: '\'' (~('\''|'\r'|'\n'))* '\''?;

STRETCH	:	'...' EOL* {skip();};

WS	:	(' '|'\t')+ {skip();};

fragment
DIGIT	:	'0'..'9';

fragment
LETTER	:	'a'..'z'|'A'..'Z';
