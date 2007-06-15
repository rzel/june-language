grammar June;

options {
	output = AST;
}

script	:	use* mainClass;

content	:	statement+;

mainClass
	:	('class' ':')? content;

statement
	:	'def';

use	:	'use'^ ID ('.'! ID)*;

ID	:	(LETTER|'$') (LETTER|DIGIT|'_')*;

WS	:	(' '|'\t'|'\r'|'\n')+ {skip();};

fragment
DIGIT	:	'0'..'9';

fragment
LETTER	:	'a'..'z'|'A'..'Z';
