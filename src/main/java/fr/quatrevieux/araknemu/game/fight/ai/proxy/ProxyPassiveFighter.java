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

import java.util.Optional;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buffs;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.States;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.team.Team;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Proxy class for override properties of other fighters
 *
 * Note: this object is immutable
 */
public final class ProxyPassiveFighter implements PassiveFighter {
    private final PassiveFighter fighter;
    private final AI ai;

    /**
     * @param fighter The fighter to wrap
     * @param ai The AI instance use to resolve the cell instance
     */
    public ProxyPassiveFighter(PassiveFighter fighter, AI ai) {
        this.fighter = fighter;
        this.ai = ai;
    }

    @Override
    public int id() {
        return fighter.id();
    }

    @Override
    @SuppressWarnings("argument") // cell id always valid
    public FightCell cell() {
        return ai.map().get(fighter.cell().id());
    }

    @Override
    public Sprite sprite() {
        return fighter.sprite();
    }

    @Override
    public Direction orientation() {
        return fighter.orientation();
    }

    @Override
    public void move(@Nullable FightCell cell) {
        throw new UnsupportedOperationException("This is a proxy fighter");
    }

    @Override
    public FighterLife life() {
        return fighter.life();
    }

    @Override
    public Buffs buffs() {
        return fighter.buffs();
    }

    @Override
    public States states() {
        return fighter.states();
    }

    @Override
    public @Positive int level() {
        return fighter.level();
    }

    @Override
    public FighterCharacteristics characteristics() {
        return fighter.characteristics();
    }

    @Override
    public Team<? extends PassiveFighter> team() {
        return fighter.team();
    }

    @Override
    public boolean dead() {
        return fighter.dead();
    }

    @Override
    public boolean hidden() {
        return fighter.hidden();
    }

    @Override
    public void setHidden(PassiveFighter caster, boolean hidden) {
        throw new UnsupportedOperationException("Cannot modify a proxy fighter");
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PassiveFighter)) {
            return false;
        }

        final PassiveFighter that = (PassiveFighter) o;

        return id() == that.id();
    }

    @Override
    public int hashCode() {
        return fighter.hashCode();
    }

    @Override
    public Optional<PassiveFighter> invoker() {
        return fighter.invoker();
    }
}
