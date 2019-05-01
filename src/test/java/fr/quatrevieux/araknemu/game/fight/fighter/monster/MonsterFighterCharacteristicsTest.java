package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class MonsterFighterCharacteristicsTest extends FightBaseCase {
    private MonsterFighterCharacteristics characteristics;
    private MonsterFighter fighter;

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

        fighter = (MonsterFighter) team.fighters().stream().findFirst().get();
        Fight fight = createFight();

        fighter.joinFight(fight, fight.map().get(123));

        characteristics = new MonsterFighterCharacteristics(service.load(31).all().get(2), fighter);
    }

    @Test
    void initiative() {
        assertEquals(35, characteristics.initiative());
    }

    @Test
    void discernment() {
        assertEquals(0, characteristics.discernment());
    }

    @Test
    void get() {
        assertEquals(90, characteristics.get(Characteristic.STRENGTH));
        assertEquals(4, characteristics.get(Characteristic.ACTION_POINT));
        assertEquals(2, characteristics.get(Characteristic.MOVEMENT_POINT));
    }

    @Test
    void alter() {
        AtomicReference<FighterCharacteristicChanged> ref = new AtomicReference<>();
        fighter.dispatcher().add(FighterCharacteristicChanged.class, ref::set);

        characteristics.alter(Characteristic.STRENGTH, 10);
        assertEquals(100, characteristics.get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(10, ref.get().value());

        characteristics.alter(Characteristic.STRENGTH, -10);
        assertEquals(90, characteristics.get(Characteristic.STRENGTH));

        assertEquals(Characteristic.STRENGTH, ref.get().characteristic());
        assertEquals(-10, ref.get().value());
    }
}
