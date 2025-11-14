package com.compiler.codegen;

import com.compiler.ast.*;
import com.compiler.token.*;
import org.objectweb.asm.*;
import java.util.*;

public class BytecodeGenerator implements NodeVisitor<Void>, Opcodes {
    private ClassWriter classWriter;
    private MethodVisitor methodVisitor;
    private String className;
    private Map<String, Integer> localVariables;
    private int localVarCounter;
    private int labelCounter = 0;

    private Label newLabel() {
        labelCounter++;
        return new Label();
    }

    private void generateCondition(ExpressionNode condition, Label targetLabel, boolean jumpIfTrue) {
        if (condition instanceof BinaryOpNode) {
            BinaryOpNode binOp = (BinaryOpNode) condition;
            String operator = binOp.operator.getValue();

            // Check if it's a comparison operator
            if (isComparisonOperator(operator)) {
                // Generate comparison operands
                binOp.left.accept(this);
                binOp.right.accept(this);

                // Generate comparison jump
                int jumpOpcode = getComparisonOpcode(operator, jumpIfTrue);
                methodVisitor.visitJumpInsn(jumpOpcode, targetLabel);
            } else {
                // For non-comparison expressions (like arithmetic), check if != 0
                condition.accept(this);
                int jumpOpcode = jumpIfTrue ? IFNE : IFEQ;
                methodVisitor.visitJumpInsn(jumpOpcode, targetLabel);
            }
        } else {
            // For non-binary expressions, check if != 0
            condition.accept(this);
            int jumpOpcode = jumpIfTrue ? IFNE : IFEQ;
            methodVisitor.visitJumpInsn(jumpOpcode, targetLabel);
        }
    }

    private Map<String, FunctionDeclNode> functions;

    private boolean isComparisonOperator(String operator) {
        return operator.equals("==") || operator.equals("!=") ||
                operator.equals("<") || operator.equals("<=") ||
                operator.equals(">") || operator.equals(">=");
    }

    private int getComparisonOpcode(String operator, boolean jumpIfTrue) {
        switch (operator) {
            case "==": return jumpIfTrue ? IF_ICMPEQ : IF_ICMPNE;
            case "!=": return jumpIfTrue ? IF_ICMPNE : IF_ICMPEQ;
            case "<":  return jumpIfTrue ? IF_ICMPLT : IF_ICMPGE;
            case "<=": return jumpIfTrue ? IF_ICMPLE : IF_ICMPGT;
            case ">":  return jumpIfTrue ? IF_ICMPGT : IF_ICMPLE;
            case ">=": return jumpIfTrue ? IF_ICMPGE : IF_ICMPLT;
            default:
                throw new RuntimeException("Unknown comparison operator: " + operator);
        }
    }

    public BytecodeGenerator(String className) {
        this.className = className;
        this.localVariables = new HashMap<>();
        this.localVarCounter = 0;
    }

