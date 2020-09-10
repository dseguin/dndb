package ca.printf.dndb.logic;

import java.util.ArrayList;
import ca.printf.dndb.entity.Spell;

public class SpellFilterLogic {
    public static final String DEFAULT_OPTION_LEVEL = "Level";
    public static final String DEFAULT_OPTION_SCHOOL = "School";
    public static final String DEFAULT_OPTION_DURATION = "Duration";
    public static final String DEFAULT_OPTION_CASTTIME = "Casting Time";
    public static final String DEFAULT_OPTION_TARGET = "Target";
    public static final String DEFAULT_OPTION_ABILITY = "Saving Throw";
    public static final String DEFAULT_OPTION_ATKTYPE = "Attack Type";
    public static final String DEFAULT_OPTION_DMGTYPE = "Damage Type";
    public static final String DEFAULT_OPTION_CONDITION = "Condition";
    public static final String DEFAULT_OPTION_SOURCE = "Source";
    public static final String DEFAULT_OPTION_CLASS = "Class";
    private String searchphrase;
    private boolean checkdesc;
    private boolean checkmats;
    private boolean isritual;
    private boolean isconcentration;
    private int level;
    private String school;
    private String duration;
    private String casttime;
    private String target;
    private String save;
    private String atktype;
    private String dmgtype;
    private String condition;
    private String source;
    private String spellclass;
    private boolean isverbal;
    private boolean issomatic;
    private boolean ismaterial;
    private boolean excludecomps;
    private int mincost;
    private int maxcost;
    private String range;

    public SpellFilterLogic() {}

    public void execFilter() {
        ArrayList<Spell> spellsFiltered = new ArrayList<>();
        for(int i = 0; i < SpellListController.size(); i++) {
            Spell s = SpellListController.get(i);
            if(!searchphrase.isEmpty()) {
                if(!s.getName().toLowerCase().contains(searchphrase)
                        && !(checkdesc && s.getDesc().toLowerCase().contains(searchphrase))
                        && !(checkmats && s.getMaterials() != null && s.getMaterials().toLowerCase().contains(searchphrase)))
                    continue;
            }
            if(isconcentration && !s.isConcentration())
                continue;
            if(isritual && !s.isRitual())
                continue;
            if(level > -1 && s.getLevel() != level)
                continue;
            if(!school.contains(DEFAULT_OPTION_SCHOOL) && !school.equals(s.getSchool()))
                continue;
            if(!duration.contains(DEFAULT_OPTION_DURATION) && !duration.equals(s.getDuration()))
                continue;
            if(!casttime.contains(DEFAULT_OPTION_CASTTIME) && !casttime.equals(s.getCastTime()))
                continue;
            if(!target.contains(DEFAULT_OPTION_TARGET)) {
                boolean found = false;
                for(String t : s.getTargets()) {
                    if(found = target.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(!save.contains(DEFAULT_OPTION_ABILITY)) {
                boolean found = false;
                for(String t : s.getAbilitySaves()) {
                    if(found = save.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(!atktype.contains(DEFAULT_OPTION_ATKTYPE)) {
                boolean found = false;
                for(String t : s.getAtkTypes()) {
                    if(found = atktype.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(!dmgtype.contains(DEFAULT_OPTION_DMGTYPE)) {
                boolean found = false;
                for(String t : s.getDmgTypes()) {
                    if(found = dmgtype.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(!condition.contains(DEFAULT_OPTION_CONDITION)) {
                boolean found = false;
                for(String t : s.getConditions()) {
                    if(found = condition.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(!source.contains(DEFAULT_OPTION_SOURCE)) {
                boolean found = false;
                for(String t : s.getSources().keySet()) {
                    if(found = source.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(!spellclass.contains(DEFAULT_OPTION_CLASS)) {
                boolean found = false;
                for(String t : s.getClasses()) {
                    if(found = spellclass.equals(t))
                        break;
                }
                if(!found)
                    continue;
            }
            if(isverbal && ((!s.isVerbal() && !excludecomps) || (s.isVerbal() && excludecomps)))
                continue;
            if(issomatic && ((!s.isSomatic() && !excludecomps) || (s.isSomatic() && excludecomps)))
                continue;
            if(ismaterial && ((!s.isMaterial() && !excludecomps) || (s.isMaterial() && excludecomps)))
                continue;
            if(mincost > -1 && (!s.isMaterial() || s.getMaterialsCost() < mincost))
                continue;
            if(maxcost > -1 && (!s.isMaterial() || s.getMaterialsCost() > maxcost))
                continue;
            if(!range.isEmpty() && !s.getRange().toLowerCase().contains(range))
                continue;
            spellsFiltered.add(s);
        }
        SpellListController.setSpellList(spellsFiltered);
    }

    public void setSearchphrase(String searchphrase) {
        this.searchphrase = searchphrase.toLowerCase();
    }

    public void setCheckdesc(boolean checkdesc) {
        this.checkdesc = checkdesc;
    }

    public void setCheckmats(boolean checkmats) {
        this.checkmats = checkmats;
    }

    public void setIsritual(boolean isritual) {
        this.isritual = isritual;
    }

    public void setIsconcentration(boolean isconcentration) {
        this.isconcentration = isconcentration;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setCasttime(String casttime) {
        this.casttime = casttime;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public void setSave(String save) {
        this.save = save;
    }

    public void setAtktype(String atktype) {
        this.atktype = atktype;
    }

    public void setDmgtype(String dmgtype) {
        this.dmgtype = dmgtype;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSpellclass(String spellclass) {
        this.spellclass = spellclass;
    }

    public void setIsverbal(boolean isverbal) {
        this.isverbal = isverbal;
    }

    public void setIssomatic(boolean issomatic) {
        this.issomatic = issomatic;
    }

    public void setIsmaterial(boolean ismaterial) {
        this.ismaterial = ismaterial;
    }

    public void setExcludecomps(boolean excludecomps) {
        this.excludecomps = excludecomps;
    }

    public void setMincost(int mincost) {
        this.mincost = mincost;
    }

    public void setMaxcost(int maxcost) {
        this.maxcost = maxcost;
    }

    public void setRange(String range) {
        this.range = range.toLowerCase();
    }
}
