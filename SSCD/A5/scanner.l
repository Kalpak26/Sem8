%{
#include <stdio.h>

FILE *yyin;   // file pointer for flex
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
    yyin = fopen("input.txt", "r");   // open file

    if (!yyin) {
        printf("Could not open file\n");
        return 1;
    }

    yylex();
    fclose(yyin);
    return 0;
}

/* alt: taking CLI input as a file

*int main(int argc, char *argv[]) {

*    if (argc > 1)
*        yyin = fopen(argv[1], "r");
*    else
*        yyin = stdin;

*    yylex();
*    return 0;
*}
*/
