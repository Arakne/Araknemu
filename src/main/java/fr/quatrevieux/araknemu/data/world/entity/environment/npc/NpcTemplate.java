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

package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;

import java.util.Optional;

/**
 * Store the NPC template data
 */
final public class NpcTemplate {
    final private int id;
    final private int gfxId;
    final private int scaleX;
    final private int scaleY;
    final private Gender gender;
    final private Colors colors;
    final private String accessories;
    final private int extraClip;
    final private int customArtwork;
    final private int[] storeItems;

    public NpcTemplate(int id, int gfxId, int scaleX, int scaleY, Gender gender, Colors colors, String accessories, int extraClip, int customArtwork, int[] storeItems) {
        this.id = id;
        this.gfxId = gfxId;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.gender = gender;
        this.colors = colors;
        this.accessories = accessories;
        this.extraClip = extraClip;
        this.customArtwork = customArtwork;
        this.storeItems = storeItems;
    }

    /**
     * Get the NPC template ID
     * Should corresponds to npc SWF data
     */
    public int id() {
        return id;
    }

    /**
     * Get the NPC display sprite
     * Located at clips/sprites/[gfxId].swf
     */
    public int gfxId() {
        return gfxId;
    }

    /**
     * Get the sprite width scale in percent (default to 100)
     */
    public int scaleX() {
        return scaleX;
    }

    /**
     * Get the sprite height scale in percent (default to 100)
     */
    public int scaleY() {
        return scaleY;
    }

    /**
     * Get the sprite sex
     */
    public Gender gender() {
        return gender;
    }

    /**
     * Get the sprite colors
     * Set all to -1 for default colors
     */
    public Colors colors() {
        return colors;
    }

    /**
     * Get the accessories list as string
     */
    public String accessories() {
        return accessories;
    }

    /**
     * Get the sprite extra clip (ex: exclamation mark for quest)
     * Set -1 for no extra clip
     *
     * @todo temporary : The clip should be dynamic (ex: display quest mark only if the quest is not done)
     */
    public int extraClip() {
        return extraClip;
    }

    /**
     * Define a custom artwork on dialog
     * Set 0 for use default artwork
     */
    public int customArtwork() {
        return customArtwork;
    }

    /**
     * Get the items template ids for the store of the npc
     */
    public Optional<int[]> storeItems() {
        return Optional.ofNullable(storeItems);
    }
}
