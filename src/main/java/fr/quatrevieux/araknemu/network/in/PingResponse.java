package fr.quatrevieux.araknemu.network.in;

/**
 * Response for RPing request
 */
final public class PingResponse implements Packet {
    public static class Parser implements SinglePacketParser<PingResponse> {
        @Override
        public PingResponse parse(String input) throws ParsePacketException {
            return new PingResponse(input);
        }

        @Override
        public String code() {
            return "rpong";
        }
    }

    final private String payload;

    public PingResponse(String payload) {
        this.payload = payload;
    }

    /**
     * Get the response payload
     */
    public String payload() {
        return payload;
    }
}
