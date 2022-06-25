
## Jack Language Definition

```text
lexical typs:
 
<digit> = 0-9
<letters> = a-z | A-Z

terminal:

<keyword> ::= 'class' | 'constructor' | 'function' | 'method' | 
             'field' | 'static' | 'var' | 'int' |
             'char' | 'boolean' | 'void' | 'true' |
             'true' | 'false' | 'null' | 'this' |
             'let' | 'do' | 'if' | 'else' |
             'while' | 'return'
<symbol> ::= '{' | '}' | '(' | ')' | '[' | ']' | '.' |
             ',' | ';' | '+' | '-' | '*' | '/' | '&' |
             '|' | '<' | '>' | '=' | '~'
<integerConstant> ::= 0..32767 base 10 number
<identifier> ::= <letters> {(<letters> | <digit> | '_')}
<stringConstant> ::= '"' <unicode> - '"' - '\r' - '\n' '"'

nonterminal:

program structure:
<class> ::= class <className> '{' [<classVarDec>] [<subroutineDec>] '}'

<classVarDec> ::= ( 'static' | 'field' ) <type> <varName> {,<varName>} ;
<type> = 'int' | 'char' | 'boolean' | <className>
<subRoutineDec> = ( 'constructor' | 'function' | 'method' ) (void | <type>) <subroutineName> ([<parameterList>])
<parameterList> ::= [<type> <varName> {, <type> <varName>}]
<subRoutineBody> ::= '{' {<varDec>} <statements> '}'
<varDec> ::= 'var' <type> <varName> {, <varName>} ';'
<className> ::= <identifier>
<subroutineName> ::= <identifier>
<varName> ::= <identifier>

statement:
<statements> ::= {<statement>}
<statement> ::= <letStatement> | <ifStatement> | <whileStatement> |
                <doStatement> | <returnStatement>
<letStatement> ::= 'let' <varNAme> ['[' <expression> ']'] '=' <expression> ';'
<ifStatement> ::= 'if' '(' <expression> ')' '{' <statements> '}' {'else' '{' <statements '}'}
<doStatement> ::= 'do' <subRoutineCall> ';'
<returnStatement> ::= 'return' [<expression>]';'

expression:
<expression> ::= <term> {<op> <term>}
<term> ::= <integerConstant> | <stringConstant> | <keywordConstant> |
           <varName> ['['<expression>']'] | <subroutineCall> |
           '('<expression>')' | <unaryOp> <term> |
           <identifier> ( <subroutineCall> | ['['<expression>']'] }
           ('intConst' | 'strConst' | <keywordConst>)
           
<subroutineCall> ::= <subroutineName> '(' <expressionList> ')' |
                     <className>'.'<subroutineName>'('<expressionList>')'
<expressionList> ::= [<expression> {, <expression>}]

<op> ::= '+' | '-' | '*' | '/' | '&' | '|' | '<' | '>' | '='
<unaryOp> ::= '-' | '~'
<keywordConstant> ::= 'true' | 'false' | 'null' | 'this'
```

### 구문 분석기

```shell
./JackAnalyzer src
```

xxx.jack 형식의 소스 파일또는 하나 이상의 .jack 파일들을 담고 있는 디렉터리를 인수로 받는다

원본 파일이 위치한 디렉터리에 XXX.xml 파일을 생성한다.


출력

- terminal: <type-name> terminal </type-name> 생성한다
- non-terminal:
   <type-name> type 요소의 본뭉에 대한 재귀 코드 </type-name> 생성한다.

xml 출력시 특수기호 변환

- '<' : &lt;
- '>' : &gt;
- '"' : &quot;
- '&' : &amp;
