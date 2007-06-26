grammar June;

options {
	output = AST;
}

tokens {
	BLOCK;
	CALL;
	DRILL;
	LIST;
	MAP;
	PAIR;
	PARAM;
	PARAMS;
	SCRIPT;
	TYPE_DEF;
	TYPE_REF;
}

script	:	importStatement* mainClass -> ^(SCRIPT importStatement* mainClass);

args	:	EOL* (expression (eoi expression)* eoi?)? -> expression*;

block	:	'{' content? '}' -> content?;

call	:	ID ('(' args ')')? -> ^(CALL ID args?);

// TODO Should the visibility be grouping the statements?
classContent
	:	 EOL* (s+=statement|s+=visibility) (eol (s+=statement|s+=visibility))* eol? -> ^(BLOCK $s+);

classStatement
	:	typeKind ID ('{' classContent? '}')? -> ^(TYPE_DEF typeKind ID classContent?);

collection
	:	'[' args ']' -> ^(LIST args)
	|	'[' EOL* (pair (eoi pair)* eoi?)? ']' -> ^(MAP pair*)
	|	'[' EOL* ':' EOL* ']' -> ^(MAP);

content	:	EOL* statement (eol statement)* eol? -> ^(BLOCK statement+);

defStatement
	:	'def'^ ID ('('! params? ')'!)? (':'! type)? block?;

eoi	:	(','|EOL) EOL* ->;

eol	:	(';'|EOL) EOL* ->;

expression
	:	introExpression (-> introExpression | ('.' call)+ -> ^(DRILL introExpression call+));

importStatement
	:	'import'^ ID ('.'! ID)* eol;

introExpression
	:	call | collection | NUMBER;

mainClass
	:	(typeKind ':')? EOL* classContent? -> ^(TYPE_DEF typeKind? classContent?);

pair	:	ID ':' EOL* expression -> ^(PAIR ID expression); // ID or String (or Integer?)!

params	:	EOL* varDef (eoi varDef)* eoi? -> ^(PARAMS ^(PARAM varDef)+);

statement
	:	expression
	|	classStatement
	|	defStatement
	|	varStatement;

type	:	ID ('.' ID)* ('[' types ']')? (c='?'|c='*')? -> ^(TYPE_REF ID+ types? $c?);

typeKind
	:	'annotation'|'class'|'interface'|'role';

types	:	EOL* type (eoi type)* eoi? -> ^(PARAMS type+);

varDef	:	ID (c='?'|c='*') -> ID $c
	|	ID (':' EOL* type)? -> ID type?;

varStatement
	:	'var'^ varDef ('='! EOL!* expression)?;

visibility
	:	('internal'|'protected'|'private'|'public') ':'!;

COMMENT	:	'#' (~('\r'|'\n'))* {skip();};

EOL	:	'\r'|('\r'? '\n');

ID	:	(LETTER|'$') (LETTER|DIGIT|'_')*;

NUMBER	:	DIGIT+;

STRETCH	:	'...' EOL* {skip();};

WS	:	(' '|'\t')+ {skip();};

fragment
DIGIT	:	'0'..'9';

fragment
LETTER	:	'a'..'z'|'A'..'Z';
