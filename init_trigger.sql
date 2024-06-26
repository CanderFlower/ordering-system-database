CREATE TRIGGER update_merchant_score
AFTER INSERT ON `用户评价商户`
FOR EACH ROW
BEGIN
    DECLARE new_average_score NUMERIC;
    SELECT AVG(`评分`) INTO new_average_score
    FROM `用户评价商户`
    WHERE `商户_id` = NEW.`商户_id`;
    UPDATE `商户`
    SET `评分` = new_average_score
    WHERE `id` = NEW.`商户_id`;
END;

CREATE TRIGGER update_dish_score
AFTER INSERT ON `用户评价菜品`
FOR EACH ROW
BEGIN
    DECLARE new_average_score NUMERIC;
    SELECT AVG(`评分`) INTO new_average_score
    FROM `用户评价菜品`
    WHERE `菜品_id` = NEW.`菜品_id`;
    UPDATE `菜品`
    SET `评分` = new_average_score
    WHERE `id` = NEW.`菜品_id`;
END;

CREATE TRIGGER update_favorite_dishes
AFTER INSERT ON `用户收藏菜品`
FOR EACH ROW
BEGIN
    DECLARE new_favorites INT;
    SELECT COUNT(*) INTO new_favorites
    FROM `用户收藏菜品`
    WHERE `菜品_id` = NEW.`菜品_id`;
    UPDATE `菜品`
    SET `收藏量` = new_favorites
    WHERE `id` = NEW.`菜品_id`;
END;

CREATE TRIGGER update_queue_order_sales
AFTER INSERT ON `排队订单包含菜品`
FOR EACH ROW
BEGIN
    DECLARE new_queue_sales INT;
    SELECT COUNT(*) INTO new_queue_sales
    FROM `排队订单包含菜品`
    WHERE `菜品_id` = NEW.`菜品_id`;
    UPDATE `菜品`
    SET `排队销量` = new_queue_sales
    WHERE `id` = NEW.`菜品_id`;
END;

CREATE TRIGGER update_online_order_sales
AFTER INSERT ON `在线订单包含菜品`
FOR EACH ROW
BEGIN
    DECLARE new_online_sales INT;
    SELECT COUNT(*) INTO new_online_sales
    FROM `在线订单包含菜品`
    WHERE `菜品_id` = NEW.`菜品_id`;
    UPDATE `菜品`
    SET `在线销量` = new_online_sales
    WHERE `id` = NEW.`菜品_id`;
END;
