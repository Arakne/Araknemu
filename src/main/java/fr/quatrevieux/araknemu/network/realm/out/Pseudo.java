package fr.quatrevieux.araknemu.network.realm.out;

/**
 * Send to the client its pseudo
 */
final public class Pseudo {
    final private String pseudo;

    public Pseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    public String toString(){
        return "Ad" + pseudo;
    }
}
