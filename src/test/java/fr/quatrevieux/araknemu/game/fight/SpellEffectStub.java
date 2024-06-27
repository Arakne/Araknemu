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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.SpellEffectArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.EffectTarget;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

public class SpellEffectStub implements SpellEffect {
    private int id;
    private int min;
    private int max;
    private int special;
    private int duration;
    private int probability;
    private String text;
    private SpellEffectArea area;
    private EffectTarget target;
    private int boost;
    private boolean trap;

    public SpellEffectStub(int id, int min, int max, int special, int duration, int probability, String text, SpellEffectArea area, EffectTarget target, int boost, boolean trap) {
        this.id = id;
        this.min = min;
        this.max = max;
        this.special = special;
        this.duration = duration;
        this.probability = probability;
        this.text = text;
        this.area = area;
        this.target = target;
        this.boost = boost;
        this.trap = trap;
    }

    public SpellEffectStub(int id, int min) {
        this(id, min, 0, 0, 0, 0, "", new CellArea(), SpellEffectTarget.DEFAULT, 0, false);
    }

    @Override
    public int effect() {
        return id;
    }

    @Override
    public @NonNegative int min() {
        return min;
    }

    @Override
    public @NonNegative int max() {
        return max;
    }

    @Override
    public int special() {
        return special;
    }

    @Override
    public @GTENegativeOne int duration() {
        return duration;
    }

    @Override
    public @NonNegative int probability() {
        return probability;
    }

    @Override
    public String text() {
        return text;
    }

    @Override
    public SpellEffectArea area() {
        return area;
    }

    @Override
    public EffectTarget target() {
        return target;
    }

    @Override
    public int boost() {
        return boost;
    }

    @Override
    public boolean trap() {
        return trap;
    }

    public SpellEffectStub setId(int id) {
        this.id = id;
        return this;
    }

    public SpellEffectStub setMin(int min) {
        this.min = min;
        return this;
    }

    public SpellEffectStub setMax(int max) {
        this.max = max;
        return this;
    }

    public SpellEffectStub setSpecial(int special) {
        this.special = special;
        return this;
    }

    public SpellEffectStub setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public SpellEffectStub setProbability(int probability) {
        this.probability = probability;
        return this;
    }

    public SpellEffectStub setText(String text) {
        this.text = text;
        return this;
    }

    public SpellEffectStub setArea(SpellEffectArea area) {
        this.area = area;
        return this;
    }

    public SpellEffectStub setTarget(EffectTarget target) {
        this.target = target;
        return this;
    }

    public SpellEffectStub setBoost(int boost) {
        this.boost = boost;
        return this;
    }

    public SpellEffectStub setTrap(boolean trap) {
        this.trap = trap;
        return this;
    }

    public static SpellEffectStub fixed(int id, int min) {
        return new SpellEffectStub(id, min, 0, 0, 0, 0, "", new CellArea(), SpellEffectTarget.DEFAULT, 0, false);
    }
}
