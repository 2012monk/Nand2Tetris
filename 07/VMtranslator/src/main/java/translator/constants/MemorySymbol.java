package translator.constants;

import java.util.HashMap;
import java.util.Map;

public enum MemorySymbol {
    ARG("argument", "@ARG"),
    LCL("local", "@LCL"),
    THIS("this", "@THIS"),
    THAT("that", "@THAT"),
    PTR("pointer", "@R3"),
    TMP("temp", "@R5"),
    X0(null, "@R13"),
    X1(null, "@R14"),
    X2(null, "@R15"),
    SP(null, "@SP"),
    STATIC("static", null),
    CONSTANT("constant", null)
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

    MemorySymbol(String vm, String reg) {
        this.vm = vm;
        this.reg = reg;
    }
    public String reg() {
        return reg;
    }

    public static MemorySymbol symbol(String vm) {
        return symbolMap.get(vm);
    }
}
