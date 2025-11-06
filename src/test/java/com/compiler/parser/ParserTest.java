package com.compiler.parser;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.ProgramNode;
import com.compiler.lexer.Lexer;
import com.compiler.utils.SourceReader;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {

    private String parseAndPrint(String source) throws IOException {
        Lexer lexer = new Lexer(SourceReader.fromString(source));
        Parser parser = new Parser(lexer);
        ProgramNode program = parser.parse();
        System.out.println(new AstPrinter().print(program));
        return new AstPrinter().print(program);
    }

    @Test
    public void testFunctionDeclaration() throws IOException {
        String source = """
            function myFunc(a, b) {
                return a + b;
            }
            """;
        String result = parseAndPrint(source);
        assertTrue(result.contains("(fun myFunc (a b)"));
        assertTrue(result.contains("(return (+ a b))"));
    }

    @Test
    public void testIfStatement() throws IOException {
        String source = """
            function main() {
                if (x > 10) {
                    print(x);
                } else {
                    print(10);
                }
            }
            """;
        String result = parseAndPrint(source);
        assertTrue(result.contains("(if-else (> x 10)"));
        assertTrue(result.contains("(block\n  (print x)\n)"));
        assertTrue(result.contains("(block\n  (print 10)\n)"));
    }

    @Test
    public void testWhileStatement() throws IOException {
        String source = """
            function main() {
                i = 0;
                while (i < 10) {
                    i = i + 1;
                }
            }
            """;
        String result = parseAndPrint(source);
        assertTrue(result.contains("(while (< i 10)"));
        assertTrue(result.contains("(= i (+ i 1))"));
    }

    @Test
    public void testFunctionCallStatement() throws IOException {
        String source = """
            function main() {
                myFunc(a, 10);
            }
            """;
        String result = parseAndPrint(source);
        assertTrue(result.contains("(expr-stmt (call myFunc a 10))"));
    }

    @Test
    public void testFullProgram() throws IOException {
        String source = """
            function fib(n) {
                if (n < 2) {
                    return n;
                }
                return fib(n - 1) + fib(n - 2);
            }
            
            function main() {
                print(fib(7));
            }
            """;
        String result = parseAndPrint(source);
        assertTrue(result.startsWith("(program"));
        assertTrue(result.contains("(fun fib (n)"));
        assertTrue(result.contains("(fun main ()"));
        assertTrue(result.contains("(call fib (- n 1))"));
        assertTrue(result.contains("(call fib 7)"));
    }
}
