package components;

public interface SymbolTable {
    void addEntry(String symbol, int address);
    boolean contains(String symbol);
    int getAddress(String symbol);
}
