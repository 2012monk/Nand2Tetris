// function label argc
(f-label)
@argc
D=A
@X2
M=D
// if argc > 0
(fn$__init__loop)
@X2
M=M-1
push constant 0
@X2
D=M
@loop
D;JNE

// call f argc
//push ret-addr
@ret-addr
D=A
//push-inst

// push lcl
@LCL
D=M
// push-inst

// push arg
@ARG
D=M
// push-inst

// push this
@THIS
D=M
// push-inst

// push that
@THAT
D=M
// push-inst

// LCL=SP
@SP
D=M
@LCL
M=D
//ARG=SP-n-5
@(argc + 5)
D=D-A
@ARG
M=D

//goto f
@f-label
0;JMP
(ret-addr)

// return

// FRAME=LCL
@LCL
D=M
@FRAME
M=D
// RET=*(FRAME-5)
@5
D=D-A
@RET
M=D
// *ARG = pop()
// pop arg 0
@ARG
A=M
D=A
@X0
M=D
@SP
AM=M-1
D=M
@X0
A=M
M=D

// SP=ARG+1
@ARG
D=M
@SP
M=D+1

// THAT=*(FRAME-1)
@FRAME
AM=M-1
D=M
@THAT
M=D
// THIS=*(FRAME-1)
@FRAME
AM=M-1
D=M
@THIS
M=D
// ARG=*(FRAME-1)
@FRAME
AM=M-1
D=M
@ARG
M=D
// LCL=*(FRAME-1)
@FRAME
AM=M-1
D=M
@LCL
M=D

// goto RET
@RET
0;JMP