/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  rborowski
 * Created: 01.12.2025
 */

ALTER TABLE EVENT ADD COLUMN INTERNALNOTE TEXT DEFAULT NULL;
ALTER TABLE EVENT ADD COLUMN EXTERNALNOTE TEXT DEFAULT NULL;

ALTER TABLE SEASONPRODUCTION ADD COLUMN INTERNALNOTE TEXT DEFAULT NULL;
ALTER TABLE SEASONPRODUCTION ADD COLUMN EXTERNALNOTE TEXT DEFAULT NULL;