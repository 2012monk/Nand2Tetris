// TMP_0 TMP_1
// X0 = R13 X1 = R14, X2 = R15

// NOT
@SP
A=M-1
D=!M

// NEG
@SP
A=M-1
D=-M

// --------------------------------
// AND
@SP
AM=M-1
D=M
A=A-1
M=D&M

// OR
@SP
AM=M-1
D=M
A=A-1
M=D|M

// --------------------------------
// ADD
@SP
AM=M-1
D=M
A=A-1
M=D+M

// SUB
@SP
AM=M-1
D=M
A=A-1
M=D-M

// --------------------------------
// EQ
@SP
AM=M-1
D=M
A=A-1
D=D-M // x - y
M=0
@_EQ:FILE_NAME.LINE_NO
D;JEQ
@SP
A=M
M=-1
(_EQ:FILE_NAME.LINE_NO)

// LT
@SP
AM=M-1
D=M
A=A-1
D=D-M // x - y
M=0
@_LT:FILE_NAME.LINE_NO
D;JLT
@SP
A=M
M=-1
(_LT:FILE_NAME.LINE_NO)

// GT
@SP
AM=M-1
D=M
A=A-1
D=D-M // x - y
M=0
@_GT:FILE_NAME.LINE_NO
D;JGT
@SP
A=M
M=-1
(_GT:FILE_NAME.LINE_NO)

