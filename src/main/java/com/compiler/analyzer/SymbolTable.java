package com.compiler.analyzer;

public class SymbolTable {
    private Scope currentScope;

    public SymbolTable(){
        this.currentScope=new Scope(null);
    }

    public void enterScope(){
        currentScope=new Scope(currentScope);
    }

    public void exitScope(){
        if(currentScope.parent!=null){
            currentScope=currentScope.parent;
        }
    }

    public void declare(String name){
        if(!currentScope.declare(name)){
            throw new RuntimeException("Semantic Error: Variable '" + name + "' already declared in this scope.");
        }
    }

    public boolean isDeclared(String name){
        return currentScope.isDeclared(name);
    }

    public void initialize(String name){
        currentScope.initialize(name);
    }

    public boolean isInitialize(String name){
        return currentScope.isInitialized(name);
    }
}
