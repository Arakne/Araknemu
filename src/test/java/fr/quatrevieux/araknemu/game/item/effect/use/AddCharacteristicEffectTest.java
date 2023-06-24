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

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import fr.quatrevieux.araknemu.network.game.out.object.InventoryWeight;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddCharacteristicEffectTest extends GameBaseCase {
    private ExplorationPlayer player;
    private AddCharacteristicEffect effect;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        gamePlayer(true);
        player = explorationPlayer();

        effect = new AddCharacteristicEffect(Characteristic.WISDOM);
        requestStack.clear();
    }

    @Test
    void applyFixedValue() {
        effect.apply(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {2, 0, 0}), player);

        assertEquals(2, player.properties().characteristics().base().get(Characteristic.WISDOM));
        requestStack.assertLast(
            Information.characteristicBoosted(Characteristic.WISDOM, 2)
        );
    }

    @Test
    void applyRandomValue() {
        effect.apply(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {1, 10, 0}), player);

        int value = player.properties().characteristics().base().get(Characteristic.WISDOM);
        assertBetween(1, 10, value);

        requestStack.assertAll(
            new Stats(player.properties()),
            new InventoryWeight(player.player()),
            Information.characteristicBoosted(Characteristic.WISDOM, value)
        );
    }

    @Test
    void applyNoMessage() {
        effect = new AddCharacteristicEffect(Characteristic.RESISTANCE_ACTION_POINT);
        effect.apply(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {10, 0, 0}), player);

        assertEquals(10, player.properties().characteristics().base().get(Characteristic.RESISTANCE_ACTION_POINT));
        requestStack.assertAll(
            new Stats(player.properties()),
            new InventoryWeight(player.player())
        );
    }

    @Test
    void check() {
        assertTrue(effect.check(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {2, 0, 0}), player));
        assertFalse(effect.checkTarget(new UseEffect(null, Effect.ADD_CHARACT_WISDOM, new int[] {2, 0, 0}), player, null, null));
    }
}
