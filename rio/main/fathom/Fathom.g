grammar Fathom;

options {
	output = AST;
	ASTLabelType = CommonTree;
}

tokens {
	CALL;
	LINE;
}

@header {
	package fathom;
}

@lexer::header {
	package fathom;
}

script: statements;

arguments: '('^ statements ')'!;

b: EOL* ->;

block: '{'^ statements '}'!;

blockish: block | lambdaBlock;

call: ((callPart nb)+ ID? | ID) -> ^(CALL callPart* ID?);

callPart: ID nb (arguments nb blockish? | blockish);

def: 'def'^ ((defPart nb)+ defName? | defName) nb ('=' b expression)?;

defName: ID quantity?;

defPart: defName parameters;

eoi: ',' b ->;

eol: (';'|EOL) b ->;

expression: block | call | def | lambda | list | number | string;

lambda: 'do'^ parameters? expression;

lambdaBlock: 'do'^ parameters? block;

line: expression (eoi expression)* -> ^(LINE expression+);

list: '['^ statements ']'!;

nb: ('...' EOL+)? ->;

statements: EOL!* (line (eol line)* eol?)?;

string: POWER_STRING | RAW_STRING;

number: NUMBER;

parameter: ID quantity?;

parameters: '('^ (EOL!* (parameter (eoi parameter)* eoi?)?) ')'!;

quantity: '?' | '*';

//STRETCH: '...' EOL* {skip();};

COMMENT: '#' (~('\r'|'\n'))* {skip();};

ID: (LETTER|'$') (LETTER|DIGIT|'_')*;

EOL: '\r' | ('\r'? '\n');

NUMBER: DIGIT+ ('.' DIGIT+)?;

POWER_STRING: '"' (('\\'~('\r'|'\n'))|~('"'|'\r'|'\n'))* '"'?;

RAW_STRING: '\'' (~('\''|'\r'|'\n'))* '\''?;

WS: (' '|'\t')+ {skip();};

fragment
DIGIT: '0'..'9';

fragment
LETTER: 'a'..'z'|'A'..'Z';
