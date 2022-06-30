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
import java.util.logging.Logger;

public class IdentifierTable {

    private static final Logger LOG = Logger.getGlobal();
    private static final String ERR_DUPLICATED_NAME = "duplicated name";
    private final Set<String> unResolved = new HashSet<>();
    private Map<String, VariableIdentifier> classVars = new HashMap<>();
    private Map<String, VariableIdentifier> subroutineVars = new HashMap<>();
    private Map<String, VariableIdentifier> cur = classVars;
    private Stack<Identifier> parent = new Stack<>();
    private Map<String, Identifier> subroutines = new HashMap<>();
    private String currentClass;

    public VariableIdentifier declareVariable(String id, IdentifierType type, DataType dataType) {
        if (cur.containsKey(id)) {
            throw new IllegalArgumentException(ERR_DUPLICATED_NAME);
        }
        VariableIdentifier identifier = new VariableIdentifier(id, varCount(type), type,
            dataType,
            parent.peek());

        LOG.info(String.format("%s %s %s %d \n", type, dataType, id, varCount(type)));
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
        cur.put(id, identifier);
        return identifier;
    }

    public void declareClass(String name) {
        currentClass = name;
        parent.push(new Identifier(name, IdentifierType.CLASS_NAME));
    }

    public Identifier declareIdentifier(String id, IdentifierType type) {
        Identifier identifier = new Identifier(id, type);
        subroutines.put(id, identifier);
        return identifier;
    }

    public String getCurrentClass() {
        return currentClass;
    }

    public Identifier reference(String id) {
        if (cur.containsKey(id)) {
            return cur.get(id);
        }
        if (classVars.containsKey(id)) {
            return classVars.get(id);
        }
        if (subroutines.containsKey(id)) {
            return subroutines.get(id);
        }
        unResolved.add(id);
        return new Identifier(id, IdentifierType.NONE);
    }

    public int varCount(IdentifierType type) {
        return (int) (classVars.values().stream().filter(i -> i.is(type)).count() +
            subroutineVars.values().stream().filter(i -> i.is(type)).count());
    }

    public void startSubroutine() {
        subroutineVars.clear();
        cur = subroutineVars;
    }
}
