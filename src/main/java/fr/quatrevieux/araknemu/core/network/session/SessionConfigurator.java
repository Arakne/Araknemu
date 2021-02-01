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

package fr.quatrevieux.araknemu.core.network.session;

import fr.quatrevieux.araknemu.core.network.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Simple session factory using ConfigurableSession
 *
 * @param <S> The session type
 */
public final class SessionConfigurator<S extends Session> implements SessionFactory<S> {
    public interface Configurator<S> {
        public void configure(ConfigurableSession inner, S session);
    }

    private final Function<ConfigurableSession, S> factory;
    private final List<Configurator<S>> configurators = new ArrayList<>();

    public SessionConfigurator(Function<ConfigurableSession, S> factory) {
        this.factory = factory;
    }

    @Override
    public S create(Channel channel) {
        final ConfigurableSession inner = new ConfigurableSession(channel);
        final S session = factory.apply(inner);

        configurators.forEach(configurator -> configurator.configure(inner, session));

        return session;
    }

    /**
     * Add a new configurator
     *
     * <code>
     *     factory
     *         .add((config, session) -> config.addReceiveMiddleware((packet, next) -> dispatcher.dispatch(packet))
     *         .create(channel)
     *     ;
     * </code>
     *
     * @param configurator The configurator
     *
     * @return this instance
     */
    public SessionConfigurator<S> add(Configurator<S> configurator) {
        configurators.add(configurator);

        return this;
    }
}
