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

package fr.quatrevieux.araknemu.game.fight.ai.proxy;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import java.util.stream.Stream;

/**
 * Proxy for AI to allow performing modification without change the real fight
 *
 * Note: this object is immutable : any change will result to a creation of a new instance
 */
public final class ProxyAI implements AI<ActiveFighter> {
    private final AI<?> ai;

    private ProxyBattlefield map;
    private ProxyActiveFighter fighter;
    private ProxyTurn turn;

    private final Map<FighterData, FighterData> fighters = new WeakHashMap<>();

    public ProxyAI(AI<?> ai) {
        this.ai = ai;
        this.fighter = new ProxyActiveFighter(ai.fighter());
        this.map = new ProxyBattlefield(ai.map());
        this.turn = new ProxyTurn(ai, this.fighter);
    }

    private ProxyAI(ProxyAI other) {
        this.ai = other.ai;
        this.map = other.map;
        this.fighter = other.fighter;
        this.turn = other.turn;
    }

    @Override
    public void start(Turn turn) {
        throw new UnsupportedOperationException("This is a proxy AI");
    }

    @Override
    public ActiveFighter fighter() {
        return fighter;
    }

    @Override
    public BattlefieldMap map() {
        return map;
    }

    @Override
    public Turn turn() {
        return turn;
    }

    @Override
    public Stream<? extends FighterData> fighters() {
        return ai.fighters().map(this::getProxyFighter);
    }

    @Override
    public Optional<? extends FighterData> enemy() {
        return ai.enemy().map(this::getProxyFighter);
    }

    /**
     * Change the current cell of the handled fighter
     *
     * @param cellId The target cell id
     *
     * @return The new AI instance with the updated position
     *
     * @see fr.quatrevieux.araknemu.game.fight.ai.util.AIHelper#withPosition(FightCell)
     */
    public ProxyAI withPosition(@NonNegative int cellId) {
        final ProxyAI newAi = new ProxyAI(this);

        newAi.map = newAi.map.modify(modifier -> {
            newAi.setCurrentFighter(newAi.fighter.withPosition(modifier.get(cellId)));

            modifier.free(fighter().cell().id()).setFighter(cellId, newAi.fighter);

            ai.fighters().filter(other -> other.id() != this.fighter.id()).forEach(other -> {
                final FighterData proxyFighter = new ProxyPassiveFighter(other, newAi);

                newAi.fighters.put(other, proxyFighter);
                modifier.setFighter(other.cell().id(), proxyFighter);
            });
        });

        return newAi;
    }

    /**
     * Get or create the proxy fighter wrapper for a given fighter
     *
     * @param fighter The real fighter
     *
     * @return The proxy fighter
     */
    private FighterData getProxyFighter(FighterData fighter) {
        if (fighter.id() == this.fighter.id()) {
            return this.fighter;
        }

        return fighters.computeIfAbsent(fighter, f -> new ProxyPassiveFighter(f, this));
    }

    /**
     * Redefine the current fighter instance
     * This method will also update all related objects
     *
     * @param fighter New instance to use
     */
    private void setCurrentFighter(ProxyActiveFighter fighter) {
        this.fighter = fighter;
        this.turn = new ProxyTurn(ai, fighter);
    }
}
