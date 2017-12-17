package fr.quatrevieux.araknemu.network.game.in.info;

import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Send the screen information (size, and state)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Infos.as#L23
 */
final public class ScreenInfo implements Packet {
    public enum State {
        NORMAL,
        FULLSCREEN,
        OTHER
    }

    final static public class Parser implements SinglePacketParser<ScreenInfo> {
        @Override
        public ScreenInfo parse(String input) throws ParsePacketException {
            String[] parts = StringUtils.split(input, ";", 3);

            if (parts.length != 3) {
                throw new ParsePacketException("Ir"+input, "Screen info must be composed of 3 parts");
            }

            return new ScreenInfo(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                State.values()[parts[2].charAt(0) - '0']
            );
        }

        @Override
        public String code() {
            return "Ir";
        }
    }

    final private int width;
    final private int height;
    final private State state;

    public ScreenInfo(int width, int height, State state) {
        this.width = width;
        this.height = height;
        this.state = state;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public State state() {
        return state;
    }
}
