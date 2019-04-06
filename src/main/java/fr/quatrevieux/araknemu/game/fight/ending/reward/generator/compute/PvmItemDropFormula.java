package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Formula for compute dropped items on a Pvm fight
 *
 * - get all item rewards from monsters
 * - filters items with a discernment rate higher than the team discernment
 * - randomize the dropped items
 * - distribute the same number of items between all winners
 * - for each items, remove one from the quantity, and test the rate
 *
 * Note: each winners can only have one occurrence of an item, per monster
 */
final public class PvmItemDropFormula implements ItemDropFormula {
    private class Scope implements ItemDropFormula.Scope {
        final private List<Pair<MonsterRewardItem, Integer>> dropsAndQuantity;
        final private int maxPerFighter;

        public Scope(List<Pair<MonsterRewardItem, Integer>> dropsAndQuantity, int maxPerFighter) {
            this.dropsAndQuantity = dropsAndQuantity;
            this.maxPerFighter = maxPerFighter;
        }

        @Override
        public Map<Integer, Integer> compute(Fighter fighter) {
            Map<Integer, Integer> droppedItems = new HashMap<>();

            ItemDropIterator iterator = new ItemDropIterator(dropsAndQuantity);

            for (int count = maxPerFighter; count > 0 && iterator.hasNext(); --count) {
                MonsterRewardItem drop = iterator.next();
                iterator.remove();

                if (random.decimal(100) > drop.rate()) {
                    continue;
                }

                droppedItems.put(
                    drop.itemTemplateId(),
                    droppedItems.getOrDefault(drop.itemTemplateId(), 0) + 1
                );
            }

            return droppedItems;
        }
    }

    static private class ItemDropIterator implements Iterator<MonsterRewardItem> {
        final private List<Pair<MonsterRewardItem, Integer>> dropsAndQuantity;

        private int current = -1;

        public ItemDropIterator(List<Pair<MonsterRewardItem, Integer>> dropsAndQuantity) {
            this.dropsAndQuantity = dropsAndQuantity;
        }

        @Override
        public boolean hasNext() {
            return dropsAndQuantity.size() > current + 1;
        }

        @Override
        public MonsterRewardItem next() {
            return dropsAndQuantity.get(++current).getKey();
        }

        @Override
        public void remove() {
            Pair<MonsterRewardItem, Integer> currentPair = dropsAndQuantity.get(current);

            // Remove one from the quantity
            currentPair.setValue(currentPair.getValue() - 1);

            // Out of quantity : remove the item and move cursor to left
            if (currentPair.getValue() == 0) {
                dropsAndQuantity.remove(current--);
            }
        }
    }

    final private RandomUtil random = new RandomUtil();

    @Override
    public ItemDropFormula.Scope initialize(EndFightResults results) {
        final int totalDiscernment = results.winners().stream().mapToInt(fighter -> fighter.characteristics().discernment()).sum();

        List<Pair<MonsterRewardItem, Integer>> dropsAndQuantity = new ArrayList<>();

        for (Fighter fighter : results.loosers()) {
            // @todo visitor
            if (!(fighter instanceof MonsterFighter)) {
                continue;
            }

            for (MonsterRewardItem item : ((MonsterFighter) fighter).reward().items()) {
                if (totalDiscernment >= item.discernment()) {
                    dropsAndQuantity.add(new MutablePair<>(item, item.quantity()));
                }
            }
        }

        return new Scope(
            random.shuffle(dropsAndQuantity),
            (int) Math.ceil((double) dropsAndQuantity.size() / (double) results.winners().size())
        );
    }
}
