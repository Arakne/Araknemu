package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;

/**
 * Base formula for compute the Pvm experience
 */
final public class PvmXpFormula implements XpFormula {
    private static class Scope implements XpFormula.Scope {
        final private long totalXp;
        final private double teamsLevelsRate;
        final private double teamLevelDeviationBonus;
        final private double winnersLevel;

        public Scope(long totalXp, double teamsLevelsRate, double teamLevelDeviationBonus, double winnersLevel) {
            this.totalXp = totalXp;
            this.teamsLevelsRate = teamsLevelsRate;
            this.teamLevelDeviationBonus = teamLevelDeviationBonus;
            this.winnersLevel = winnersLevel;
        }

        @Override
        public long compute(Fighter fighter) {
            return (long) (
                totalXp
                * teamsLevelsRate
                * teamLevelDeviationBonus
                * (1 + ((double) fighter.level() / winnersLevel))
                * (1 + (double) fighter.characteristics().get(Characteristic.WISDOM) / 100)
            );
        }
    }

    @Override
    public XpFormula.Scope initialize(EndFightResults results) {
        return new Scope(
            totalXp(results),
            teamsLevelsRate(results),
            teamLevelDeviationBonus(results),
            results.winners().stream().mapToInt(Fighter::level).sum()
        );
    }

    private long totalXp(EndFightResults results) {
        long xp = 0;

        for (Fighter looser : results.loosers()) {
            // @todo visitor
            if (looser instanceof MonsterFighter) {
                MonsterFighter monsterFighter = (MonsterFighter) looser;

                xp += monsterFighter.reward().experience();
            }
        }

        return xp;
    }

    private double teamLevelDeviationBonus(EndFightResults results) {
        final int level = results.winners().stream().mapToInt(Fighter::level).max().orElse(0) / 3;

        int number = 0;

        for (Fighter winner : results.winners()) {
            if (winner.level() > level) {
                ++number;
            }
        }

        switch (number) {
            case 1:
                return 1.0;

            case 2:
                return 1.1;

            case 3:
                return 1.3;

            case 4:
                return 2.2;

            case 5:
                return 2.5;

            case 6:
                return 2.8;

            case 7:
                return 3.1;

            default:
                return 3.5;
        }
    }

    private double teamsLevelsRate(EndFightResults results) {
        final double winnersLevel = results.winners().stream().mapToInt(Fighter::level).sum();
        final double loosersLevel = results.loosers().stream().mapToInt(Fighter::level).sum();

        return Math.min(
            1.3,
            1 + loosersLevel / winnersLevel
        );
    }
}
