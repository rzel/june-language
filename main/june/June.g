grammar June;

options {
	output = AST;
}

script	:	use* mainClass;

block	:	'{' content? '}';

classContent
	:	 EOL* (statement|visibility) (eol (statement|visibility))* eol?;

classStatement
	:	'class' ID ('{' classContent? '}')?;

classType
	:	'annotation'|'class'|'interface'|'role';

content	:	EOL* statement (eol statement)* eol?;

defStatement
	:	'def' ID ('(' params? ')')? (':' type)? block?;

eoi	:	(','|EOL) EOL* ->;

eol	:	(';'|EOL) EOL* ->;

expression
	:	ID | NUMBER;

mainClass
	:	(classType ':')? EOL* classContent?;

param	:	ID ('?'|'*'|':' type)?;

params	:	EOL* param (eoi param)* eoi?;

statement
	:	(classStatement|defStatement|varStatement);

type	:	ID ('.'! ID)* ('[' types ']')? ('?'|'*')?;

types	:	EOL* type (eoi type)* eoi?;

use	:	'use'^ ID ('.'! ID)* eol;

varStatement
	:	'var' ID (':' type)? ('=' expression)?;

visibility
	:	('internal'|'protected'|'private'|'public') ':';

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
