package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Response for {@link fr.quatrevieux.araknemu.network.realm.in.AskQueuePosition}
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
