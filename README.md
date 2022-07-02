# Nand to Tetris Project code

Nand to Tetris Study 실습 코드들 저장소 입니다.


## TODO

- Refactor Compiler
- Refactor Tokenizer
- VM Translator 최적화

## project

> Chapter 10 ~ 11
> JackAnalyzer


Jack 언어 컴파일러 프로젝트

구문 분석과 VM 코드로 컴파일을 하는 모듈.


> Chapter 7 ~ 8
> VM Translator


Jack 언어의 중간코드인 VM 코드에서 Assmbly 코드로 번역하는 모듈.

> Chapter 6
> Assembler


Assembly 코드를 Hack CPU Instruction 바이너리로 변환 하는 모듈.

실제 바이너리가 아닌 Ascii '0' '1' 기호로 변환된다.


> Chapter 5
> Computer Architecture

Computer 칩, CPU, RAM, Screen, Keyboard

하드웨어 구현

> Chapter 3
> 순차 논리 회로 구현

Bit Register, n-Register memory, Program Counter

> Chapter 2
> Bool 연산 하드웨어

ALU, Adder, Full Adder, Half Adder 구현

> Chapter 1
> Bool 논리 게이트 구현

주어진 Nand 게이트를 이용해서 모든 논리 회로를 구현한다.

hdl 언어를 사용해 시뮬레이션 한다.

- n-bit And
- n-bit Not
- n-bit Or
- n-bit Xor

- n-bit Multiplexor
- n-bit DeMultiplexor



