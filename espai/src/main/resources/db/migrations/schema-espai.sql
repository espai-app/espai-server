--
--  DATABASE SCHEMA FOR ESPAI
--  For use with MySQL or compatible SQL dialects.
--
--  This software was created with xprs.
--  Have a look at https://xprs.rocks/ for more details.
--


--
-- AGENCY TABLE
--
CREATE TABLE AGENCY (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(200) DEFAULT NULL
);
CREATE SEQUENCE agency_id_seq START WITH 1 INCREMENT BY 1;




--
-- ATTACHMENT TABLE
--
CREATE TABLE ATTACHMENT (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  ENTITYID BIGINT DEFAULT NULL,
  ENTITYTYPE VARCHAR(100) DEFAULT NULL,
  MEDIATYPE VARCHAR(50) DEFAULT 'UNSET',
  MIMETYPE VARCHAR(100) DEFAULT NULL,
  POSITION INT DEFAULT NULL,
  CATEGORY VARCHAR(100) DEFAULT NULL,
  CAPTION VARCHAR(200) DEFAULT NULL,
  LOCATION VARCHAR(300) DEFAULT NULL,
  COPYRIGHT VARCHAR(300) DEFAULT NULL,
  CHECKSUM VARCHAR(100) DEFAULT NULL
);
CREATE SEQUENCE attachment_id_seq START WITH 1 INCREMENT BY 1;




--
-- PRODUCTIONTAG TABLE
--
CREATE TABLE PRODUCTIONTAG (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL,
  POSITION INT DEFAULT NULL,
  CATEGORY VARCHAR(50) DEFAULT NULL,
  VALUE TEXT DEFAULT NULL
);
CREATE SEQUENCE productionTag_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_PRODUCTIONTAG_PRODUCTION ON PRODUCTIONTAG(PRODUCTION_ID);


--
-- PRODUCTIONVERSION TABLE
--
CREATE TABLE PRODUCTIONVERSION (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL,
  VERSIONNAME VARCHAR(30) DEFAULT NULL,
  MEDIAFORMAT VARCHAR(30) DEFAULT NULL,
  SPOKENLANGUAGE VARCHAR(3) DEFAULT NULL,
  SUBTITLES VARCHAR(3) DEFAULT NULL
);
CREATE SEQUENCE productionVersion_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_PRODUCTIONVERSION_PRODUCTION ON PRODUCTIONVERSION(PRODUCTION_ID);


--
-- PRODUCTION TABLE
--
CREATE TABLE PRODUCTION (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  TITLE VARCHAR(200) DEFAULT NULL,
  DESCRIPTION TEXT DEFAULT NULL,
  DURATIONINMINUTES INT DEFAULT NULL,
  FROMAGE INT DEFAULT NULL,
  TOAGE INT DEFAULT NULL,
  RATING INT DEFAULT NULL,
  AGENCY_ID BIGINT DEFAULT NULL,
  AVAILABLE TINYINT DEFAULT NULL,
  FIXEDPRICE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  FIXEDPRICE_CURRENCY VARCHAR(5) DEFAULT NULL,
  TICKETSHARE DOUBLE DEFAULT NULL,
  ADDITIONALCOST_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  ADDITIONALCOST_CURRENCY VARCHAR(5) DEFAULT NULL,
  EXTERNALID VARCHAR(250) DEFAULT NULL,
  INTERNALNOTE TEXT DEFAULT NULL,
  PRODUCTIONCOUNTRIES VARCHAR(200) DEFAULT NULL,
  PRODUCTIONYEAR VARCHAR(10) DEFAULT NULL,
  RELEASEDATE DATE DEFAULT NULL,
  DIRECTOR VARCHAR(200) DEFAULT NULL,
  STARRING TEXT DEFAULT NULL,
  BOOK VARCHAR(200) DEFAULT NULL,
  TRAILER VARCHAR(200) DEFAULT NULL,
  DTYPE VARCHAR(200) DEFAULT NULL
);
CREATE SEQUENCE production_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_PRODUCTION_AGENCY ON PRODUCTION(AGENCY_ID);


