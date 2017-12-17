package fr.quatrevieux.araknemu.network.in;

/**
 * Ask the position on the login queue
 *
 * @todo Implements queue position
 */
final public class AskQueuePosition implements Packet {
    final static public class Parser implements SinglePacketParser<AskQueuePosition> {
        @Override
        public AskQueuePosition parse(String input) throws ParsePacketException {
            return new AskQueuePosition();
        }

        @Override
        public String code() {
            return "Af";
        }
    }
}
