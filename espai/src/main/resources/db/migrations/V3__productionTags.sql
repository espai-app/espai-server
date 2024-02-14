/**
 * Author:  rborowski
 * Created: 22.12.2023
 */

ALTER TABLE PRODUCTIONTAG ADD COLUMN POSITION INTEGER DEFAULT 1;

CREATE TABLE PRODUCTIONTAGTEMPLATE (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(50) DEFAULT NULL,
  PRESETS TEXT DEFAULT NULL,
  EXCLUSIVE TINYINT DEFAULT NULL
);
CREATE SEQUENCE productionTagTemplate_id_seq START WITH 1 INCREMENT BY 0;

INSERT INTO PRODUCTIONTAGTEMPLATE (ID, CREATEUSER, CREATEDATE, MODIFYUSER, MODIFYDATE, `NAME`, PRESETS, EXCLUSIVE) 
	VALUES (nextval(productionTagTemplate_id_seq), DEFAULT, DEFAULT, DEFAULT, DEFAULT, 'Genre', 'Literaturverfilmung
Kinderfilm
Weihnachtsfilm
Kurzfilmprogramm
Animationsfilm
Literaturadaption
Abenteuerfilm
Kinderbuchverfilmung
Komödie
Märchen
Dokumentarfilm mit Spielfilmelementen
Dokumentarfilm
Stummfilm
Kinder- und Familienfilm
Drama
Tierfilm
Roadmovie
Kinder- und Jugendfilm
Road Movie
Coming-of-Age
Tragikomödie
Kinderkrimi
Essayfilm
Comicverfilmung
Jugendfilm
Tanzfilm
Fantasy
Superheld*innen Film
(Historien-)Drama
Familienkomödie
Kinder-und Jugendfilm
Biografie
(Anti-)Kriegsfilm
dokumentarische Form
Coming of Age
Musikfilm
Experimentalfilm
Filmklassiker
Biopic
Thriller
Dokumentarischer Spielfilm
Science Fiction
Zeichentrickfilm
Autobiografie
historische Biografie
Romanze
Liebesfilm
Historienfilm
Drama mit dokumentarischen Elementen
Polit-Drama
(Sozial-)Drama
Musical
Anime
Satire
Gesellschaftssatire
Melodram
(Jugend-)Drama
Kriminalfilm
Trickfilm
Abenteuer- und Familienfilm
Liebeskomödie', DEFAULT);

INSERT INTO PRODUCTIONTAGTEMPLATE (ID, CREATEUSER, CREATEDATE, MODIFYUSER, MODIFYDATE, `NAME`, PRESETS, EXCLUSIVE) 
	VALUES (nextval(productionTagTemplate_id_seq), DEFAULT, DEFAULT, DEFAULT, DEFAULT, 'Themen', 'Weihnachten
Freundschaft
Winter
Abenteuer
Tiere
Aufwachsen
Vertrauen
Selbstwirksamkeit
Selbstständigkeit
Fantasie
Familie
Kindheit
Zusammenhalt
Mut
Kindheit/Kinder
Mädchen
Werte
Umzug
Zuhause
Stadt-Land
Großstadt
Märchen
Geschwister
Einsamkeit
Magie
Freiheit
Träume
Sport
Natur
Umwelt
Musik
Tanz
Begegnung
Heimat
Filmsprache
Geburtstag
Bauernhof
Reise
Gespenster
Lüge
Wahrheit
Literaturverfilmung
Außenseiter
Erziehung
Identität
Selbstbewusstsein
Schule
Vorurteile
Freundschaft und Zusammenhalt
Ausgrenzung
Toleranz
Miteinander leben
Ziele und Träume
Lehrer
Anderssein
Mobbing
Diskriminierung
Literatur
Barmherzigkeit
Wasser
fremde Länder und Kulturen
Planet Erde
Vulkane
Meer
Klimawandel
Wissenschaft
Konfliktlösung
Forschergeist
logisches Denken
Empathie
Berufe
Alter
Sozialtraining
Krimi
Gemeinschaft
Ökologie
Heldentum
Wünsche
Fremdheit
Konflikt/Konfliktbewältigung
Literaturadaption
Geister
Selbstlosigkeit
Verantwortung
Ostern
Psychische Erkrankung
Naturschutz
Lebensräume
Tradition
Moderne
Faszination Weltraum
Kurzfilm
Stummfilm
Filmgeschichte
Raumfahrt
neue Technologien
Tierschutz
Vegetarismus
Ehrgeiz
Animationstechniken
Erwachsenwerden
Tod/Sterben
Trauer
Reisen
Chancengleichheit
Zukunft
Integration
Rassismus
Leistungsdruck
Kinder
Jugendliche
soziale Ungleichheit
Einwanderungsgesellschaft
Artenvielfalt
Gender/Geschlechterrollen
Pubertät
Vorbilder
Mutproben
Dialekte
Migrationsgesellschaft
Generationen
Menschen mit Behinderung
erste Liebe
Solidarität
Ängste
Akzeptanz von Unterschiedlichkeit
Leben
Tod
alleinerziehende Eltern
Alltag
Arbeit
Armut
Wohlstand
Gerechtigkeit
Biotope
Lebensraum
Ökosystem Wald
Haustiere
Beziehung Mensch-Tier
Land-Stadt-Konflikt
Scheidung
Liebe
Erwachsenerden
alternative Lebensformen
getrennte Eltern
Empowerment
Anerkennung
Gewalt
Waisenkinder
Zweiter Weltkrieg
(Helden-)Reise
eigene Stärken
Träume und Ziele
Idole
Superheld*innen/Superkräfte
Krankheit
Sterben/Tod
Film/Filmschaffen
Kino
Afrika
Demenz
Alleinsein/Einsamkeit
Ausbeutung
Globalisierung
Mongolei
Talentwettbewerb
Migration
Menschen ohne Papiere
Flucht/Geflüchtete
Flucht
Teamgeist
Umweltschutz
Kriminalität
Forschung
Kinderrechte
kulturelle Vielfalt
Arbeit/Kinderarbeit
Bildung
Utopien
Innovation
Gesundheit
Geschlechtergerechtigkeit
Nachhaltigkeit
Umwelt/Umweltschutz
Tiere/Tierschutz
Tierhaltung
Tierrechte
Ethik
Ernährung
industrielle vs. ökologische Landwirtschaft
biologische Vielfalt
Emanzipation
Erwachsen-werden
Glück
Philosophie
Animation
Abschiednehmen
Inklusion
Behinderung
Taub sein
CODA
Audismus
Ableismus
Selbstfindung
Astronomie
Asteroid
Freikirchen
Russlanddeutsche
hohe Begabung
Erinnerung
Ferien
Verlust
Geheimnisse
Jugoslawien-Kriege
Serbien
Kroatien
Individuum (und Gesellschaft)
Transgender
fremde Kulturen
Kommunikation
Jugend
Delinquenz
Vater-Sohn-Konflikt
Sexualität
Geschlechterrollen
Hoffnung
Adoption
(Deutsche) Geschichte
Trauer/Trauerarbeit
Biografie
Superhelden
Superkraft
Wissenschaft und Technik
Comics
Zukunftsvisionen
Energieversorgung
Mobilität
Demokratie
Nationalsozialismus
Antisemitismus
Judenverfolgung
Zuversicht
Krieg
Tagebau
Umsiedlung
Komödie
Witze
(Un-)Angepasstheit
Gesellschaft
Landschaftstypen
Biodiversität
Kulturlandschaften
Renaturierung
Artensterben
bedrohte Arten
Ökosysteme
USA
Berufswelt
Paper Cut
Kapitalismuskritik
Theater
Schulsystem
Kreativität
NS-Diktatur
Verfolgung politisch Andersdenkender im Nationalsozialismus
Transitraum
Angst
Loyalität
Familienkonstellationen
Religion/Religiosität
Magersucht
Rebellion
Tiere und Natur
Eltern-Kind-Konflikt
Jugend/Jugendliche
Popkultur
American Dream
Religion
Stadt und Land
Landwirtschaft
Erwachsenenwerden
Pflege
Wert des Lebens
Menschenwürde
Mitgefühl
gutes Leben
Sinnsuche
globaler Süden
Chancengerechtigkeit
Berufswahl
Frauenrechte
Suche nach Identität
Wehmut
Naturerfahrung
Technik/neue Technologien
Konsum
Werbung
Manipulation
Medizin
Individuum und Gesellschaft
Modeindustrie
Kapitalismus
Homosexualität
sexuelle Identität
soziale Medien
Geschichte
Medien
Konsumverhalten
Handel
Forstwirtschaft (und Konsum)
Leistungssport
Filmklassiker
Zeit
Zufall
Filmgeschichte/Filmsprache
Berlin
Journalismus
Informationsfreiheit
(Massen-)Medien
Gesellschaft im Wandel
Idealismus
Geflüchtete
Recht auf Asyl
Abschiebung
Krieg/Kriegsfolgen
Libanon
Nahost-Konflikt
(erste) Liebe
Geopolitik
Israel
Konfessionalismus
Afghanistan
Waisen
Zeitgeschichte
Bürgerkrieg
Kalter Krieg
Bollywood
Internet
Influencer*innen
Wirtschaft
Sucht/Suchtgefahren
Hate-Speech
Angst/Ängste
Autismus
Neurodiversität
(In-)Toleranz
(Individuum und) Gesellschaft
Propaganda
Ideologie
Kinderarbeit
psychische Krankheiten
Sucht
Film
Filmemachen
Technik(Neue Technologien
Generationen/-konflikt
Zerstörung
Politik
Widerstand
Verschwörungsmythen
Fake-News
Klima
Forschung/Wissenschaft
Meeresspiegel
Gletscher
Grönland
Kunst
Kultur
Subkultur
Migration/Migrationsgesellschaft
Migrationsgeschichte
Rechtsextremismus
politischer Widerstand
Protest
Mikroplastik
Plastikmüll
Umweltverschmutzung
Ozeane
Lebensmut
Entführung
Tochter-Vater-Beziehung
Selbstbestimmung
Sterben
Indien
Wirtschaftswachstum
Armut und Reichtum
Stadt
Oper
klassische Musik
Musikgeschichte
Fantasy
Anne Frank
Holocaust
Fiktion und Realität
Gesellschaftskritik
Aktivismus
Macht
Machtmissbrauch
Ballett/Tanz
deutsche Geschichte
Architektur
Industrie
Verkehr
Freizeit
Judentum
Menschenrechte
Bürgerrechte
(US-amerikanische) Geschichte
Zivilcourage
Recht und Gerechtigkeit
Öffentlichkeit
Datenschutz
Macht/Machtgefüge
Helden
Weltall
DDR
Wiedervereinigung
Rollen- und Körperbilder
mediale Selbstdarstellung
Jugendkultur
Mensch und Umwelt
Umweltzerstörung
Erde
Pflanzen
Ressourcen
Terraforming
Moral
Klimaschutz
Engagement
Jugendbewegung
internationale Zusammenarbeit
Europa
Nordirland-Konflikt
Verlust(angst)
Arbeit/Arbeitslosikeit
Religionen
Film und Fernsehen
Artenschutz
Digitalisierung
Meere
Fluchtursachen
Meinungsfreiheit
Pressefreiheit
Ernährungssicherheit
Social Media
Zukunftspläne
Trennung der Eltern
LGBTQ+
Corona
Iran
Islam
Frauen
Todesstrafe
Menschenrechte/-würde
Bürgerrechtsbewegung
Naturwissenschaft
Antikriegsfilm
Generationenkonflikt
Verrat
Verfolgung
Recycling
Elektroindustrie
Elektroschrott
Rap/HipHop
Klassengemeinschaft
Schönheit
Männlichkeit
Authentizität
geteiltes Deutschland
deutsch-deutsche Grenze
Stasi
Diktatur
Schießbefehl
Denunziation
Kenia
Konzentrationslager
Anpassung
Coming-of Age
Trennung
Schwangerschaft
"""Ehrenmord"""
Leibarbeit/Arbeitsmigration
Lebensmittelproduktion
(deutsch-französische) Geschichte
Geheimdienst
Frankreich
Vichy-Regime
Aufklärung
Abtreibung
Krebs
Extremismus
Kampf (gegen rechts)
Naturkatastrophen
Ressourcenknappheit
Individuum (und Gesellschaft) Mensch und Umwelt
Ökonomie
Forschung/Wissen
künstliche Intelligenz
Interdisziplinarität
Spiritualität
Atomenergie
Protestbewegungen
Sowjetunion
Spionage
Bosnienkrieg
Genozid
Schuld
UNO
NATO
Völkerrecht
Hass
Nationalismus
Völkermord
Lüge & Wahrheit
Folter
Wert eines Menschen
Textilindustrie
Arbeitsrecht
Arbeitskampf
Gewerkschaften
Sozialpolitik
Patriarchat
Lieferketten(-gesetz)
Islamophobie
Intersektionalität
Diversität
Feminismus
Selbstbehauptung
Familienkonflikte
Prostitution
Gesundheitssystem
Terrorismus
Lebenskrisen
Drogen/Sucht
Plastik
Wertschöpfungsketten
Design
Jugend/Jugendliche/Jugendkultur
Überwachung
(häusliche) Gewalt
Stigmatisierung
Klischees
Selbstverwirklichung
Strukturwandel
Aufenthaltsrecht
Mode
Sozialismus
Rivalität
LGTBQ+
Parodie
Film-/Medienerziehung
Trauma/Traumabewältigung
Menschrechte
Freundinnenschaft
Zugehörigkeit
kulturelle Aneignung
Jugend/Jugendkultur
Rap
Drogen
Strafe/Strafvollzug
Konflikte/ Konfliktbewältigung
Identität
Rollenbilder
Individuum & Gesellschaft
LGBTIQ+
ADHS
Arbeitslosigkeit
Vater-Tochter-Beziehung
Depression
Klassengesellschaft
Film über Film
Humor
Katastrophen
Gruppendynamik
Lehrer-Schüler*innen-Verhältnis
soziale Rollen
Diplomatie
Vereinte Nationen
Populismus
Militär
Islam/Islamismus
9/11
Irak
Russland
Ukraine
Großbritannien
Chancenungleichheit
Elitarismus
Mut/Zivilcourage
Ost-West-Konflikt
politisches Erwachen
Exil
Unterdrückung
Entfremdung
Lust
Gewalt/sexualisierte Gewalt
Brauchtum
Recht/Gerechtigkeit
Justiz
Selbstjustiz
Sühne
Pflicht
Menschlichkeit
"„entartete"" Kunst"
Macht/Machtmissbrauch
Suizid
Schuld und Sühne
Geld
Finanzen
Kredit
New Economy
Schulden
Polizei
Weimarer Republik
NS-Ideologie
Rechtsterrorismus
Radikalisierung
Rechtspopulismus
Neue Rechte
Sprache
Syrien
ETA
Konfliktbewältigung
Separatismus
Spanien
Täter-Opfer-Beziehung
Vergebung
Strafvollzug
Selbstkonstituierung des Ich
Repräsentation
Öffentlichkeit und Privatraum
Melancholie
Erinnern
Verdrängen
Todesmärsche
Science-Fiction
Computer
Evolution
Verzweiflung
sexuelle Gewalt
Meinungs- und Pressefreiheit
Rechtsstaat
Fake News
Kurd*innen
Nachrichten
Kurdischer Konflikt
Entfremdung/Distanzierung
Peer Groups
Schuldgefühle
Verdrängung
Trauerbewältigung
LGBTQIA*
Partnerschaft
Invasion und Besatzung
Christentum
Trauma
Schuld (und Sühne)
Erster Weltkrieg
Strafe
Psychologie
künstliches Bewusstsein
Verhaltenskodex für KI
Unterscheidbarkeit Mensch-Maschine
Verhältnis Mensch-Roboter
ethische Normen in Bezug auf KI
technische Visionen und Dystopien
Autoritäten
Professionelle Haltung
Erziehungs-partnerschaft mit Eltern
Hilfen zur Erziehung
herausforderndes Verhalten bei Kindern und Jugendlichen
Bindungstheorie
Gemeinschaft/Gemeinschaftssinn
Film/Filmgeschichte/Filmsprache
Wendezeit
Identität/kulturelle Identität
Opfer
Gentrifizierung
Gleichberechtigung
Deutsch
Sachkunde/ Lebenskunde
Religion/Ethik
Amazonas
Regenwald
Mythos
Irland
Jahreszeiten
Lappland
Teamwork
Gut & Böse
modernes Märchen
Lebensträume
Detektivgeschichten
Leben in der Stadt
Hochbegabung/Tiefbegabung
Abenteuer/Krimi
(Liebes-)kummer
Hochbegabung
Brasilien
Industrialisierung
Jungen
Leben auf dem Land
Eltern
Technik
Gruppen
Konflikt
Mauerfall
friedliche Revolution
jüdisches Leben in Berlin
Glaube
gruppenbezogene Hetze
Luft
Wasserkreislauf
Luftverschmutzung
Asien
Lateinamerika
indigene Bevölkerung
Queer
Kolonialismus
Biologie
Wahrnehmung
ziviler Ungehorsam
Ökologie vs. Ökonomie', DEFAULT);

INSERT INTO PRODUCTIONTAGTEMPLATE (ID, CREATEUSER, CREATEDATE, MODIFYUSER, MODIFYDATE, `NAME`, PRESETS, EXCLUSIVE) 
	VALUES (nextval(productionTagTemplate_id_seq), DEFAULT, DEFAULT, DEFAULT, DEFAULT, 'Trigger-Warnungen', 'Alkohol
Drogen
Gewalt
Rassismus
Antisemitismus
Krieg
Flucht
Suizid
Essstörungen', DEFAULT);

INSERT INTO PRODUCTIONTAGTEMPLATE (ID, CREATEUSER, CREATEDATE, MODIFYUSER, MODIFYDATE, `NAME`, PRESETS, EXCLUSIVE) 
	VALUES (nextval(productionTagTemplate_id_seq), DEFAULT, DEFAULT, DEFAULT, DEFAULT, 'Altersempfehlung', '"
Prädikat "wertvoll"', DEFAULT);
