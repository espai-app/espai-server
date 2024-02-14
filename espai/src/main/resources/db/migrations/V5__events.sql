/**
 * Author:  rborowski
 * Created: 05.01.2024
 */

ALTER TABLE EVENT CHANGE COLUMN STARTSHOW TIME TIME DEFAULT NULL;
ALTER TABLE EVENT CHANGE COLUMN MAINSHOW_ID PRODUCTION_ID BIGINT DEFAULT NULL;
ALTER TABLE EVENT CHANGE COLUMN LIMITMAINSHOW TICKETLIMIT INTEGER DEFAULT NULL;

ALTER TABLE EVENT DROP COLUMN SOLDMAINSHOW;

ALTER TABLE EVENT DROP COLUMN PRESHOW_ID;
ALTER TABLE EVENT DROP COLUMN LIMITPRESHOW;
ALTER TABLE EVENT DROP COLUMN SOLDPRESHOW;

ALTER TABLE EVENT DROP COLUMN AFTERSHOW_ID;
ALTER TABLE EVENT DROP COLUMN LIMITAFTERSHOW;
ALTER TABLE EVENT DROP COLUMN SOLDAFTERSHOW;
ALTER TABLE EVENT DROP COLUMN REQUIREAFTERSHOW;

ALTER TABLE EVENT DROP COLUMN TRACK;
ALTER TABLE EVENT ADD COLUMN EVENTSERIAL_ID BIGINT DEFAULT NULL;
ALTER TABLE EVENT ADD COLUMN PARENTEVENT_ID BIGINT DEFAULT NULL;
ALTER TABLE EVENT ADD COLUMN MANDATORY TINYINT DEFAULT 0;

CREATE TABLE EVENTSERIAL (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(150) DEFAULT NULL,
  DESCRIPTION TEXT DEFAULT NULL
);
CREATE SEQUENCE eventSerial_id_seq START WITH 1 INCREMENT BY 1;

CREATE VIEW TICKETSALES AS
SELECT R.EVENT_ID AS EVENT_ID, SUM(T.AMOUNT) AS TICKETSSOLD FROM RESERVATIONTICKET T LEFT JOIN RESERVATION R ON R.ID = T.RESERVATION_ID GROUP BY R.EVENT_ID;

DROP VIEW RESERVATIONSUMMARY;
CREATE VIEW RESERVATIONSUMMARY AS
SELECT
  R.ID AS ID,
  R.CREATEDATE AS CREATEDATE,
  R.CREATEUSER AS CREATEUSER,
  R.MODIFYDATE AS MODIFYDATE,
  R.MODIFYUSER AS MODIFYUSER,
  E.SEASON_ID AS SEASON_ID,
  E.DATE AS EVENTDATE, 
  E.TIME AS EVENTSTARTTIME, 
  P.TITLE AS PRODUCTIONTITLE,
  E.ID AS EVENT_ID,
  V.ID as VENUE_ID,
  V.NAME AS VENUENAME, 
  V.CITY AS VENUECITY, 
  R.COMPANY AS COMPANY, 
  R.GIVENNAME AS GIVENNAME, 
  R.SURNAME AS SURNAME, 
  S.TICKETSSOLD AS TICKETS, 
  R.STATUS AS STATUS
FROM EVENT E, PRODUCTION P, VENUE V, RESERVATION R, RESERVATIONTICKET T, HALL H, TICKETSALES S
WHERE
  R.EVENT_ID = E.ID AND E.HALL_ID = H.ID AND H.VENUE_ID = V.ID AND T.RESERVATION_ID = R.ID AND S.EVENT_ID = E.ID
GROUP BY T.RESERVATION_ID;