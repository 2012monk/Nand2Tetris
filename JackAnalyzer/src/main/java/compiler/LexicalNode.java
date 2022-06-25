package compiler;

import java.util.List;

public class LexicalNode {

    private boolean isTerminal;
    private boolean isRepeatAble;
    private boolean isSkipAble;
    private List<LexicalNode> nodes;
    private String name;
    /*
    name: class
    node[0] = class terminal
    node[1] = <className>
    node[2] = {
    node[3] = <classVarDec> skip=true
    node[4] = <subroutineDec> skip=true
    node[5] = }
     */
}
