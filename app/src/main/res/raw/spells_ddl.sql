-- Example query: get all spells that have a "V" component
-- SELECT spell.name, spell.description
-- FROM spell
-- INNER JOIN spell_component ON spell.rowid = spell_component.spell_id
-- WHERE spell_component.component_id = (SELECT rowid FROM component WHERE symbol LIKE "V");

DROP TABLE IF EXISTS spell;
CREATE TABLE spell (
	concentration INTEGER NOT NULL,
	description TEXT NOT NULL,
	higher_level_description TEXT,
	duration TEXT,
	casting_time TEXT,
	reaction_condition TEXT,
	name TEXT NOT NULL UNIQUE,
	ritual INTEGER NOT NULL,
	school INTEGER NOT NULL,
	level INTEGER NOT NULL,
	range TEXT,
	materials TEXT,
	materials_cost INTEGER
);

DROP TABLE IF EXISTS school;
CREATE TABLE school (
	name TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS spell_target;
CREATE TABLE spell_target (
	spell_id INTEGER NOT NULL,
	target_id INTEGER NOT NULL,
	UNIQUE(spell_id,target_id)
);

DROP TABLE IF EXISTS target;
CREATE TABLE target (
	type TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS spell_ability;
CREATE TABLE spell_ability (
	spell_id INTEGER NOT NULL,
	ability_id INTEGER NOT NULL,
	UNIQUE(spell_id,ability_id)
);

DROP TABLE IF EXISTS ability;
CREATE TABLE ability (
	shortname TEXT NOT NULL UNIQUE,
	name TEXT NOT NULL
);

DROP TABLE IF EXISTS spell_attack_type;
CREATE TABLE spell_attack_type (
	spell_id INTEGER NOT NULL,
	attack_type_id INTEGER NOT NULL,
	UNIQUE(spell_id,attack_type_id)
);

DROP TABLE IF EXISTS attack_type;
CREATE TABLE attack_type (
	type TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS spell_damage_type;
CREATE TABLE spell_damage_type (
	spell_id INTEGER NOT NULL,
	damage_type_id INTEGER NOT NULL,
	UNIQUE(spell_id,damage_type_id)
);

DROP TABLE IF EXISTS damage_type;
CREATE TABLE damage_type (
	type TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS spell_condition;
CREATE TABLE spell_condition (
	spell_id INTEGER NOT NULL,
	condition_id INTEGER NOT NULL,
	UNIQUE(spell_id,condition_id)
);

DROP TABLE IF EXISTS condition;
CREATE TABLE condition (
	name TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS spell_source;
CREATE TABLE spell_source (
	spell_id INTEGER NOT NULL,
	source_id INTEGER NOT NULL,
	UNIQUE(spell_id,source_id)
);

DROP TABLE IF EXISTS source;
CREATE TABLE source (
	shortname TEXT NOT NULL UNIQUE,
	name TEXT NOT NULL
);

DROP TABLE IF EXISTS spell_class_list;
CREATE TABLE spell_class_list (
	spell_id INTEGER NOT NULL,
	class_list_id INTEGER NOT NULL,
	UNIQUE(spell_id,class_list_id)
);

DROP TABLE IF EXISTS class_list;
CREATE TABLE class_list (
	class TEXT NOT NULL UNIQUE
);

DROP TABLE IF EXISTS spell_component;
CREATE TABLE spell_component (
	spell_id INTEGER NOT NULL,
	component_id INTEGER NOT NULL,
	UNIQUE(spell_id,component_id)
);

DROP TABLE IF EXISTS component;
CREATE TABLE component (
	symbol TEXT NOT NULL UNIQUE,
	name TEXT NOT NULL
);
