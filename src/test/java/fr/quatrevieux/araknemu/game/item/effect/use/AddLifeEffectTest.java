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

package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddLifeEffectTest extends GameBaseCase {
    private ExplorationPlayer player;
    private AddLifeEffect effect;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);
        player = explorationPlayer();

        effect = new AddLifeEffect();

        requestStack.clear();
    }

    @Test
    void applyFixed() {
        player.player().properties().life().set(100);
        requestStack.clear();

        effect.apply(new UseEffect(effect, Effect.ADD_LIFE, new int[] {10, 0, 0}), player);

        assertEquals(110, player.properties().life().current());
        requestStack.assertAll(
            new Stats(player.properties()),
            Information.heal(10)
        );
    }

    @Test
    void applyRandom() {
        player.player().properties().life().set(100);
        requestStack.clear();

        effect.apply(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player);

        assertBetween(101, 110, player.properties().life().current());
        requestStack.assertAll(
            new Stats(player.properties()),
            Information.heal(player.properties().life().current() - 100)
        );
    }

    @Test
    void applyToTarget() throws Exception {
        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());
        target.player().properties().life().set(10);

        effect.applyToTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {10, 0, 0}), player, target, null);

        assertEquals(20, target.properties().life().current());
    }

    @Test
    void applyToFighter() throws Exception {
        GamePlayer target = makeOtherPlayer();
        target.properties().life().set(10);

        PlayerFighter fighter = container.get(FighterFactory.class).create(target);

        effect.applyToFighter(new UseEffect(effect, Effect.ADD_LIFE, new int[] {10, 0, 0}), fighter);

        assertEquals(20, target.properties().life().current());
        assertEquals(20, fighter.life().current());
    }

    @Test
    void checkFullLife() {
        assertFalse(effect.check(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player));
    }

    @Test
    void checkOk() {
        player.player().properties().life().set(10);

        assertTrue(effect.check(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player));
    }

    @Test
    void checkTargetFullLife() throws Exception {
        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());

        assertTrue(effect.checkTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player, target, null));
    }

    @Test
    void checkTargetNotPlayer() {
        assertFalse(effect.checkTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player, null, null));
    }

    @Test
    void checkTargetOk() throws Exception {
        ExplorationPlayer target = new ExplorationPlayer(makeOtherPlayer());
        target.player().properties().life().set(10);

        assertTrue(effect.checkTarget(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), player, target, null));
    }

    @Test
    void checkFighterFullLife() throws SQLException, ContainerException {
        PlayerFighter fighter = container.get(FighterFactory.class).create(gamePlayer());

        assertFalse(effect.checkFighter(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), fighter));
    }

    @Test
    void checkFighterOk() throws SQLException, ContainerException {
        gamePlayer().properties().life().set(10);
        PlayerFighter fighter = container.get(FighterFactory.class).create(gamePlayer());

        assertTrue(effect.checkFighter(new UseEffect(effect, Effect.ADD_LIFE, new int[] {1, 10, 0}), fighter));
    }
}
