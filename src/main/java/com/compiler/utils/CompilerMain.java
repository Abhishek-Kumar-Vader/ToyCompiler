package com.compiler.utils;

import com.compiler.ast.*;
import com.compiler.lexer.*;
import com.compiler.parser.*;
import com.compiler.analyzer.*;
import com.compiler.codegen.*;
import java.io.*;
import java.nio.file.*;

public class CompilerMain {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: java CompilerMain <source-file>");
            System.exit(1);
        }

        String sourceFile = args[0];
        String className = "Main";

        // Read source
        String source = Files.readString(Paths.get(sourceFile));

        // Lex
        Lexer lexer = new Lexer(new SourceReader(source));

        // Parse
        Parser parser = new Parser(lexer);
        ProgramNode program = parser.parse();

        // Semantic analysis
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(program);

        // Generate bytecode
        BytecodeGenerator generator = new BytecodeGenerator(className);
        byte[] bytecode = generator.generate(program);

        // Write .class file
        Files.write(Paths.get(className + ".class"), bytecode);

        System.out.println("Compilation successful: " + className + ".class");
    }
}
