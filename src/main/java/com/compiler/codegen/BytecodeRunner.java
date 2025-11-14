package com.compiler.codegen;

import java.lang.reflect.Method;

public class BytecodeRunner {
    public static void runMain(byte[] bytecode, String className) throws Exception {
        // Create a custom class loader
        ClassLoader classLoader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (name.equals(className)) {
                    return defineClass(name, bytecode, 0, bytecode.length);
                }
                return super.findClass(name);
            }
        };

        // Load the class
        Class<?> clazz = classLoader.loadClass(className);

        // Find and invoke main method
        Method mainMethod = clazz.getMethod("main", String[].class);
        mainMethod.invoke(null, (Object) new String[0]);
    }
}
