package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.util.RandomUtil;

/**
 * Provide win kamas on Pvm fight
 */
final public class PvmKamasProvider implements DropRewardProvider {
    final private RandomUtil random = new RandomUtil();

    @Override
    public Scope initialize(EndFightResults results) {
        final Interval totalKamas = totalKamas(results);

        return reward -> reward.setKamas(random.rand(totalKamas));
    }

    private Interval totalKamas(EndFightResults results) {
        int minKamas = 0;
        int maxKamas = 0;

        for (Fighter looser : results.loosers()) {
            // @todo visitor
            if (looser instanceof MonsterFighter) {
                MonsterFighter monsterFighter = (MonsterFighter) looser;

                minKamas += monsterFighter.reward().kamas().min();
                maxKamas += monsterFighter.reward().kamas().max();
            }
        }

        return new Interval(minKamas, maxKamas);
    }
}
