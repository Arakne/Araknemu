package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionTest {
    @Test
    void answer() {
        String s = "my answer";
        Question a = new Question(s);

        assertSame(s, a.answer());
    }

    @Test
    void test_toString() {
        assertEquals("AQmy+answer", new Question("my answer").toString());
    }
}
