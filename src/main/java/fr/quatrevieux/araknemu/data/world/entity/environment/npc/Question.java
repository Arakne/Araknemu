package fr.quatrevieux.araknemu.data.world.entity.environment.npc;

/**
 * Store NPC questions data
 */
final public class Question {
    final private int id;
    final private int[] responseIds;
    final private String[] parameters;
    final private String condition;

    public Question(int id, int[] responseIds, String[] parameters, String condition) {
        this.id = id;
        this.responseIds = responseIds;
        this.parameters = parameters;
        this.condition = condition;
    }

    /**
     * The question ID
     * Should corresponds with dialog SWF
     */
    public int id() {
        return id;
    }

    /**
     * List of available response ids
     */
    public int[] responseIds() {
        return responseIds;
    }

    /**
     * Get the question variable / parameters like name
     */
    public String[] parameters() {
        return parameters;
    }

    /**
     * Get the question condition
     * If not match, the next question should be asked
     */
    public String condition() {
        return condition;
    }
}
