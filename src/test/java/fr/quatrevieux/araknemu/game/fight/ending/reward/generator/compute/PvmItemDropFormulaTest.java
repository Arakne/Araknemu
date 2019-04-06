package fr.quatrevieux.araknemu.game.fight.ending.reward.generator.compute;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterRewardItem;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PvmItemDropFormulaTest extends FightBaseCase {
    private PvmItemDropFormula formula;
    private Fight fight;
    private List<MonsterFighter> monsterFighters;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMonsterTemplates();

        formula = new PvmItemDropFormula();
        fight = createPvmFight();

        monsterFighters = new ArrayList<>();

        for (Fighter fighter : fight.team(1).fighters()) {
            monsterFighters.add((MonsterFighter) fighter);
        }
    }

    @Test
    void withOneMonsterAndFighter() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        Map<Integer, Integer> items = formula.initialize(results).compute(player.fighter());

        assertEquals(2, items.size());
        assertEquals(1, (int) items.get(12));
        assertEquals(1, (int) items.get(13));
    }

    @Test
    void withRandomRate() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 10));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            count += formula.initialize(results).compute(player.fighter()).getOrDefault(12, 0);
        }

        assertBetween(90, 110, count);
    }

    @Test
    void filterByDiscernment() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 200, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 150, 100));

        player.properties().characteristics().specials().add(SpecialEffects.Type.DISCERNMENT, 60);

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        Map<Integer, Integer> items = formula.initialize(results).compute(player.fighter());

        assertEquals(1, items.size());
        assertEquals(1, (int) items.get(13));
    }

    @Test
    void withMultipleFighters() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 14, 4, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 15, 1, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Arrays.asList(player.fighter(), player.fighter(), player.fighter()),
            Collections.singletonList(monsterFighters.get(0))
        );

        ItemDropFormula.Scope scope = formula.initialize(results);

        Map<Integer, Integer> items = scope.compute(player.fighter());

        assertEquals(2, items.size());
        assertTrue(items.containsKey(12));
        assertTrue(items.containsKey(15));

        items = scope.compute(player.fighter());

        assertEquals(2, items.size());
        assertTrue(items.containsKey(12));
        assertTrue(items.containsKey(13));

        items = scope.compute(player.fighter());

        assertEquals(1, items.size());
        assertTrue(items.containsKey(14));
    }

    @Test
    void withMultipleMonsters() {
        monsterFighters.get(0).reward().items().clear();

        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));
        monsterFighters.get(0).reward().items().add(new MonsterRewardItem(0, 13, 1, 100, 100));
        monsterFighters.get(1).reward().items().add(new MonsterRewardItem(0, 12, 2, 100, 100));

        EndFightResults results = new EndFightResults(
            fight,
            Collections.singletonList(player.fighter()),
            Arrays.asList(monsterFighters.get(0), monsterFighters.get(1))
        );

        Map<Integer, Integer> items = formula.initialize(results).compute(player.fighter());

        assertEquals(2, items.size());
        assertEquals(2, (int) items.get(12));
        assertEquals(1, (int) items.get(13));
    }
}
