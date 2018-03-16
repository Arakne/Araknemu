package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.game.player.characteristic.Life;
import fr.quatrevieux.araknemu.game.player.characteristic.PlayerCharacteristics;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;

/**
 * @todo rename ?
 */
public interface PlayerData {
    /**
     * Get the player characteristics
     */
    public PlayerCharacteristics characteristics();

    /**
     * Get the player life
     */
    public Life life();

    /**
     * Get the player spells
     */
    public SpellBook spells();
}
