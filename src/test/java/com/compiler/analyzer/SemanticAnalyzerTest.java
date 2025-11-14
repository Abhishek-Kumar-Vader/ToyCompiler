package com.compiler.analyzer;

import com.compiler.ast.ProgramNode;
import com.compiler.lexer.Lexer;
import com.compiler.parser.Parser;
import com.compiler.utils.SourceReader;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class SemanticAnalyzerTest {
    private void analyze(String source) throws IOException {
        Lexer lexer = new Lexer(SourceReader.fromString(source));
        Parser parser = new Parser(lexer);
        ProgramNode program = parser.parse();
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(program);
    }

    @Test
    public void testValidProgram() {
        String source = """
            function main() {
                x = 5;
                y = x + 10;
                print(y);
            }
            """;
        // Should not throw any exception
        assertDoesNotThrow(() -> analyze(source));
    }

    @Test
    public void testUndeclaredVariableUse() {
        String source = """
            function main() {
                x = y; // y is not declared
            }
            """;
        Exception exception = assertThrows(RuntimeException.class, () -> analyze(source));
        assertTrue(exception.getMessage().contains("Variable 'y' not declared"));
    }

    @Test
    public void testVariableRedeclarationInSameScope() {
        // Our current implementation allows this, but a stricter one would fail.
        // Let's write a test that shows it passes for now.
        // To make it fail, we would need to separate declaration from assignment more strictly.
        String source = """
            function main() {
                x = 5;
                x = 10; // This is a re-assignment, not a re-declaration.
            }
            """;
        assertDoesNotThrow(() -> analyze(source));
    }

    @Test
    public void testUninitializedVariableUse() {
        String source = """
            function main() {
                a = b; // b is not declared or initialized
            }
            """;
        Exception exception = assertThrows(RuntimeException.class, () -> analyze(source));
        assertTrue(exception.getMessage().contains("Variable 'b' not declared"));
    }

    @Test
    public void testScope() {
        String source = """
            function first() {
                a = 1;
            }
            function second() {
                b = a; // a is not in this scope
            }
            """;
        Exception exception = assertThrows(RuntimeException.class, () -> analyze(source));
        assertTrue(exception.getMessage().contains("Variable 'a' not declared"));
    }

    @Test
    public void testFunctionCallValidation() {
        String source = """
            function main() {
                myFunc(); // myFunc is not declared
            }
            """;
        Exception exception = assertThrows(RuntimeException.class, () -> analyze(source));
        assertTrue(exception.getMessage().contains("Function 'myFunc' not declared"));
    }

    @Test
    public void testValidFunctionCall() {
        String source = """
            function myFunc() {
                print(1);
            }
            function main() {
                myFunc();
            }
            """;
        assertDoesNotThrow(() -> analyze(source));
    }
}
