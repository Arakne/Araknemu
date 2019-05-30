package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.util.RandomUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Set of grades for a monster template
 */
final public class GradeSet {
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    final private List<Monster> grades;

    GradeSet(List<Monster> grades) {
        this.grades = grades;
    }

    /**
     * Get all available grades
     * The grades are sorted by level (lower to higher level)
     */
    public List<Monster> all() {
        return grades;
    }

    /**
     * Get all grades that contained into the given level interval
     * The interval is inclusive (min <= grade level <= max)
     *
     * If the interval is larger than grade levels interval, all grades are returned
     * If the interval is disjoint with grade levels, an empty list is returned
     */
    public List<Monster> in(Interval levels) {
        // All levels are requested
        if (levels.min() == 1 && levels.max() == Integer.MAX_VALUE) {
            return grades;
        }

        return grades.stream()
            .filter(monster -> levels.contains(monster.level()))
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get one random grade extracted from matching grades into levels interval (including)
     *
     * @see GradeSet#in(Interval)
     *
     * @throws NoSuchElementException When levels interval is disjoint with grades levels
     */
    public Monster random(Interval levels) {
        final List<Monster> matching = in(levels);

        switch (matching.size()) {
            case 0:
                throw new NoSuchElementException("Cannot found any valid grade for monster " + grades.get(0).id());

            case 1:
                return matching.get(0);

            default:
                return RANDOM.of(matching);
        }
    }
}
