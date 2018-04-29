package fr.quatrevieux.araknemu.game.event;

import fr.quatrevieux.araknemu.core.event.SimpleListener;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.exploration.event.MapJoined;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SimpleListenerTest {
    @Test
    void event() {
        SimpleListener<MapJoined> listener = new SimpleListener<>(MapJoined.class, mapLoaded -> {});

        assertSame(MapJoined.class, listener.event());
    }

    @Test
    void on() {
        Consumer<Disconnected> consumer = Mockito.mock(Consumer.class);
        SimpleListener<Disconnected> listener = new SimpleListener<>(Disconnected.class, consumer);
        Disconnected evt = new Disconnected();

        listener.on(evt);

        Mockito.verify(consumer).accept(evt);
    }
}