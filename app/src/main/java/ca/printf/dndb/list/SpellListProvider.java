package ca.printf.dndb.list;

import java.util.Comparator;
import ca.printf.dndb.entity.Spell;

public interface SpellListProvider {
    public int size();
    public Spell get(int index);
    public void sort(Comparator<Spell> c);
}
