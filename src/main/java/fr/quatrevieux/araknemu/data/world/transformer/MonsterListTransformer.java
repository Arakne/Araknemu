package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Transform database monster list
 *
 * Format :
 * [id 1],[level min 1],[level max 1]x[rate1]|[id 2],[level min 2],[level max 2]x[rate2]
 *
 * Monsters are separated by pipe "|"
 * Monster properties are separated by comma ","
 * Monster spawn rate is an integer that follow "x"
 *
 * Levels are not required :
 * - If not set, all available levels are used
 * - If only one is set, the level is constant
 * - If interval is set, only grades into the interval are used
 *
 * The spawn rate is not required, and by default, its value is 1
 *
 * @see MonsterGroupData#monsters()
 * @see MonsterGroupData.Monster
 */
final public class MonsterListTransformer implements Transformer<List<MonsterGroupData.Monster>> {
    @Override
    public String serialize(List<MonsterGroupData.Monster> value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<MonsterGroupData.Monster> unserialize(String serialize) throws TransformerException {
        final String[] monstersStr = StringUtils.split(serialize, "|");

        List<MonsterGroupData.Monster> monsters = new ArrayList<>(monstersStr.length);

        for (String monsterStr : monstersStr) {
            String[] dataAndRate = StringUtils.split(monsterStr, "x", 2);
            String[] data = StringUtils.split(dataAndRate[0], ",", 3);

            int rate = 1;

            if (dataAndRate.length == 2) {
                rate = Integer.parseInt(dataAndRate[1]);
            }

            int monsterId = Integer.parseInt(data[0]);
            int minLevel = 1;
            int maxLevel = Integer.MAX_VALUE;

            if (data.length > 1) {
                minLevel = maxLevel = Integer.parseInt(data[1]);
            }

            if (data.length > 2) {
                maxLevel = Integer.parseInt(data[2]);
            }

            monsters.add(new MonsterGroupData.Monster(monsterId, new Interval(minLevel, maxLevel), rate));
        }

        return monsters;
    }
}
