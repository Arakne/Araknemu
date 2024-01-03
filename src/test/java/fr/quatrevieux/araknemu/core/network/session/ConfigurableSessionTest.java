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
import fr.quatrevieux.araknemu.core.network.SessionClosed;
import fr.quatrevieux.araknemu.core.network.util.DummyChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigurableSessionTest extends TestCase {
    private ConfigurableSession session;
    private DummyChannel channel;

    @BeforeEach
    void setUp() {
        channel = new DummyChannel();
        session = new ConfigurableSession(channel);
    }

    @Test
    void channel() {
        assertSame(channel, session.channel());
    }

    @Test
    void send() {
        session.addSendTransformer(packet -> packet + "a");
        session.addSendTransformer(packet -> "b" + packet);

        session.send("test");

        assertEquals("btesta", channel.getMessages().peek());
    }

    @Test
    void sendWithCancelSendPacket() {
        session.addSendTransformer(packet -> null);

        session.send("test");

        assertTrue(channel.getMessages().empty());
    }

    @Test
    void sendWithSessionClosed() {
        session.close();
        session.send("test");

        assertTrue(channel.getMessages().empty());
    }

    @Test
    void receiveFinalMiddleware() throws Exception {
        ConfigurableSession.ReceivePacketMiddleware middleware = Mockito.mock(ConfigurableSession.ReceivePacketMiddleware.class);
        session.addReceiveMiddleware(middleware);

        session.receive("packet");

        Mockito.verify(middleware).handlePacket(Mockito.eq("packet"), Mockito.any(Consumer.class));
    }

    @Test
    void receiveWithTransformationMiddleware() throws Exception {
        ConfigurableSession.ReceivePacketMiddleware middleware = Mockito.mock(ConfigurableSession.ReceivePacketMiddleware.class);

        session.addReceiveMiddleware((packet, next) -> next.accept(packet.toString() + "a"));
        session.addReceiveMiddleware((packet, next) -> next.accept(packet.toString() + "b"));
        session.addReceiveMiddleware(middleware);

        session.receive("packet");

        Mockito.verify(middleware).handlePacket(Mockito.eq("packetab"), Mockito.any(Consumer.class));
    }

    @Test
    void receiveWithExceptionShouldBeCatchByHandler() {
        final Exception e = new Exception("my error");

        session.addReceiveMiddleware((packet, next) -> { throw e; });

        Predicate predicate = Mockito.mock(Predicate.class);
        session.addExceptionHandler(Exception.class, predicate);

        session.receive("");

        Mockito.verify(predicate).test(e);
    }

    @Test
    void receiveWithClosedSessionShouldIgnoreThePacket() throws Exception {
        ConfigurableSession.ReceivePacketMiddleware middleware = Mockito.mock(ConfigurableSession.ReceivePacketMiddleware.class);
        session.addReceiveMiddleware(middleware);

        session.close();
        session.receive("packet");

        Mockito.verify(middleware, Mockito.never()).handlePacket(Mockito.anyString(), Mockito.any(Consumer.class));
    }

    @Test
    void receiveInternalPacketOnClosedSessionShouldHandleThePacket() throws Exception {
        ConfigurableSession.ReceivePacketMiddleware middleware = Mockito.mock(ConfigurableSession.ReceivePacketMiddleware.class);
        session.addReceiveMiddleware(middleware);

        SessionClosed packet = new SessionClosed();

        session.close();
        session.receive(packet);

        Mockito.verify(middleware).handlePacket(Mockito.eq(packet), Mockito.any(Consumer.class));
    }

    @Test
    void exceptionStopHandlingWhenReturnFalse() {
        Predicate handler1 = Mockito.mock(Predicate.class);
        Predicate handler2 = Mockito.mock(Predicate.class);

        session.addExceptionHandler(Exception.class, handler1);
        session.addExceptionHandler(Exception.class, handler2);

        Mockito.when(handler1.test(Mockito.any())).thenReturn(false);
        Mockito.when(handler2.test(Mockito.any())).thenReturn(false);

        Exception e = new Exception();

        session.exception(e);

        Mockito.verify(handler1).test(e);
        Mockito.verify(handler2, Mockito.never()).test(e);
    }

    @Test
    void exceptionContinueHandlingWhenReturnTrue() {
        Predicate handler1 = Mockito.mock(Predicate.class);
        Predicate handler2 = Mockito.mock(Predicate.class);

        session.addExceptionHandler(Exception.class, handler1);
        session.addExceptionHandler(Exception.class, handler2);

        Mockito.when(handler1.test(Mockito.any())).thenReturn(true);
        Mockito.when(handler2.test(Mockito.any())).thenReturn(true);

        Exception e = new Exception();

        session.exception(e);

        Mockito.verify(handler1).test(e);
        Mockito.verify(handler2).test(e);
    }

    @Test
    void exceptionFilterByExceptionType() {
        Predicate handler1 = Mockito.mock(Predicate.class);
        Predicate handler2 = Mockito.mock(Predicate.class);

        session.addExceptionHandler(RuntimeException.class, handler1);
        session.addExceptionHandler(Exception.class, handler2);

        Mockito.when(handler1.test(Mockito.any())).thenReturn(false);
        Mockito.when(handler2.test(Mockito.any())).thenReturn(false);

        Exception e = new Exception();

        session.exception(e);

        Mockito.verify(handler1, Mockito.never()).test(e);
        Mockito.verify(handler2).test(e);
    }

    @Test
    void exceptionWithPacket() {
        BiPredicate handler1 = Mockito.mock(BiPredicate.class);
        BiPredicate handler2 = Mockito.mock(BiPredicate.class);

        session.addExceptionHandler(RuntimeException.class, handler1);
        session.addExceptionHandler(Exception.class, handler2);

        Mockito.when(handler1.test(Mockito.any(), Mockito.anyString())).thenReturn(false);
        Mockito.when(handler2.test(Mockito.any(), Mockito.anyString())).thenReturn(false);

        Exception e = new Exception();

        session.exception(e, "foo");

        Mockito.verify(handler1, Mockito.never()).test(e, "foo");
        Mockito.verify(handler2).test(e, "foo");
    }

    @Test
    void exceptionNotHandled() {
        assertThrows(IllegalArgumentException.class, () -> session.exception(new Exception()));
    }

    @Test
    void close() {
        session.close();

        assertFalse(session.isAlive());
    }
}
