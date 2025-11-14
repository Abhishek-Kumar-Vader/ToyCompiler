package com.compiler.codegen;

import com.compiler.ast.*;
import com.compiler.lexer.*;
import com.compiler.parser.*;
import com.compiler.utils.SourceReader;
import org.junit.jupiter.api.*;
import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class BytecodeGeneratorTest {

    @Test
    public void testSimpleArithmetic() throws Exception {
        String source = """
            function main() {
                x = 5 + 3;
                print(x);
            }
            """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
    }

    @Test
    public void testVariableAssignment() throws Exception {
        String source = """
            function main() {
                x = 10;
                y = x + 5;
                print(y);
            }
            """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
    }

    @Test
    public void testIfStatement() throws Exception {
        String source = """
        function main() {
            x = 10;
            if (x > 5) {
                print(1);
            } else {
                print(0);
            }
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
    }

    @Test
    public void testIfWithoutElse() throws Exception {
        String source = """
        function main() {
            x = 3;
            if (x < 5) {
                print(x);
            }
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
    }

    @Test
    public void testWhileLoop() throws Exception {
        String source = """
        function main() {
            i = 0;
            while (i < 5) {
                print(i);
                i = i + 1;
            }
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
    }

    @Test
    public void testNestedIfElse() throws Exception {
        String source = """
        function main() {
            x = 15;
            if (x > 10) {
                if (x > 20) {
                    print(2);
                } else {
                    print(1);
                }
            } else {
                print(0);
            }
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
    }

    @Test
    public void testSimpleFunction() throws Exception {
        String source = """
        function add(a, b) {
            return a + b;
        }
        
        function main() {
            result = add(5, 3);
            print(result);
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
    }

    @Test
    public void testFunctionWithMultipleStatements() throws Exception {
        String source = """
        function calculate(x) {
            y = x * 2;
            z = y + 10;
            return z;
        }
        
        function main() {
            result = calculate(5);
            print(result);
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
    }

    @Test
    public void testRecursiveFunction() throws Exception {
        String source = """
        function factorial(n) {
            if (n <= 1) {
                return 1;
            }
            return n * factorial(n - 1);
        }
        
        function main() {
            result = factorial(5);
            print(result);
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
        assertTrue(bytecode.length > 0);
    }

    @Test
    public void testMultipleFunctions() throws Exception {
        String source = """
        function multiply(a, b) {
            return a * b;
        }
        
        function add(a, b) {
            return a + b;
        }
        
        function calculate(x, y) {
            temp = multiply(x, y);
            return add(temp, 10);
        }
        
        function main() {
            result = calculate(3, 4);
            print(result);
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
    }

    @Test
    public void testFunctionWithNoParameters() throws Exception {
        String source = """
        function getNumber() {
            return 42;
        }
        
        function main() {
            x = getNumber();
            print(x);
        }
        """;

        byte[] bytecode = compile(source);
        assertNotNull(bytecode);
    }

    @Test
    public void testExecuteFactorial() throws Exception {
        String source = """
        function factorial(n) {
            if (n <= 1) {
                return 1;
            }
            return n * factorial(n - 1);
        }
        
        function main() {
            result = factorial(5);
            print(result);  // Should print 120
        }
        """;

        byte[] bytecode = compile(source);

        // Actually run the generated code
        BytecodeRunner.runMain(bytecode, "TestClass");
    }

    private byte[] compile(String source) throws IOException {
        SourceReader sourceReader = SourceReader.fromString(source);
        Lexer lexer = new Lexer(sourceReader);
        Parser parser = new Parser(lexer);
        ProgramNode program = parser.parse();

        BytecodeGenerator generator = new BytecodeGenerator("TestClass");
        return generator.generate(program);
    }
}