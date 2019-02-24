package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;

/**
 * Monster grade data
 * This object is immutable, and shared between "real" creatures (fighters and groups)
 *
 * @todo implement creature ? Not necessary because MonsterGroup is already the creature
 */
final public class Monster {
    final private MonsterTemplate template;
    final private SpellList spellList;
    final private int grade;

    public Monster(MonsterTemplate template, SpellList spellList, int grade) {
        this.template = template;
        this.spellList = spellList;
        this.grade = grade;
    }

    /**
     * The monster template id
     */
    public int id() {
        return template.id();
    }

    /**
     * The monster sprite
     */
    public int gfxId() {
        return template.gfxId();
    }

    public int gradeNumber() {
        return grade + 1;
    }

    /**
     * The grade level
     */
    public int level() {
        return template.grades()[grade].level();
    }

    /**
     * Colors of the sprite
     */
    public Colors colors() {
        return template.colors();
    }

    /**
     * List of spells
     */
    public SpellList spells() {
        return spellList;
    }

    /**
     * Get life points of the monster
     */
    public int life() {
        return template.grades()[grade].life();
    }

    /**
     * Get characteristics of the monster
     */
    public Characteristics characteristics() {
        return template.grades()[grade].characteristics();
    }

    /**
     * Get the initiative value of the monster on fight
     *
     * @see fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter
     * @see fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics#initiative()
     */
    public int initiative() {
        return template.grades()[grade].initiative();
    }
}
