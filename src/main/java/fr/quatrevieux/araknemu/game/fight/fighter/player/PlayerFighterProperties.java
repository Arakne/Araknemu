package fr.quatrevieux.araknemu.game.fight.fighter.player;

import fr.quatrevieux.araknemu.game.player.CharacterProperties;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;

/**
 * Wrap player fighter properties
 */
final public class PlayerFighterProperties implements CharacterProperties {
    final private PlayerFighter fighter;
    final private CharacterProperties baseProperties;

    final private PlayerFighterCharacteristics characteristics;
    final private PlayerFighterLife life;

    public PlayerFighterProperties(PlayerFighter fighter, CharacterProperties baseProperties) {
        this.fighter = fighter;
        this.baseProperties = baseProperties;

        this.characteristics = new PlayerFighterCharacteristics(baseProperties.characteristics(), fighter);
        this.life = new PlayerFighterLife(baseProperties.life(), fighter);
    }

    @Override
    public PlayerFighterCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public PlayerFighterLife life() {
        return life;
    }

    @Override
    public SpellBook spells() {
        return baseProperties.spells();
    }

    @Override
    public GamePlayerExperience experience() {
        return baseProperties.experience();
    }

    @Override
    public long kamas() {
        return baseProperties.kamas();
    }
}
