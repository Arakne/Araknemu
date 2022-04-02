/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.monster;

import fr.arakne.utils.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterReward;
import fr.quatrevieux.araknemu.game.spell.SpellList;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.Characteristics;
import org.checkerframework.checker.index.qual.IndexFor;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Monster grade data
 * This object is immutable, and shared between "real" creatures (fighters and groups)
 */
public final class Monster {
    private final MonsterTemplate template;
    private final SpellList spellList;
    private final MonsterReward reward;
    private final @NonNegative @IndexFor("template.grades()") int grade;

    @SuppressWarnings({"argument", "assignment"}) // checker do not track reference of template
    public Monster(MonsterTemplate template, SpellList spellList, MonsterReward reward, @NonNegative @IndexFor("#1.grades()") int grade) {
        this.template = template;
        this.spellList = spellList;
        this.reward = reward;
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

    public @Positive int gradeNumber() {
        return grade + 1;
    }

    /**
     * The grade level
     */
    public @Positive int level() {
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
     * Get the end fight rewards of the monster
     */
    public MonsterReward reward() {
        return reward;
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

    /**
     * Get the AI type of the monster
     */
    public String ai() {
        return template.ai();
    }
}
