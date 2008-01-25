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
	TYPE_DEF;
	TYPE_REF;
	TYPE_ARGS;
	TYPE_PARAM;
	TYPE_PARAMS;
}

@header {
	package tj;
}

@lexer::header {
	package tj;
}

script: packageStatement? importStatement* classContent? -> ^(SCRIPT packageStatement? importStatement* classContent?);

addExpression
	:	multiplyExpression (('+'^|'-'^) multiplyExpression)*;

annotation: '@'^ typeNoDo constructorArgs? eol*;

annotations: annotation*;

args: '(' items ')' -> ^(ARGS items);

block: '{' '^'? EOL* (statement (eol statement)* eol?)? '}' -> ^(BLOCK '^'? statement*);

blockExpression:
	block -> ^('do' block) |
	'do'^ params? block |
	'def' params? block -> ^(DEF_EXPR params? block);

booleanExpression: compareExpression (('&&'^|'||'^) compareExpression)*;

call: ID args? (blockExpression|map)? callPart* ->
	^(CALL ID args? blockExpression? map? callPart*);

callNew: 'new'^ typeNoDo? constructorArgs ('{'! classContent? '}'!)?;

callPart: ID args? (blockExpression|map)? -> ^(CALL_PART ID args? blockExpression? map?);

// TODO Should the visibility be grouping the statements?
classContent: EOL* (s+=statement|s+=visibility) (eol (s+=statement|s+=visibility))* eol? -> ^(BLOCK $s+);

classStatement:
	typeKind ID typeParams? params? supers? ('{' classContent? '}')? ->
	^(TYPE_DEF typeKind ID typeParams? params? supers? classContent?);

collection: '[' items ']' -> ^(LIST items) | map;

compareExpression
	:	addExpression (('=='^|'!='^|'<'^|'<='^|'>'^|'>='^) addExpression)?;

constructorArgs:
	'(' EOL* (expression (eoi expression)* (eoi pair)* eoi? | pair (eoi pair)* eoi?)? ')'
	-> ^(ARGS expression* pair*);

controlStatement:
	'return'^ expression |
	'throw'^ expression |
	'break'^ ID? (':'! expression)? |
	'continue'^ ID? (':'! expression)? |
	'redo'^ ID?;

defPart: ID typeParams? ('?'|'*')? params?;

defStatement:
	('final'|'native'|'override')* 'def'^
	ID typeParams? params? defPart* (':'! EOL!* type)? throwsClause? block?;

eoi: (','|EOL) EOL* ->;

eol: (';'|EOL) EOL* ->;

expression
	:	booleanExpression;

// TODO How to guarantee a good left side? Fancy grammar or a check in the Analyzer?
expressionOrAssignment: expression (
	('='^|'+='^|'-='^|'*='^|'/='^) EOL!* expression |
	('++'^|'--'^)
)?;

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

map: stringMapNotEmpty |
	'[' EOL* ':' EOL* (expressionPair (eoi expressionPair)* eoi?)? ']' -> ^(MAP expressionPair*);

memberExpression:
	(staticExpression | impliedThis)
	(('.'^ call) | ('['^ items ']'!) | ('.&'^ memberRef))*;

memberRef: ID ('(' typeArgs? ')')? -> ^(MEMBER_REF ID typeArgs?);

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
		classStatement -> ^(DECLARATION annotations classStatement) |
		defStatement -> ^(DECLARATION annotations defStatement) |
		varStatement -> ^(DECLARATION annotations varStatement)
	);

supers: 'is'^ type ('&'! type)*;

staticExpression: 'static'^ introExpression | introExpression;

string: LINE_STRING | POWER_STRING | RAW_STRING;

stringMapNotEmpty: '[' EOL* (pair (eoi pair)* eoi?)? ']' -> ^(MAP pair*);

strings: string (EOL* string)* -> ^(STRINGS string+);

throwsClause: 'throws'^ type ('|'! type)*;

type: typeNoDo | (d='do'|d='def') '?'? ('(' typeArgs ')')? type? -> ^(TYPE_REF $d '?'? typeArgs? type?);

typeNoDo: ID ('.' ID)* ('<' typeArgs '>')? (c='?'|c='*')? -> ^(TYPE_REF ID+ typeArgs? $c?);

typeArgs: EOL* type (eoi type)* eoi? -> ^(TYPE_ARGS type+);

typeKind: 'annotation' | 'class' | 'enum' | 'interface' | 'role';

typeParam: ID supers? -> ^(TYPE_PARAM ID supers?);

typeParams: '<' EOL* typeParam (eoi typeParam)* eoi? '>' -> ^(TYPE_PARAMS typeParam+);

varDef	:	ID (c='?'|c='*') -> ID $c
	|	ID (':' EOL* type)? -> ID type?;

varStatement
	:	('val'^|'var'^) varDef ('='! EOL!* expression)?;

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
