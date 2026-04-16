%{
#include "calc.tab.h"
#include <stdlib.h>
%}

%%
[0-9]+      { yylval = atoi(yytext); return NUMBER; }

"+"         { return '+'; }
"-"         { return '-'; }
"*"         { return '*'; }
"/"         { return '/'; }

"("         { return '('; }
")"         { return ')'; }

[ \t]       ;        // ignore space and tab only
\n          { return '\n'; }   // pass newline to YACC

.           { return yytext[0]; }
%%

int yywrap() {
    return 1;
}