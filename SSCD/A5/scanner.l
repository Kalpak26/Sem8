%{
#include <stdio.h>
%}

%%
int|float|char      { printf("%s -> this is datatype\n", yytext); }

[a-z]+              { printf("%s -> this is variable\n", yytext); }

[ \t\n]+            ;   // ignore spaces

.                   { printf("%s -> unknown token\n", yytext); }
%%

int yywrap() {
    return 1;
}

int main() {
    printf("Enter input:\n");
    yylex();
    return 0;
}
