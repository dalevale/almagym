-- 
-- El contenido de este fichero se cargará al arrancar la aplicación, suponiendo que uses
-- 		application-default ó application-externaldb en modo 'create'
--

-- Usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name) VALUES (
	1, 1, 'a', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER,ADMIN',
	'Abundio', 'Ejémplez'
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name) VALUES (
	2, 1, 't', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER,TEACHER',
	'Profesoro', 'Muéstrez'
);

INSERT INTO user(id,enabled,username,password,roles,first_name,last_name) VALUES (
	4, 1, 't2', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER,TEACHER',
	'OtroProfesoro', 'Garcia'
);

-- Otro usuario de ejemplo con username = b y contraseña = aa  
INSERT INTO user(id,enabled,username,password,roles,first_name,last_name) VALUES (
	3, 1, 'c', 
	'{bcrypt}$2a$10$xLFtBIXGtYvAbRqM95JhcOaG23fHRpDoZIJrsF2cCff9xEHTTdK1u',
	'USER,CUSTOMER',
	'Customerio', 'Muéstrez'
);


-- Aquí empieza la table de rooms
INSERT INTO room(id,name,descrip,max_size) VALUES (
	1, 'Sala Spinning',
	'En esta sala podras disfrutar de nuestras bicicletas estaticas, a la vez que te pones mas fuerte que la clavicula de un transformer.',
	30
);

INSERT INTO room(id,name,descrip,max_size) VALUES (
	2, 'Sala Aerobics',
	'En esta sala podras disfrutar de nuestras bicicletas estaticas, a la vez que te pones mas fuerte que la clavicula de un transformer.',
	50
);

INSERT INTO room(id,name,descrip,max_size) VALUES (
	3, 'Sala Ejercicio',
	'En esta sala podras disfrutar de nuestras bicicletas estaticas, a la vez que te pones mas fuerte que la clavicula de un transformer.',
	40
);

--Aquí empieza la tabla de lessons
INSERT INTO lesson(id,name,date_ini,date_fin,total_students,profe_id,room_id) VALUES (
	1, 'Boxeo mañana',
	'2020-05-22 11:00:00', '2020-05-22 13:00:00',
	12, 2, 1
);

INSERT INTO lesson(id,name,date_ini,date_fin,total_students,profe_id,room_id) VALUES (
	2, 'Boxeo tarde',
	'2020-05-22 15:00:00', '2020-05-22 17:00:00',
	25, 2, 3
);

INSERT INTO lesson(id,name,date_ini,date_fin,total_students,profe_id,room_id) VALUES (
	3, 'Twerking',
	'2020-05-22 13:00:00', '2020-05-22 15:00:00',
	50, 2, 2
);

INSERT INTO lesson(id,name,date_ini,date_fin,total_students,profe_id,room_id) VALUES (
	4, 'Boxeo noche',
	'2020-05-22 20:00:00', '2020-05-22 22:00:00', 
	28, 2, 1
);

-- Aquí empieza la tabla de los equipments
INSERT INTO equipment(id,name,quantity,room_id) VALUES (
	1, 'Bicicleta estática', 5, 1
);

INSERT INTO equipment(id,name,quantity,room_id) VALUES (
	2, 'Cintas de correr', 5, 1
);

INSERT INTO equipment(id,name,quantity,room_id) VALUES (
	3, 'Peso 5 kgs', 6, 2
);

INSERT INTO equipment(id,name,quantity,room_id) VALUES (
	4, 'Banco Press', 4, 2
);

-- Aquí empieza la tabla de los mensajes
INSERT INTO MESSAGE VALUES(1,'2020-03-23 10:48:12.074000','2020-03-23 10:48:11.074000','probando 1',1,2);
INSERT INTO MESSAGE VALUES(2,'2020-03-23 10:48:16.149000','2020-03-23 10:48:15.149000','probando 2',1,2);
INSERT INTO MESSAGE VALUES(3,'2020-03-23 10:48:19.149000','2020-03-23 10:48:18.005000','probando 3',2,1);
INSERT INTO MESSAGE VALUES(4,'2020-03-23 10:48:21.149000','2020-03-23 10:48:20.971000','probando 4',2,1);
INSERT INTO MESSAGE VALUES(5,'2020-03-23 10:48:23.149000','2020-03-23 10:48:22.926000','probando 5',1,2);
INSERT INTO MESSAGE VALUES(6,NULL,'2020-03-23 10:48:25.729000','probando 6',2,1);
INSERT INTO MESSAGE VALUES(7,'2020-03-23 10:48:31.149000','2020-03-23 10:48:30.522000','probando 7',1,3);
INSERT INTO MESSAGE VALUES(8,'2020-03-23 10:48:33.149000','2020-03-23 10:48:32.332000','probando 8',3,1);
INSERT INTO MESSAGE VALUES(9,'2020-03-23 10:48:36.149000','2020-03-23 10:48:35.531000','probando 9',3,1);
INSERT INTO MESSAGE VALUES(10,NULL,'2020-03-23 10:48:39.911000','probando 10aasñdjalskdlaskjdlkasjkldjaskldjalskjdklasjdlasjldkjaslkdjalskjdlksajdlksalkdjsalkdjaslkdjlaksjdkl',1,3);