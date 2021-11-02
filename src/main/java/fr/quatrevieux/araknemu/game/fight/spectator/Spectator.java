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

package fr.quatrevieux.araknemu.game.fight.spectator;

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StartWatchFight;
import fr.quatrevieux.araknemu.game.fight.spectator.event.StopWatchFight;
import fr.quatrevieux.araknemu.game.player.CharacterProperties;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerSessionScope;
import fr.quatrevieux.araknemu.network.game.GameSession;

import java.util.Objects;

/**
 * A spectator session for a player to watch a fight
 *
 * The spectator instance should be created using the {@link SpectatorFactory} to ensure that listeners are registers
 *
 * To start and stop the session, {@link Spectator#join()} and {@link Spectator#leave()} should be used instead of
 * directly call {@link GamePlayer#start(PlayerSessionScope)} and {@link GamePlayer#stop(PlayerSessionScope)}
 *
 * To mitigate race condition, ensure that any spectator session actions will be performed within {@link Fight#execute(Runnable)}
 *
 * @see GamePlayer#isSpectator() To check if the current player is on spectator session
 * @see GamePlayer#spectator() To get the spectator session
 */
public final class Spectator implements PlayerSessionScope {
    private final GamePlayer player;
    private final Fight fight;
    private final ListenerAggregate dispatcher = new DefaultListenerAggregate();

    public Spectator(GamePlayer player, Fight fight) {
        this.player = player;
        this.fight = fight;
    }

    @Override
    public void dispatch(Object event) {
        player.dispatch(event);
    }

    @Override
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    @Override
    public CharacterProperties properties() {
        return player.properties();
    }

    @Override
    public void register(GameSession session) {
        session.setSpectator(this);
    }

    @Override
    public void unregister(GameSession session) {
        session.setSpectator(null);
    }

    @Override
    public void send(Object packet) {
        player.send(packet);
    }

    /**
     * Join the fight as spectator
     * Will dispatch a {@link StartWatchFight}
     *
     * Note: this method will also start the spectator session
     *
     * @see Spectator#leave() To leave the fight
     * @see Spectators#add(Spectator)
     */
    public void join() {
        fight.spectators().add(this);
        player.start(this);
        dispatch(new StartWatchFight());
    }

    /**
     * Leave the fight and stop the spectator session
     * Will dispatch a {@link StopWatchFight}
     *
     * @see Spectators#remove(Spectator)
     */
    public void leave() {
        fight.spectators().remove(this);
        dispatch(new StopWatchFight());
        stop();
    }

    /**
     * Only stop the spectator session without remove from the fight
     */
    public void stop() {
        player.stop(this);
    }

    /**
     * Get the related player name
     */
    public String name() {
        return player.name();
    }

    /**
     * Get the watched fight
     */
    public Fight fight() {
        return fight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Spectator spectator = (Spectator) o;

        return Objects.equals(player, spectator.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
}
