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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.common.account.banishment.AbstractBanIpSynchronizer;
import fr.quatrevieux.araknemu.common.account.banishment.BanIpService;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.GameStopped;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Synchronize ban ip table for game service
 */
public final class GameBanIpSynchronizer extends AbstractBanIpSynchronizer implements EventsSubscriber, PreloadableService {
    public GameBanIpSynchronizer(BanIpService<GameAccount> service, Supplier<Collection<? extends Session>> sessionsSupplier, Logger logger, Duration refreshDelay) {
        super(service, sessionsSupplier, logger, refreshDelay);
    }

    @Override
    public void preload(Logger logger) {
        startPulling();
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<GameStopped>() {
                @Override
                public void on(GameStopped event) {
                    stopPulling();
                }

                @Override
                public Class<GameStopped> event() {
                    return GameStopped.class;
                }
            },
            ipBannedListener(),
        };
    }
}
