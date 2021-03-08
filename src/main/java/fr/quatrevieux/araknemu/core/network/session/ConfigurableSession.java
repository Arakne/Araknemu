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
import fr.quatrevieux.araknemu.core.network.InternalPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Simple session implementation with configurable handlers for each methods
 */
public final class ConfigurableSession implements Session {
    private final Channel channel;

    private final List<SendPacketTransformer> sendTransformers = new ArrayList<>();
    private final List<ReceivePacketMiddleware> receiveMiddlewares = new ArrayList<>();
    private final List<ExceptionHandler> exceptionHandlers = new ArrayList<>();

    public ConfigurableSession(Channel channel) {
        this.channel = channel;
    }

    @Override
    public Channel channel() {
        return channel;
    }

    @Override
    public void send(Object packet) {
        if (!isAlive()) {
            return;
        }

        for (SendPacketTransformer transformer : sendTransformers) {
            if ((packet = transformer.transformPacket(packet)) == null) {
                return;
            }
        }

        channel.write(packet);
    }

    @Override
    public void receive(Object packet) {
        // Do not handle received packet if the session is closed
        // But handle internal packets (like SessionClosed)
        if (!(packet instanceof InternalPacket) && !isAlive()) {
            return;
        }

        final Consumer<Object> next = new Consumer<Object>() {
            private int index = 0;

            @Override
            public void accept(Object o) {
                try {
                    receiveMiddlewares.get(index++).handlePacket(o, this);
                } catch (Exception e) {
                    exception(e);
                }
            }
        };

        next.accept(packet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void exception(Throwable cause) {
        boolean handled = false;

        for (ExceptionHandler handler : exceptionHandlers) {
            if (handler.type().isInstance(cause)) {
                handled = true;

                if (!handler.handleException(cause)) {
                    break;
                }
            }
        }

        if (!handled) {
            throw new IllegalArgumentException("Unhandled exception", cause);
        }
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public boolean isAlive() {
        return channel.isAlive();
    }

    /**
     * Add a transformer for sending packets
     * All the transformers are called before write into the channel
     * If a transformer return null, the packet will be not sent
     */
    public void addSendTransformer(SendPacketTransformer transformer) {
        sendTransformers.add(transformer);
    }

    /**
     * Add a middleware for and received packets
     * The last added middleware will be the last executed (endpoint)
     *
     * <code>
     *     session.addReceiveMiddleware((packet, next) -> {
     *         before(packet);
     *
     *         next.accept(transformPacket(packet));
     *
     *         after(packet);
     *     })
     * </code>
     */
    public void addReceiveMiddleware(ReceivePacketMiddleware middleware) {
        receiveMiddlewares.add(middleware);
    }

    /**
     * Add a new exception handler at the end of the list
     */
    public void addExceptionHandler(ExceptionHandler handler) {
        exceptionHandlers.add(handler);
    }

    /**
     * Add a simple exception handler
     *
     * @param type The handled exception class
     * @param handle The exception handler
     *
     * @param <E> The exception type
     */
    public <E> void addExceptionHandler(Class<E> type, Predicate<E> handle) {
        addExceptionHandler(new ExceptionHandler() {
            @Override
            public Class<E> type() {
                return type;
            }

            @Override
            @SuppressWarnings("unchecked")
            public boolean handleException(Throwable cause) {
                return handle.test((E) cause);
            }
        });
    }

    /**
     * Handle exceptions thrown during a session
     *
     * @param <E> The exception type
     */
    public interface ExceptionHandler<E extends Throwable> {
        /**
         * The supported exception type
         */
        public Class<E> type();

        /**
         * Handle the exception
         *
         * @return true for call next handlers, or false to stop exception handling
         */
        public boolean handleException(E cause);
    }

    /**
     * Middleware for handle received packets
     */
    @FunctionalInterface
    public interface ReceivePacketMiddleware {
        /**
         * Handle a packet
         *
         * @param packet Packet to handle
         * @param next The next middleware to call
         */
        public void handlePacket(Object packet, Consumer<Object> next) throws Exception;
    }

    /**
     * Transform packets before sending
     */
    @FunctionalInterface
    public interface SendPacketTransformer {
        /**
         * Transform the packet
         *
         * @param packet Original packet
         *
         * @return The transformed packet, or null to cancel sending
         */
        public Object transformPacket(Object packet);
    }
}
