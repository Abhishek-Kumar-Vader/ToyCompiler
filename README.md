# ToyCompiler

A Java-based compiler for a simple programming language that generates executable JVM bytecode, and pipeline including lexical analysis, syntax parsing, semantic analysis, and code generation.

## Overview

ToyCompiler is an educational compiler implementation that demonstrates the fundamental phases of compilation. It compiles source code written in a Java-like syntax into JVM bytecode that can be executed on any Java Virtual Machine.

## Features

### Compiler Phases

- **Lexical Analysis**: Tokenizes source code into meaningful tokens  
- **Syntax Analysis**: Constructs an Abstract Syntax Tree (AST) from tokens  
- **Semantic Analysis**: Validates variable declarations, scoping, and initialization  
- **Code Generation**: Generates executable JVM bytecode using the ASM library  

### Language Features

#### Data Types
- `int` – 32-bit signed integers  
- `float` – Single-precision floating-point numbers  

#### Control Flow
- `if` / `else` statements  
- `while` loops  
- Comparison operators: `==`, `!=`, `<`, `<=`, `>`, `>=`  

#### Functions
- Function declarations with parameters  
- Function calls with argument passing  
- Return statements  
- Recursion support  
- Static method generation  

#### Operators
- Arithmetic: `+`, `-`, `*`, `/`, `%`  
- Comparison: `==`, `!=`, `<`, `<=`, `>`, `>=`  
- Unary: `-` (negation)  

#### Built-in Functions
- `print(expression)` – Outputs integer values to console  

## Installation

### Prerequisites
- Java Development Kit (JDK) 11 or higher  
- Apache Maven 3.6 or higher  

### Build from Source

Clone the repository:

```bash
git clone https://github.com/Abhishek-Kumar-Vader/ToyCompiler.git
cd ToyCompiler
```

Build the project:

```bash
mvn clean compile
```

Run tests:

```bash
mvn test
```

## Usage

### Compiling a Program

Example source file (`factorial.toy`):

```toy
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
```

Compile the file:

```bash
java -cp target/classes com.compiler.utils.CompilerMain factorial.toy
```

This generates `Main.class`.

### Running the Compiled Program

```bash
java Main
```

Output:

```
120
```

## Example Programs

### Fibonacci Sequence

```toy
function fib(n) {
    if (n <= 1) {
        return n;
    }
    return fib(n - 1) + fib(n - 2);
}

function main() {
    i = 0;
    while (i < 10) {
        print(fib(i));
        i = i + 1;
    }
}
```


## Project Structure

```
ToyCompiler/
├── src/
│   ├── main/java/com/compiler/
│   │   ├── lexer/            # Lexical analysis
│   │   │   └── Lexer.java
│   │   ├── parser/           # Syntax analysis
│   │   │   └── Parser.java
│   │   ├── ast/              # AST node definitions
│   │   │   ├── Node.java
│   │   │   ├── ExpressionNode.java
│   │   │   ├── StatementNode.java
│   │   │   └── ...
│   │   ├── analyzer/         # Semantic analysis
│   │   │   ├── SemanticAnalyzer.java
│   │   │   ├── SymbolTable.java
│   │   │   └── Scope.java
│   │   ├── codegen/          # Code generation
│   │   │   └── BytecodeGenerator.java
│   │   ├── token/            # Token definitions
│   │   │   ├── Token.java
│   │   │   └── TokenType.java
│   │   └── utils/            # Utility classes
│   │       ├── SourceReader.java
│   │       └── CompilerMain.java
│   └── test/java/com/compiler/codegen/
│       ├── BytecodeGeneratorTest.java
│       ├── IntegrationTest.java
│       └── BytecodeRunner.java
├── pom.xml
└── README.md
```

## Architecture

### Compilation Pipeline

1. **Source Code** → Lexer → **Token Stream**  
2. **Token Stream** → Parser → **AST**  
3. **AST** → Semantic Analyzer → **Validated AST**  
4. **Validated AST** → Bytecode Generator → **JVM Bytecode (.class)**  
5. **Bytecode** → JVM → **Execution**  

### Design Patterns

- **Visitor Pattern**: AST traversal  
- **Factory Pattern**: Token/AST node creation  
- **Builder Pattern**: ASM ClassWriter & MethodVisitor  

## Technical Details

### Dependencies

- **ASM 9.7** – Bytecode manipulation  
- **JUnit 5.10.0** – Testing  
- **Maven** – Build & dependency management  

### Bytecode Generation

Each function is compiled to a static JVM method.  
`main()` becomes:

```
public static void main(String[] args)
```

#### Supported JVM Instructions

- Stack ops: `ICONST`, `BIPUSH`, `SIPUSH`, `LDC`  
- Arithmetic: `IADD`, `ISUB`, `IMUL`, `IDIV`, `IREM`, `INEG`  
- Comparisons: `IF_ICMPEQ`, `IF_ICMPNE`, etc.  
- Control flow: `GOTO`, `IFEQ`, `IFNE`  
- Variables: `ILOAD`, `ISTORE`  
- Methods: `INVOKESTATIC`, `INVOKEVIRTUAL`  
- Returns: `IRETURN`, `RETURN`  

## Testing

Includes:

### Unit Tests
- Lexer  
- Parser  
- Semantic Analyzer  
- Bytecode Generator  

### Integration Tests
- End-to-end compilation  
- Execution correctness  
- Recursion  
- Complex control flow  

Run tests:

```bash
mvn test
```

## Limitations
### Current Limitations (To Be Implemented)
- Partial float support  
- No strings  
- No arrays  
- No OOP features  
- Only `print()` standard library  
- No logical operators (`&&`, `||`, `!`)  
- No `for` loops  
- Error messages limited  
- No optimizations  

## Future Enhancements

- Full float/double support  
- Strings  
- Arrays  
- For loops  
- Logical operators  
- OOP features  
- Standard library additions  
- Better error messages  
- Optimizations  
- Debugging metadata  
- Interactive REPL  

## Contributing

Pull requests and issues are welcome.

## License

This project is available for educational use.

## Author

**Abhishek Kumar**  
GitHub: [@Abhishek-Kumar-Vader](https://github.com/Abhishek-Kumar-Vader)

## Acknowledgments

- Inspired by classic compiler design books  
- Powered by ASM bytecode framework  
- Built as part of a compiler construction learning project  
