package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Factory for constraint builder
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
public interface BuilderFactory<T, E> {
    /**
     * Build the builder
     */
    public void build(ConstraintBuilder<T, E> builder);
}
