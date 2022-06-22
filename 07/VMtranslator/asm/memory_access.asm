// PUSH constant

@CONST
D=A

@SP
A=M
M=D
@SP
M=M+1

// PUSH SP,ARG,LCL,THIS,THAT index

@CONST
D=A
@SEG
A=M+D

D=M

@SP
A=M
M=D
@SP
M=M+1


// POP SP,ARG,LCL,THIS,THAT index

@CONST
D=A
@SEG
A=M+D

D=A

@X0
M=D

@SP
AM=M-1
D=M
@X0
A=M
M=D


// PUSH temp, X0..X2 static index

@CONST
D=A
@SEG
A=A+D

D=M
@SP
A=M
M=D
@SP
M=M+1

// POP temp, X0..X2 static index

@CONST
D=A
@SEG
A=A+D

@X0
M=A

@SP
AM=M-1
D=M
@X0
A=M
M=D


// POP static index

@XXX.index
D=A
@X0
M=D