    public byte[] generate(ProgramNode program) {
        // Collect all functions
        functions = new HashMap<>();
        for (FunctionDeclNode func : program.functions) {
            functions.put(func.functionName.getValue(), func);
        }

        // Create class
        classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classWriter.visit(V11, ACC_PUBLIC, className, null, "java/lang/Object", null);

        // Generate constructor
        generateConstructor();

        // Generate all functions as static methods
        for (FunctionDeclNode func : program.functions) {
            if (func.functionName.getValue().equals("main")) {
                generateMainMethod(func);
            } else {
                generateFunction(func);
            }
        }

        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void generateConstructor() {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private void generateMainMethod(FunctionDeclNode mainFunc) {
        methodVisitor = classWriter.visitMethod(
                ACC_PUBLIC | ACC_STATIC,
                "main",
                "([Ljava/lang/String;)V",
                null,
                null
        );
        methodVisitor.visitCode();

        // Reset local variables
        localVariables = new HashMap<>();
        localVarCounter = 1; // 0 is for String[] args

        // Generate main body
        mainFunc.body.accept(this);

        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private void generateFunction(FunctionDeclNode func) {
        // Reset local variables for this function
        localVariables = new HashMap<>();
        localVarCounter = 0;

        // Map parameters to local variables
        for (Token param : func.parameters) {
            localVariables.put(param.getValue(), localVarCounter++);
        }

        // Create method descriptor
        String descriptor = createMethodDescriptor(func);

        methodVisitor = classWriter.visitMethod(
                ACC_PUBLIC | ACC_STATIC,
                func.functionName.getValue(),
                descriptor,
                null,
                null
        );
        methodVisitor.visitCode();

        // Generate function body
        func.body.accept(this);

        // Add default return if no explicit return
        methodVisitor.visitInsn(ICONST_0); // Default return value
        methodVisitor.visitInsn(IRETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private String createMethodDescriptor(FunctionDeclNode func) {
        StringBuilder desc = new StringBuilder("(");

        // Add parameter types (assume all are int for simplicity)
        for (int i = 0; i < func.parameters.size(); i++) {
            desc.append("I");
        }

        // Return type (int)
        desc.append(")I");
        return desc.toString();
    }


    @Override
    public Void visitProgramNode(ProgramNode node) {
        return null;
    }

    @Override
    public Void visitIntLiteralNode(IntLiteralNode node) {
        // node.value is already an int, no need to parse
        int value = node.value;
        if (value >= -1 && value <= 5) {
            methodVisitor.visitInsn(ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            methodVisitor.visitIntInsn(BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            methodVisitor.visitIntInsn(SIPUSH, value);
        } else {
            methodVisitor.visitLdcInsn(value);
        }
        return null;
    }

    @Override
    public Void visitFloatLiteralNode(FloatLiteralNode node) {
        // node.value is already a float, no need to parse
        float value = node.value;
        methodVisitor.visitLdcInsn(value);
        return null;
    }

    @Override
    public Void visitVariableNode(VariableNode node) {
        Integer varIndex = localVariables.get(node.name);
        if (varIndex == null) {
            throw new RuntimeException("Variable not found: " + node.name);
        }
        methodVisitor.visitVarInsn(ILOAD, varIndex);
        return null;
    }

    @Override
    public Void visitAssignmentNode(AssignmentNode node) {
        String varName = node.identifier.getValue();

        // Allocate variable if not exists
        if (!localVariables.containsKey(varName)) {
            localVariables.put(varName, localVarCounter++);
        }

        // Generate code for the expression
        node.expression.accept(this);

        // Store the result
        Integer varIndex = localVariables.get(varName);
        methodVisitor.visitVarInsn(ISTORE, varIndex);
        return null;
    }

    @Override
    public Void visitBinaryOpNode(BinaryOpNode node) {
        String operator = node.operator.getValue();

        // Check if it's a comparison operator
        if (isComparisonOperator(operator)) {
            // For comparisons used as expressions (not in conditions)
            // Generate code that pushes 1 (true) or 0 (false) onto stack
            Label trueLabel = newLabel();
            Label endLabel = newLabel();

            // Generate operands
            node.left.accept(this);
            node.right.accept(this);

            // Compare and jump if true
            int jumpOpcode = getComparisonOpcode(operator, true);
            methodVisitor.visitJumpInsn(jumpOpcode, trueLabel);

            // False case: push 0
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitJumpInsn(GOTO, endLabel);

            // True case: push 1
            methodVisitor.visitLabel(trueLabel);
            methodVisitor.visitInsn(ICONST_1);

            methodVisitor.visitLabel(endLabel);
            return null;
        }

        // Generate left operand
        node.left.accept(this);

        // Generate right operand
        node.right.accept(this);

        // Generate operation
        switch (operator) {
            case "+":
                methodVisitor.visitInsn(IADD);
                break;
            case "-":
                methodVisitor.visitInsn(ISUB);
                break;
            case "*":
                methodVisitor.visitInsn(IMUL);
                break;
            case "/":
                methodVisitor.visitInsn(IDIV);
                break;
            case "%":
                methodVisitor.visitInsn(IREM);
                break;
            default:
                throw new RuntimeException("Unsupported operator: " + operator);
        }
        return null;
    }

    @Override
    public Void visitUnaryOpNode(UnaryOpNode node) {
        node.operand.accept(this);

        String operator = node.operator.getValue();
        if (operator.equals("-")) {
            methodVisitor.visitInsn(INEG);
        }
        return null;
    }

    @Override
    public Void visitPrintNode(PrintNode node) {
        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Generate expression
        node.expression.accept(this);

        // Call println
        methodVisitor.visitMethodInsn(
                INVOKEVIRTUAL,
                "java/io/PrintStream",
                "println",
                "(I)V",
                false
        );
        return null;
    }

    @Override
    public Void visitBlockNode(BlockNode node) {
        for (StatementNode statement : node.statements) {
            statement.accept(this);
        }
        return null;
    }

    @Override
    public Void visitExpressionStatementNode(ExpressionStatementNode node) {
        node.expression.accept(this);
        methodVisitor.visitInsn(POP); // Discard unused result
        return null;
    }

    // Placeholder methods for Days 8-9
    @Override
    public Void visitIfNode(IfNode node) {
        Label elseLabel = newLabel();
        Label endLabel = newLabel();

        // Generate condition (jump to else/end if false)
        generateCondition(node.condition, elseLabel, false);

        // Then block
        node.thenBlock.accept(this);

        // Jump to end (skip else block)
        methodVisitor.visitJumpInsn(GOTO, endLabel);

        // Else block
        methodVisitor.visitLabel(elseLabel);
        if (node.elseBlock != null) {
            node.elseBlock.accept(this);
        }

        // End label
        methodVisitor.visitLabel(endLabel);
        return null;
    }

    @Override
    public Void visitWhileNode(WhileNode node) {
        Label startLabel = newLabel();
        Label endLabel = newLabel();

        // Loop start
        methodVisitor.visitLabel(startLabel);

        // Generate condition (jump to end if false)
        generateCondition(node.condition, endLabel, false);

        // Loop body
        node.body.accept(this);

        // Jump back to start
        methodVisitor.visitJumpInsn(GOTO, startLabel);

        // Loop end
        methodVisitor.visitLabel(endLabel);
        return null;
    }

    @Override
    public Void visitFunctionDeclNode(FunctionDeclNode node) {
        return null;
    }

    @Override
    public Void visitFunctionCallNode(FunctionCallNode node) {
        String functionName = node.functionName.getValue();

        // Special handling for print (built-in function)
        if (functionName.equals("print")) {
            return visitPrintNode(new PrintNode(node.functionName, node.arguments.get(0)));
        }

        // Generate arguments (push onto stack)
        for (ExpressionNode arg : node.arguments) {
            arg.accept(this);
        }

        // Get function declaration to create descriptor
        FunctionDeclNode funcDecl = functions.get(functionName);
        if (funcDecl == null) {
            throw new RuntimeException("Function not found: " + functionName);
        }

        // Create descriptor
        String descriptor = createMethodDescriptor(funcDecl);

        // Call function
        methodVisitor.visitMethodInsn(
                INVOKESTATIC,
                className,
                functionName,
                descriptor,
                false
        );

        return null;
    }

    @Override
    public Void visitReturnNode(ReturnNode node) {
        if (node.expression != null) {
            // Generate the return expression
            node.expression.accept(this);
            methodVisitor.visitInsn(IRETURN);
        } else {
            // Void return
            methodVisitor.visitInsn(RETURN);
        }
        return null;
    }
}
