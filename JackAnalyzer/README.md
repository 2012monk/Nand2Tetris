## Jack Language Definition

EBNF 표기
```text
lexical types:
 
<digit> = (0-9)
<letters> = (a-z | A-Z)

terminal:

<identifier> ::= <letters> {(<letters> | <digit> | '_')}
<integerConstant> ::= {<digit>}; 0 <= int < 2^16
<stringConstant> ::= '"' {<unicode> - '"' - '\r' - '\n'} '"'
<keyword> ::= 'class' | 'constructor' | 'function' | 'method' | 
             'field' | 'static' | 'var' | 'int' |
             'char' | 'boolean' | 'void' | 'true' |
             'true' | 'false' | 'null' | 'this' |
             'let' | 'do' | 'if' | 'else' |
             'while' | 'return'
<symbol> ::= '{' | '}' | '(' | ')' | '[' | ']' | '.' |
             ',' | ';' | '+' | '-' | '*' | '/' | '&' |
             '|' | '<' | '>' | '=' | '~'

identifier:
<className> ::= <identifier>
<subroutineName> ::= (<functionName> | <methodName> | <constructorName>)
<varName> ::= <identifier>

nonterminal:

<varName> ::= ( <argumentName> | <className> | <fieldVarName> | <staticVarName> )
program structure:
declare:
<class> ::= class <className> '{' {<classVarDec>} { <subroutineDec> } '}'
<classVarDec> ::= ( 'static' | 'field' ) <varDec>
<localVarDec> ::= 'var' <varDec>
<subRoutineDec> = <subroutinePrefix> <returnType> <subroutineName> ([<parameterList>])
<subroutinePrefix> = ( 'constructor' | 'function' | 'method' )
<varDec> ::= <type> <varName> {, <varName>} ';'
<returnType> = (void | <type>)

reference:
<type> = 'int' | 'char' | 'boolean' | <className>
<parameterList> ::= [<parameter> { ',' <parameter>}]
<parameter> ::= <type> <varName>
<subRoutineBody> ::= '{' {<localVarDec>} <statements> '}'

statement:
<statements> ::= {<statement>}
<statement> ::= <letStatement> | <ifStatement> | <whileStatement> |
                <doStatement> | <returnStatement>
<letStatement> ::= 'let' <varName> [<arrayAccessSuffix>] '=' <expression> ';'
<ifStatement> ::= 'if' '(' <expression> ')' '{' <statements> '}' {'else' '{' <statements '}'}
<doStatement> ::= 'do' <subRoutineCall> ';'
<returnStatement> ::= 'return' [<expression>]';'
<whileStatement> ::= 'while' '(' <expression> ')' '{' <statements> '}'

expression:
<expression> ::= <term> {<op> <term>}
<term> ::= <integerConstant> | <stringConstant> | <keywordConstant> |
           <varName> ['['<expression>']'] | <subroutineCall> |
           '('<expression>')' | <unaryOp> <term> |
           <identifier> [ ('.' <functionCall> | ['['<expression>']']) ]
           ('intConst' | 'strConst' | <keywordConst>)

<expression> ::= <unaryExpr> | <ternaryExpr>
<ternaryExpr> ::= <unaryExpr> { <binaryExpr> }
<unaryExpr> ::= (<unaryOp> <term> | <term> )
<binaryExpr> ::= <op> <expression>
<arrayAccessExpr> ::= <varName> <arrayAccessSuffix>

<arrayAccessSuffix> ::= '[' expression> ']'

<subroutineCall> ::= <subroutineName> '(' <expressionList> ')' |
                     <className>'.'<subroutineName>'('<expressionList>')'
<expressionList> ::= [<expression> {, <expression>}]

<subroutineCall> ::= [<className> '.'] <functionsCall> ;
<subroutineCall> ::= (<methodCall> | <functionCall> | <constructorCall>)

<methodCall> ::= <methodName> <args>
<constructorCall> ::= <className> <args>
<functionCall> ::= <subroutineName> <args>

<args> = '('<expressionList> ')' ;

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

## VMWriter

분석 된 구문들을 vm 으로 번역해 파일로 쓴다.

### name convention

- file: Xxx.jack -> Xxx.vm
- subroutine: <subroutineName> -> Xxx.name

### class

default constructor new() 삽입

Memory.alloc(size)

default destructor dispose() 삽입

Memory.deAlloc(instance)

### subroutine

> method name type (k args)

인수가 k + 1 인 function 으로 컴파일 arg[0] = ptr[0]
> function name type (k args):

인수가 k 인 function 으로 컴파일


> constructor name type=name (k args)

인수가 k 인 function 으로 컴파일

할당 되어야 할 메모리 사이즈를 계산 size

새로운 메모리를 할당하고 Memory.Alloc(size) 이용

this 포인터를 할당 된 주소로 설정 ptr[0] = alloc(size)

> call subroutine

호출 하기 전에 인수들을 스택에 푸시 한다

push val : for k times

> call method

foo.bar(v1, v2) 라는 메서드 호출은 foo 객체를 인수로 전달한다

push foo

push v1

push v2

call bar

> method 동작

인수로 넘어온 객체 포인터를 재 설정한다

ptr[0] = arg[0]

> subroutine call

호출 되는 subroutine 이 어떤 클래스의 객체 foo 에 속하는지 찾아야 한다

var f = Foo.new();

do f.bar()

push f

call Foo.bar()

this -> class pointer

### variables

> static

static 0..15
> field

this 1...
> parameter

arg 0...
> var

local 0...

> array


array[i]: that 포인터에 array 를 할당 후 접근

var = array[i] =>

ptr[1] = *array

val = that[i]
   

> return type void
 
리턴 타입이 void 인 method 나 function 은 상수 0을 반환한다

void 함수를 호출 할때는 반환값을 pop 으로 꺼내고 무시한다


call void_f

pop tmp 0

ignore
 
 
> constants

null, false = 0

true = -1


### 표현식 평가

후위 표기법 (역 폴란드 표기법) 으로 변환 한다

파스 트리를 postorder 로 순회한다


### 제어문

재귀적 컴파일 방법으로 중첩 제어문을 해결한다


