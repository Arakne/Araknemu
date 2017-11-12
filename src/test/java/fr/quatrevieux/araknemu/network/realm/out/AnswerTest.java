package fr.quatrevieux.araknemu.network.realm.out;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnswerTest {
    @Test
    void answer() {
        String s = "my answer";
        Answer a = new Answer(s);

        assertSame(s, a.answer());
    }

    @Test
    void test_toString() {
        assertEquals("AQmy+answer", new Answer("my answer").toString());
    }
}
