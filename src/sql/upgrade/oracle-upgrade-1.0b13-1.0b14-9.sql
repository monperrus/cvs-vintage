/*
 * Remove unnecessary module-option mappings from the Global Module 
 * In the default data, there were some option mappings whose 
 * Attributes were not mapped to the module, leading to errors.
 * Also, we do not need module-option mappings for the template issue types.
 *
 * Created By: Elicia David
 * $Id: oracle-upgrade-1.0b13-1.0b14-9.sql,v 1.1 2003/02/03 08:20:05 jon Exp $
 */

DELETE FROM SCARAB_R_MODULE_OPTION where module_id=0;

/*
 * populate the root module with all options.
 * module_id, issue_type_id, option_id, display_value, active, preferred order
 */
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,1,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,2,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,3,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,4,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,5,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,6,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,7,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,8,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,9,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,10,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,11,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,12,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,13,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,14,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,15,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,16,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,17,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,18,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,19,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,20,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,21,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,22,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,23,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,24,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,25,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,26,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,27,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,28,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,29,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,30,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,31,NULL,1,10,10);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,32,NULL,1,11,11);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,33,NULL,1,12,12);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,34,NULL,1,13,13);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,35,NULL,1,14,14);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,36,NULL,1,15,15);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,37,NULL,1,16,16);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,38,NULL,1,18,18);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,39,NULL,1,28,28);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,40,NULL,1,24,24);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,41,NULL,1,25,25);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,42,NULL,1,26,26);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,43,NULL,1,29,29);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,44,NULL,1,30,30);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,45,NULL,1,31,31);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,46,NULL,1,34,34);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,47,NULL,1,39,39);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,48,NULL,1,42,42);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,49,NULL,1,41,41);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,50,NULL,1,35,35);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,51,NULL,1,32,32);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,52,NULL,1,33,33);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,53,NULL,1,40,40);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,54,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,55,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,56,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,57,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,58,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,59,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,60,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,61,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,62,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,63,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,64,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,65,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,66,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,67,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,68,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,69,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,70,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,71,NULL,1,10,10);
/* Tracking DISABLED
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,72,NULL,1,1,1)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,73,NULL,1,2,2)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,74,NULL,1,3,3)
 */
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,75,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,76,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,77,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,78,NULL,1,19,19);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,79,NULL,1,20,20);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,80,NULL,1,21,21);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,81,NULL,1,22,22);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,82,NULL,1,23,23);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,83,NULL,1,27,27);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,84,NULL,1,38,38);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,85,NULL,1,36,36);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,86,NULL,1,37,37);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,87,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,88,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,89,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,1,90,NULL,1,3,3);

INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,1,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,2,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,3,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,4,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,5,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,6,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,7,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,8,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,9,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,10,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,11,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,12,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,13,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,14,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,15,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,16,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,17,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,18,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,19,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,20,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,21,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,22,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,23,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,24,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,25,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,26,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,27,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,28,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,29,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,30,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,31,NULL,1,10,10);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,32,NULL,1,11,11);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,33,NULL,1,12,12);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,34,NULL,1,13,13);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,35,NULL,1,14,14);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,36,NULL,1,15,15);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,37,NULL,1,16,16);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,38,NULL,1,18,18);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,39,NULL,1,28,28);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,40,NULL,1,24,24);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,41,NULL,1,25,25);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,42,NULL,1,26,26);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,43,NULL,1,29,29);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,44,NULL,1,30,30);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,45,NULL,1,31,31);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,46,NULL,1,34,34);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,47,NULL,1,39,39);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,48,NULL,1,42,42);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,49,NULL,1,41,41);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,50,NULL,1,35,35);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,51,NULL,1,32,32);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,52,NULL,1,33,33);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,53,NULL,1,40,40);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,54,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,55,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,56,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,57,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,58,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,59,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,60,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,61,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,62,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,63,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,64,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,65,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,66,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,67,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,68,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,69,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,70,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,71,NULL,1,10,10);
/* Tracking DISABLED
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,72,NULL,1,1,1)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,73,NULL,1,2,2)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,74,NULL,1,3,3)
 */
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,75,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,76,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,77,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,78,NULL,1,19,19);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,79,NULL,1,20,20);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,80,NULL,1,21,21);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,81,NULL,1,22,22);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,82,NULL,1,23,23);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,83,NULL,1,27,27);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,84,NULL,1,38,38);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,85,NULL,1,36,36);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,86,NULL,1,37,37);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,87,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,88,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,89,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,3,90,NULL,1,3,3);

INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,1,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,2,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,3,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,4,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,5,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,6,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,7,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,8,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,9,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,10,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,11,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,12,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,13,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,14,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,15,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,16,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,17,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,18,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,19,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,20,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,21,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,22,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,23,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,24,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,25,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,26,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,27,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,28,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,29,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,30,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,31,NULL,1,10,10);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,32,NULL,1,11,11);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,33,NULL,1,12,12);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,34,NULL,1,13,13);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,35,NULL,1,14,14);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,36,NULL,1,15,15);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,37,NULL,1,16,16);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,38,NULL,1,18,18);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,39,NULL,1,28,28);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,40,NULL,1,24,24);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,41,NULL,1,25,25);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,42,NULL,1,26,26);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,43,NULL,1,29,29);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,44,NULL,1,30,30);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,45,NULL,1,31,31);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,46,NULL,1,34,34);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,47,NULL,1,39,39);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,48,NULL,1,42,42);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,49,NULL,1,41,41);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,50,NULL,1,35,35);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,51,NULL,1,32,32);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,52,NULL,1,33,33);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,53,NULL,1,40,40);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,54,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,55,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,56,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,57,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,58,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,59,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,60,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,61,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,62,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,63,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,64,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,65,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,66,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,67,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,68,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,69,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,70,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,71,NULL,1,10,10);
/* Tracking DISABLED
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,72,NULL,1,1,1)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,73,NULL,1,2,2)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,74,NULL,1,3,3)
 */
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,75,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,76,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,77,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,78,NULL,1,19,19);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,79,NULL,1,20,20);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,80,NULL,1,21,21);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,81,NULL,1,22,22);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,82,NULL,1,23,23);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,83,NULL,1,27,27);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,84,NULL,1,38,38);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,85,NULL,1,36,36);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,86,NULL,1,37,37);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,87,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,88,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,89,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,5,90,NULL,1,3,3);

INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,1,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,2,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,3,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,4,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,5,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,6,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,7,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,8,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,9,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,10,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,11,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,12,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,13,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,14,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,15,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,16,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,17,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,18,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,19,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,20,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,21,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,22,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,23,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,24,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,25,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,26,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,27,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,28,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,29,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,30,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,31,NULL,1,10,10);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,32,NULL,1,11,11);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,33,NULL,1,12,12);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,34,NULL,1,13,13);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,35,NULL,1,14,14);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,36,NULL,1,15,15);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,37,NULL,1,16,16);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,38,NULL,1,18,18);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,39,NULL,1,28,28);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,40,NULL,1,24,24);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,41,NULL,1,25,25);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,42,NULL,1,26,26);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,43,NULL,1,29,29);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,44,NULL,1,30,30);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,45,NULL,1,31,31);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,46,NULL,1,34,34);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,47,NULL,1,39,39);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,48,NULL,1,42,42);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,49,NULL,1,41,41);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,50,NULL,1,35,35);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,51,NULL,1,32,32);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,52,NULL,1,33,33);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,53,NULL,1,40,40);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,54,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,55,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,56,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,57,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,58,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,59,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,60,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,61,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,62,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,63,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,64,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,65,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,66,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,67,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,68,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,69,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,70,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,71,NULL,1,10,10);
/* Tracking DISABLED
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,72,NULL,1,1,1)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,73,NULL,1,2,2)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,74,NULL,1,3,3)
 */
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,75,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,76,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,77,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,78,NULL,1,19,19);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,79,NULL,1,20,20);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,80,NULL,1,21,21);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,81,NULL,1,22,22);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,82,NULL,1,23,23);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,83,NULL,1,27,27);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,84,NULL,1,38,38);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,85,NULL,1,36,36);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,86,NULL,1,37,37);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,87,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,88,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,89,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,7,90,NULL,1,3,3);

INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,1,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,2,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,3,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,4,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,5,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,6,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,7,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,8,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,9,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,10,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,11,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,12,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,13,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,14,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,15,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,16,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,17,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,18,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,19,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,20,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,21,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,22,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,23,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,24,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,25,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,26,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,27,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,28,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,29,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,30,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,31,NULL,1,10,10);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,32,NULL,1,11,11);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,33,NULL,1,12,12);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,34,NULL,1,13,13);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,35,NULL,1,14,14);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,36,NULL,1,15,15);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,37,NULL,1,16,16);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,38,NULL,1,18,18);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,39,NULL,1,28,28);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,40,NULL,1,24,24);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,41,NULL,1,25,25);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,42,NULL,1,26,26);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,43,NULL,1,29,29);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,44,NULL,1,30,30);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,45,NULL,1,31,31);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,46,NULL,1,34,34);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,47,NULL,1,39,39);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,48,NULL,1,42,42);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,49,NULL,1,41,41);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,50,NULL,1,35,35);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,51,NULL,1,32,32);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,52,NULL,1,33,33);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,53,NULL,1,40,40);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,54,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,55,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,56,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,57,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,58,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,59,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,60,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,61,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,62,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,63,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,64,NULL,1,3,3);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,65,NULL,1,4,4);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,66,NULL,1,5,5);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,67,NULL,1,6,6);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,68,NULL,1,7,7);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,69,NULL,1,8,8);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,70,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,71,NULL,1,10,10);
/* Tracking DISABLED
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,72,NULL,1,1,1)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,73,NULL,1,2,2)
 * INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,74,NULL,1,3,3)
 */
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,75,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,76,NULL,1,9,9);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,77,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,78,NULL,1,19,19);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,79,NULL,1,20,20);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,80,NULL,1,21,21);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,81,NULL,1,22,22);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,82,NULL,1,23,23);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,83,NULL,1,27,27);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,84,NULL,1,38,38);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,85,NULL,1,36,36);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,86,NULL,1,37,37);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,87,NULL,1,17,17);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,88,NULL,1,1,1);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,89,NULL,1,2,2);
INSERT INTO SCARAB_R_MODULE_OPTION VALUES (0,9,90,NULL,1,3,3);


