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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.fighter.monster;

import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.AbstractFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.BaseFighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.BaseFighterSpellList;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterCharacteristics;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterLife;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterSpellList;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterReward;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Fighter for a monster
 */
public final class MonsterFighter extends AbstractFighter {
    private final int id;
    private final Monster monster;
    private final MonsterGroupTeam team;

    private final BaseFighterLife life;
    private final MonsterFighterCharacteristics characteristics;
    private final MonsterFighterSprite sprite;
    private final FighterSpellList spells;

    @SuppressWarnings({"assignment", "argument"})
    public MonsterFighter(int id, Monster monster, MonsterGroupTeam team) {
        this.id = id;
        this.monster = monster;
        this.team = team;

        this.life = new BaseFighterLife(this, monster.life());
        this.characteristics = new MonsterFighterCharacteristics(monster, this);
        this.sprite = new MonsterFighterSprite(this, monster);
        this.spells = new BaseFighterSpellList(monster.spells());
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public Sprite sprite() {
        return sprite;
    }

    @Override
    public FighterLife life() {
        return life;
    }

    @Override
    public FighterCharacteristics characteristics() {
        return characteristics;
    }

    @Override
    public FighterSpellList spells() {
        return spells;
    }

    @Override
    public CastableWeapon weapon() {
        throw new FightException("The fighter do not have any weapon");
    }

    @Override
    public @Positive int level() {
        return monster.level();
    }

    @Override
    public FightTeam team() {
        return team;
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public <O extends FighterOperation> O apply(O operation) {
        operation.onMonster(this);

        return operation;
    }

    /**
     * Get the end fight rewards
     *
     * @see Monster#reward()
     */
    public MonsterReward reward() {
        return monster.reward();
    }

    /**
     * Get the monster data for the fighter
     */
    public Monster monster() {
        return monster;
    }
}
