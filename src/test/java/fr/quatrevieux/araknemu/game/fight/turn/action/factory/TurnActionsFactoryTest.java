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

package fr.quatrevieux.araknemu.game.fight.turn.action.factory;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.SpellNotFound;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombat;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TurnActionsFactoryTest extends FightBaseCase {
    private Fight fight;
    private TurnActionsFactory factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        factory = new TurnActionsFactory(new FightTurn(player.fighter(), fight, Duration.ofSeconds(30)));
    }

    @Test
    void createActionNotFound() {
        assertThrows(FightException.class, () -> factory.create(ActionType.NONE, new String[] {}));
    }

    @Test
    void createMove() throws Exception {
        player.fighter().move(fight.map().get(185));

        assertInstanceOf(Move.class, factory.create(ActionType.MOVE, new String[] {"ddvfdg"}));
    }

    @Test
    void createCast() throws Exception {
        assertInstanceOf(Cast.class, factory.create(ActionType.CAST, new String[] {"3", "123"}));
    }

    @Test
    void createCastSpellNotFound() throws Exception {
        assertInstanceOf(SpellNotFound.class, factory.create(ActionType.CAST, new String[] {"7458", "123"}));
    }

    @Test
    void createCloseCombat() throws Exception {
        assertInstanceOf(CloseCombat.class, factory.create(ActionType.CLOSE_COMBAT, new String[] {"123"}));
    }

    @Test
    void getters() {
        assertInstanceOf(CastFactory.class, factory.cast());
        assertInstanceOf(CloseCombatFactory.class, factory.closeCombat());
        assertInstanceOf(MoveFactory.class, factory.move());
        assertInstanceOf(SpellConstraintsValidator.class, factory.cast().validator());
    }
}