--
-- ATTACHMENTCATEGORY TABLE
--
CREATE TABLE ATTACHMENTCATEGORY (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(20) DEFAULT NULL,
  ENTITYTYPE VARCHAR(100) DEFAULT NULL,
  ATTACHMENTTYPE VARCHAR(50) DEFAULT 'UNSET'
);
CREATE SEQUENCE attachmentCategory_id_seq START WITH 1 INCREMENT BY 1;




--
-- PRODUCTIONTAGTEMPLATE TABLE
--
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
CREATE SEQUENCE productionTagTemplate_id_seq START WITH 1 INCREMENT BY 1;




--
-- LICENSECONDITION TABLE
--
CREATE TABLE LICENSECONDITION (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL,
  AGENCY_ID BIGINT DEFAULT NULL,
  COMMERCIAL TINYINT DEFAULT NULL,
  GUARANTEE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  GUARANTEE_CURRENCY VARCHAR(5) DEFAULT NULL,
  FIXEDPRICE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  FIXEDPRICE_CURRENCY VARCHAR(5) DEFAULT NULL,
  TICKETSHARE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  TICKETSHARE_CURRENCY VARCHAR(5) DEFAULT NULL,
  ADDITIONALCOST_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  ADDITIONALCOST_CURRENCY VARCHAR(5) DEFAULT NULL,
  NOTES TEXT DEFAULT NULL
);
CREATE SEQUENCE licenseCondition_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_LICENSECONDITION_PRODUCTION ON LICENSECONDITION(PRODUCTION_ID);
CREATE INDEX IX_LICENSECONDITION_AGENCY ON LICENSECONDITION(AGENCY_ID);


--
-- PRODUCTIONMEDIUM TABLE
--
CREATE TABLE PRODUCTIONMEDIUM (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL,
  NAME VARCHAR(150) DEFAULT NULL,
  FORMAT VARCHAR(50) DEFAULT NULL,
  REFERENCE VARCHAR(300) DEFAULT NULL,
  NOTE TEXT DEFAULT NULL
);
CREATE SEQUENCE productionMedium_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_PRODUCTIONMEDIUM_PRODUCTION ON PRODUCTIONMEDIUM(PRODUCTION_ID);


--
-- PRESENTER TABLE
--
CREATE TABLE PRESENTER (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  GIVENNAME VARCHAR(50) DEFAULT NULL,
  SURNAME VARCHAR(50) DEFAULT NULL,
  ADDRESS VARCHAR(300) DEFAULT NULL,
  POSTCODE VARCHAR(10) DEFAULT NULL,
  CITY VARCHAR(200) DEFAULT NULL,
  PHONE VARCHAR(50) DEFAULT NULL,
  EMAIL VARCHAR(100) DEFAULT NULL
);
CREATE SEQUENCE presenter_id_seq START WITH 1 INCREMENT BY 1;




--
-- SEASONVENUE TABLE
--
CREATE TABLE SEASONVENUE (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL,
  VENUE_ID BIGINT DEFAULT NULL,
  SEASONNOTES TEXT DEFAULT NULL,
  SELFPROGRAMMING TINYINT DEFAULT NULL
);
CREATE SEQUENCE seasonVenue_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_SEASONVENUE_SEASON ON SEASONVENUE(SEASON_ID);
CREATE INDEX IX_SEASONVENUE_VENUE ON SEASONVENUE(VENUE_ID);


--
-- EVENT TABLE
--
CREATE TABLE EVENT (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL,
  EVENTSERIAL_ID BIGINT DEFAULT NULL,
  DATE DATE DEFAULT NULL,
  TIME TIME DEFAULT NULL,
  HALL_ID BIGINT DEFAULT NULL,
  HOST_ID BIGINT DEFAULT NULL,
  COHOST_ID BIGINT DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL,
  TICKETLIMIT INT DEFAULT NULL,
  PARENTEVENT_ID BIGINT DEFAULT NULL,
  RESERVABLE TINYINT DEFAULT NULL,
  MANDATORY TINYINT DEFAULT NULL,
  HIDDEN TINYINT DEFAULT NULL
);
CREATE SEQUENCE event_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_EVENT_SEASON ON EVENT(SEASON_ID);
CREATE INDEX IX_EVENT_EVENTSERIAL ON EVENT(EVENTSERIAL_ID);
CREATE INDEX IX_EVENT_HALL ON EVENT(HALL_ID);
CREATE INDEX IX_EVENT_HOST ON EVENT(HOST_ID);
CREATE INDEX IX_EVENT_COHOST ON EVENT(COHOST_ID);
CREATE INDEX IX_EVENT_PRODUCTION ON EVENT(PRODUCTION_ID);
CREATE INDEX IX_EVENT_PARENTEVENT ON EVENT(PARENTEVENT_ID);


