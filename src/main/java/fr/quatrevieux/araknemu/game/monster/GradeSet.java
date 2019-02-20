package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.util.RandomUtil;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 *
 */
final public class GradeSet {
    final static private RandomUtil RANDOM = RandomUtil.createShared();

    final private List<Monster> grades;

    GradeSet(List<Monster> grades) {
        this.grades = grades;
    }

    public List<Monster> all() {
        return grades;
    }

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

    public Monster random(Interval levels) {
        final List<Monster> grades = in(levels);

        switch (grades.size()) {
            case 0:
                throw new NoSuchElementException("Cannot found any valid grade");

            case 1:
                return grades.get(0);

            default:
                return RANDOM.of(grades);
        }
    }
}
