grammar June;

options {
	output = AST;
}

script	:	use* mainClass;

block	:	'{' content? '}';

content	:	statement (';' statement)* ';'?;

defStatement
	:	'def' ID ('(' params? ')')? (':' type)? block?;

mainClass
	:	('class' ':')? content?;

param	:	ID ('?'|'*'|':' type)?;

params	:	param (',' param)* ','?;

statement
	:	defStatement;

type	:	ID ('.'! ID)* ('[' types ']')? ('?'|'*')?; // Need parameters.

types	:	type (',' type)* ','?;

use	:	'use'^ ID ('.'! ID)*;

ID	:	(LETTER|'$') (LETTER|DIGIT|'_')*;

WS	:	(' '|'\t'|'\r'|'\n')+ {skip();};

fragment
DIGIT	:	'0'..'9';

fragment
LETTER	:	'a'..'z'|'A'..'Z';