--
-- VENUE TABLE
--
CREATE TABLE VENUE (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(300) DEFAULT NULL,
  ADDRESS VARCHAR(300) DEFAULT NULL,
  POSTCODE VARCHAR(10) DEFAULT NULL,
  CITY VARCHAR(100) DEFAULT NULL,
  PHONE VARCHAR(50) DEFAULT NULL,
  EMAIL VARCHAR(150) DEFAULT NULL,
  PUBLICNOTES TEXT DEFAULT NULL,
  INTERNALNOTES TEXT DEFAULT NULL
);
CREATE SEQUENCE venue_id_seq START WITH 1 INCREMENT BY 1;




--
-- VENUECONTACT TABLE
--
CREATE TABLE VENUECONTACT (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  VENUE_ID BIGINT DEFAULT NULL,
  POSITION VARCHAR(50) DEFAULT NULL,
  FORMOFADDRESS VARCHAR(100) DEFAULT NULL,
  GIVENNAME VARCHAR(100) DEFAULT NULL,
  FAMILYNAME VARCHAR(100) DEFAULT NULL,
  PHONE VARCHAR(100) DEFAULT NULL,
  EMAIL VARCHAR(150) DEFAULT NULL,
  NOTIFYONRESERVATION TINYINT DEFAULT NULL
);
CREATE SEQUENCE venueContact_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_VENUECONTACT_VENUE ON VENUECONTACT(VENUE_ID);


--
-- HALL TABLE
--
CREATE TABLE HALL (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  VENUE_ID BIGINT DEFAULT NULL,
  NAME VARCHAR(50) DEFAULT NULL,
  CAPACITY INT DEFAULT NULL,
  WHEELCHAIRACCESSABLE TINYINT DEFAULT NULL,
  CAPACITYWHEELCHAIRS INT DEFAULT NULL,
  HEARINGLOOPS TINYINT DEFAULT NULL,
  FILM35 TINYINT DEFAULT NULL,
  FILM16 TINYINT DEFAULT NULL,
  BETASP TINYINT DEFAULT NULL,
  DVD TINYINT DEFAULT NULL,
  BLURAY TINYINT DEFAULT NULL,
  DCP TINYINT DEFAULT NULL,
  MPEG TINYINT DEFAULT NULL,
  PUBLICNOTES TEXT DEFAULT NULL,
  INTERNALNOTES TEXT DEFAULT NULL
);
CREATE SEQUENCE hall_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_HALL_VENUE ON HALL(VENUE_ID);


--
-- SEATCATEGORY TABLE
--
CREATE TABLE SEATCATEGORY (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(100) DEFAULT NULL,
  DESCRIPTION TEXT DEFAULT NULL
);
CREATE SEQUENCE seatCategory_id_seq START WITH 1 INCREMENT BY 1;




--
-- HALLCAPACITY TABLE
--
CREATE TABLE HALLCAPACITY (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEATCATEGORY_ID BIGINT DEFAULT NULL,
  HALL_ID BIGINT DEFAULT NULL,
  CAPACITY INT DEFAULT NULL,
  PARTOFTOTALCAPACITY TINYINT DEFAULT NULL
);
CREATE SEQUENCE hallCapacity_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_HALLCAPACITY_SEATCATEGORY ON HALLCAPACITY(SEATCATEGORY_ID);
CREATE INDEX IX_HALLCAPACITY_HALL ON HALLCAPACITY(HALL_ID);


