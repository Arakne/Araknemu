package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send secret answer to client
 */
final public class Question {
    final private String answer;

    public Question(String answer) {
        this.answer = answer;
    }

    public String answer() {
        return answer;
    }

    @Override
    public String toString() {
        return "AQ" + answer.replace(" ", "+");
    }
}
