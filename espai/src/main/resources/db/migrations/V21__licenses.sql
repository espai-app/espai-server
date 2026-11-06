ALTER TABLE LICENSECONDITION CHANGE COLUMN TICKETSHARE_AMOUNT TICKETSHARE DECIMAL(6,2) DEFAULT NULL;
ALTER TABLE LICENSECONDITION DROP COLUMN TICKETSHARE_CURRENCY;

UPDATE PRODUCTIONTAGTEMPLATE SET PRESETS = "" WHERE NAME = "Altersempfehlung";
INSERT INTO PRODUCTIONTAGTEMPLATE(ID, NAME, PRESETS) VALUES (5, "Auszeichnungen", "");

INSERT INTO MAILTEMPLATE(ID, SHORTCODE, NAME, DESCRIPTION) VALUES (NEXTVAL(mailTemplate_id_seq), "SUBRESERVATION_HOLD", "Wartende Buchung eines Zusatzangebotes", "Wird versendet, wenn die Buchung eines Zusatzangebot noch nicht final bestätigt werden kann.");
INSERT INTO MAILTEMPLATE(ID, SHORTCODE, NAME, DESCRIPTION) VALUES (NEXTVAL(mailTemplate_id_seq), "SUBRESERVATION_CONFIRMED", "Bestätigung der Buchung  eines Zusatzangebotes", "Wird versendet, wenn die Buchung eines Zusatzangebot final bestätigt wurde.");
INSERT INTO MAILTEMPLATE(ID, SHORTCODE, NAME, DESCRIPTION) VALUES (NEXTVAL(mailTemplate_id_seq), "SUBRESERVATION_CANCELED", "Stornierung der Buchung  eines Zusatzangebotes", "Wird versendet, wenn die Buchung eines Zusatzangebot storniert wurde.");