--
-- PRICECATEGORY TABLE
--
CREATE TABLE PRICECATEGORY (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(50) DEFAULT NULL,
  DESCRIPTION TEXT DEFAULT NULL,
  DEFAULTVALUE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  DEFAULTVALUE_CURRENCY VARCHAR(5) DEFAULT NULL,
  POSITION INT DEFAULT NULL,
  ADDBYDEFAULT TINYINT DEFAULT NULL
);
CREATE SEQUENCE priceCategory_id_seq START WITH 1 INCREMENT BY 1;




--
-- MAILACCOUNT TABLE
--
CREATE TABLE MAILACCOUNT (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(50) DEFAULT NULL,
  SMTPHOST VARCHAR(150) DEFAULT NULL,
  SMTPPORT INT DEFAULT NULL,
  SMTPTRANSPORTSECURITY VARCHAR(50) DEFAULT 'UNSET',
  SMTPUSER VARCHAR(150) DEFAULT NULL,
  SMTPPASSWORD VARCHAR(150) DEFAULT NULL,
  IMAPHOST VARCHAR(150) DEFAULT NULL,
  IMAPPORT INT DEFAULT NULL,
  IMAPTRANSPORTSECURITY VARCHAR(50) DEFAULT 'UNSET',
  IMAPUSER VARCHAR(150) DEFAULT NULL,
  IMAPPASSWORD VARCHAR(150) DEFAULT NULL,
  IMAPSENTFOLDER VARCHAR(150) DEFAULT NULL
);
CREATE SEQUENCE mailAccount_id_seq START WITH 1 INCREMENT BY 1;




--
-- SEASONPRODUCTION TABLE
--
CREATE TABLE SEASONPRODUCTION (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL
);
CREATE SEQUENCE seasonProduction_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_SEASONPRODUCTION_SEASON ON SEASONPRODUCTION(SEASON_ID);
CREATE INDEX IX_SEASONPRODUCTION_PRODUCTION ON SEASONPRODUCTION(PRODUCTION_ID);


--
-- SEASON TABLE
--
CREATE TABLE SEASON (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(50) DEFAULT NULL,
  START DATE DEFAULT NULL,
  END DATE DEFAULT NULL,
  ARCHIVED TINYINT DEFAULT NULL,
  MAILACCOUNT_ID BIGINT DEFAULT NULL
);
CREATE SEQUENCE season_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_SEASON_MAILACCOUNT ON SEASON(MAILACCOUNT_ID);


--
-- SEASONPRICETEMPLATE TABLE
--
CREATE TABLE SEASONPRICETEMPLATE (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL,
  PRICECATEGORY_ID BIGINT DEFAULT NULL,
  PRICE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  PRICE_CURRENCY VARCHAR(5) DEFAULT NULL
);
CREATE SEQUENCE seasonPriceTemplate_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_SEASONPRICETEMPLATE_SEASON ON SEASONPRICETEMPLATE(SEASON_ID);
CREATE INDEX IX_SEASONPRICETEMPLATE_PRICECATEGORY ON SEASONPRICETEMPLATE(PRICECATEGORY_ID);


--
-- VENUEPRODUCTION TABLE
--
CREATE TABLE VENUEPRODUCTION (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  VENUE_ID BIGINT DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL
);
CREATE SEQUENCE venueProduction_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_VENUEPRODUCTION_VENUE ON VENUEPRODUCTION(VENUE_ID);
CREATE INDEX IX_VENUEPRODUCTION_PRODUCTION ON VENUEPRODUCTION(PRODUCTION_ID);


--
-- EVENTSERIAL TABLE
--
CREATE TABLE EVENTSERIAL (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  NAME VARCHAR(150) DEFAULT NULL,
  DESCRIPTION TEXT DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL
);
CREATE SEQUENCE eventSerial_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_EVENTSERIAL_SEASON ON EVENTSERIAL(SEASON_ID);


