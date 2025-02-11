package info.kgeorgiy.ja.gerasimov.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;


/**
 * This class contains methods that generate java and jar files.
 * This class implements from JarImpler and implements all its methods
 */
public class Implementor implements JarImpler {
    /**
     * A {@link FileVisitor} that deletes all files it encounters
     */
    private static final FileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {
        /**
         * {@inheritDoc}
         * @throws IOException When it is not possible to delete a file
         */
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * {@inheritDoc}
         * @throws IOException When it is not possible to delete a directory
         */
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * The only constructor of {@link Implementor}
     */
    public Implementor() {
    }

    /**
     * A console program to generate jar files using the function {@link #implementJar(Class, Path)}
     *
     * @param args The first argument is the word jar.
     *             The second argument is the name of the interface that needs to be implemented.
     *             The third argument is the name of the resulting jar file
     */

    public static void main(String[] args) {
        if (args.length != 3 || !"-jar".equals(args[0])) {
            System.err.println("Your input values is incorrect");
        }
        try {
            new Implementor().implementJar(Class.forName(args[1]), Path.of(args[2]));
        } catch (ImplerException e) {
            System.err.println("Impler error" + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Class can not found" + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * Generates a java file using the {@link #implement(Class, Path)} method
     *
     * @throws ImplerException When you failed to get the name of the generated file.
     *                         When an environment error occurred. Error getting classpath
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        final Path path = Path.of(".directoryWithTrash");
        try (JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(jarFile.toFile()))) {
            implement(token, path);
            final JavaCompiler javaCompiler = getCompiler();
            runCompiler(token, javaCompiler, path);
            jarOutputStream.putNextEntry(
                    new JarEntry(
                            token.getPackageName().replace('.', '/') + "/" +
                            token.getSimpleName() + "Impl.class"
                    )
            );
            Files.copy(path.resolve(getPath(token, ".class")), jarOutputStream);
        } catch (FileNotFoundException e) {
            throw new ImplerException("Can not get name of file", e);
        } catch (IOException e) {
            throw new ImplerException("I/O error has occurred", e);
        } catch (URISyntaxException e) {
            throw new ImplerException("Fail to get classpath", e);
        } finally {
            try {
                Files.walkFileTree(path, DELETE_VISITOR);
            } catch (IOException e) {
                //noinspection ThrowFromFinallyBlock
                throw new ImplerException("Can not delete files", e);
            }
        }
    }

    /**
     * {@inheritDoc}
     * @throws ImplerException When the interface is private or when the submitted {@code  token} is not an interface
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (Modifier.isPrivate(token.getModifiers()) || !token.isInterface()) {
            throw new ImplerException("You can not create this class");
        }
        final Path path = getPath(token, root);
        // :NOTE: /n плохой перевод строки
        try (BufferedWriter bufferedWriter = createBufferedWriter(path)) {
            printPackage(token, bufferedWriter);
            printClassName(token, bufferedWriter);
            printMethods(bufferedWriter,
                    Arrays.stream(token.getMethods())
                            .filter(
                                    method ->
                                            Modifier.isAbstract(method.getModifiers())
                            )
                            .toArray(Method[]::new)
            );
            bufferedWriter.write("}");
        } catch (IOException e) {
            throw new ImplerException("IOException", e);
        }
    }

    /**
     * A function that writes all the methods
     *
     * @param bufferedWriter {@link BufferedWriter} into which function must write
     * @param methods        Incoming methods
     * @throws IOException When it is not possible to write to a file
     */
    private static void printMethods(BufferedWriter bufferedWriter, Method... methods) throws IOException {
        for (Method method : methods) {
            printMethodName(bufferedWriter, method);
            printMethodParameters(bufferedWriter, method);
            printMethodExceptions(bufferedWriter, method);
            printMethodBody(bufferedWriter, method);
        }
    }

    /**
     * A function that writes the name of method
     *
     * @param bufferedWriter {@link BufferedWriter} into which function must write
     * @param method         Incoming method
     * @throws IOException When it is not possible to write to a file
     */
    private static void printMethodName(BufferedWriter bufferedWriter, Method method) throws IOException {
        bufferedWriter.write(
                "@Override\n" +
                Modifier.toString(method.getModifiers() & ~Modifier.TRANSIENT & ~Modifier.ABSTRACT) +
                " " +
                method.getReturnType().getCanonicalName() +
                " " +
                method.getName()
        );
    }

    /**
     * A function that writes the parameters of method
     *
     * @param bufferedWriter {@link BufferedWriter} into which function must write
     * @param method         Incoming method
     * @throws IOException When it is not possible to write to a file
     */
    private static void printMethodParameters(BufferedWriter bufferedWriter, Method method) throws IOException {
        bufferedWriter.write(
                "(" +
                String.join(
                        ", ",
                        Arrays.stream(method.getParameters())
                                .map(parameter -> parameter.getType().getCanonicalName() + " " + parameter.getName())
                                .toArray(String[]::new)
                ) +
                ") "
        );
    }

    /**
     * A function that writes the exceptions to method
     *
     * @param bufferedWriter {@link BufferedWriter} into which function must write
     * @param method         Incoming method
     * @throws IOException When it is not possible to write to a file
     */
    private static void printMethodExceptions(BufferedWriter bufferedWriter, Method method) throws IOException {
        final String[] exceptions =
                Arrays.stream(method.getExceptionTypes())
                        .map(Class::getCanonicalName)
                        .toArray(String[]::new);
        bufferedWriter.write(
                exceptions.length == 0 ?
                        "" :
                        "throws " + String.join(", ", exceptions) + " "
        );
    }

    /**
     * A function that writes the body of method
     *
     * @param bufferedWriter {@link BufferedWriter} into which function must write
     * @param method         Incoming method
     * @throws IOException When it is not possible to write to a file
     */
    private static void printMethodBody(BufferedWriter bufferedWriter, Method method) throws IOException {
        bufferedWriter.write(
                "{ \n" +
                "return " +
                getDefaultReturnValue(method.getReturnType()) +
                "\n}\n"
        );
    }

    /**
     * Returns the default {@code type} of the type as a string
     *
     * @param type {@link Class} the {@code type} for which we want to find out the default value
     * @return default value {@code type}
     */
    private static String getDefaultReturnValue(Class<?> type) {
        if (type.equals(void.class)) {
            return ";\n";
        } else if (type.equals(boolean.class)) {
            return "false;";
        } else if (type.isPrimitive()) {
            return "0;";
        } else {
            return "null;";
        }
    }

    /**
     * Get the {@link Path} to the source file of the implementation
     *
     * @param token Implemented interface
     * @param root  Current directory
     * @return the {@link Path} to the source file of the implementation
     */
    private static Path getPath(Class<?> token, Path root) {
        return root.resolve(
                token.getPackageName().replace('.', '/') +
                "/" +
                token.getSimpleName() +
                "Impl.java"
        );
    }

    /**
     * Creates a {@link BufferedWriter} and folders along the current path
     *
     * @param path the path of the created folders
     * @return Created {@link BufferedWriter}
     * @throws IOException When the {@link BufferedWriter} could not be created or something else
     */
    private static BufferedWriter createBufferedWriter(Path path) throws IOException {
        Files.createDirectories(path.getParent());
        return Files.newBufferedWriter(path);
    }

    /**
     * Prints the line with the package to a file
     *
     * @param token          Implemented interface
     * @param bufferedWriter {@link BufferedWriter} into which package must write
     * @throws IOException When it is not possible to write to a file
     */
    private static void printPackage(Class<?> token, BufferedWriter bufferedWriter) throws IOException {
        if (!token.getPackageName().isEmpty()) {
            bufferedWriter.write(
                    "package " +
                    token.getPackageName() +
                    ";\n"
            );
        }
    }

    /**
     * Prints the line with the name of Class to a file
     *
     * @param token          Implemented interface
     * @param bufferedWriter {@link BufferedWriter} into which name of {@link Class} must write
     * @throws IOException When it is not possible to write to a file
     */
    private static void printClassName(Class<?> token, BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write("public class " +
                             replaceRussianLetters(token.getSimpleName()) +
                             "Impl implements " +
                             replaceRussianLetters(token.getCanonicalName()) +
                             " {\n"
        );
    }

    /**
     * Replaces Russian letters with normal ones to avoid compilation errors
     *
     * @param string the line to rename
     * @return {@link String}  with normal letters
     */
    private static String replaceRussianLetters(final CharSequence string) {
        return string.chars().mapToObj(ch -> String.format("\\u%04x", ch)).collect(Collectors.joining());
    }

    /**
     * Gets the java compiler
     *
     * @return {@link JavaCompiler}
     * @throws ImplerException If it is not there
     */
    private static JavaCompiler getCompiler() throws ImplerException {
        final JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        if (javaCompiler != null)
            return javaCompiler;
        throw new ImplerException("Can not get Java Compiler");
    }

    /**
     * Starts the compiler
     *
     * @param token        Implemented interface
     * @param javaCompiler java compiler
     * @param path         Compiled file
     * @throws URISyntaxException if this URL is not formatted strictly according to RFC2396 and cannot be converted to a URI
     * @throws ImplerException    When the compilation failed with an error
     */
    private static void runCompiler(
            Class<?> token,
            JavaCompiler javaCompiler,
            Path path) throws URISyntaxException, ImplerException {
        if (javaCompiler.run(
                null,
                null,
                null,
                "-encoding",
                StandardCharsets.UTF_8.name(),
                path.resolve(getPath(token, ".java"))
                        .toString(),
                "-cp",
                path.resolve(Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI())).toString()) != 0) {
            throw new ImplerException("Compilation error");
        }
        ;
    }

    /**
     * Get the {@link Path} to the source file of the implementation
     *
     * @param token     Implemented interface
     * @param extension The end of the attributed line
     * @return the {@link Path} to the source file of the implementation
     */

    private static Path getPath(Class<?> token, String extension) {
        return Path.of(
                        token
                                .getPackageName()
                                .replace('.', File.separatorChar))
                .resolve(token.getSimpleName() + "Impl" + extension);
    }
}
