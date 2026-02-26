%{
#include <stdio.h>
#include <string.h>

struct SymbolTable {
    char name[50];
    char type[50];
} table[100];

int count = 0;

void add_to_table(char *name, char *type) {
    for(int i = 0; i < count; i++) {
        if(strcmp(table[i].name, name) == 0) return;
    }
    strcpy(table[count].name, name);
    strcpy(table[count].type, type);
    count++;
}

void print_table() {
    printf("\n%-20s | %-20s\n", "Symbol Name", "Type");
    printf("------------------------------------------\n");
    for(int i = 0; i < count; i++) {
        printf("%-20s | %-20s\n", table[i].name, table[i].type);
    }
}
%}

ID     [a-zA-Z_][a-zA-Z0-9_]*
DIGIT  [0-9]

%%

"int"|"float"|"char"|"void"|"return" { printf("%s -> keyword\n", yytext); }

{ID}"["{DIGIT}*"]"  { 
    printf("%s -> array\n", yytext); 
    add_to_table(yytext, "Array Variable"); 
}

{ID}"(" { 
    /* Cleaning: Remove the '(' from the end for a cleaner table */
    yytext[strlen(yytext)-1] = '\0'; 
    printf("%s() -> function\n", yytext); 
    add_to_table(yytext, "Function Name"); 
}

{ID} { 
    printf("%s -> identifier\n", yytext); 
    add_to_table(yytext, "Identifier"); 
}

[ \t\n]+            ; /* ignore whitespace */
.                   ; /* ignore other symbols for clarity */

%%

int yywrap() { return 1; }

int main(int argc, char *argv[]) {
    /* CLI Logic: Check if a filename was provided */
    if (argc < 2) {
        printf("Usage: %s <filename.c>\n", argv[0]);
        return 1;
    }

    /* Open the file provided in the command line (argv[1]) */
    FILE *file = fopen(argv[1], "r");
    if (!file) {
        printf("Error: Could not open file '%s'\n", argv[1]);
        return 1;
    }

    yyin = file;  /* Tell Lex to read from this file */
    yylex();
    
    print_table();

    fclose(file);
    return 0;
}
