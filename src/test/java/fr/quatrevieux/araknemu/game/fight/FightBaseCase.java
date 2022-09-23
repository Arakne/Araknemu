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

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterGroupData;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.module.StatesModule;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.state.FinishState;
import fr.quatrevieux.araknemu.game.fight.state.InitialiseState;
import fr.quatrevieux.araknemu.game.fight.state.NullState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.state.StatesFlow;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.action.factory.FightActionsFactoryRegistry;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
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
import fr.quatrevieux.araknemu.game.player.PlayerData;
import fr.quatrevieux.araknemu.game.player.inventory.slot.WeaponSlot;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.util.ExecutorFactory;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FightBaseCase extends GameBaseCase {
    protected GamePlayer player;
    protected GamePlayer other;
    protected ScheduledExecutorService executor;

    private int lastPlayerId = 5;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        player = gamePlayer(true);
        other  = makeOtherPlayer();
        executor = ExecutorFactory.createSingleThread();
    }

    @Override
    @AfterEach
    public void tearDown() throws ContainerException {
        executor.shutdownNow();

        super.tearDown();
    }

    public Fight createFight(boolean init) throws Exception {
        FightMap map;
        Fight fight = new Fight(
            1,
            new ChallengeType(configuration.fight()),
            map = loadFightMap(10340),
            new ArrayList<>(Arrays.asList(
                createTeam0(map),
                createTeam1(map)
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(false),
                new ActiveState(),
                new FinishState()
            ),
            container.get(Logger.class),
            executor,
            container.get(FightActionsFactoryRegistry.class)
        );

        fight.register(new StatesModule(fight));

        if (init) {
            fight.nextState();
        }

        return fight;
    }

    public Fight createPvmFight() throws Exception {
        FightMap map;
        Fight fight = new Fight(
            1,
            container.get(PvmType.class),
            map = loadFightMap(10340),
            new ArrayList<>(Arrays.asList(
                createTeam0(map),
                createMonsterTeam(map)
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(false),
                new ActiveState(),
                new FinishState()
            ),
            container.get(Logger.class),
            executor,
            container.get(FightActionsFactoryRegistry.class)
        );

        fight.register(new StatesModule(fight));
        fight.nextState();

        return fight;
    }

    public Fight createFight() throws Exception {
        return createFight(true);
    }

    public FightTeam.Factory createTeam0(FightMap map) throws ContainerException {
        return fight -> new SimpleTeam(
            fight,
            makePlayerFighter(player),
            Arrays.asList(map.get(122), map.get(123), map.get(124)),
            0
        );
    }

    public FightTeam.Factory createTeam1(FightMap map) throws ContainerException {
        return fight -> new SimpleTeam(
            fight,
            makePlayerFighter(other),
            Arrays.asList(map.get(125), map.get(126), map.get(127)),
            0
        );
    }

    public FightTeam.Factory createMonsterTeam(FightMap map) throws ContainerException, SQLException {
        MonsterService service = container.get(MonsterService.class);

        dataSet
            .pushMonsterSpells()
            .pushMonsterTemplates()
        ;

        return fight -> new MonsterGroupTeam(
            new MonsterGroup(
                new LivingMonsterGroupPosition(
                    container.get(MonsterGroupFactory.class),
                    container.get(MonsterEnvironmentService.class),
                    container.get(FightService.class),
                    new MonsterGroupData(3, Duration.ofMillis(60000), 4, 3, Arrays.asList(new MonsterGroupData.Monster(31, new Interval(1, 100), 1), new MonsterGroupData.Monster(34, new Interval(1, 100), 1), new MonsterGroupData.Monster(36, new Interval(1, 100), 1)), "", new Position(0, 0), false),
                    new RandomCellSelector(), false
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
            Arrays.asList(map.get(125), map.get(126), map.get(127)),
            1,
            container.get(FighterFactory.class)
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
        return CastScope.simple(castable, caster, target, Collections.singletonList(effect));
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

    public FightBuilder fightBuilder() {
        return new FightBuilder();
    }

    public FightMap loadFightMap(int mapId) {
        return container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(mapId)
        );
    }

    private static int lastFighterId = 10;

    public class FightBuilder {
        private int id = 1;
        private FightMap map;
        private FightType type = new ChallengeType(configuration.fight());
        private StatesFlow states = new StatesFlow(
            new NullState(),
            new InitialiseState(),
            new PlacementState(false),
            new ActiveState(),
            new FinishState()
        );
        private List<List<PlayerFighter>> fightersByTeamNumber = new ArrayList<>();
        private List<FighterBuilder> fighterBuilders = new ArrayList<>();

        public FightBuilder map(int mapId) {
            map = loadFightMap(mapId);

            return this;
        }

        public FightBuilder addSelf(Consumer<FighterBuilder> configurator) {
            return addAlly(builder -> {
                builder.player(player);
                configurator.accept(builder);
            });
        }

        public FightBuilder addAlly(Consumer<FighterBuilder> configurator) {
            try {
                return addToTeam(0, configurator);
            } catch (Exception throwables) {
                throw new RuntimeException(throwables);
            }
        }

        public FightBuilder addEnemy(Consumer<FighterBuilder> configurator) {
            try {
                return addToTeam(1, configurator);
            } catch (Exception throwables) {
                throw new RuntimeException(throwables);
            }
        }

        public FightBuilder addToTeam(int teamNumber, Consumer<FighterBuilder> configurator) throws SQLException, NoSuchFieldException {
            FighterBuilder builder = new FighterBuilder();
            configurator.accept(builder);
            fighterBuilders.add(builder);

            PlayerFighter fighter = builder.build();

            // Create all teams
            while (fightersByTeamNumber.size() <= teamNumber) {
                fightersByTeamNumber.add(new ArrayList<>());
            }

            fightersByTeamNumber.get(teamNumber).add(fighter);

            return this;
        }

        public Fight build(boolean init) {
            final List<FightTeam.Factory> teams = new ArrayList<>();

            for (int teamNumber = 0; teamNumber < fightersByTeamNumber.size(); ++teamNumber) {
                teams.add(buildTeam(teamNumber, fightersByTeamNumber.get(teamNumber)));
            }

            Fight fight = new Fight(
                id,
                type,
                map(),
                teams,
                states,
                container.get(Logger.class),
                executor,
                container.get(FightActionsFactoryRegistry.class)
            );

            fight.register(new StatesModule(fight));
            fight.register(new CommonEffectsModule(fight));
            fight.dispatcher().add(new Listener<FightStarted>() {
                @Override
                public void on(FightStarted event) {
                    fighterBuilders.forEach(FighterBuilder::init);
                }

                @Override
                public Class<FightStarted> event() {
                    return FightStarted.class;
                }
            });

            if (init) {
                fight.nextState();
            }

            return fight;
        }

        private FightTeam.Factory buildTeam(int number, List<PlayerFighter> fighters) {
            final List<FightCell> startPlaces = IntStream.range(10, 300).filter(value -> value % 2 == number)
                .mapToObj(map()::get)
                .collect(Collectors.toList())
            ;

            return fight -> {
                FightTeam team = new SimpleTeam(fight, fighters.get(0), startPlaces, number);
                fighters.stream().skip(1).forEach(team::join);

                return team;
            };
        }

        private FightMap map() {
            if (map != null) {
                return map;
            }

            return map = loadFightMap(10340);
        }

        public class FighterBuilder {
            private GamePlayer player;
            private PlayerFighter fighter;
            private int maxLife = 100;
            private int currentLife = 100;
            private int cell = -1;
            private Map<Integer, Integer> spells = new HashMap<>();
            private Map<Characteristic, Integer> characteristics = new HashMap<>();

            // @todo characteristics

            public FighterBuilder player(GamePlayer player) {
                this.player = player;
                return this;
            }

            public FighterBuilder fighter(PlayerFighter fighter) {
                this.fighter = fighter;
                return this;
            }

            public FighterBuilder maxLife(int maxLife) {
                this.maxLife = maxLife;
                return this;
            }

            public FighterBuilder currentLife(int currentLife) {
                this.currentLife = currentLife;
                return this;
            }

            public FighterBuilder cell(int cell) {
                this.cell = cell;
                return this;
            }

            public FighterBuilder spells(int... spellIds) {
                for (int spellId : spellIds) {
                    spells.put(spellId, 1);
                }

                return this;
            }

            public FighterBuilder spell(int id) {
                return spell(id, 1);
            }

            public FighterBuilder spell(int id, int level) {
                spells.put(id, level);

                return this;
            }

            public FighterBuilder charac(Characteristic characteristic, int value) {
                characteristics.put(characteristic, value);

                return this;
            }

            public PlayerFighter build() throws SQLException, NoSuchFieldException {
                if (fighter == null) {
                    if (player == null) {
                        player = makeSimpleGamePlayer(++lastFighterId);
                    }

                    final PlayerData properties = player.properties();

                    properties.characteristics().base().add(Characteristic.VITALITY, maxLife - properties.life().max());
                    properties.life().rebuild();
                    properties.life().set(currentLife);

                    final SpellBook spellBook = properties.spells();
                    final SpellService service = container.get(SpellService.class);

                    spells.forEach((id, level) -> {
                        if (!spellBook.has(id)) {
                            spellBook.learn(service.get(id));
                        }

                        spellBook.entry(id).entity().setLevel(level);
                    });

                    characteristics.forEach((characteristic, value) -> {
                        properties.characteristics().base().set(characteristic, value);
                    });

                    fighter = new PlayerFighter(player);
                }

                return fighter;
            }

            public void init() {
                if (cell != -1) {
                    fighter.move(fighter.fight().map().get(cell));
                }
            }
        }
    }
}
