package compiler.constants;


import java.util.HashMap;
import java.util.Map;

public enum DataType {
    TYPE_INT(Keyword.INT),
    TYPE_BOOLEAN(Keyword.BOOLEAN),
    TYPE_CHAR(Keyword.CHAR),
    TYPE_CLASS(Keyword.CLASS),
    TYPE_VOID(Keyword.VOID),
    REF_TYPE(Keyword.FUNCTION),
    TYPE_SUBROUTINE(Keyword.FUNCTION) ;

    private static final Map<Keyword, DataType> map = new HashMap<>();

    static {
        for (DataType t : values()) {
            map.put(t.key, t);
        }
    }

    private Keyword key;

    DataType(Keyword key) {
        this.key = key;
    }

    public static DataType get(Keyword key) {
        return map.get(key);
    }

    public static boolean contains(Keyword type) {
        return map.containsKey(type);
    }
}
