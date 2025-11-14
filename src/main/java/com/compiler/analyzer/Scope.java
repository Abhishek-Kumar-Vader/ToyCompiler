package com.compiler.analyzer;

import java.util.HashMap;
import java.util.Map;

public class Scope {
    private final Map<String,Boolean> symbols = new HashMap<>();
    public final Scope parent;

    public Scope(Scope parent){
        this.parent=parent;
    }

    public boolean declare(String name){
        if(symbols.containsKey(name)){
            return false;
        }
        symbols.put(name,false);
        return true;
    }

    public boolean isDeclared(String name){
        if(symbols.containsKey(name)){
            return true;
        }
        if(parent!=null){
            return parent.isDeclared(name);
        }
        return false;
    }

    public void initialize(String name){
        if(symbols.containsKey(name)){
            symbols.put(name,true);
        }else if(parent!=null){
            parent.initialize(name);
        }
    }

    public boolean isInitialized(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        }
        if (parent != null) {
            return parent.isInitialized(name);
        }
        return false;
    }
}
