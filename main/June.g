grammar June;

options {
	output=AST;
}

script	:	package_? imports? scriptType? content? S*;

content	:	S* ('def' END)+;

import_	:	S* 'import' S* ID ('.' ID)* END;

imports	:	import_+;

package_:	S* 'package' S* ID ('.' ID)* END;

scriptType
	:	S* 'class' S* ID ':' END;

/*stat:   expr NEWLINE        -> expr
    |   ID '=' expr NEWLINE -> ^('=' ID expr)
    |   NEWLINE             ->
    ;

expr:   multExpr (('+'^|'-'^) multExpr)*
    ; 

multExpr
    :   atom ('*'^ atom)*
    ; 

atom:   INT 
    |   ID
    |   '('! expr ')'!
    ;*/

END	:	';' S* | NLS S*;

ID	:	('a'..'z'|'A'..'Z')+ ;

INT	:	'0'..'9'+;

NLS	:	'\r' '\n'? | '\n';

S	:	' ' | '\t' | NLS;
