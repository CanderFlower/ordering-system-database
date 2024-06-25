DROP DATABASE IF EXISTS canteen;

CREATE DATABASE canteen;
USE canteen;

CREATE TABLE `�û�` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����` VARCHAR(255) NOT NULL,
	`�Ա�` VARCHAR(255) NOT NULL,
	`��������` DATE NOT NULL,
	`ѧ����` VARCHAR(255) NOT NULL UNIQUE,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�̻�` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����` VARCHAR(255) NOT NULL,
	`��ַ` VARCHAR(255),
	`����` NUMERIC,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�û��ղ��̻�` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�û�_id` INT NOT NULL,
	`�̻�_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `��Ʒ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����` VARCHAR(255) NOT NULL,
	`�Ƿ������` BOOLEAN NOT NULL,
	`����` TEXT(65535),
	`�۸�` NUMERIC NOT NULL,
	`ͼƬ���` INT UNIQUE,
	`�ղ���` INT,
	`��������` INT,
	`�Ŷ�����` INT,
	`���_id` INT NOT NULL,
	`����` NUMERIC,
	PRIMARY KEY(`id`)
);

CREATE TABLE `���` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����` VARCHAR(255) NOT NULL UNIQUE,
	PRIMARY KEY(`id`)
);

CREATE TABLE `��Ʒ�����̻�` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�̻�_id` INT NOT NULL,
	`��Ʒ_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�û��ղز�Ʒ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�û�_id` INT NOT NULL,
	`��Ʒ_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `��Ʒ��������ԭ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`��Ʒ_id` INT NOT NULL,
	`����ԭ_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `����ԭ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����` VARCHAR(255) NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�û������̻�` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�û�_id` INT NOT NULL,
	`�̻�_id` INT NOT NULL,
	`����` NUMERIC NOT NULL,
	`��������` TEXT(65535),
	PRIMARY KEY(`id`)
);

CREATE TABLE `�û����۲�Ʒ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�û�_id` INT NOT NULL,
	`��Ʒ_id` INT NOT NULL,
	`����` NUMERIC NOT NULL,
	`��������` TEXT(65535),
	PRIMARY KEY(`id`)
);

CREATE TABLE `�ŶӶ���` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����ʱ��` TIME NOT NULL,
	`�Ƿ����` BOOLEAN NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�û������ŶӶ���` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�û�_id` INT NOT NULL,
	`�ŶӶ���_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�ŶӶ���������Ʒ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�ŶӶ���_id` INT NOT NULL,
	`��Ʒ_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `���߶���` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����ʱ��` TIME NOT NULL,
	`�Ƿ����` BOOLEAN NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `�û��������߶���` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`�û�_id` INT NOT NULL,
	`���߶���_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `���߶���������Ʒ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`���߶���_id` INT NOT NULL,
	`��Ʒ_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `��Ϣ` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`����` TEXT(65535),
	`�û�_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

CREATE TABLE `��Ʒ�������` (
	`id` INT NOT NULL AUTO_INCREMENT UNIQUE,
	`��Ʒ_id` INT NOT NULL,
	`���_id` INT NOT NULL,
	PRIMARY KEY(`id`)
);

