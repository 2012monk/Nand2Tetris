package translator.constants;

import java.util.HashMap;
import java.util.Map;

public enum MemorySymbol {
    LCL("local", "@LCL", 15),
    ARG("argument", "@ARG", 15),
    THIS("this", "@THIS",15),
    THAT("that", "@THAT", 15),
    PTR("pointer", "@R3", 15),
    TMP("temp", "@R5", 12),
    X0(null, "@R13", 15),
    X1(null, "@R14", 15),
    X2(null, "@R15", 15),
    SP(null, "@SP", 0),
    STATIC("static", null, 255),
    CONSTANT("constant", null, (1<<16) - 1)
    ;
    private static final Map<String, MemorySymbol> symbolMap = new HashMap<>();
    static {
        for (MemorySymbol s : MemorySymbol.values()) {
            if (s==null) continue;
            symbolMap.put(s.vm, s);
        }
    }
    private final String vm;
    private final String reg;
    private final int limit;

    MemorySymbol(String vm, String reg, int liimit) {
        this.vm = vm;
        this.reg = reg;
        this.limit = liimit;
    }
    public String reg() {
        return reg;
    }

    public static MemorySymbol symbol(String vm) {
        return symbolMap.get(vm);
    }

    public int limit() {
        return limit;
    }

    public boolean withinBound(int index) {
        return index <= limit;
    }
}
