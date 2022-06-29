package compiler.componenets;

import compiler.constants.DataType;
import compiler.constants.IdentifierType;
import compiler.constants.MemorySegment;

public class VariableIdentifier extends Identifier {

    private MemorySegment segment;
    private final DataType dataType;
    private String ref;
    private final int index;

    public VariableIdentifier(String name, int index, IdentifierType type,
        DataType dataType, Identifier parent) {
        super(name, type, parent);
        this.index = index;
        this.dataType = dataType;
        set(type);
    }

    public VariableIdentifier(String name, int index, IdentifierType type,
        DataType dataType, Identifier parent, String ref) {
        this(name, index, type, dataType, parent);
        this.ref = ref;
    }

    public MemorySegment getSegment() {
        return segment;
    }

    private void set(IdentifierType type) {
        if (type == IdentifierType.FILED_NAME) {
            segment = MemorySegment.THIS;
        }
        if (type == IdentifierType.VAR_NAME) {
            segment = MemorySegment.LCL;
        }
        if (type == IdentifierType.STATIC_NAME) {
            segment = MemorySegment.STATIC;
        }
        if (type == IdentifierType.ARGUMENT_NAME) {
            segment = MemorySegment.ARG;
        }
    }

    public int getIndex() {
        return index;
    }

    public DataType getDataType() {
        return dataType;
    }

    public String getRef() {
        return ref;
    }

    @Override
    public boolean isVariable() {
        return true;
    }
}
