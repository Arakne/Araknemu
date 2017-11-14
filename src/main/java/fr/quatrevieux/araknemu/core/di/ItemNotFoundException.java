package fr.quatrevieux.araknemu.core.di;

/**
 * Exception thrown when a type is not found in the container
 */
public class ItemNotFoundException extends ContainerException {
    final private Class type;

    public ItemNotFoundException(Class type) {
        super("For class " + type.getCanonicalName());
        this.type = type;
    }

    public Class type() {
        return type;
    }
}
