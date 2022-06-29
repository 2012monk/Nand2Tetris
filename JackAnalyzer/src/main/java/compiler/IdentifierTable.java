package compiler;

import compiler.componenets.Identifier;
import compiler.componenets.VariableIdentifier;
import compiler.constants.DataType;
import compiler.constants.IdentifierType;
import compiler.constants.LexicalType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class IdentifierTable {

    private static final String ERR_DUPLICATED_NAME = "duplicated name";
    private final Set<String> unResolved = new HashSet<>();
    private Map<String, VariableIdentifier> claaVars = new HashMap<>();
    private Map<String, VariableIdentifier> subroutineVars = new HashMap<>();
    private Map<String, VariableIdentifier> cur = claaVars;
    private Stack<Identifier> parent = new Stack<>();
    private Map<String, Identifier> identifiers = new HashMap<>();
    private Set<LexicalType> keys = new HashSet<>();
    private String currentClass;

    public VariableIdentifier declareVariable(String id, IdentifierType type, DataType dataType) {
        if (cur.containsKey(id)) {
            throw new IllegalArgumentException(ERR_DUPLICATED_NAME);
        }
        VariableIdentifier identifier = new VariableIdentifier(id, varCount(type), type, dataType,
            parent.peek());
        System.out.printf("%s %s %s %d \n", type, dataType, id, varCount(type));
        cur.put(id, identifier);
        return identifier;
    }

    public VariableIdentifier declareVariable(String id, IdentifierType type, DataType dataType,
        String ref) {
        if (cur.containsKey(id)) {
            throw new IllegalArgumentException(ERR_DUPLICATED_NAME);
        }
        VariableIdentifier identifier = new VariableIdentifier(id, varCount(type), type, dataType,
            parent.peek(), ref);
        System.out.printf("%s %s %s %d %s\n", type, dataType, id, varCount(type), ref);
        cur.put(id, identifier);
        return identifier;
    }

    public void declareClass(String name) {
        currentClass = name;
        parent.push(new Identifier(name, IdentifierType.CLASS_NAME));
    }

    public Identifier declareIdentifier(String id, IdentifierType type) {
        Identifier identifier = new Identifier(id, type);
        if (!identifier.is(IdentifierType.CLASS_NAME)) {
//            currentClass = id;
            identifier.setParent(parent.peek());
        }
        parent.push(identifier);
        return identifier;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public Identifier reference(String id) {
        if (cur.containsKey(id)) {
            return cur.get(id);
        }
        if (claaVars.containsKey(id)) {
            return claaVars.get(id);
        }
        if (identifiers.containsKey(id)) {
            return identifiers.get(id);
        }
        unResolved.add(id);
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
