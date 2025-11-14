package com.compiler.codegen;

import com.compiler.ast.*;
import com.compiler.lexer.*;
import com.compiler.parser.*;
import com.compiler.utils.SourceReader;
import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    @Test
    public void testCompleteProgram() throws Exception {
        String source = """
            function square(x) {
                return x * x;
            }
            
            function sumOfSquares(a, b) {
                return square(a) + square(b);
            }
            
            function main() {
                result = sumOfSquares(3, 4);
                print(result);  // Should be 25
            }
            """;

        byte[] bytecode = compileAndVerify(source,"TestProgram");
        BytecodeRunner.runMain(bytecode, "TestProgram");
    }

    @Test
    public void testFibonacci() throws Exception {
        String source = """
            function fib(n) {
                if (n <= 1) {
                    return n;
                }
                return fib(n - 1) + fib(n - 2);
            }
            
            function main() {
                result = fib(10);
                print(result);  // Should be 55
            }
            """;

        byte[] bytecode = compileAndVerify(source,"Fibonacci");
        BytecodeRunner.runMain(bytecode, "Fibonacci");
    }

    @Test
    public void testComplexControlFlow() throws Exception {
        String source = """
            function classify(x) {
                if (x < 0) {
                    return -1;
                }
                if (x == 0) {
                    return 0;
                }
                return 1;
            }
            
            function main() {
                i = -5;
                while (i <= 5) {
                    result = classify(i);
                    print(result);
                    i = i + 1;
                }
            }
            """;

        byte[] bytecode = compileAndVerify(source,"Classifier");
        BytecodeRunner.runMain(bytecode, "Classifier");
    }

    @Test
    public void testNestedLoopsAndConditions() throws Exception {
        String source = """
            function main() {
                i = 1;
                while (i <= 3) {
                    j = 1;
                    while (j <= 3) {
                        if (i == j) {
                            print(1);
                        } else {
                            print(0);
                        }
                        j = j + 1;
                    }
                    i = i + 1;
                }
            }
            """;

        byte[] bytecode = compileAndVerify(source,"NestedTest");
        BytecodeRunner.runMain(bytecode, "NestedTest");
    }

    private byte[] compileAndVerify(String source, String className) throws IOException {
        SourceReader sourceReader = SourceReader.fromString(source);
        Lexer lexer = new Lexer(sourceReader);
        Parser parser = new Parser(lexer);
        ProgramNode program = parser.parse();

        BytecodeGenerator generator = new BytecodeGenerator(className);
        byte[] bytecode = generator.generate(program);

        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);

        return bytecode;
    }
}
