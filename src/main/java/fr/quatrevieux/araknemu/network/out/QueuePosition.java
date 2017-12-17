package fr.quatrevieux.araknemu.network.out;

import fr.quatrevieux.araknemu.network.in.AskQueuePosition;

/**
 * Response for {@link AskQueuePosition}
 */
final public class QueuePosition {
    final private int position;

    public QueuePosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Aq" + position;
    }
}
