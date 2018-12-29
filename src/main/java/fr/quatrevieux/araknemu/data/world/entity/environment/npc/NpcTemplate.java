package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;

/**
 * Store the NPC template data
 */
final public class NpcTemplate {
    final private int id;
    final private int gfxId;
    final private int scaleX;
    final private int scaleY;
    final private Sex sex;
    final private Colors colors;
    final private String accessories;
    final private int extraClip;
    final private int customArtwork;

    public NpcTemplate(int id, int gfxId, int scaleX, int scaleY, Sex sex, Colors colors, String accessories, int extraClip, int customArtwork) {
        this.id = id;
        this.gfxId = gfxId;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.sex = sex;
        this.colors = colors;
        this.accessories = accessories;
        this.extraClip = extraClip;
        this.customArtwork = customArtwork;
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
    public Sex sex() {
        return sex;
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
}
