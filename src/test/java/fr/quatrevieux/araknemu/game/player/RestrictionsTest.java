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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.RestrictionsChanged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestrictionsTest extends GameBaseCase {
    private Restrictions restrictions;
    private ListenerAggregate dispatcher;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        restrictions = new Restrictions(
            dispatcher = new DefaultListenerAggregate()
        );
    }

    @Test
    void initDefaults() throws SQLException, ContainerException {
        restrictions.init(gamePlayer());

        assertTrue(restrictions.canAssault());
        assertTrue(restrictions.canChallenge());
        assertTrue(restrictions.canExchange());
        assertFalse(restrictions.canAttack());
        assertTrue(restrictions.canChat());
        assertFalse(restrictions.canBeMerchant());
        assertTrue(restrictions.canUseObject());
        assertTrue(restrictions.canInteractCollector());
        assertTrue(restrictions.canUseIO());
        assertTrue(restrictions.canSpeakNPC());
        assertFalse(restrictions.canAttackDungeonWhenMutant());
        assertTrue(restrictions.canMoveAllDirections());
        assertFalse(restrictions.canAttackMonsterWhenMutant());
        assertTrue(restrictions.canInteractWithPrism());

        assertEquals(8192 | 32, restrictions.toInt());
    }

    @Test
    void set() {
        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();
        dispatcher.add(RestrictionsChanged.class, ref::set);

        restrictions.set(
            Restrictions.Restriction.DENY_ASSAULT,
            Restrictions.Restriction.ALLOW_ATTACK
        );

        assertTrue(restrictions.canAttack());
        assertFalse(restrictions.canAssault());

        assertSame(restrictions, ref.get().restrictions());
    }

    @Test
    void setAlreadySetShouldNotDispatchEvent() {
        restrictions.set(Restrictions.Restriction.DENY_ASSAULT);

        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();
        dispatcher.add(RestrictionsChanged.class, ref::set);

        restrictions.set(Restrictions.Restriction.DENY_ASSAULT);

        assertNull(ref.get());
    }

    @Test
    void unset() {
        restrictions.set(Restrictions.Restriction.DENY_ASSAULT);

        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();
        dispatcher.add(RestrictionsChanged.class, ref::set);

        restrictions.unset(
            Restrictions.Restriction.DENY_ASSAULT,
            Restrictions.Restriction.ALLOW_ATTACK
        );

        assertFalse(restrictions.canAttack());
        assertTrue(restrictions.canAssault());

        assertSame(restrictions, ref.get().restrictions());
    }

    @Test
    void unsetNotYetSetShouldNotDispatchEvent() {
        AtomicReference<RestrictionsChanged> ref = new AtomicReference<>();
        dispatcher.add(RestrictionsChanged.class, ref::set);

        restrictions.unset(Restrictions.Restriction.DENY_ASSAULT);

        assertNull(ref.get());
    }

    @Test
    void canAssault() {
        assertTrue(restrictions.canAssault());

        restrictions.set(Restrictions.Restriction.DENY_ASSAULT);

        assertFalse(restrictions.canAssault());
    }

    @Test
    void canChallenge() {
        assertTrue(restrictions.canChallenge());

        restrictions.set(Restrictions.Restriction.DENY_CHALLENGE);

        assertFalse(restrictions.canChallenge());
    }

    @Test
    void canExchange() {
        assertTrue(restrictions.canExchange());

        restrictions.set(Restrictions.Restriction.DENY_EXCHANGE);

        assertFalse(restrictions.canExchange());
    }

    @Test
    void canAttack() {
        assertFalse(restrictions.canAttack());

        restrictions.set(Restrictions.Restriction.ALLOW_ATTACK);

        assertTrue(restrictions.canAttack());
    }

    @Test
    void canChat() {
        assertTrue(restrictions.canChat());

        restrictions.set(Restrictions.Restriction.DENY_CHAT);

        assertFalse(restrictions.canChat());
    }

    @Test
    void canBeMerchant() {
        assertTrue(restrictions.canBeMerchant());

        restrictions.set(Restrictions.Restriction.DENY_MERCHANT);

        assertFalse(restrictions.canBeMerchant());
    }

    @Test
    void canUseObject() {
        assertTrue(restrictions.canUseObject());

        restrictions.set(Restrictions.Restriction.DENY_USE_OBJECT);

        assertFalse(restrictions.canUseObject());
    }

    @Test
    void canInteractCollector() {
        assertTrue(restrictions.canInteractCollector());

        restrictions.set(Restrictions.Restriction.DENY_INTERACT_COLLECTOR);

        assertFalse(restrictions.canInteractCollector());
    }

    @Test
    void canUseIO() {
        assertTrue(restrictions.canUseIO());

        restrictions.set(Restrictions.Restriction.DENY_USE_IO);

        assertFalse(restrictions.canUseIO());
    }

    @Test
    void canSpeakNPC() {
        assertTrue(restrictions.canSpeakNPC());

        restrictions.set(Restrictions.Restriction.DENY_SPEAK_NPC);

        assertFalse(restrictions.canSpeakNPC());
    }

    @Test
    void canAttackDungeonWhenMutant() {
        assertFalse(restrictions.canAttackDungeonWhenMutant());

        restrictions.set(Restrictions.Restriction.ALLOW_DUNGEON_MUTANT);

        assertTrue(restrictions.canAttackDungeonWhenMutant());
    }

    @Test
    void canMoveAllDirections() {
        assertFalse(restrictions.canMoveAllDirections());

        restrictions.set(Restrictions.Restriction.ALLOW_MOVE_ALL_DIRECTION);

        assertTrue(restrictions.canMoveAllDirections());
    }

    @Test
    void canAttackMonsterWhenMutant() {
        assertFalse(restrictions.canAttackMonsterWhenMutant());

        restrictions.set(Restrictions.Restriction.ALLOW_ATTACK_MONSTERS_MUTANT);

        assertTrue(restrictions.canAttackMonsterWhenMutant());
    }

    @Test
    void canInteractWithPrism() {
        assertTrue(restrictions.canInteractWithPrism());

        restrictions.set(Restrictions.Restriction.DENY_INTERACT_PRISM);

        assertFalse(restrictions.canInteractWithPrism());
    }
}
