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

package fr.quatrevieux.araknemu.game.fight.castable.weapon;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WeaponConstraintsValidatorTest extends FightBaseCase {
    private Fight fight;
    private PlayableFighter fighter;
    private FightTurn turn;
    private WeaponConstraintsValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushWeaponTemplates()
            .pushItemSets()
        ;

        fight = createFight();
        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        fighter.move(fight.map().get(235));

        validator = new WeaponConstraintsValidator();
    }

    @Test
    void success() {
        CastableWeapon weapon = weapon(40);

        assertTrue(validator.check(turn, weapon, fight.map().get(221)));
        assertNull(validator.validate(turn, weapon, fight.map().get(221)));
    }

    @Test
    void error() {
        CastableWeapon weapon = weapon(40);

        assertFalse(validator.check(turn, weapon, fight.map().get(186)));
        assertEquals(
            Error.cantCastBadRange(new Interval(1, 1), 18).toString(),
            validator.validate(turn, weapon, fight.map().get(186)).toString()
        );
    }

    private CastableWeapon weapon(int id) {
        return new CastableWeapon((Weapon) container.get(ItemService.class).create(id));
    }
}
