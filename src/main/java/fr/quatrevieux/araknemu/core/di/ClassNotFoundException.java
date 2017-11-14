package fr.quatrevieux.araknemu.core.di;

/**
 * Exception thrown when a type is not found in the container
 */
public class ClassNotFoundException extends Exception {
    final private Class type;

    public ClassNotFoundException(Class type) {
        super("For class " + type.getCanonicalName());
        this.type = type;
    }

    public Class type() {
        return type;
    }
}
