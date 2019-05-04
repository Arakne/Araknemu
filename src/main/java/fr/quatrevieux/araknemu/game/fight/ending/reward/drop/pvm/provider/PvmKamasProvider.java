package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Provide win kamas on Pvm fight
 */
final public class PvmKamasProvider implements DropRewardProvider {
    final private RandomUtil random = new RandomUtil();

    static private class ExtractKamas implements FighterOperation {
        private int minKamas = 0;
        private int maxKamas = 0;

        @Override
        public void onMonster(MonsterFighter fighter) {
            minKamas += fighter.reward().kamas().min();
            maxKamas += fighter.reward().kamas().max();
        }

        public Interval get() {
            return new Interval(minKamas, maxKamas);
        }
    }

    @Override
    public Scope initialize(EndFightResults results) {
        return reward -> reward.setKamas(
            random.rand(
                results.applyToLoosers(new ExtractKamas()).get()
            )
        );
    }
}
