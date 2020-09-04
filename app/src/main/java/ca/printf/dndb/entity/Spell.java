package ca.printf.dndb.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ca.printf.dndb.data.DndbSQLManager;

public class Spell implements Serializable {
    private static final long serialVersionUID = 1L;
    // Columns in "spell" table
    public static final String COL_ID = DndbSQLManager.TABLE_SPELL + ".rowid";
    public static final String COL_NAME = DndbSQLManager.TABLE_SPELL + ".name";
    public static final String COL_DESC = DndbSQLManager.TABLE_SPELL + ".description";
    public static final String COL_HIGHER_DESC = DndbSQLManager.TABLE_SPELL + ".higher_level_description";
    public static final String COL_LEVEL = DndbSQLManager.TABLE_SPELL + ".level";
    public static final String COL_CONCENTRATION = DndbSQLManager.TABLE_SPELL + ".concentration";
    public static final String COL_RITUAL = DndbSQLManager.TABLE_SPELL + ".ritual";
    public static final String COL_RANGE = DndbSQLManager.TABLE_SPELL + ".range";
    public static final String COL_DURATION = DndbSQLManager.TABLE_SPELL + ".duration";
    public static final String COL_CAST_TIME = DndbSQLManager.TABLE_SPELL + ".casting_time";
    public static final String COL_REACTION_DESC = DndbSQLManager.TABLE_SPELL + ".reaction_condition";
    public static final String COL_MATERIALS = DndbSQLManager.TABLE_SPELL + ".materials";
    public static final String COL_MATERIALS_COST = DndbSQLManager.TABLE_SPELL + ".materials_cost";
    // Columns in "school" table
    public static final String COL_SCHOOL = DndbSQLManager.TABLE_SCHOOL + ".name";
    public static final String JOIN_SCHOOL = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_SCHOOL);
    // Columns in "target" table
    public static final String COL_TARGET = DndbSQLManager.TABLE_TARGET + ".type";
    public static final String JOIN_TARGET = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_TARGET, DndbSQLManager.TABLE_SPELL_TARGET);
    // Columns in "ability" table
    public static final String COL_ABILITY_SHORTNAME = DndbSQLManager.TABLE_ABILITY + ".shortname";
    public static final String COL_ABILITY_FULLNAME = DndbSQLManager.TABLE_ABILITY + ".name";
    public static final String JOIN_ABILITY = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_ABILITY, DndbSQLManager.TABLE_SPELL_ABILITY);
    // Columns in "attack_type" table
    public static final String COL_ATK_TYPE = DndbSQLManager.TABLE_ATTACK_TYPE + ".type";
    public static final String JOIN_ATK_TYPE = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_ATTACK_TYPE, DndbSQLManager.TABLE_SPELL_ATTACK_TYPE);
    // Columns in "damage_type" table
    public static final String COL_DMG_TYPE = DndbSQLManager.TABLE_DAMAGE_TYPE + ".type";
    public static final String JOIN_DMG_TYPE = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_DAMAGE_TYPE, DndbSQLManager.TABLE_SPELL_DAMAGE_TYPE);
    // Columns in "condition" table
    public static final String COL_CONDITION = DndbSQLManager.TABLE_CONDITION + ".name";
    public static final String JOIN_CONDITION = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_CONDITION, DndbSQLManager.TABLE_SPELL_CONDITION);
    // Columns in "source" table
    public static final String COL_SOURCE_SHORTNAME = DndbSQLManager.TABLE_SOURCE + ".shortname";
    public static final String COL_SOURCE_FULLNAME = DndbSQLManager.TABLE_SOURCE + ".name";
    public static final String JOIN_SOURCE = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_SOURCE, DndbSQLManager.TABLE_SPELL_SOURCE);
    // Columns in "class_list" table
    public static final String COL_CLASS = DndbSQLManager.TABLE_CLASS_LIST + ".class";
    public static final String JOIN_CLASS = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_CLASS_LIST, DndbSQLManager.TABLE_SPELL_CLASS_LIST);
    // Columns in "component" table
    public static final String COL_COMPONENT_SYMBOL = DndbSQLManager.TABLE_COMPONENT + ".symbol";
    public static final String COL_COMPONENT_NAME = DndbSQLManager.TABLE_COMPONENT + ".name";
    public static final String JOIN_COMPONENT = JOIN_SPELL_TABLE(DndbSQLManager.TABLE_COMPONENT, DndbSQLManager.TABLE_SPELL_COMPONENT);
    // Spell table columns for queries
    public static final String[] QUERY_SPELL_COLS = {COL_ID, COL_NAME, COL_DESC,
            COL_HIGHER_DESC, COL_CONCENTRATION, COL_RITUAL, COL_RANGE, COL_DURATION, COL_LEVEL,
            COL_SCHOOL + " AS school_name", COL_CAST_TIME, COL_REACTION_DESC, COL_MATERIALS, COL_MATERIALS_COST};
    public static final String[] QUERY_TARGET_COLS = {COL_TARGET};
    public static final String[] QUERY_ABILITY_COLS = {COL_ABILITY_SHORTNAME, COL_ABILITY_FULLNAME};
    public static final String[] QUERY_ATK_TYPE_COLS = {COL_ATK_TYPE};
    public static final String[] QUERY_DMG_TYPE_COLS = {COL_DMG_TYPE};
    public static final String[] QUERY_CONDITION_COLS = {COL_CONDITION};
    public static final String[] QUERY_SOURCE_COLS = {COL_SOURCE_SHORTNAME, COL_SOURCE_FULLNAME};
    public static final String[] QUERY_CLASS_COLS = {COL_CLASS};
    public static final String[] QUERY_COMPONENT_COLS = {COL_COMPONENT_SYMBOL, COL_COMPONENT_NAME};
    // Attribute options for filter spinners
    public static final String QUERY_LEVEL_OPTIONS = QUERY_ATTR_OPTIONS(COL_LEVEL, DndbSQLManager.TABLE_SPELL);
    public static final String QUERY_SCHOOL_OPTIONS = QUERY_ATTR_OPTIONS(COL_SCHOOL, DndbSQLManager.TABLE_SCHOOL);
    public static final String QUERY_DURATION_OPTIONS = QUERY_ATTR_OPTIONS(COL_DURATION, DndbSQLManager.TABLE_SPELL);
    public static final String QUERY_CASTTIME_OPTIONS = QUERY_ATTR_OPTIONS(COL_CAST_TIME, DndbSQLManager.TABLE_SPELL);
    public static final String QUERY_TARGET_OPTIONS = QUERY_ATTR_OPTIONS(COL_TARGET, DndbSQLManager.TABLE_TARGET);
    public static final String QUERY_ABILITY_OPTIONS = QUERY_ATTR_OPTIONS(COL_ABILITY_SHORTNAME, DndbSQLManager.TABLE_ABILITY);
    public static final String QUERY_ATK_TYPE_OPTIONS = QUERY_ATTR_OPTIONS(COL_ATK_TYPE, DndbSQLManager.TABLE_ATTACK_TYPE);
    public static final String QUERY_DMG_TYPE_OPTIONS = QUERY_ATTR_OPTIONS(COL_DMG_TYPE, DndbSQLManager.TABLE_DAMAGE_TYPE);
    public static final String QUERY_CONDITION_OPTIONS = QUERY_ATTR_OPTIONS(COL_CONDITION, DndbSQLManager.TABLE_CONDITION);
    public static final String QUERY_SOURCE_OPTIONS = QUERY_ATTR_OPTIONS(COL_SOURCE_SHORTNAME, DndbSQLManager.TABLE_SOURCE);
    public static final String QUERY_CLASS_OPTIONS = QUERY_ATTR_OPTIONS(COL_CLASS, DndbSQLManager.TABLE_CLASS_LIST);
    // Instance vars
    private long id;
    private String name;
    private String desc;
    private String higher_desc;
    private int level;
    private boolean concentration;
    private boolean ritual;
    private String range;
    private ArrayList<String> targets = new ArrayList<>();
    private String duration;
    private String cast_time;
    private String reaction_desc;
    private String school;
    private boolean comp_v;
    private boolean comp_s;
    private boolean comp_m;
    private String materials;
    private int materials_cost;
    private ArrayList<String> ability_saves = new ArrayList<>();
    private ArrayList<String> atk_types = new ArrayList<>();
    private ArrayList<String> dmg_types = new ArrayList<>();
    private ArrayList<String> conditions = new ArrayList<>();
    // <SHORTNAME, FULLNAME> (shortname for list/selection, fullname for details)
    private Map<String, String> sources = new HashMap<>();
    private ArrayList<String> classes = new ArrayList<>();

    public Spell(long id) {this.id = id;}
    public Spell() {this.id = -1;}

    public long getId() {return id;}
    public String getName() {return name;}
    public String getDesc() {return desc;}
    public String getHigherDesc() {return higher_desc;}
    public int getLevel() {return level;}
    public boolean isConcentration() {return concentration;}
    public boolean isRitual() {return ritual;}
    public String getRange() {return range;}
    public ArrayList<String> getTargets() {return targets;}
    public String getDuration() {return duration;}
    public String getCastTime() {return cast_time;}
    public String getReactionDesc() {return reaction_desc;}
    public String getSchool() {return school;}
    public boolean isVerbal() {return comp_v;}
    public boolean isSomatic() {return comp_s;}
    public boolean isMaterial() {return comp_m;}
    public String getMaterials() {return materials;}
    public int getMaterialsCost() {return materials_cost;}
    public ArrayList<String> getAbilitySaves() {return ability_saves;}
    public ArrayList<String> getAtkTypes() {return atk_types;}
    public ArrayList<String> getDmgTypes() {return dmg_types;}
    public ArrayList<String> getConditions() {return conditions;}
    public Map<String, String> getSources() {return sources;}
    public ArrayList<String> getClasses() {return classes;}

    public void setId(long id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setDesc(String desc) {this.desc = desc;}
    public void setHigherDesc(String higher_desc) {this.higher_desc = higher_desc;}
    public void setLevel(int level) {this.level = level;}
    public void setConcentration(boolean concentration) {this.concentration = concentration;}
    public void setRitual(boolean ritual) {this.ritual = ritual;}
    public void setRange(String range) {this.range = range;}
    public void setTargets(ArrayList<String> targets) {this.targets = targets;}
    public void setDuration(String duration) {this.duration = duration;}
    public void setCastTime(String cast_time) {this.cast_time = cast_time;}
    public void setReactionDesc(String reaction_desc) {this.reaction_desc = reaction_desc;}
    public void setSchool(String school) {this.school = school;}
    public void setVerbal(boolean comp_v) {this.comp_v = comp_v;}
    public void setSomatic(boolean comp_s) {this.comp_s = comp_s;}
    public void setMaterial(boolean comp_m) {this.comp_m = comp_m;}
    public void setMaterials(String materials) {this.materials = materials;}
    public void setMaterialsCost(int materials_cost) {this.materials_cost = materials_cost;}
    public void setAbilitySaves(ArrayList<String> ability_saves) {this.ability_saves = ability_saves;}
    public void setAtkTypes(ArrayList<String> atk_types) {this.atk_types = atk_types;}
    public void setDmgTypes(ArrayList<String> dmg_types) {this.dmg_types = dmg_types;}
    public void setConditions(ArrayList<String> conditions) {this.conditions = conditions;}
    public void setSources(Map<String, String> sources) {this.sources = sources;}
    public void setClasses(ArrayList<String> classes) {this.classes = classes;}

    private static final String JOIN_SPELL_TABLE(final String TABLE) {
        return "INNER JOIN " + TABLE + " ON " + TABLE + ".rowid = "
                + DndbSQLManager.TABLE_SPELL + "." + TABLE;
    }

    private static final String JOIN_SPELL_TABLE(final String TABLE, final String JOIN_TABLE) {
        return "INNER JOIN " + JOIN_TABLE + " ON " + DndbSQLManager.TABLE_SPELL + ".rowid = "
                + JOIN_TABLE + "." + DndbSQLManager.TABLE_SPELL + "_id INNER JOIN " + TABLE + " ON "
                + TABLE + ".rowid = " + JOIN_TABLE + "." + TABLE + "_id";
    }

    private static final String COLATE_COLS(final String[] COLS) {
        String ret = "";
        for(String s : COLS)
            ret += (s + ",");
        if(ret.isEmpty())
            return ret;
        return ret.substring(0, ret.length() - 1);
    }

    private static String createWhereClause(String spellname) {
        if(spellname == null || spellname.isEmpty())
            return " ";
        spellname = ca.printf.dndb.data.CommonIO.sanitizeString(spellname);
        return " WHERE " + COL_NAME + " LIKE '" + spellname + "'";
    }

    public static String querySpell() {
        return querySpell(null);
    }

    public static String querySpell(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_SPELL_COLS) + " FROM " + DndbSQLManager.TABLE_SPELL
                + " " + JOIN_SCHOOL + createWhereClause(spellname) + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryComponent(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_COMPONENT_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_COMPONENT + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryTarget(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_TARGET_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_TARGET + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryAbility(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_ABILITY_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_ABILITY + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryAttackType(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_ATK_TYPE_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_ATK_TYPE + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryDamageType(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_DMG_TYPE_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_DMG_TYPE + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryCondition(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_CONDITION_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_CONDITION + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String querySource(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_SOURCE_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_SOURCE + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    public static String queryClass(String spellname) {
        return "SELECT " + COLATE_COLS(QUERY_CLASS_COLS) + " FROM "
                + DndbSQLManager.TABLE_SPELL + " " + JOIN_CLASS + createWhereClause(spellname)
                + " ORDER BY " + COL_NAME + ";";
    }

    private static final String QUERY_ATTR_OPTIONS(final String COL, final String TABLE) {
        return "SELECT DISTINCT(" + COL + ") FROM " + TABLE + " ORDER BY " + COL + ";";
    }
}