package fr.quatrevieux.araknemu.game.world.map.path;

import fr.quatrevieux.araknemu.game.world.map.MapCell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Path for dofus map
 * @param <C> The cell type
 */
final public class Path<C extends MapCell> implements Collection<PathStep<C>> {
    final private Decoder<C> decoder;
    final private List<PathStep<C>> steps;

    public Path(Decoder<C> decoder, List<PathStep<C>> steps) {
        this.decoder = decoder;
        this.steps = steps;
    }

    /**
     * Create an empty path
     */
    public Path(Decoder<C> decoder) {
        this(decoder, new ArrayList<>());
    }

    /**
     * Get one step
     *
     * @param step The step number. The step zero is the current cell, The step (size - 1) is the last step (target)
     */
    public PathStep<C> get(int step) {
        return steps.get(step);
    }

    /**
     * Get the first step of the path
     *
     * @see Path#start() For get the start cell
     */
    public PathStep<C> first() {
        return steps.get(0);
    }

    /**
     * Get the last step
     *
     * @see Path#target() For get the last cell
     */
    public PathStep<C> last() {
        return steps.get(steps.size() - 1);
    }

    /**
     * Get the start cell
     */
    public C start() {
        return first().cell();
    }

    /**
     * Get the last cell
     */
    public C target() {
        return last().cell();
    }

    /**
     * Encode the path to string
     */
    public String encode() {
        return decoder.encode(this);
    }

    /**
     * Keep the path steps while the condition is valid
     * The path will be stop when an invalid step is found
     *
     * @param condition The condition to apply on all steps. If returns false, the path will be stopped
     *
     * @return The new path instance
     */
    public Path<C> keepWhile(Predicate<PathStep<C>> condition) {
        Path<C> newPath = new Path<>(decoder, new ArrayList<>(size()));

        for (PathStep<C> step : steps) {
            if (!condition.test(step)) {
                break;
            }

            newPath.add(step);
        }

        return newPath;
    }

    /**
     * Truncate the path, to the new size
     *
     * @return The new path instance
     */
    public Path<C> truncate(int newSize) {
        if (newSize > size()) {
            return this;
        }

        return new Path<>(decoder, steps.subList(0, newSize));
    }

    @Override
    public int size() {
        return steps.size();
    }

    @Override
    public boolean isEmpty() {
        return steps.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return steps.contains(o);
    }

    @Override
    public Iterator<PathStep<C>> iterator() {
        return steps.iterator();
    }

    @Override
    public Object[] toArray() {
        return steps.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return steps.toArray(a);
    }

    @Override
    public boolean add(PathStep<C> step) {
        return steps.add(step);
    }

    @Override
    public boolean remove(Object o) {
        return steps.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return steps.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends PathStep<C>> c) {
        return steps.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return steps.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return steps.retainAll(c);
    }

    @Override
    public void clear() {
        steps.clear();
    }
}
