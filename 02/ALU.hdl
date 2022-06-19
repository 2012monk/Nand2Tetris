// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/02/ALU.hdl

/**
 * The ALU (Arithmetic Logic Unit).
 * Computes one of the following functions:
 * x+y, x-y, y-x, 0, 1, -1, x, y, -x, -y, !x, !y,
 * x+1, y+1, x-1, y-1, x&y, x|y on two 16-bit inputs, 
 * according to 6 input bits denoted zx,nx,zy,ny,f,no.
 * In addition, the ALU computes two 1-bit outputs:
 * if the ALU output == 0, zr is set to 1; otherwise zr is set to 0;
 * if the ALU output < 0, ng is set to 1; otherwise ng is set to 0.
 */

// Implementation: the ALU logic manipulates the x and y inputs
// and operates on the resulting values, as follows:
// if (zx == 1) set x = 0        // 16-bit constant
// if (nx == 1) set x = !x       // bitwise not
// if (zy == 1) set y = 0        // 16-bit constant
// if (ny == 1) set y = !y       // bitwise not
// if (f == 1)  set out = x + y  // integer 2's complement addition
// if (f == 0)  set out = x & y  // bitwise and
// if (no == 1) set out = !out   // bitwise not
// if (out == 0) set zr = 1
// if (out < 0) set ng = 1

CHIP ALU {
    IN  
        x[16], y[16],  // 16-bit inputs        
        zx, // zero the x input?
        nx, // negate the x input?
        zy, // zero the y input?
        ny, // negate the y input?
        f,  // compute out = x + y (if 1) or x & y (if 0)
        no; // negate the out output?

    OUT 
        out[16], // 16-bit output
        zr, // 1 if (out == 0), 0 otherwise
        ng; // 1 if (out < 0),  0 otherwise

    PARTS:
    Mux16(a=true, b=false,sel=zx, out=zx16);
    And16(a=x, b=zx16, out=ozx);
    
    Mux16(a=false, b=true, sel=nx, out=nx16);
    Xor16(a=ozx, b=nx16, out=onx);
    
    Mux16(a=true, b=false, sel=zy, out=zy16);
    And16(a=y, b=zy16, out=ozy);
    
    Mux16(a=false, b=true, sel=ny, out=ny16);
    Xor16(a=ozy, b=ny16, out=ony);

    Add16(a=onx, b=ony, out=oa);
    And16(a=onx, b=ony, out=oand);
    Mux16(a=oand, b=oa, sel=f, out=of);

    Mux16(a=false, b=true, sel=no, out=no16);
    Xor16(a=of, b=no16, out=out);
    Xor16(a=of, b=no16, out=o);

    Or16Way(in=o, out=ozr);
    Not(in=ozr, out=zr);
    And16(a=o, b=true, out[0..14]=dump, out[15]=ng);
    

}