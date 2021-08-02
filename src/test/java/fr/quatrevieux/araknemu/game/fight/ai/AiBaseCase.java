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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.DummyGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.factory.ChainAiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.module.AiModule;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class AiBaseCase extends FightBaseCase {
    protected Fighter fighter;
    protected Fight fight;

    protected ActionGenerator action;
    protected FighterAI ai;

    protected FightTurn turn;
    protected Action lastAction;

    public void configureFight(Consumer<FightBuilder> configurator) {
        if (fight != null) {
            fight.cancel(true);
        }

        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        fight.register(new AiModule(new ChainAiFactory()));
        fight.register(new CommonEffectsModule(fight));

        fighter = player.fighter();
        fight.nextState();

        fight.turnList().start();

        ai = new FighterAI(fighter, fight, new ActionGenerator[] { new DummyGenerator() });
        ai.start(turn = fight.turnList().current().get());
        action.initialize(ai);
    }

    public Optional<Action> generateAction() {
        lastAction = null;
        final Optional<Action> generated = action.generate(ai);

        generated.ifPresent(a -> lastAction = a);

        return generated;
    }

    public <T> T generateAndValidateAction(Class<T> type) {
        Optional<Action> result = generateAction();

        assertTrue(result.isPresent());
        assertInstanceOf(type, result.get());

        return (T) result.get();
    }

    public Cast generateCast() {
        return generateAndValidateAction(Cast.class);
    }

    public Move generateMove() {
        return generateAndValidateAction(Move.class);
    }

    public Move generateAndPerformMove() {
        final Move action = generateMove();

        performGeneratedAction();

        return action;
    }

    public void performGeneratedAction() {
        assertNotNull(lastAction, "No action has been generated");
        turn.perform(lastAction);
        turn.terminate();
    }

    public void assertCast(int spellId, int targetCell) {
        Cast cast = generateCast();

        assertEquals(spellId, cast.spell().id(), "Invalid spell id");
        assertEquals(targetCell, cast.target().id(), "Invalid target cell");
    }

    public void assertInCastEffectArea(int... cellIds) {
        Set<Integer> targetCells = getCastEffectAreaCellIds();

        for (int cellId : cellIds) {
            assertTrue(targetCells.contains(cellId), "The cell " + cellId + " is not in the effect area");
        }
    }

    public void assertNotInCastEffectArea(int... cellIds) {
        Set<Integer> targetCells = getCastEffectAreaCellIds();

        for (int cellId : cellIds) {
            assertFalse(targetCells.contains(cellId), "The cell " + cellId + " is in the effect area");
        }
    }

    public Set<Integer> getCastEffectAreaCellIds() {
        assertNotNull(lastAction, "No action has been generated");
        Cast cast = (Cast) lastAction;

        return cast.spell().effects().stream()
            .map(SpellEffect::area)
            .flatMap(area -> area.resolve(cast.caster().cell(), cast.target()).stream())
            .map(FightCell::id)
            .collect(Collectors.toSet())
        ;
    }

    public void assertDotNotGenerateAction() {
        generateAction().ifPresent(generated -> fail("An action has been generated : " + generated));
    }

    public Fighter[] getEnemies() {
        return fight.teams().get(1).fighters().toArray(new Fighter[0]);
    }

    public Fighter getEnemy(int number) {
        return getEnemies()[number];
    }

    public Fighter[] getAllies() {
        return fight.teams().get(0).fighters().toArray(new Fighter[0]);
    }

    public Fighter getAlly(int number) {
        return getAllies()[number];
    }

    public void removeAllMP() {
        turn.points().removeMovementPoints(turn.points().movementPoints());
    }

    public void removeAllAP() {
        turn.points().removeActionPoints(turn.points().actionPoints());
    }

    public void setAP(int points) {
        final int toAdd = points - turn.points().actionPoints();

        if (toAdd == 0) {
            return;
        }

        if (toAdd > 0) {
            turn.points().addActionPoints(toAdd);
        } else {
            turn.points().removeActionPoints(-toAdd);
        }
    }

    /**
     * Remove a spell from current fighter spell list
     * Use this method to ensure that the given spell will not be used
     *
     * Note: this method should be called after the fight has been built
     *
     * @param spellId The spell id
     */
    public void removeSpell(int spellId) throws NoSuchFieldException, IllegalAccessException {
        SpellBook spells = (SpellBook) fighter.spells();
        Field field = spells.getClass().getDeclaredField("entries");
        field.setAccessible(true);

        ((Map) field.get(spells)).remove(spellId);
    }
}
