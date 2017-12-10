package fr.quatrevieux.araknemu.data.living.constraint;

/**
 * Base entity constraint class for build constraints
 *
 * @param <T> The entity type
 * @param <E> The error type
 */
abstract public class ConstraintBuilderFactory<T, E> implements EntityConstraint<T, E>, BuilderFactory<T, E> {
    private E error;
    private EntityConstraint<T, E> constraint;

    @Override
    public boolean check(T entity) {
        return constraint().check(entity);
    }

    @Override
    public E error() {
        return constraint().error();
    }

    private EntityConstraint<T, E> constraint() {
        if (constraint != null) {
            return constraint;
        }

        ConstraintBuilder<T, E> builder = new ConstraintBuilder<>();

        build(builder);

        return constraint = builder.build();
    }
}
