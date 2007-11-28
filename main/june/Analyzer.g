tree grammar Analyzer;

options {
	tokenVocab = June;
}

@header {
	package june;
}

script: ^(SCRIPT importStatement* mainClass);

block: content?;

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID classContent?);

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
	^('-' expression expression);

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? classContent?);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: expression|classStatement|defStatement|varStatement;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(PARAMS type+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^('var' varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
