grammar June;

options {
	output = AST;
}

tokens {
	PARAMS;
	SCRIPT;
	TYPEDEF;
	TYPEREF;
	VISIBILITY;
}

script	:	use* mainClass -> ^(SCRIPT use* mainClass);

block	:	'{'! content? '}'!;

classContent
	:	 EOL* (statement|visibility) (eol (statement|visibility))* eol?;

classStatement
	:	'class' ID ('{' classContent? '}')?;

content	:	EOL* statement (eol statement)* eol?;

defStatement
	:	DEF ID ('(' params? ')')? (':' type)? block? -> ^(DEF ID params? type? block?);

eoi	:	(','|EOL) EOL* ->;

eol	:	(';'|EOL) EOL* ->;

expression
	:	ID | NUMBER;

mainClass
	:	(typeKind ':')? EOL* classContent? -> ^(TYPEDEF typeKind? classContent?);

param	:	ID ('?'|'*'|':' type)?;

params	:	EOL* param (eoi param)* eoi? -> ^(PARAMS param+);

statement
	:	classStatement
	|	defStatement
	|	varStatement
	;

type	:	ID ('.' ID)* ('[' types ']')? (c='?'|c='*')? -> ^(TYPEREF ID+ types? $c?);

typeKind
	:	'annotation'|'class'|'interface'|'role';

types	:	EOL* type (eoi type)* eoi? -> type+;

use	:	'use'^ ID ('.'! ID)* eol;

varStatement
	:	'var' ID (':' type)? ('=' expression)?;

visibility
	:	(v='internal'|v='protected'|v='private'|v='public') ':' -> ^(VISIBILITY $v);

COMMENT	:	'#' (~('\r'|'\n'))* {skip();};

DEF	:	'def';

EOL	:	'\r'|('\r'? '\n');

ID	:	(LETTER|'$') (LETTER|DIGIT|'_')*;

NUMBER	:	DIGIT+;

STRETCH	:	'...' EOL* {skip();};

VAR	:	'var';

WS	:	(' '|'\t')+ {skip();};

fragment
DIGIT	:	'0'..'9';

fragment
LETTER	:	'a'..'z'|'A'..'Z';
