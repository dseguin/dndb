package ca.printf.dndb.list;

import java.util.Comparator;
import ca.printf.dndb.entity.Spell;

public class SpellSortComparator implements Comparator<Spell> {
    public static final String SORT_NAME = "Spell Name";
    public static final String SORT_LEVEL = "Level";
    public static final String SORT_SCHOOL = "School";
    public static final String SORT_DURATION = "Duration";
    public static final String SORT_CASTTIME = "Casting Time";
    public static final String SORT_RANGE = "Range";
    public static final String SORT_MATCOST = "Material Cost";
    private String sortCondition;
    private boolean reverseOrder;

    public SpellSortComparator() {
        this(SORT_NAME);
    }

    public SpellSortComparator(String sortCondition) {
        this.sortCondition = sortCondition;
        this.reverseOrder = false;
    }

    public SpellSortComparator(String sortCondition, boolean reverseOrder) {
        this.sortCondition = sortCondition;
        this.reverseOrder = reverseOrder;
    }

    public void setSortCondition(String sortCondition) {
        this.sortCondition = sortCondition;
    }

    public void setReverseOrder(boolean sortDescending) {
        this.reverseOrder = sortDescending;
    }

    public int compare(Spell o1, Spell o2) {
        Spell s1 = reverseOrder ? o2 : o1;
        Spell s2 = reverseOrder ? o1 : o2;
        switch(sortCondition) {
            case SORT_LEVEL :
                return s1.getLevel() - s2.getLevel();
            case SORT_SCHOOL :
                return s1.getSchool().compareTo(s2.getSchool());
            case SORT_DURATION :
                return s1.getDuration().compareTo(s2.getDuration());
            case SORT_CASTTIME :
                return s1.getCastTime().compareTo(s2.getCastTime());
            case SORT_RANGE :
                return s1.getRange().compareTo(s2.getRange());
            case SORT_MATCOST :
                return s1.getMaterialsCost() - s2.getMaterialsCost();
            default :
                return s1.getName().compareTo(s2.getName());
        }
    }
}
