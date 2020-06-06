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

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupPosition;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.PvmBuilder;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.module.StatesModule;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import fr.quatrevieux.araknemu.game.monster.environment.LivingMonsterGroupPosition;
import fr.quatrevieux.araknemu.game.monster.environment.MonsterEnvironmentService;
import fr.quatrevieux.araknemu.game.monster.environment.RandomCellSelector;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroupFactory;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FightBaseCase extends GameBaseCase {
    protected GamePlayer player;
    protected GamePlayer other;

    private int lastPlayerId = 5;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        player = gamePlayer(true);
        other  = makeOtherPlayer();
    }

    public Fight createFight(boolean init) throws Exception {
        Fight fight = new Fight(
            1,
            new ChallengeType(),
            container.get(FightService.class).map(
                container.get(ExplorationMapService.class).load(10340)
            ),
            new ArrayList<>(Arrays.asList(
                createTeam0(),
                createTeam1()
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(false),
                new ActiveState(),
                new FinishState()
            ),
            container.get(Logger.class)
        );

        fight.register(new StatesModule(fight));

        if (init) {
            fight.nextState();
        }

        return fight;
    }

    public Fight createPvmFight() throws Exception {
        Fight fight = new Fight(
            1,
            container.get(PvmType.class),
            container.get(FightService.class).map(
                container.get(ExplorationMapService.class).load(10340)
            ),
            new ArrayList<>(Arrays.asList(
                createTeam0(),
                createMonsterTeam()
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(false),
                new ActiveState(),
                new FinishState()
            ),
            container.get(Logger.class)
        );

        fight.register(new StatesModule(fight));
        fight.nextState();

        return fight;
    }

    public Fight createFight() throws Exception {
        return createFight(true);
    }

    public FightTeam createTeam0() throws ContainerException {
        return new SimpleTeam(
            makePlayerFighter(player),
            Arrays.asList(122, 123, 124),
            0
        );
    }

    public FightTeam createTeam1() throws ContainerException {
        return new SimpleTeam(
            makePlayerFighter(other),
            Arrays.asList(125, 126, 127),
            0
        );
    }

    public FightTeam createMonsterTeam() throws ContainerException, SQLException {
        MonsterService service = container.get(MonsterService.class);

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        return new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector()
                ),
                5,
                Arrays.asList(
                    service.load(31).all().get(2),
                    service.load(34).all().get(3)
                ),
                Direction.WEST,
                container.get(ExplorationMapService.class).load(10340).get(123),
                new Position(0, 0)
            ),
            Arrays.asList(125, 126, 127),
            1
        );
    }

    public Fight createSimpleFight(ExplorationMap map) throws ContainerException, SQLException {
        GamePlayer player1 = makeSimpleGamePlayer(++lastPlayerId);
        GamePlayer player2 = makeSimpleGamePlayer(++lastPlayerId);

        return container.get(FightService.class)
            .handler(ChallengeBuilder.class)
            .start(builder -> builder
                .map(map)
                .fighter(player1)
                .fighter(player2)
            )
        ;
    }

    public void equipWeapon(GamePlayer player, int weaponId) throws ContainerException, InventoryException, SQLException {
        dataSet.pushItemTemplates();
        Item weapon = container.get(ItemService.class).create(weaponId);

        player.inventory().add(weapon, 1, WeaponSlot.SLOT_ID);
    }

    public void equipWeapon(GamePlayer player) throws ContainerException, InventoryException, SQLException {
        equipWeapon(player, 40);
    }

    public CastScope makeCastScope(Fighter caster, Castable castable, SpellEffect effect, FightCell target) {
        return new CastScope(castable, caster, target).withEffects(Collections.singletonList(effect));
    }

    public CastScope makeCastScopeForEffect(int effectId) {
        return makeCastScopeForEffect(effectId, player.fighter(), other.fighter().cell());
    }

    public CastScope makeCastScopeForEffect(int effectId, Fighter caster, FightCell target) {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(effectId);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        return makeCastScope(caster, spell, effect, target);
    }

    public PlayerFighter makePlayerFighter(GamePlayer player) throws ContainerException {
        return container.get(FighterFactory.class).create(player);
    }
}
