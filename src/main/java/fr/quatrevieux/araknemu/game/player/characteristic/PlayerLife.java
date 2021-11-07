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

package fr.quatrevieux.araknemu.game.player.characteristic;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.characteristic.event.LifeChanged;
import fr.quatrevieux.araknemu.game.world.creature.Life;

/**
 * Handle player life
 */
public final class PlayerLife implements Life {
    private final GamePlayer player;
    private final Player entity;

    private int max;
    /**
     * Time in microseconds. The value should be set to 0 to disable the regeneration
     */
    private long lifeRegenerationStart;
    private int lifeRegenerationSpeed;

    public PlayerLife(GamePlayer player, Player entity) {
        this.player = player;
        this.entity = entity;
        this.lifeRegenerationStart = 0;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public int current() {
        return entity.life() + calculateLifeRegeneration();
    }

    private int calculateLifeRegeneration() {
        if (lifeRegenerationStart == 0) {
            return 0;
        }

        final long currentTime = System.currentTimeMillis();
        final int currentLife = entity.life();

        int lifeToAdd = (int) (currentTime - lifeRegenerationStart) / lifeRegenerationSpeed;

        if (this.max <= (lifeToAdd + currentLife)) {
            lifeToAdd = this.max - currentLife;
        }

        return lifeToAdd;
    }

    /**
     * calculate the life regeneration and set the lifeRegenerationStart timestamp to zero
     */
    public void stopLifeRegeneration() {
        setLifeWithCurrentRegeneration();
        lifeRegenerationStart = 0;
    }

    /**
     * set the life to: entity.life() + calculateLifeRegeneration()
     */
    public void setLifeWithCurrentRegeneration() {
        if (lifeRegenerationStart != 0) {
            entity.setLife(current());
            lifeRegenerationStart = System.currentTimeMillis();
        }
    }

    /**
     * Set the lifeRegenerationStart timestamps to System.currentTimeMillis()
     * @param lifeRegenerationSpeed The required delay in milliseconds to regenerate 1 life point
     */
    public void startLifeRegeneration(int lifeRegenerationSpeed) {
        // Regen already started : restart the regen
        if (lifeRegenerationStart != 0) {
            stopLifeRegeneration();
        }

        // Only start if regen speed is > 0
        if (lifeRegenerationSpeed > 0) {
            this.lifeRegenerationSpeed = lifeRegenerationSpeed;
            this.lifeRegenerationStart = System.currentTimeMillis();
        }
    }

    /**
     * Get the current percent life
     */
    public byte percent() {
        return (byte) (100 * current() / max());
    }

    /**
     * Add to player current life
     *
     * @param value Value to add. If the value is upper than remaining life, only the remaining life will be added
     */
    public void add(int value) {
        set(value + current());
    }

    /**
     * Change the current life
     *
     * @param value The new life value
     */
    public void set(int value) {
        final int last = current();

        if (value < 0) {
            value = 0;
        } else if (value > max) {
            value = max;
        }

        entity.setLife(value);
        player.dispatch(new LifeChanged(last, value));
    }

    /**
     * Rebuild the life points
     */
    public void rebuild() {
        final int percent = percent();

        max = computeMaxLife();
        entity.setLife(max * percent / 100);
    }

    private int computeMaxLife() {
        return player.race().life(entity.level()) + player.properties().characteristics().get(Characteristic.VITALITY);
    }

    /**
     * Initialize player life
     * This method will compute max life points
     */
    public void init() {
        max = computeMaxLife();

        if (current() < 0 || current() > max) {
            entity.setLife(max);
        }
    }
}
