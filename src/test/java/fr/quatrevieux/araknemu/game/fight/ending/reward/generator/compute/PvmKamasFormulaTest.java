package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PvmKamasFormulaTest extends FightBaseCase {
    private PvmKamasFormula formula;
    private Fight fight;
    private List<Fighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmKamasFormula();
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

        assertBetween(50, 70, formula.initialize(results).compute(player.fighter()));
        assertNotEquals(
            formula.initialize(results).compute(player.fighter()),
            formula.initialize(results).compute(player.fighter())
        );
    }

    @Test
    void withMultipleMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            monsterFighters
        );

        assertBetween(100, 140, formula.initialize(results).compute(player.fighter()));
        assertNotEquals(
            formula.initialize(results).compute(player.fighter()),
            formula.initialize(results).compute(player.fighter())
        );
    }

    @Test
    void withoutMonsters() {
        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(player.fighter())
        );

        assertEquals(0, formula.initialize(results).compute(player.fighter()));
    }
}
