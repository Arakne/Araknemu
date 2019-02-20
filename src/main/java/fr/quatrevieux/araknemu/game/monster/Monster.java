package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.game.spell.SpellList;

/**
 * Monster grade data
 * This object is immutable, and shared between "real" creatures (fighters and groups)
 *
 * @todo implement creature ? Not necessary because MonsterGroup is already the creature
 */
final public class Monster {
    final private MonsterTemplate template;
    final private MonsterTemplate.Grade grade;
    final private SpellList spellList;

    public Monster(MonsterTemplate template, MonsterTemplate.Grade grade, SpellList spellList) {
        this.template = template;
        this.grade = grade;
        this.spellList = spellList;
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

    /**
     * The grade level
     */
    public int level() {
        return grade.level();
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
}
