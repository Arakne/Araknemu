package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send secret answer to client
 */
final public class Answer {
    final private String answer;

    public Answer(String answer) {
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

        Answer answer1 = (Answer) o;

        return answer != null ? answer.equals(answer1.answer) : answer1.answer == null;
    }

    @Override
    public int hashCode() {
        return answer != null ? answer.hashCode() : 0;
    }
}
