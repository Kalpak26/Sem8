#include "iostream"
#include "string"
using namespace std;

//after removing left recursion
// E  → T E'
// E' → + T E' | e
// T  → F T'
// T' → * F T' | e
// F  → (E) | id

string input;
int i = 0;

void E();
void E_dash();
void F();
void T();
void T_dash();

void match(char expected){
    if(input[i] == expected){
        i++;
    }
    else{
        cout << "error";
    }
}

void E(){
    T();
    E_dash();
}

void E_dash(){
    if(input[i] == '+'){
        match('+');
        T();
        E_dash();
    }
}

void F(){
    if(input[i] == '('){
        match('(');
        E();
        match(')');
    }
    else if(input[i] == 'i' && input[i+1] == 'd'){
        i += 2;
    }
}
void T(){
    F();
    T_dash();
}

void T_dash(){
    if(input[i] == '*'){
        match('*');
        F();
        T_dash();
    }
}

int main(){
    cout << "Enter input: \n";
    cin >> input;

    E();

    if(input[i] == '\0'){
        cout << "Success\n";
    } else {
        cout << "Failure\n";
    }

    return 0;

}