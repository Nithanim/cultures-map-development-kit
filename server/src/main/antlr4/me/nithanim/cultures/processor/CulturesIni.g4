grammar CulturesIni;

fullfile: line+ EOF;
line: (metaline|categoryline|commandline)? LINEEND;

metaline: METACHAR (includeline | defineline);
includeline: 'include' includepath;
includepath: STRING;
defineline: 'define' definename (definenumber|definestring);
definename: CHARS;
definenumber: DIGITS;
definestring: STRING;

categoryline: '[' categoryname ']';
categoryname: CHARS; //CATEGORYCHARS

commandline: commandname commandarg*;

commandname: CHARS;
commandarg: (stringarg | numberarg | constantarg);
stringarg: STRING;
constantarg: '#' constantname;
constantname: CHARS;
numberarg: DIGITS;

//comment : '//' ~('\r' | '\n')*;
LINE_COMMENT: '//' ~[\r\n]* -> skip;


METACHAR: '#';

DIGITS : '-'?('0'..'9' | '1'..'9' '0'..'9'*);
//STRING: '"' (~('\n' | '\r' | '"'))+ '"';
STRING: '"' (~('"'))* '"';

CHARS: ('a'..'z' | 'A'..'Z' | '_')('a'..'z' | 'A'..'Z' | '_' | '0'..'9')*;



WHITESPACES: WHITESPACE+ -> skip;
fragment WHITESPACE : (' ' | '\t');
LINEEND : NEWLINE+ | EOF;
fragment NEWLINE: ('\r'? '\n' | '\r')+;
