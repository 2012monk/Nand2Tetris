package compiler.parser;

public class Location {
    private int offset;
    private int line;
    private int column;

    public Location(int offset, int line, int column) {
        this.offset = offset;
        this.line = line;
        this.column = column;
    }

    public int getOffset() {
        return offset;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void updateOffset(int offset) {
        this.offset = offset;
    }

    public void updateLine(int line) {
        this.line = line;
    }

    public void updateCol(int col) {
        this.column = col;
    }

    public Location copy() {
        return new Location(offset, line, column);
    }
}
