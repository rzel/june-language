grammar June;

options {
	output = AST;
}

tokens {
	ARGS;
	CALL;
	SCRIPT;
	STRINGS;
	TYPE;
}

@header {
	package tj;
}

@lexer::header {
	package tj;
}

script: EOL* content? -> ^(SCRIPT content?);

arg: (ID ':'^)? expression;

argGroup: '('^ EOL!* args? ')'! block?;

argGroups: argGroup argTrail?;

args: arg (eoi arg)* eoi? -> ^(ARGS arg+);

argTrail: (ID (argGroup | block))+;

assignment: '='^ expression;

baseExpression: ID | NUMBER | strings;

baseType: ID '?'? typeArgs? -> ^(TYPE ID '?'? typeArgs?);

block: '{'^ '^'? EOL!* content? '}'!;

blocks: block argTrail?;

callExpression:
	baseExpression (('.'^ ID blocks?) | ('.&'^ ID) | ('('^ EOL!* args?')'! blocks?))* |
	ID blocks -> ^(CALL ID blocks);

content: statement (eol statement)* eol? -> statement+;

defStatement: defType^ ID typeParams? paramGroups? itemType? (assignment | block)?;

defType: 'class' | 'def' | 'package' | 'role' | 'trait' | 'val' | 'var';

eoi: (','|EOL) EOL* ->;

eol: (';'|EOL) EOL* ->;

expression: callExpression;

itemType: ':'^ type;

param: ID itemType?;

paramGroups: params (ID params?)*;

params: '('^ param* ')'!;

statement: (defStatement | expression) ;

string: LINE_STRING | POWER_STRING | RAW_STRING;

strings: string (EOL* string)* -> ^(STRINGS string+);

type: baseType (('|'^ | '&'^) baseType)*;

typeArgs: '['^ EOL!* ID (eoi ID)* eoi?']'!;

typeParam: ID (':' type)?;

typeParams: '['^ EOL!* typeParam (eoi typeParam)* eoi?']'!;

COMMENT: '#' (~('\r'|'\n'))* {skip();};

EOL: '\r'|('\r'? '\n');

ID: (LETTER|'$') (LETTER|DIGIT|'_')*;

LINE_STRING: '`' (~('\r'|'\n'))*;

NUMBER: DIGIT+;// ('.' DIGIT+)?;

// TODO So I do just have to parse this afterwards?
POWER_STRING: '"' (('\\'~('\r'|'\n'))|~('"'|'\r'|'\n'))* '"'?;

RAW_STRING: '\'' (~('\''|'\r'|'\n'))* '\''?;

STRETCH: '...' EOL* {skip();};

WS: (' '|'\t')+ {skip();};

fragment
DIGIT: '0'..'9';

fragment
LETTER: 'a'..'z'|'A'..'Z';
