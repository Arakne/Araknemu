/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.BuffHook;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        FightMap map = loadFightMap(10340);

        MonsterGroupTeam team = new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector(), false
                ),
                5,
                Collections.singletonList(service.load(31).all().get(2)),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123),
                new Position(0, 0)
            ),
            Collections.singletonList(map.get(123)),
            1,
            container.get(FighterFactory.class)
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

        characteristics.alterDiscernment(10);
        assertEquals(0, characteristics.discernment());

        characteristics.alterDiscernment(-10);
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

    @Test
    void initial() {
        assertEquals(90, characteristics.initial().get(Characteristic.STRENGTH));

        characteristics.alter(Characteristic.STRENGTH, 10);
        assertEquals(90, characteristics.initial().get(Characteristic.STRENGTH));
    }
}
