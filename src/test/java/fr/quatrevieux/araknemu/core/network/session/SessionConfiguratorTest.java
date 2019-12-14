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

import fr.quatrevieux.araknemu._test.TestCase;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SessionConfiguratorTest extends TestCase {
    class TestSession extends AbstractDelegatedSession {
        public TestSession(Session session) {
            super(session);
        }
    }

    @Test
    void create() {
        SessionConfigurator<TestSession> configurator = new SessionConfigurator<>(TestSession::new);

        SessionConfigurator.Configurator<TestSession> conf = Mockito.mock(SessionConfigurator.Configurator.class);
        configurator.add(conf);

        TestSession session = configurator.create(new DummyChannel());

        Mockito.verify(conf).configure(Mockito.any(ConfigurableSession.class), Mockito.eq(session));
        assertInstanceOf(DummyChannel.class, session.channel());
    }
}
