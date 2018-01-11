package fr.quatrevieux.araknemu.network.adapter.util;

import fr.quatrevieux.araknemu.network.adapter.Channel;

import java.util.Stack;

/**
 * Dummy implementation of channel
 */
final public class DummyChannel implements Channel {
    private long id = 1;
    private boolean isAlive = true;
    private Stack<Object> messages = new Stack<>();

    @Override
    public Object id() {
        return id;
    }

    @Override
    public void write(Object message) {
        messages.push(message);
    }

    @Override
    public void close() {
        isAlive = false;
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    public Stack<Object> getMessages() {
        return messages;
    }

    public void setId(long id) {
        this.id = id;
    }
}
