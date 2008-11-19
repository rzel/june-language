grammar Fathom;

options {
	output = AST;
	ASTLabelType = CommonTree;
}

tokens {
	CALL;
	LINE;
}

program: statements;

arguments: '('^ statements ')'!;

b: EOL* ->;

block: '{'^ statements '}'!;

blockish: block | lambdaBlock;

call: ((callPart nb)+ ID? | ID) -> ^(CALL callPart* ID?);

callPart: ID nb (arguments nb blockish? | blockish);

def: 'def'^ ((defPart nb)+ defName? | defName) nb ('=' b expression)?;

defName: ID '*'?;

defPart: defName parameters;

eoi: ',' b ->;

eol: (';'|EOL) b ->;

expression: block | call | def | lambda | list | number | string;

lambda: '@'^ parameters? expression;

lambdaBlock: '@'^ parameters? block;

line: expression (eoi expression)* -> ^(LINE expression+);

list: '['^ statements ']'!;

nb: ('...' EOL+)? ->;

statements: EOL!* (line (eol line)* eol?)?;

string: POWER_STRING | RAW_STRING;

number: NUMBER;

parameter: ID '*'?;

parameters: '('^ (EOL!* (parameter (eoi parameter)* eoi?)?) ')'!;

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
