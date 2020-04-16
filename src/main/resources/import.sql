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
INSERT INTO lesson(id,name,date_ini,date_fin,time_ini,duration,total_students,profe_id,room_id) VALUES (
	1, 'Boxeo mañana',
	'2020-03-09', '2020-03-09', 11, 2,
	12, 2, 1
);

INSERT INTO lesson(id,name,date_ini,date_fin,time_ini,duration,total_students,profe_id,room_id) VALUES (
	2, 'Boxeo tarde',
	'2020-03-09', '2020-03-09', 15, 2,
	25, 2, 3
);

INSERT INTO lesson(id,name,date_ini,date_fin,time_ini,duration,total_students,profe_id,room_id) VALUES (
	3, 'Twerking',
	'2020-03-16', '2020-03-16', 13, 2,
	50, 2, 2
);

INSERT INTO lesson(id,name,date_ini,date_fin,time_ini,duration,total_students,profe_id,room_id) VALUES (
	4, 'Boxeo noche',
	'2020-04-02', '2020-04-02', 20, 2,
	28, 2, 1
);

-- Aquí empieza la tabla de los equipments
INSERT INTO equipment(id,name,img_path,quantity,room_id) VALUES (
	1, 'Bicicleta estática', '/img/maquina1.jpg', 5, 1
);

INSERT INTO equipment(id,name,img_path,quantity,room_id) VALUES (
	2, 'Cintas de correr', '/img/sala2.jpg', 5, 1
);

INSERT INTO equipment(id,name,img_path,quantity,room_id) VALUES (
	3, 'Peso 5 kgs', '/img/portada.jpg', 6, 2
);

INSERT INTO equipment(id,name,img_path,quantity,room_id) VALUES (
	4, 'Banco Press', '/img/portada.jpg', 4, 2
);