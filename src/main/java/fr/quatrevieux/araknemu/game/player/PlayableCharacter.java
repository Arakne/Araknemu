package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessories;

/**
 * Base interface for playable characters
 */
public interface PlayableCharacter {
    /**
     * Printer for displaying a character
     */
    interface Printer {
        public Printer level(int level);
        public Printer name(String name);
        public Printer race(Race race);
        public Printer sex(Sex sex);
        public Printer colors(Colors colors);
        public Printer id(int id);
        public Printer gfxID(int gfxID);
        public Printer server(int id);
        public Printer accessories(Accessories accessories);
    }

    /**
     * Print the current character
     */
    public void print(Printer printer);

    /**
     * Get the character ID
     */
    public int id();

    /**
     * Get the related account
     */
    public GameAccount account();
}
