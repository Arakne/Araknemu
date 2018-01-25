package fr.quatrevieux.araknemu.network.game.out.basic.admin;

/**
 * Change the console prompt
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L116
 */
final public class ConsolePrompt {
    final private String prompt;

    public ConsolePrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String toString() {
        return "BAP" + prompt;
    }
}