--
-- EVENTTICKETPRICE TABLE
--
CREATE TABLE EVENTTICKETPRICE (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  EVENT_ID BIGINT DEFAULT NULL,
  SEATCATEGORY_ID BIGINT DEFAULT NULL,
  PRICECATEGORY_ID BIGINT DEFAULT NULL,
  PRICE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  PRICE_CURRENCY VARCHAR(5) DEFAULT NULL,
  VENUE_ID BIGINT DEFAULT NULL,
  HALL_ID BIGINT DEFAULT NULL,
  DTYPE VARCHAR(200) DEFAULT NULL
);
CREATE SEQUENCE eventTicketPrice_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_EVENTTICKETPRICE_EVENT ON EVENTTICKETPRICE(EVENT_ID);
CREATE INDEX IX_EVENTTICKETPRICE_SEATCATEGORY ON EVENTTICKETPRICE(SEATCATEGORY_ID);
CREATE INDEX IX_EVENTTICKETPRICE_PRICECATEGORY ON EVENTTICKETPRICE(PRICECATEGORY_ID);
CREATE INDEX IX_EVENTTICKETPRICE_VENUE ON EVENTTICKETPRICE(VENUE_ID);
CREATE INDEX IX_EVENTTICKETPRICE_HALL ON EVENTTICKETPRICE(HALL_ID);


--
-- RESERVATION TABLE
--
CREATE TABLE RESERVATION (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  STATUS VARCHAR(50) DEFAULT 'UNSET',
  EVENT_ID BIGINT DEFAULT NULL,
  PARENTRESERVATION_ID BIGINT DEFAULT NULL,
  COMPANY VARCHAR(150) DEFAULT NULL,
  GIVENNAME VARCHAR(50) DEFAULT NULL,
  SURNAME VARCHAR(50) DEFAULT NULL,
  ADDRESS VARCHAR(300) DEFAULT NULL,
  POSTCODE VARCHAR(10) DEFAULT NULL,
  CITY VARCHAR(200) DEFAULT NULL,
  PHONE VARCHAR(50) DEFAULT NULL,
  EMAIL VARCHAR(100) DEFAULT NULL,
  MESSAGE TEXT DEFAULT NULL,
  INTERNALNOTES TEXT DEFAULT NULL,
  TERMSACCEPTED TINYINT DEFAULT NULL
);
CREATE SEQUENCE reservation_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_RESERVATION_EVENT ON RESERVATION(EVENT_ID);
CREATE INDEX IX_RESERVATION_PARENTRESERVATION ON RESERVATION(PARENTRESERVATION_ID);


--
-- RESERVATIONEXTRA TABLE
--
CREATE TABLE RESERVATIONEXTRA (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  RESERVATION_ID BIGINT DEFAULT NULL,
  FIELDNAME VARCHAR(100) DEFAULT NULL,
  VALUE VARCHAR(250) DEFAULT NULL
);
CREATE SEQUENCE reservationExtra_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_RESERVATIONEXTRA_RESERVATION ON RESERVATIONEXTRA(RESERVATION_ID);


--
-- RESERVATIONSUMMARY TABLE
--
CREATE TABLE RESERVATIONSUMMARY (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL,
  PARENTRESERVATION_ID BIGINT DEFAULT NULL,
  EVENTDATE DATE DEFAULT NULL,
  EVENTSTARTTIME TIME DEFAULT NULL,
  PRODUCTIONTITLE VARCHAR(300) DEFAULT NULL,
  PRODUCTION_ID BIGINT DEFAULT NULL,
  EVENT_ID BIGINT DEFAULT NULL,
  VENUE_ID BIGINT DEFAULT NULL,
  VENUENAME VARCHAR(300) DEFAULT NULL,
  VENUECITY VARCHAR(300) DEFAULT NULL,
  COMPANY VARCHAR(150) DEFAULT NULL,
  GIVENNAME VARCHAR(50) DEFAULT NULL,
  SURNAME VARCHAR(50) DEFAULT NULL,
  TICKETS INT DEFAULT NULL,
  MESSAGE TEXT DEFAULT NULL,
  STATUS VARCHAR(50) DEFAULT 'UNSET'
);
CREATE SEQUENCE reservationSummary_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_RESERVATIONSUMMARY_SEASON ON RESERVATIONSUMMARY(SEASON_ID);
CREATE INDEX IX_RESERVATIONSUMMARY_PARENTRESERVATION ON RESERVATIONSUMMARY(PARENTRESERVATION_ID);
CREATE INDEX IX_RESERVATIONSUMMARY_PRODUCTION ON RESERVATIONSUMMARY(PRODUCTION_ID);
CREATE INDEX IX_RESERVATIONSUMMARY_EVENT ON RESERVATIONSUMMARY(EVENT_ID);
CREATE INDEX IX_RESERVATIONSUMMARY_VENUE ON RESERVATIONSUMMARY(VENUE_ID);


