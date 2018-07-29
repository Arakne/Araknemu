package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.game.player.characteristic.CharacterCharacteristics;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.world.creature.Life;

/**
 * Define properties and characteristics of the current character
 */
public interface CharacterProperties {
    /**
     * Get the player characteristics
     */
    public CharacterCharacteristics characteristics();

    /**
     * Get the player life
     */
    public Life life();

    /**
     * Get the player spells
     */
    public SpellBook spells();

    /**
     * Get the player level and experience
     */
    public GamePlayerExperience experience();
}
