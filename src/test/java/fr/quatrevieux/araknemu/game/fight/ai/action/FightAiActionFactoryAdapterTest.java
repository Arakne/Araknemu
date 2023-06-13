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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.path.Decoder;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.proxy.ProxyBattlefield;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertSame;

class FightAiActionFactoryAdapterTest extends FightBaseCase {
    private FightAiActionFactoryAdapter factory;
    private Fight fight;
    private PlayableFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight(true);
        fighter = player.fighter();
        factory = new FightAiActionFactoryAdapter(fighter, fight, fight.actions());
    }

    @Test
    void cast() {
        Action action = factory.cast(
            fighter.spells().get(3),
            fight.map().get(123)
        );

        assertInstanceOf(Cast.class, action);
        assertSame(fighter.spells().get(3), Cast.class.cast(action).spell());
        assertSame(fight.map().get(123), Cast.class.cast(action).target());
        assertSame(fighter, Cast.class.cast(action).caster());
    }

    @Test
    void castShouldConvertTargetToActualFightCell() {
        FightCell fakeCell = Mockito.mock(FightCell.class);
        Mockito.when(fakeCell.id()).thenReturn(123);

        Action action = factory.cast(
            fighter.spells().get(3),
            fakeCell
        );

        assertSame(fight.map().get(123), Cast.class.cast(action).target());
    }

    @Test
    void move() {
        Action action = factory.move(
            new Decoder<>(fight.map()).decode("aaJbbF", fight.map().get(35))
        );

        assertInstanceOf(Move.class, action);
        assertSame(fight.map().get(35), MoveResult.class.cast(action.start()).path().start());
        assertSame(fight.map().get(95), MoveResult.class.cast(action.start()).target());
    }

    @Test
    void moveShouldConvertCellsToActualFightCell() {
        ProxyBattlefield aiMap = new ProxyBattlefield(fight.map());
        aiMap = aiMap.modify(modifier -> {}); // Force creation of proxy cells

        Action action = factory.move(
            new Decoder<>(aiMap).decode("aaJbbF", aiMap.get(35))
        );

        assertSame(fight.map().get(35), MoveResult.class.cast(action.start()).path().start());
        assertSame(fight.map().get(95), MoveResult.class.cast(action.start()).target());
    }

    @Test
    void castSpellValidator() {
        assertSame(fight.actions().cast().validator(), factory.castSpellValidator());
    }
}
