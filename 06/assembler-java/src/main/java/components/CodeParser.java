package components;

import java.util.HashMap;
import java.util.Map;

public class CodeParser implements Code {

    private static final Map<String, Byte> compSymbols = new HashMap<>();
    private static final Map<String, Byte> destSymbols = new HashMap<>();
    private static final Map<String, Byte> jumpSymbols = new HashMap<>();
    static {
        String[] jumps = new String[] {
            null, "JGT", "JEQ", "JGE", "JLT", "JNE", "JLE", "JMP"
        };
        String[] dest = new String[] {
            null, "M", "D", "MD", "A", "AM", "AD", "AMD"
        };
        for (byte i = 0; i <= 7; i++) {
            jumpSymbols.put(jumps[i], i);
            destSymbols.put(dest[i], i);
        }
        setSymbol("0", 0b101010);
        setSymbol("1", 0b111111);
        setSymbol("-1", 0b111010);
        setSymbol("D", 0b001100);
        setSymbol("A", 0b110000);
        setSymbol("!D", compSymbols.get("D") | 1);
        setSymbol("!A", compSymbols.get("A") | 1);
        setSymbol("-D", compSymbols.get("D") | 0b11);
        setSymbol("-A", compSymbols.get("A") | 0b11);
        setSymbol("D+1", 0b011111);
        setSymbol("A+1", 0b110111);
        setSymbol("D-1", compSymbols.get("D") | 0b10);
        setSymbol("A-1", compSymbols.get("A") | 0b10);
        setSymbol("D+A", 0b000010);
        setSymbol("D-A", 0b010011);
        setSymbol("A-D", 0b000111);
        setSymbol("D&A", 0);
        setSymbol("D|A", 0b010101);
    }

    static void setSymbol(String a, int val) {
        compSymbols.put(a, (byte) val);
    }
    @Override
    public byte dest(String dest) {
        return destSymbols.get(dest);
    }

    @Override
    public byte comp(String comp) {
        byte a = 0;
        if (comp.contains("M")) {
            comp = comp.replace("M", "A");
            a = 0b1000000;
        }
        return (byte) (compSymbols.get(comp) | a);
    }

    @Override
    public byte jump(String jump) {
        if (jump == null) return 0;
        return jumpSymbols.get(jump);
    }
}
