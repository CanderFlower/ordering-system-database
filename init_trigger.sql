CREATE TRIGGER update_merchant_score
AFTER INSERT ON `�û������̻�`
FOR EACH ROW
BEGIN
    DECLARE new_average_score NUMERIC;
    SELECT AVG(`����`) INTO new_average_score
    FROM `�û������̻�`
    WHERE `�̻�_id` = NEW.`�̻�_id`;
    UPDATE `�̻�`
    SET `����` = new_average_score
    WHERE `id` = NEW.`�̻�_id`;
END;

CREATE TRIGGER update_dish_score
AFTER INSERT ON `�û����۲�Ʒ`
FOR EACH ROW
BEGIN
    DECLARE new_average_score NUMERIC;
    SELECT AVG(`����`) INTO new_average_score
    FROM `�û����۲�Ʒ`
    WHERE `��Ʒ_id` = NEW.`��Ʒ_id`;
    UPDATE `��Ʒ`
    SET `����` = new_average_score
    WHERE `id` = NEW.`��Ʒ_id`;
END;

CREATE TRIGGER update_favorite_dishes
AFTER INSERT ON `�û��ղز�Ʒ`
FOR EACH ROW
BEGIN
    DECLARE new_favorites INT;
    SELECT `�ղ���` INTO new_favorites
    FROM `��Ʒ`
    WHERE `id` = NEW.`��Ʒ_id`;
    SET new_favorites = new_favorites + 1;
    UPDATE `��Ʒ`
    SET `�ղ���` = new_favorites
    WHERE `id` = NEW.`��Ʒ_id`;
END;

CREATE TRIGGER update_queue_order_sales
AFTER INSERT ON `�ŶӶ���������Ʒ`
FOR EACH ROW
BEGIN
    DECLARE new_queue_sales INT;
    SELECT `�Ŷ�����` INTO new_queue_sales
    FROM `��Ʒ`
    WHERE `id` = NEW.`��Ʒ_id`;
    SET new_queue_sales = new_queue_sales + 1;
    UPDATE `��Ʒ`
    SET `�Ŷ�����` = new_queue_sales
    WHERE `id` = NEW.`��Ʒ_id`;
END;

CREATE TRIGGER update_online_order_sales
AFTER INSERT ON `���߶���������Ʒ`
FOR EACH ROW
BEGIN
    DECLARE new_online_sales INT;
    SELECT `��������` INTO new_online_sales
    FROM `��Ʒ`
    WHERE `id` = NEW.`��Ʒ_id`;
    SET new_online_sales = new_online_sales + 1;
    UPDATE `��Ʒ`
    SET `��������` = new_online_sales
    WHERE `id` = NEW.`��Ʒ_id`;
END;

