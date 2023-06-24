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
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.state.FinishState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFailed;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastSuccess;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombat;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatSuccess;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.validators.FightPathValidator;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.FightEnd;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.FightAction;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FighterTurnOrder;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.FinishTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.StartTurn;
import fr.quatrevieux.araknemu.network.game.out.fight.turn.TurnMiddle;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
public class FunctionalTest extends GameBaseCase {
    private FightService service;
    private Fight fight;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        service = container.get(FightService.class);
    }

    @RepeatedIfExceptionsTest
    void challengeFight() throws Exception {
        dataSet.pushWeaponTemplates();
        FightHandler<ChallengeBuilder> handler = service.handler(ChallengeBuilder.class);

        GamePlayer player = gamePlayer(true);
        GamePlayer other = makeOtherPlayer();

        // Equip a weapon
        other.inventory().add(container.get(ItemService.class).create(88), 1, 1);

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        fight = handler.start(
            builder -> {
                builder
                    .fighter(player)
                    .fighter(other)
                    .map(map)
                ;
            }
        );

        assertTrue(player.isFighting());
        assertTrue(other.isFighting());

        assertInstanceOf(PlacementState.class, fight.state());

        PlayerFighter fighter1, fighter2;

        if (player.fighter().team().number() == 0) {
            fighter1 = player.fighter();
            fighter2 = other.fighter();
        } else {
            fighter1 = other.fighter();
            fighter2 = player.fighter();
        }

        requestStack.assertOne(new JoinFight(fight));
        requestStack.assertOne(new AddSprites(Arrays.asList(fighter1.sprite(), fighter2.sprite())));

        if (fighter1.cell().id() != 185) {
            fight.state(PlacementState.class).changePlace(fighter1, fight.map().get(185));
            requestStack.assertLast(new FighterPositions(Arrays.asList(fighter1, fighter2)));
        }

        if (fighter2.cell().id() != 150) {
            fight.state(PlacementState.class).changePlace(fighter2, fight.map().get(150));
            requestStack.assertLast(new FighterPositions(Arrays.asList(fighter1, fighter2)));
        }

        assertEquals(185, fighter1.cell().id());
        assertEquals(150, fighter2.cell().id());

        fighter1.setReady(true);
        requestStack.assertLast(new FighterReadyState(fighter1));
        assertInstanceOf(PlacementState.class, fight.state());

        fighter1.setReady(false);
        requestStack.assertLast(new FighterReadyState(fighter1));

        fighter2.setReady(true);
        requestStack.assertLast(new FighterReadyState(fighter2));
        assertInstanceOf(PlacementState.class, fight.state());

        fighter1.setReady(true);
        assertInstanceOf(ActiveState.class, fight.state());
        requestStack.assertOne(new BeginFight());
        requestStack.assertOne(new FighterTurnOrder(fight.turnList()));

        Thread.sleep(300);
        requestStack.assertOne(new TurnMiddle(fight.fighters()));
        requestStack.assertOne(new StartTurn(fight.turnList().current().get()));
        requestStack.clear();

        assertSame(player.fighter(), fight.turnList().current().get().fighter());
        assertNotNull(player.fighter().turn());

        FightTurn currentTurn = player.fighter().turn();
        currentTurn.stop();

        requestStack.assertAll(
            new FinishTurn(currentTurn),
            new TurnMiddle(fight.fighters()),
            new StartTurn(currentTurn = fight.turnList().current().get())
        );

        assertSame(other.fighter(), currentTurn.fighter());

        if (currentTurn.fighter() != fighter1) {
            currentTurn.stop();
        }

        requestStack.clear();

        Move move = new Move(
            fighter1,
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(185), Direction.EAST),
                    new PathStep<>(fight.map().get(199), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(213), Direction.SOUTH_WEST),
                    new PathStep<>(fight.map().get(198), Direction.NORTH_WEST)
                )
            ),
            new FightPathValidator[0]
        );

        fighter1.turn().perform(move);

        requestStack.assertOne(
            new FightAction(
                new ActionResult() {
                    @Override
                    public int action() {
                        return 1;
                    }

                    @Override
                    public PlayableFighter performer() {
                        return fighter1;
                    }

                    @Override
                    public Object[] arguments() {
                        return new Object[] {"ac5ddvfdg"};
                    }

                    @Override
                    public boolean success() {
                        return true;
                    }

                    @Override
                    public boolean secret() {
                        return false;
                    }

                    @Override
                    public void apply(FightTurn turn) {

                    }
                }
            )
        );

        fighter1.turn().terminate();
        requestStack.assertOne(ActionEffect.usedMovementPoints(fighter1, 3));
        assertEquals(198, fighter1.cell().id());
        assertEquals(Direction.NORTH_WEST, fighter1.orientation());
        assertEquals(0, fighter1.turn().points().movementPoints());

        fighter1.turn().stop();

        move = new Move(
            fighter2,
            new Path<>(
                fight.map().decoder(),
                Arrays.asList(
                    new PathStep<>(fight.map().get(150), Direction.SOUTH_EAST),
                    new PathStep<>(fight.map().get(195), Direction.SOUTH_EAST)
                )
            ),
            new FightPathValidator[0]
        );

        fighter2.turn().perform(move);
        fighter2.turn().terminate();
        assertEquals(195, fighter2.cell().id());
        assertEquals(Direction.SOUTH_EAST, fighter2.orientation());

        fight.turnList().current()
            .filter(turn -> turn.fighter() != player.fighter())
            .ifPresent(FightTurn::stop)
        ;

        castNormal(3, other.fighter().cell());

        requestStack.assertLast(
            new FightAction(
                new CastSuccess(
                    new Cast(null, null, null, null, null),
                    player.fighter(),
                    player.fighter().spells().get(3),
                    other.fighter().cell(),
                    false
                )
            )
        );

        player.fighter().turn().terminate();

        assertEquals(1, player.fighter().turn().points().actionPoints());
        requestStack.assertOne(ActionEffect.usedActionPoints(player.fighter(), 5));

        int damage = other.fighter().life().max() - other.fighter().life().current();

        assertBetween(4, 15, damage);
        requestStack.assertOne(ActionEffect.alterLifePoints(player.fighter(), other.fighter(), -damage));

        nextTurn();

        castCritical(3, player.fighter().cell());
        requestStack.assertLast(
            new FightAction(
                new CastSuccess(
                    null,
                    other.fighter(),
                    other.fighter().spells().get(3),
                    player.fighter().cell(),
                    false
                )
            )
        );

        other.fighter().turn().terminate();
        requestStack.assertOne(ActionEffect.criticalHitSpell(other.fighter(), other.fighter().spells().get(3)));
        requestStack.assertOne(ActionEffect.usedActionPoints(other.fighter(), 5));
        requestStack.assertOne(ActionEffect.alterLifePoints(other.fighter(), player.fighter(), -8));
        assertEquals(player.fighter().life().max() - 8, player.fighter().life().current());

        nextTurn();
        requestStack.clear();

        castFailed(3, other.fighter().cell());
        requestStack.assertAll(
            new FightAction(new CastFailed(player.fighter(), player.fighter().spells().get(3))),
            ActionEffect.usedActionPoints(player.fighter(), 5)
        );

        nextTurn();

        // Close combat
        other.fighter().turn().perform(
            new CloseCombat(
                other.fighter(),
                player.fighter().cell(),
                new WeaponConstraintsValidator(fight),
                new CriticalityStrategy() {
                    public int hitRate(ActiveFighter fighter, int base) { return 0; }
                    public int failureRate(ActiveFighter fighter, int base) { return 0; }
                    public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                    public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
                }
            )
        );

        requestStack.assertLast(
            new FightAction(
                new CloseCombatSuccess(
                    other.fighter(),
                    other.fighter().weapon(),
                    player.fighter().cell(),
                    false
                )
            )
        );

        int baseLife = player.fighter().life().current();

        other.fighter().turn().terminate();

        assertEquals(2, other.fighter().turn().points().actionPoints());
        requestStack.assertOne(ActionEffect.usedActionPoints(other.fighter(), 4));

        damage = baseLife - player.fighter().life().current();

        assertBetween(1, 4, damage);
        requestStack.assertOne(ActionEffect.alterLifePoints(other.fighter(), player.fighter(), -damage));
        nextTurn();

        assertThrows(FightException.class, () -> castNormal(3, fight.map().get(95)));
        requestStack.assertLast(Error.cantCastBadRange(new Interval(1, 6), 7));
        nextTurn();

        castNormal(6, other.fighter().cell());
        other.fighter().turn().terminate();

        assertThrows(FightException.class, () -> castNormal(6, other.fighter().cell())); // Cooldown

        // Skip 5 turns
        for (int i = 0; i < 5; ++i) {
            nextTurn();
            nextTurn();
        }

        castNormal(6, other.fighter().cell());
        other.fighter().turn().terminate();

        for (;;) {
            nextTurn();
            requestStack.clear();

            cast(3, other.fighter().cell());
            player.fighter().turn().terminate();

            if (other.fighter().dead()) {
                break;
            }

            nextTurn();
        }

        requestStack.assertLast(ActionEffect.fighterDie(player.fighter(), other.fighter()));

        Fighter winner = player.fighter();
        Fighter looser = other.fighter();

        Thread.sleep(1550); // Wait for finish state

        assertInstanceOf(FinishState.class, fight.state());
        requestStack.assertLast(
            new FightEnd(
                new FightRewardsSheet(
                    new EndFightResults(fight, Collections.singletonList(winner), Collections.singletonList(looser)),
                    FightRewardsSheet.Type.NORMAL,
                    Arrays.asList(
                        new DropReward(RewardType.WINNER, winner, Collections.emptyList()),
                        new DropReward(RewardType.LOOSER, looser, Collections.emptyList())
                    )
                )
            )
        );

        assertFalse(player.isFighting());
        assertFalse(other.isFighting());
    }

    private void cast(int spellId, FightCell target) {
        FightTurn currentTurn = fight.turnList().current().get();

        currentTurn.perform(new Cast(
            currentTurn.fighter(),
            currentTurn.fighter().spells().get(spellId),
            target,
            new SpellConstraintsValidator(fight),
            new BaseCriticalityStrategy()
        ));
    }

    private void castNormal(int spellId, FightCell target) {
        FightTurn currentTurn = fight.turnList().current().get();

        currentTurn.perform(new Cast(
            currentTurn.fighter(),
            currentTurn.fighter().spells().get(spellId),
            target,
            new SpellConstraintsValidator(fight),

            // Ensure no critical hit / fail
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        ));
    }

    private void castCritical(int spellId, FightCell target) {
        FightTurn currentTurn = fight.turnList().current().get();

        currentTurn.perform(new Cast(
            currentTurn.fighter(),
            currentTurn.fighter().spells().get(spellId),
            target,
            new SpellConstraintsValidator(fight),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return true; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        ));
    }

    private void castFailed(int spellId, FightCell target) {
        FightTurn currentTurn = fight.turnList().current().get();

        currentTurn.perform(new Cast(
            currentTurn.fighter(),
            currentTurn.fighter().spells().get(spellId),
            target,
            new SpellConstraintsValidator(fight),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return true; }
            }
        ));
    }

    private void nextTurn() {
        fight.turnList().current().ifPresent(FightTurn::stop);
    }
}
