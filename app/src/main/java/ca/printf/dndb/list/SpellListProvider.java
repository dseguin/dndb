package ca.printf.dndb.list;

import java.util.Comparator;
import ca.printf.dndb.entity.Spell;

public interface SpellListProvider {
    int size();
    Spell get(int index);
    void sort(Comparator<Spell> c);
}
