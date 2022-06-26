package compiler;

import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.DataType;
import compiler.constants.IdentifierType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class IdentifierTable {

    private static final String ERR_DUPLICATED_NAME = "duplicated name";
    private Map<String, VariableIdentifier> claaVars = new HashMap<>();
    private Map<String, VariableIdentifier> subroutineVars = new HashMap<>();
    private Map<String, VariableIdentifier> cur = claaVars;
    private Stack<Identifier> parent = new Stack<>();
    private Map<String, Identifier> identifiers = new HashMap<>();
    private Set<String> undefined = new HashSet<>();

    public VariableIdentifier declareVariable(String id, IdentifierType type, DataType dataType) {
        if (cur.containsKey(id)) {
            throw new IllegalArgumentException(ERR_DUPLICATED_NAME);
        }
        VariableIdentifier identifier = new VariableIdentifier(id, varCount(type), type, dataType, parent.peek());
        cur.put(id, identifier);
        return identifier;
    }

    public Identifier declareIdentifier(String id, IdentifierType type) {
        Identifier identifier = new Identifier(id, type);
        if (!identifier.is(IdentifierType.CLASS_NAME)) {
            startSubroutine();
        }
        parent.push(identifier);
        return identifier;
    }

    public Identifier reference(String id) {
        if (cur.containsKey(id)) return cur.get(id);
        if (claaVars.containsKey(id)) return claaVars.get(id);
        if (identifiers.containsKey(id)) return identifiers.get(id);
        undefined.add(id);
        return new Identifier(id, IdentifierType.NONE);
    }

    public int varCount(IdentifierType type) {
        return (int) cur.values().stream().filter(i -> i.is(type))
            .count();
    }

    public void startSubroutine() {
        subroutineVars.clear();
        cur = subroutineVars;
    }
}
