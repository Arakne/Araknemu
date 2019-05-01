package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class MonsterFighterSpriteTest extends FightBaseCase {
    private MonsterFighterSprite sprite;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushMonsterTemplates()
            .pushMonsterSpells()
        ;

        MonsterService service = container.get(MonsterService.class);

        MonsterGroupTeam team = new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(FightService.class),
                    new MonsterGroupPosition(new Position(10340, -1), 3),
                    new MonsterGroupData(3, 60000, 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100)), new MonsterGroupData.Monster(34, new Interval(1, 100)), new MonsterGroupData.Monster(36, new Interval(1, 100))), "")
                ),
                5,
                Collections.singletonList(service.load(31).all().get(2)),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123)
            ),
            Collections.singletonList(123),
            1
        );

        MonsterFighter fighter = (MonsterFighter) team.fighters().stream().findFirst().get();
        sprite = new MonsterFighterSprite(fighter, service.load(31).all().get(2));

        Fight fight = createFight();
        fighter.joinFight(fight, fight.map().get(123));
    }

    @Test
    void generate() {
        assertEquals("123;1;0;-1;31;-2;1563^100;3;-1;-1;-1;0,0,0,0;20;4;2;3;7;7;-7;-7;7;5;1", sprite.toString());
    }

    @Test
    void values() {
        assertEquals(-1, sprite.id());
        assertEquals(123, sprite.cell());
        assertEquals(Direction.SOUTH_EAST, sprite.orientation());
        assertEquals(Sprite.Type.MONSTER, sprite.type());
        assertEquals("31", sprite.name());
    }
}
