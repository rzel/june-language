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
	DEF_EXPR;
	LIST;
	MAP;
	PAIR;
	PARAM;
	PARAMS;
	SCRIPT;
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

script	:	importStatement* mainClass -> ^(SCRIPT importStatement* mainClass);

addExpression
	:	multiplyExpression (('+'^|'-'^) multiplyExpression)*;

args	:	EOL* (expression (eoi expression)* eoi?)? -> expression*;

block	:	'{'! content? '}'!;

blockExpression:
	block -> ^('do' block) |
	'do'^ '('! params ')'! block |
	'def' '(' params? ')' block -> ^(DEF_EXPR params? block);

booleanExpression
	:	compareExpression (('&&'^|'||'^) compareExpression)*;

call	:	ID ('(' args ')')? blockExpression? callPart* -> ^(CALL ID ^(ARGS args)? blockExpression? callPart*);

callPart: (ID ('(' a=args ')')?) blockExpression? -> ^(CALL_PART ID ^(ARGS args)? blockExpression?);

// TODO Should the visibility be grouping the statements?
classContent
	:	 EOL* (s+=statement|s+=visibility) (eol (s+=statement|s+=visibility))* eol? -> ^(BLOCK $s+);

classStatement
	:	typeKind ID typeParams? supers? ('{' classContent? '}')? -> ^(TYPE_DEF typeKind ID typeParams? supers? classContent?);

collection
	:	'[' args ']' -> ^(LIST args)
	|	'[' EOL* (pair (eoi pair)* eoi?)? ']' -> ^(MAP pair*)
	|	'[' EOL* ':' EOL* ']' -> ^(MAP);

compareExpression
	:	addExpression (('=='^|'!='^|'<'^|'<='^|'>'^|'>='^) addExpression)?;

content	:	EOL* statement (eol statement)* eol? -> ^(BLOCK statement+);

defStatement
	:	'def'^ ID typeParams? '('! params? ')'! (':'! type)? throwsClause? block?;

eoi	:	(','|EOL) EOL* ->;

eol	:	(';'|EOL) EOL* ->;

expression
	:	booleanExpression;

importStatement
	:	'import'^ ID ('.'! ID)* eol;

introExpression
	:	blockExpression | call | collection | ('('! expression ')'!) | NUMBER | string;

mainClass
	:	(typeKind typeParams? supers? ':')? EOL* classContent? -> ^(TYPE_DEF typeKind? typeParams? supers? classContent?);

memberExpression
	:	introExpression (('.'^|'?.'^) call)*;

multiplyExpression
	:	memberExpression (('*'^|'/'^) memberExpression)*;

pair	:	ID ':' EOL* expression -> ^(PAIR ID expression); // ID or String (or Integer?)!

params	:	EOL* varDef (eoi varDef)* eoi? -> ^(PARAMS ^(PARAM varDef)+);

statement
	:	expression
	|	classStatement
	|	defStatement
	|	varStatement;

supers: 'is'^ type ('&'! type)*;

string: POWER_STRING | RAW_STRING;

throwsClause: 'throws'^ type ('|'! type)*;

type	:	ID ('.' ID)* ('<' typeArgs '>')? (c='?'|c='*')? -> ^(TYPE_REF ID+ typeArgs? $c?);

typeArgs: EOL* type (eoi type)* eoi? -> ^(TYPE_ARGS type+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role';

typeParam: ID supers? -> ^(TYPE_PARAM ID supers?);

typeParams: '<' EOL* typeParam (eoi typeParam)* eoi? '>' -> ^(TYPE_PARAMS typeParam+);

varDef	:	ID (c='?'|c='*') -> ID $c
	|	ID (':' EOL* type)? -> ID type?;

varStatement
	:	'var'^ varDef ('='! EOL!* expression)?;

visibility
	:	('internal'|'protected'|'private'|'public') ':'!;

COMMENT	:	'#' (~('\r'|'\n'))* {skip();};

EOL	:	'\r'|('\r'? '\n');

ID	:	(LETTER|'$') (LETTER|DIGIT|'_')*;

NUMBER	:	DIGIT+;// ('.' DIGIT+)?;

// TODO So I do just have to parse this afterwards?
POWER_STRING
	:	'"' (('\\'~('\r'|'\n'))|~('"'|'\r'|'\n'))* ('"'|EOL);

RAW_STRING
	:	'\'' (~('\''|'\r'|'\n'))* ('\''|EOL);

STRETCH	:	'...' EOL* {skip();};

WS	:	(' '|'\t')+ {skip();};

fragment
DIGIT	:	'0'..'9';

fragment
LETTER	:	'a'..'z'|'A'..'Z';
