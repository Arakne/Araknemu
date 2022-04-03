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

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FireworkEffectTest extends GameBaseCase {
    private FireworkEffect effect;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);
        player = explorationPlayer();

        effect = new FireworkEffect();

        requestStack.clear();
    }

    @Test
    void check() {
        assertFalse(effect.check(new UseEffect(effect, Effect.FIREWORK , new int[] {2900, 0, 6}), player));
        assertFalse(effect.checkTarget(new UseEffect(effect, Effect.FIREWORK , new int[] {2900, 0, 6}), player, null, null));
        assertTrue(effect.checkTarget(new UseEffect(effect, Effect.FIREWORK , new int[] {2900, 0, 6}), player, null, player.map().get(150)));
    }

    @Test
    void applyTarget() {
        effect.applyToTarget(new UseEffect(effect, Effect.FIREWORK , new int[] {6, 0, 2900}), player, null, player.map().get(150));

        requestStack.assertLast(
            new GameActionResponse("1", ActionType.FIREWORK, player.id(), "150,2900,11,8,6")
        );

        assertTrue(player.interactions().busy());
    }
}