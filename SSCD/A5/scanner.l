%{
#include <stdio.h>
%}

%%
(int|float|char)[ ]+[a-z]+   {
                                char dtype[20], var[20];
                                sscanf(yytext, "%s %s", dtype, var);

                                printf("%s -> this is datatype\n", dtype);
                                printf("%s -> this is variable\n", var);
                            }

[ \t\n]+                    ;   // ignore whitespace

.                           { printf("Invalid input\n"); }
%%

int yywrap() {
    return 1;
}

int main() {
    printf("Enter input:\n");
    yylex();
    return 0;
}
