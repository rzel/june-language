tree grammar Analyzer;

options {
	ASTLabelType = JuneTree;
	tokenVocab = June;
}

@header {
	package june;
}

script: ^(SCRIPT importStatement* mainClass);

args: expression*;

block: content?;

blockExpression: ^(('do'|DEF_EXPR) params? block);

call: ^(CALL ID callArgs? blockExpression?);

callArgs: ^(ARGS args);

classContent: ^(BLOCK (statement|visibility)+);

classStatement: ^(TYPE_DEF typeKind ID classContent?);

collection:	^(LIST expression*) | ^(MAP pair*);

content: ^(BLOCK statement+);

defStatement: ^('def' ID params? type? block?);

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
	string {$expression.start.type = String.class;} |
	NUMBER;

importStatement: ^('import' ID+);

mainClass: ^(TYPE_DEF typeKind? classContent?);

pair: ^(PAIR ID expression);

param: ^(PARAM varDef);

params: ^(PARAMS param+);

statement: expression|classStatement|defStatement|varStatement;

string: POWER_STRING | RAW_STRING;

type: ^(TYPE_REF ID+ types? ('?'|'*')?);

types: ^(TYPES type+);

typeKind: 'annotation'|'aspect'|'class'|'interface'|'role'|'struct';

varDef:	ID ('?'|'*'|type)?;

varStatement: ^('var' varDef expression?);

visibility: 'internal'|'protected'|'private'|'public';
