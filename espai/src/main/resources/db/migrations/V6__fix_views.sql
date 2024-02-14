/**
 * Author:  rborowski
 * Created: 09.01.2024
 */

ALTER TABLE RESERVATION ADD COLUMN PARENTRESERVATION_ID BIGINT DEFAULT NULL;

DROP VIEW RESERVATIONSUMMARY;
CREATE VIEW RESERVATIONSUMMARY AS
SELECT
  R.ID AS ID,
  R.CREATEDATE AS CREATEDATE,
  R.CREATEUSER AS CREATEUSER,
  R.MODIFYDATE AS MODIFYDATE,
  R.MODIFYUSER AS MODIFYUSER,
  E.SEASON_ID AS SEASON_ID,
  R.PARENTRESERVATION_ID AS PARENTRESERVATION_ID,
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
  SUM(T.AMOUNT) AS TICKETS, 
  R.STATUS AS STATUS
FROM RESERVATION R
JOIN EVENT E ON R.EVENT_ID = E.ID
JOIN PRODUCTIONVERSION PV ON E.PRODUCTION_ID = PV.ID
JOIN PRODUCTION P ON PV.PRODUCTION_ID = P.ID
JOIN HALL H ON E.HALL_ID = H.ID
JOIN VENUE V ON H.VENUE_ID = V.ID
JOIN RESERVATIONTICKET T ON R.ID = T.RESERVATION_ID
GROUP BY R.ID;