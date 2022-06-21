package components;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    private Map<String, Integer> map = new HashMap<>();
    public SymbolTable() {
        map.put("SP", 0);
        map.put("LCL", 1);
        map.put("ARG", 2);
        map.put("THIS", 3);
        map.put("THAT", 4);
        map.put("SCREEN", 0x4000);
        map.put("KBD", 0x6000);
        for (int i = 0; i <= 15; i++) {
            map.put("R" +i, i);
        }
    }

    public void addEntry(String symbol, int address) {
        map.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return map.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return map.get(symbol);
    }
}
