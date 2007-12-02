tree grammar Analyzer;

options {
	tokenVocab = June;
}

@header {
	package june;
}

script: ^(SCRIPT importStatement* mainClass);

args: ^(ARGS expression*);

block: content?;

call: ^(CALL ID args? block?);

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID classContent?);

collection:	^(LIST expression*) | ^(MAP pair*);

content: ^(BLOCK statement+);

defStatement: ^('def' ID params? type? block?) {System.out.println($ID);};

expression:
	^('&&' expression expression) |
	^('||' expression expression) |
	^('==' expression expression) |
	^('!=' expression expression) |
	^('<' expression expression) |
	^('<=' expression expression) |
	^('>' expression expression) |
	^('>=' expression expression) |
	^('+' expression expression) |
	^('-' expression expression) |
	^('*' expression expression) |
	^('/' expression expression) |
	^(('.'|'?.') expression call) |
	call |
	collection |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? classContent?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: expression|classStatement|defStatement|varStatement;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(PARAMS type+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^('var' varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
