/**
 * Author:  rborowski
 * Created: 04.02.2024
 */

ALTER TABLE PRICECATEGORY ADD COLUMN ADDBYDEFAULT TINYINT DEFAULT 0;
ALTER TABLE PRICECATEGORY ADD COLUMN POSITION INT DEFAULT 0;