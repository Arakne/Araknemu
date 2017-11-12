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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pseudo pseudo1 = (Pseudo) o;

        return pseudo != null ? pseudo.equals(pseudo1.pseudo) : pseudo1.pseudo == null;
    }

    @Override
    public int hashCode() {
        return pseudo != null ? pseudo.hashCode() : 0;
    }
}
