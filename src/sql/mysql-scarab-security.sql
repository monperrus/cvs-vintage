# Script to fill the tables with default roles and permissions

INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (1, 'turbine_root');
INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (2, 'Partner');
INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (3, 'Observer');
INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (4, 'Developer');
INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (5, 'QA');
INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (6, 'Project Owner');
INSERT INTO TURBINE_ROLE (ROLE_ID, ROLE_NAME) VALUES (7, 'Root');

# Add some default permissions

INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (1, 'admin_users');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (2, 'Issue | Edit');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (3, 'Issue | Enter');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (4, 'Module | Edit');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (5, 'Domain | Edit');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (6, 'Item | Approve');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (7, 'Issue | Assign');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (8, 'Vote | Manage');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (9, 'Issue | Attachment');
INSERT INTO TURBINE_PERMISSION (PERMISSION_ID, PERMISSION_NAME) 
    VALUES (10, 'User | Edit Preferences');


# Create an account 'turbine@collab.net' for system administartor
# Remeber to set a good password for this user in a production system!

INSERT INTO TURBINE_USER (USER_ID, LOGIN_NAME, PASSWORD_VALUE, FIRST_NAME, LAST_NAME, CONFIRM_VALUE) 
    VALUES (0, 'turbine@collab.net', 'NWoZK3kTsExUV00Ywo1G5jlUKKs=', 'turbine', 'turbine', 'CONFIRMED');

insert into TURBINE_USER (USER_ID, LOGIN_NAME, PASSWORD_VALUE, FIRST_NAME, LAST_NAME, EMAIL, CONFIRM_VALUE ) 
    values (2, 'jon@latchkey.com', 'NWoZK3kTsExUV00Ywo1G5jlUKKs=', 'Jon', 'Stevens', 'jon@latchkey.com', 'CONFIRMED' );
insert into TURBINE_USER (USER_ID, LOGIN_NAME, PASSWORD_VALUE, FIRST_NAME, LAST_NAME, EMAIL, CONFIRM_VALUE ) 
    values (3, 'jss@latchkey.com', 'NWoZK3kTsExUV00Ywo1G5jlUKKs=', 'Jon', 'Stevens', 'jon@latchkey.com', 'abcdef' );
insert into TURBINE_USER (USER_ID, LOGIN_NAME, PASSWORD_VALUE, FIRST_NAME, LAST_NAME, EMAIL, CONFIRM_VALUE ) 
    values (4, 'jmcnally@collab.net', 'NWoZK3kTsExUV00Ywo1G5jlUKKs=', 'John', 'McNally', 'jmcnally@collab.net', 'CONFIRMED' );
insert into TURBINE_USER (USER_ID, LOGIN_NAME, PASSWORD_VALUE, FIRST_NAME, LAST_NAME, EMAIL, CONFIRM_VALUE ) 
    values (5, 'elicia@collab.net', 'NWoZK3kTsExUV00Ywo1G5jlUKKs=', 'Elicia', 'David', 'elicia@collab.net', 'CONFIRMED' );

# Add some permissions for the root role

INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) 
SELECT TURBINE_ROLE.ROLE_ID, TURBINE_PERMISSION.PERMISSION_ID FROM 
TURBINE_ROLE, TURBINE_PERMISSION
WHERE TURBINE_PERMISSION.PERMISSION_NAME = 'admin_users' AND 
TURBINE_ROLE.ROLE_NAME = 'turbine_root';

INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) 
SELECT TURBINE_ROLE.ROLE_ID, TURBINE_PERMISSION.PERMISSION_ID FROM 
TURBINE_ROLE, TURBINE_PERMISSION
WHERE TURBINE_PERMISSION.PERMISSION_NAME = 'Item | Approve' AND 
TURBINE_ROLE.ROLE_NAME = 'turbine_root';


# Permissions for the Developer Role

INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) 
SELECT TURBINE_ROLE.ROLE_ID, TURBINE_PERMISSION.PERMISSION_ID FROM 
TURBINE_ROLE, TURBINE_PERMISSION
WHERE TURBINE_PERMISSION.PERMISSION_NAME = 'Issue | Edit' AND 
TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) 
SELECT TURBINE_ROLE.ROLE_ID, TURBINE_PERMISSION.PERMISSION_ID FROM 
TURBINE_ROLE, TURBINE_PERMISSION
WHERE TURBINE_PERMISSION.PERMISSION_NAME = 'Issue | Enter' AND 
TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_ROLE_PERMISSION (ROLE_ID,PERMISSION_ID) 
SELECT TURBINE_ROLE.ROLE_ID, TURBINE_PERMISSION.PERMISSION_ID FROM 
TURBINE_ROLE, TURBINE_PERMISSION
WHERE TURBINE_PERMISSION.PERMISSION_NAME = 'User | Edit Preferences' AND 
TURBINE_ROLE.ROLE_NAME = 'Developer';

# Notes: need to add role/permission mappings for:
# partner - issue|attach
# project owner - module|edit,vote|manage,query|approve,template|approve
# root - domain|edit
# observer - issue|enter

# Assign the user 'turbine@collab.net' a system-wide role 'turbine_root'

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID from 
TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'turbine@collab.net' AND 
SCARAB_MODULE.MODULE_NAME = 0
AND TURBINE_ROLE.ROLE_NAME = 'turbine_root';

# Insert a relationship between user_ids 2,4,5 and module_ids 5,6

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID 
from TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'jon@latchkey.com'
AND SCARAB_MODULE.MODULE_ID = 5 
AND TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID 
from TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'jmcnally@collab.net'
AND SCARAB_MODULE.MODULE_ID = 5 
AND TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID 
from TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'elicia@collab.net'
AND SCARAB_MODULE.MODULE_ID = 5 
AND TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID 
from TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'jon@latchkey.com'
AND SCARAB_MODULE.MODULE_ID = 6 
AND TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID 
from TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'jmcnally@collab.net'
AND SCARAB_MODULE.MODULE_ID = 6 
AND TURBINE_ROLE.ROLE_NAME = 'Developer';

INSERT INTO TURBINE_USER_GROUP_ROLE ( USER_ID, GROUP_ID, ROLE_ID ) 
SELECT TURBINE_USER.USER_ID, SCARAB_MODULE.MODULE_ID, TURBINE_ROLE.ROLE_ID 
from TURBINE_USER, SCARAB_MODULE, TURBINE_ROLE 
WHERE TURBINE_USER.LOGIN_NAME = 'elicia@collab.net'
AND SCARAB_MODULE.MODULE_ID = 6 
AND TURBINE_ROLE.ROLE_NAME = 'Developer';
