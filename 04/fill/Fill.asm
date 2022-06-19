// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.


@R0
M=0
(LOOP)
    // Set initail values
    @8191
    D=A
    @SCREEN
    D=A+D
    @R1
    M=D

    @KBD
    D=M
    @ON
    D;JGT
    @OFF
    0;JMP

(ON)
    @R0
    M=-1
    @FILL
    0;JMP
(OFF)
    @R0
    M=0
    @FILL
    0;JMP

(FILL)
    @R1
    D=M
    @SCREEN
    D=D-M
    @LOOP
    D;JLT

    @R0
    D=M
    @R1
    A=M
    M=D

    @R1
    M=M-1
    @FILL
    0;JMP

