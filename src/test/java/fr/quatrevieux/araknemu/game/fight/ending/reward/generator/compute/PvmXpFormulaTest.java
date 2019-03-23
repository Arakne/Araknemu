package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PvmXpFormulaTest extends FightBaseCase {
    private PvmXpFormula formula;
    private Fight fight;
    private List<Fighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmXpFormula();
        fight = createPvmFight();

        monsterFighters = new ArrayList<>(fight.team(1).fighters());
    }

    @Test
    void withOneMonster() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        assertEquals(25, formula.initialize(results).compute(player.fighter()));
    }

    @Test
    void withoutMonster() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.emptyList()
        );

        assertEquals(0, formula.initialize(results).compute(player.fighter()));
    }

    @Test
    void withTwoMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsterFighters
        );

        assertEquals(241, formula.initialize(results).compute(player.fighter()));
    }

    @Test
    void withMultipleWinners() {
        assertEquals(20, xpForMultipleWinners(2));
        assertEquals(21, xpForMultipleWinners(3));
        assertEquals(33, xpForMultipleWinners(4));
        assertEquals(36, xpForMultipleWinners(5));
        assertEquals(39, xpForMultipleWinners(6));
        assertEquals(43, xpForMultipleWinners(7));
        assertEquals(47, xpForMultipleWinners(8));
    }

    @Test
    void withMultipleMonsters() {
        assertEquals(55, xpForMultipleMonsters(2));
        assertEquals(89, xpForMultipleMonsters(3));
        assertEquals(124, xpForMultipleMonsters(4));
        assertEquals(156, xpForMultipleMonsters(5));
        assertEquals(187, xpForMultipleMonsters(6));
        assertEquals(218, xpForMultipleMonsters(7));
        assertEquals(249, xpForMultipleMonsters(8));
        assertEquals(249, xpForMultipleMonsters(8));
    }

    @Test
    void wisdomBonus() {
        player.properties().characteristics().base().add(Characteristic.WISDOM, 100);

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        assertEquals(51, formula.initialize(results).compute(player.fighter()));
    }

    private long xpForMultipleWinners(int nbWinners) {
        List<Fighter> winners = new ArrayList<>(nbWinners);

        for (; nbWinners > 0; --nbWinners) {
            winners.add(player.fighter());
        }

        EndFightResults results = new EndFightResults(
            fight,
            winners,
            Collections.singletonList(monsterFighters.get(0))
        );

        return formula.initialize(results).compute(player.fighter());
    }

    private long xpForMultipleMonsters(int nbMonsters) {
        List<Fighter> monsters = new ArrayList<>(nbMonsters);

        for (; nbMonsters > 0; --nbMonsters) {
            monsters.add(monsterFighters.get(0));
        }

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsters
        );

        return formula.initialize(results).compute(player.fighter());
    }
}