--
-- RESERVATIONTICKET TABLE
--
CREATE TABLE RESERVATIONTICKET (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  RESERVATION_ID BIGINT DEFAULT NULL,
  AMOUNT INT DEFAULT NULL,
  PRICECATEGORY_ID BIGINT DEFAULT NULL,
  SEATCATEGORY_ID BIGINT DEFAULT NULL,
  PRICE_AMOUNT DECIMAL(16,4) DEFAULT NULL,
  PRICE_CURRENCY VARCHAR(5) DEFAULT NULL
);
CREATE SEQUENCE reservationTicket_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_RESERVATIONTICKET_RESERVATION ON RESERVATIONTICKET(RESERVATION_ID);
CREATE INDEX IX_RESERVATIONTICKET_PRICECATEGORY ON RESERVATIONTICKET(PRICECATEGORY_ID);
CREATE INDEX IX_RESERVATIONTICKET_SEATCATEGORY ON RESERVATIONTICKET(SEATCATEGORY_ID);


--
-- ACCESSRIGHT TABLE
--
CREATE TABLE ACCESSRIGHT (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  USER_ID BIGINT DEFAULT NULL,
  ROLE VARCHAR(50) DEFAULT 'UNSET',
  VENUE_ID BIGINT DEFAULT NULL
);
CREATE SEQUENCE accessRight_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_ACCESSRIGHT_USER ON ACCESSRIGHT(USER_ID);
CREATE INDEX IX_ACCESSRIGHT_VENUE ON ACCESSRIGHT(VENUE_ID);


--
-- USER TABLE
--
CREATE TABLE USER (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  USERNAME VARCHAR(100) DEFAULT NULL,
  EMAIL VARCHAR(100) DEFAULT NULL,
  PASSWORD VARCHAR(250) DEFAULT NULL,
  DISPLAYNAME VARCHAR(100) DEFAULT NULL,
  RESETTOKEN VARCHAR(50) DEFAULT NULL,
  RESETTOKENVALID DATE DEFAULT NULL,
  BLOCKED TINYINT DEFAULT NULL,
  LASTLOGIN DATE DEFAULT NULL
);
CREATE SEQUENCE user_id_seq START WITH 1 INCREMENT BY 1;




--
-- MAILTEMPLATE TABLE
--
CREATE TABLE MAILTEMPLATE (
  ID BIGINT PRIMARY KEY NOT NULL,
  CREATEUSER VARCHAR(50) DEFAULT NULL,
  CREATEDATE DATETIME DEFAULT NULL,
  MODIFYUSER VARCHAR(50) DEFAULT NULL,
  MODIFYDATE DATETIME DEFAULT NULL,
  SEASON_ID BIGINT DEFAULT NULL,
  SHORTCODE VARCHAR(50) DEFAULT NULL,
  NAME VARCHAR(150) DEFAULT NULL,
  DESCRIPTION TEXT DEFAULT NULL,
  SENDER VARCHAR(250) DEFAULT NULL,
  REPLYTO VARCHAR(250) DEFAULT NULL,
  BCC VARCHAR(250) DEFAULT NULL,
  SUBJECT VARCHAR(250) DEFAULT NULL,
  HTMLMESSAGE TEXT DEFAULT NULL
);
CREATE SEQUENCE mailTemplate_id_seq START WITH 1 INCREMENT BY 1;


CREATE INDEX IX_MAILTEMPLATE_SEASON ON MAILTEMPLATE(SEASON_ID);

