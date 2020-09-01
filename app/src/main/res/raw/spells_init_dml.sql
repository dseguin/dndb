BEGIN TRANSACTION;

DELETE FROM spell;
DELETE FROM school;
DELETE FROM spell_target;
DELETE FROM target;
DELETE FROM spell_ability;
DELETE FROM ability;
DELETE FROM spell_attack_type;
DELETE FROM attack_type;
DELETE FROM spell_damage_type;
DELETE FROM damage_type;
DELETE FROM spell_condition;
DELETE FROM condition;
DELETE FROM spell_source;
DELETE FROM source;
DELETE FROM spell_class_list;
DELETE FROM class_list;
DELETE FROM spell_component;
DELETE FROM component;

INSERT INTO school (name) VALUES
    ('Abjuration'),
    ('Conjuration'),
    ('Divination'),
    ('Enchantment'),
    ('Evocation'),
    ('Illusion'),
    ('Necromancy'),
    ('Transmutation');

INSERT INTO target (type) VALUES
    ('Self'),
    ('Creature'),
    ('Object'),
    ('Point in space'),
    ('Corpse');

INSERT INTO ability (shortname,name) VALUES
    ('STR','Strength'),
    ('DEX','Dexterity'),
    ('CON','Constitution'),
    ('INT','Intelligence'),
    ('WIS','Wisdom'),
    ('CHA','Charisma');

INSERT INTO attack_type (type) VALUES
    ('Melee Weapon'),
    ('Melee Spell'),
    ('Ranged Weapon'),
    ('Ranged Spell');

INSERT INTO damage_type (type) VALUES
    ('Bludgeoning'),
    ('Piercing'),
    ('Slashing'),
    ('Acid'),
    ('Cold'),
    ('Fire'),
    ('Force'),
    ('Lightning'),
    ('Necrotic'),
    ('Poison'),
    ('Psychic'),
    ('Radiant'),
    ('Thunder'),
    ('Healing'),
    ('Raw');

INSERT INTO condition (name) VALUES
    ('Blinded'),
    ('Charmed'),
    ('Confused'),
    ('Deafened'),
    ('Exhaustion'),
    ('Frightened'),
    ('Grappled'),
    ('Incapacitated'),
    ('Invisible'),
    ('Paralyzed'),
    ('Petrified'),
    ('Poisoned'),
    ('Prone'),
    ('Restrained'),
    ('Stunned'),
    ('Unconscious');

INSERT INTO class_list (class) VALUES
    ('Bard'),
    ('Cleric'),
    ('Druid'),
    ('Paladin'),
    ('Ranger'),
    ('Sorcerer'),
    ('Warlock'),
    ('Wizard'),
    ('Artificer');

INSERT INTO component (symbol,name) VALUES
    ('V','Verbal'),
    ('S','Somatic'),
    ('M','Material');

COMMIT;
