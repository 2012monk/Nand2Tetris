// bootstrap init


initiate stack address

// label

(FILE_NAME.FUNC$label)

// if-goto label

if-goto label
label if-label

// pop
@SP
AM=M-1
D=M
@FILE_NAME.FUNC$label
D;JEQ

// goto label

@FILE_NAME.FUNC$label
0;JMP


