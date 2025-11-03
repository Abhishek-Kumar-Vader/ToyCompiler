package com.compiler.parser;

import com.compiler.ast.AstPrinter;
import com.compiler.ast.ExpressionNode;
import com.compiler.lexer.Lexer;
import com.compiler.utils.SourceReader;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest {
    private String parseAndPrint(String source) throws IOException{
        Lexer lexer = new Lexer(SourceReader.fromString(source));
        Parser parser = new Parser(lexer);
        ExpressionNode expression = parser.parseExpression();
        return new AstPrinter().print(expression);
    }

    @Test
    public void testSimpleAddition() throws IOException {
        String source = "2 + 3";
        String expected = "(+ 2 3)";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testOperatorPrecedence() throws IOException {
        String source = "2 + 3 * 4";
        String expected = "(+ 2 (* 3 4))";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testOperatorPrecedenceWithParentheses() throws IOException {
        String source = "(2 + 3) * 4";
        String expected = "(* (+ 2 3) 4)";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testUnaryOperator() throws IOException {
        String source = "-5 + 10";
        String expected = "(+ (- 5) 10)";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testComplexExpression() throws IOException {
        String source = "-(5 + 2) * 3 / 4";
        String expected = "(/ (* (- (+ 5 2)) 3) 4)";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testComparisonOperators() throws IOException {
        String source = "a > b == c < d";
        //this is parsed left-to-right
        String expected = "(== (> a b) (< c d))";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testAllOperators() throws IOException {
        String source = "a + b * c == d / e - f";
        String expected = "(== (+ a (* b c)) (- (/ d e) f))";
        assertEquals(expected, parseAndPrint(source));
    }

    @Test
    public void testVariableParsing() throws IOException {
        String source = "myVariable";
        String expected = "myVariable";
        assertEquals(expected, parseAndPrint(source));
    }
}