ALTER TABLE `�û��ղ��̻�`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û��ղ��̻�`
ADD FOREIGN KEY(`�̻�_id`) REFERENCES `�̻�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ʒ�����̻�`
ADD FOREIGN KEY(`�̻�_id`) REFERENCES `�̻�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ʒ�����̻�`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û��ղز�Ʒ`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û��ղز�Ʒ`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ʒ��������ԭ`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ʒ��������ԭ`
ADD FOREIGN KEY(`����ԭ_id`) REFERENCES `����ԭ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û������̻�`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û������̻�`
ADD FOREIGN KEY(`�̻�_id`) REFERENCES `�̻�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û����۲�Ʒ`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û����۲�Ʒ`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û������ŶӶ���`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û������ŶӶ���`
ADD FOREIGN KEY(`�ŶӶ���_id`) REFERENCES `�ŶӶ���`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�ŶӶ���������Ʒ`
ADD FOREIGN KEY(`�ŶӶ���_id`) REFERENCES `�ŶӶ���`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�ŶӶ���������Ʒ`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û��������߶���`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `�û��������߶���`
ADD FOREIGN KEY(`���߶���_id`) REFERENCES `���߶���`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `���߶���������Ʒ`
ADD FOREIGN KEY(`���߶���_id`) REFERENCES `���߶���`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `���߶���������Ʒ`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ϣ`
ADD FOREIGN KEY(`�û�_id`) REFERENCES `�û�`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ʒ�������`
ADD FOREIGN KEY(`���_id`) REFERENCES `���`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE `��Ʒ�������`
ADD FOREIGN KEY(`��Ʒ_id`) REFERENCES `��Ʒ`(`id`)
ON UPDATE NO ACTION ON DELETE NO ACTION;

DELIMITER //
CREATE TRIGGER update_merchant_score
AFTER
INSERT
   ON `�û������̻�` FOR EACH ROW BEGIN DECLARE new_average_score NUMERIC;

SELECT
   AVG(`����`) INTO new_average_score
FROM
   `�û������̻�`
WHERE
   `�̻�_id` = NEW.`�̻�_id`;

UPDATE
   `�̻�`
SET
   `����` = new_average_score
WHERE
   `id` = NEW.`�̻�_id`;

END //
DELIMITER ;

DELIMITER // CREATE TRIGGER update_dish_score
AFTER
INSERT
   ON `�û����۲�Ʒ` FOR EACH ROW BEGIN DECLARE new_average_score NUMERIC;

SELECT
   AVG(`����`) INTO new_average_score
FROM
   `�û����۲�Ʒ`
WHERE
   `��Ʒ_id` = NEW.`��Ʒ_id`;

UPDATE
   `��Ʒ`
SET
   `����` = new_average_score
WHERE
   `id` = NEW.`��Ʒ_id`;

END // DELIMITER ;

DELIMITER // CREATE TRIGGER update_favorite_dishes
AFTER
INSERT
   ON `�û��ղز�Ʒ` FOR EACH ROW BEGIN DECLARE new_favorites INT;

SELECT
   `�ղ���` INTO new_favorites
FROM
   `��Ʒ`
WHERE
   `id` = NEW.`��Ʒ_id`;

SET
   new_favorites = new_favorites + 1;

UPDATE
   `��Ʒ`
SET
   `�ղ���` = new_favorites
WHERE
   `id` = NEW.`��Ʒ_id`;

END // DELIMITER ;

DELIMITER // CREATE TRIGGER update_queue_order_sales
AFTER
INSERT
   ON `�ŶӶ���������Ʒ` FOR EACH ROW BEGIN DECLARE new_queue_sales INT;

SELECT
   `�Ŷ�����` INTO new_queue_sales
FROM
   `��Ʒ`
WHERE
   `id` = NEW.`��Ʒ_id`;

SET
   new_queue_sales = new_queue_sales + 1;

UPDATE
   `��Ʒ`
SET
   `�Ŷ�����` = new_queue_sales
WHERE
   `id` = NEW.`��Ʒ_id`;

END // DELIMITER ;

DELIMITER // CREATE TRIGGER update_online_order_sales
AFTER
INSERT
   ON `���߶���������Ʒ` FOR EACH ROW BEGIN DECLARE new_online_sales INT;

SELECT
   `��������` INTO new_online_sales
FROM
   `��Ʒ`
WHERE
   `id` = NEW.`��Ʒ_id`;

SET
   new_online_sales = new_online_sales + 1;

UPDATE
   `��Ʒ`
SET
   `��������` = new_online_sales
WHERE
   `id` = NEW.`��Ʒ_id`;

END // DELIMITER ;