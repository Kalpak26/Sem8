%{
#include <stdio.h>
#include <stdlib.h>

/* Function prototypes to avoid compiler warnings */
int yylex();
void yyerror(char *s);
%}

%token IF THEN ID ROP AOP NUM   

%%

/* The Start Symbol (Augmented Grammar Part) */
SD: S { 
    printf("\nInput is valid according to the grammar.\n"); 
    exit(0); 
};

/* S: IF CMP THEN STMT */
S: IF CMP THEN STMT ;

/* CMP: ID ROP ID */
CMP: ID ROP ID 
   | ID ROP NUM ;

/* logic to assign/operate an ID with a NUM */
STMT: ID AOP ID { printf("-> Rule triggered: ID AOP ID\n"); }
    | ID AOP NUM { printf("-> Rule triggered: ID AOP NUM\n"); } ;

%%

int main() {
    printf("Enter a statement (e.g., IF a < b THEN x = y) (e.g., IF a < 5 THEN a = 10):\n");
    yyparse();
    return 0;
}

void yyerror(char *s) {
    fprintf(stderr, "Grammar Error: %s\n", s);
}