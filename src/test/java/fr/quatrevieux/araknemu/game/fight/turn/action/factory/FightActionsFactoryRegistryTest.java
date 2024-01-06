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
import fr.quatrevieux.araknemu.game.fight.castable.closeCombat.CloseCombatValidator;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.CastFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.SpellNotFound;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombat;
import fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat.CloseCombatFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FightActionsFactoryRegistryTest extends FightBaseCase {
    private Fight fight;
    private FightActionsFactoryRegistry factory;
    private PlayableFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();
        factory = new FightActionsFactoryRegistry(
            container.get(fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFactory.class),
            new CastFactory(
                new SpellConstraintsValidator(fight),
                container.get(CriticalityStrategy.class)
            ),
            new CloseCombatFactory(
                new CloseCombatValidator(fight),
                container.get(CriticalityStrategy.class)
            )
        );
    }

    @Test
    void createActionNotFound() {
        assertThrows(FightException.class, () -> factory.create(fighter, ActionType.NONE, new String[] {}));
    }

    @Test
    void createMove() throws Exception {
        player.fighter().move(fight.map().get(185));

        assertInstanceOf(Move.class, factory.create(fighter, ActionType.MOVE, new String[] {"ddvfdg"}));
        assertThrows(IllegalArgumentException.class, () -> factory.create(fighter, ActionType.MOVE, new String[] {}));
    }

    @Test
    void createCast() throws Exception {
        assertInstanceOf(Cast.class, factory.create(fighter, ActionType.CAST, new String[] {"3", "123"}));
        assertThrows(IllegalArgumentException.class, () -> factory.create(fighter, ActionType.CAST, new String[] {"3"}));
        assertThrows(IllegalArgumentException.class, () -> factory.create(fighter, ActionType.CAST, new String[] {"3", "1000"}));
    }

    @Test
    void createCastSpellNotFound() throws Exception {
        assertInstanceOf(SpellNotFound.class, factory.create(fighter, ActionType.CAST, new String[] {"7458", "123"}));
    }

    @Test
    void createCloseCombat() throws Exception {
        assertInstanceOf(CloseCombat.class, factory.create(fighter, ActionType.CLOSE_COMBAT, new String[] {"123"}));
        assertThrows(IllegalArgumentException.class, () -> factory.create(fighter, ActionType.CLOSE_COMBAT, new String[] {}));
        assertThrows(IllegalArgumentException.class, () -> factory.create(fighter, ActionType.CLOSE_COMBAT, new String[] {"1000"}));
    }

    @Test
    void getters() {
        assertInstanceOf(CastFactory.class, factory.cast());
        assertInstanceOf(CloseCombatFactory.class, factory.closeCombat());
        assertInstanceOf(MoveFactory.class, factory.move());
        assertInstanceOf(SpellConstraintsValidator.class, factory.cast().validator());
    }
}
