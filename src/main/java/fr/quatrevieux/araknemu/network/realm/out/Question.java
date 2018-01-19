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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Question question1 = (Question) o;

        return answer != null ? answer.equals(question1.answer) : question1.answer == null;
    }

    @Override
    public int hashCode() {
        return answer != null ? answer.hashCode() : 0;
    }
}
