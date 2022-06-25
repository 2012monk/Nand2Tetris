package compiler;

import compiler.constants.IdentifierType;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class IdentifierTable {

    private static final String ERR_DUPLICATED_NAME = "duplicated name";
    private Map<String, IdentifierType> map = new HashMap<>();
    private Stack<String> old = new Stack<>();
    private Stack<String> cur = new Stack<>();
    private Stack<Integer> counts = new Stack<>();
    private int count = 0;

    public void addIdentifier(String id) {
        if (cur.contains(id)) {
            throw new IllegalArgumentException(ERR_DUPLICATED_NAME +" " + id);
        }
        count++;
        cur.push(id);
    }

    public void addIdentifier(String id, IdentifierType type) {
        if (cur.contains(id)) {
            throw new IllegalArgumentException(ERR_DUPLICATED_NAME +" " + id);
        }
        count++;
        cur.push(id);
        map.put(id, type);
    }

    public boolean contains(String id, IdentifierType type) {
//        System.out.println(map);
//        return map.containsKey(id) && map.get(id) == type;
        // TODO check reference
        return true;
    }

    public void openSection() {
        old.addAll(cur);
        cur.clear();
        counts.push(count);
        count = 0;
    }

    public void closeSection() {
        for (String s : cur) {
            map.remove(s);
        }
        cur.clear();
        count = counts.pop();
        for (int i = 0; i < count; i++) {
            cur.add(old.pop());
        }
    }
}
