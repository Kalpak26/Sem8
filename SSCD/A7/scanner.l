%{
#include "y.tab.h" 
%}

%%

"IF"        { return IF; }
"THEN"      { return THEN; }

"<"|">"|"<="|">="|"=="|"!="  { return ROP; }

"="|"+"|"-"|"*"|"/"          { return AOP; }

[0-9]+                  { return NUM; }

[a-zA-Z][a-zA-Z0-9]* { return ID; }

[ \t\n]     ; 

.           { return yytext[0]; }

%%

int yywrap() {
    return 1;
}