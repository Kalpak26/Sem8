%{
#include <stdio.h>
/* yyin is the file pointer Lex uses to read input */
FILE *yyin;
%}

/* Definitions: These are like variables for Regular Expressions */
DIGIT      [0-9]
ID         [a-zA-Z_][a-zA-Z0-9_]*
HEADER     #include<[^>]+>

%%

{HEADER}            { printf("%s -> header file\n", yytext); }

"/*"([^*]|\*+[^*/])*\*+"/" { printf("Multi-line Comment\n"); }
"//".* { printf("Single-line Comment\n"); }

"int"|"float"|"char"|"void"|"return" { printf("%s -> keyword\n", yytext); }
"if"|"else"|"for"|"while"|"do"      { printf("%s -> control flow\n", yytext); }

"printf"|"scanf"    { printf("%s -> built-in function\n", yytext); }

{ID}"("             { printf("%s -> function call/definition\n", yytext); }
{ID}                { printf("%s -> identifier\n", yytext); }
{DIGIT}+            { printf("%s -> number\n", yytext); }

"=="|"!="|">="|"<="|">"|"<" { printf("%s -> relational operator\n", yytext); }
"+"|"-"|"*"|"/"|"%"         { printf("%s -> arithmetic operator\n", yytext); }
"="                         { printf("= -> assignment operator\n"); }

"["|"]"|"{"|"}"|"("|")"|";"|"," { printf("%s -> special symbol\n", yytext); }

\".*\"              { printf("%s -> string literal\n", yytext); }

[ \t\n]+            ; /* Ignore whitespace, tabs, and newlines */

.                   { printf("%s -> invalid token\n", yytext); }

%%

/* This function is called when Lex reaches the end of the file */
int yywrap() {
    return 1;
}

int main(int argc, char *argv[]) {
    /* If a filename is provided, open it; otherwise look for "program.c" */
    if (argc > 1)
        yyin = fopen(argv[1], "r");
    else
        yyin = fopen("input.txt", "r");

    if (!yyin) {
        printf("Error: Could not open input file.\n");
        return 1;
    }

    /* yylex() starts the lexical analysis */
    yylex();

    fclose(yyin);
    return 0;
}
