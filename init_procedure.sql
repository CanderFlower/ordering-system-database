DELIMITER //

CREATE PROCEDURE 某商户菜品分析(IN 商户ID INT)
BEGIN
    SELECT
        p.id AS 菜品ID,
        p.名称 AS 菜品名称,
        p.评分 AS 菜品评分,
        IFNULL(online_sales.在线销量, 0) + IFNULL(queue_sales.排队销量, 0) AS 菜品总销量,
        uc.用户名称 AS 最频繁购买者姓名,
        MAX(uc.购买次数) AS 最大购买次数
    FROM
        菜品 p
        JOIN 菜品属于商户 ps ON p.id = ps.菜品_id
        JOIN 商户 s ON ps.商户_id = s.id
        LEFT JOIN (
            SELECT
                u.id AS 用户ID,
                u.名称 AS 用户名称,
                ucp.菜品_id AS 菜品ID,
                COUNT(ucp.菜品_id) + IFNULL(queue_sales.购买次数, 0) AS 购买次数
            FROM
                用户 u
                JOIN 用户创建在线订单 uco ON u.id = uco.用户_id
                JOIN 在线订单 oo ON uco.在线订单_id = oo.id
                JOIN 在线订单包含菜品 ucp ON oo.id = ucp.在线订单_id
            LEFT JOIN (
                SELECT
                    uq.用户_id,
                    qcp.菜品_id,
                    COUNT(qcp.菜品_id) AS 购买次数
                FROM
                    用户创建排队订单 uq
                    JOIN 排队订单 qo ON uq.排队订单_id = qo.id
                    JOIN 排队订单包含菜品 qcp ON qo.id = qcp.排队订单_id
                GROUP BY
                    uq.用户_id, qcp.菜品_id
            ) AS queue_sales ON u.id = queue_sales.用户_id AND ucp.菜品_id = queue_sales.菜品_id
            GROUP BY
                u.id,
                u.名称,
                ucp.菜品_id
        ) AS uc ON uc.菜品ID = p.id
        LEFT JOIN (
            SELECT
                ucp.菜品_id,
                COUNT(ucp.菜品_id) AS 在线销量
            FROM
                在线订单包含菜品 ucp
                JOIN 在线订单 oo ON ucp.在线订单_id = oo.id
                JOIN 用户创建在线订单 uco ON oo.id = uco.在线订单_id
            WHERE
                uco.用户_id IN (
                    SELECT id FROM 用户
                )
            GROUP BY
                ucp.菜品_id
        ) AS online_sales ON online_sales.菜品_id = p.id
        LEFT JOIN (
            SELECT
                qcp.菜品_id,
                COUNT(qcp.菜品_id) AS 排队销量
            FROM
                排队订单包含菜品 qcp
                JOIN 排队订单 qo ON qcp.排队订单_id = qo.id
                JOIN 用户创建排队订单 uq ON qo.id = uq.排队订单_id
            WHERE
                uq.用户_id IN (
                    SELECT id FROM 用户
                )
            GROUP BY
                qcp.菜品_id
        ) AS queue_sales ON queue_sales.菜品_id = p.id
    WHERE
        s.id = 商户ID
    GROUP BY
        p.id,
        p.名称,
        p.评分,
        online_sales.在线销量,
        queue_sales.排队销量,
        uc.用户名称
    ORDER BY
        p.id;
END //

DELIMITER ;


DELIMITER //

CREATE PROCEDURE 商户热销菜品分析(IN 商户ID INT)
BEGIN
    CREATE TEMPORARY TABLE IF NOT EXISTS 商户热销菜品分析 (
        商户名称 VARCHAR(255),
        热销菜品_id INT,
        热销菜品名称 VARCHAR(255),
        总销量 INT
    );

    INSERT INTO 商户热销菜品分析 (商户名称, 热销菜品_id, 热销菜品名称, 总销量)
    SELECT
        s.名称 AS 商户名称,
        d.id AS 热销菜品_id,
        d.名称 AS 热销菜品名称,
        (
            COALESCE(SUM(oiq.在线订单数量), 0) +
            COALESCE(SUM(qoq.排队订单数量), 0)
        ) AS 总销量
    FROM
        商户 s
        JOIN 菜品属于商户 ds ON s.id = ds.商户_id
        JOIN 菜品 d ON ds.菜品_id = d.id
        LEFT JOIN (
            SELECT
                oic.菜品_id,
                COUNT(*) AS 在线订单数量
            FROM
                在线订单包含菜品 oic
            GROUP BY
                oic.菜品_id
        ) AS oiq ON d.id = oiq.菜品_id
        LEFT JOIN (
            SELECT
                qic.菜品_id,
                COUNT(*) AS 排队订单数量
            FROM
                排队订单包含菜品 qic
            GROUP BY
                qic.菜品_id
        ) AS qoq ON d.id = qoq.菜品_id
    WHERE
        s.id = 商户ID
    GROUP BY
        s.名称, d.id, d.名称
    ORDER BY
        总销量 DESC
    LIMIT 3;

    SELECT
        商户名称,
        热销菜品_id,
        热销菜品名称,
        总销量
    FROM
        商户热销菜品分析;

    DROP TEMPORARY TABLE 商户热销菜品分析;

END //

DELIMITER ;
DELIMITER //

CREATE PROCEDURE 用户收藏菜品销量分析(IN 用户ID INT, IN 最低销量 INT, IN 时间段 INT)
BEGIN
    CASE 时间段
        WHEN 0 THEN -- 近一周销量分析
            SELECT
                p.名称 AS 菜品名称,
                SUM(
                    CASE
                        WHEN DATEDIFF(CURRENT_DATE, ucp.创建时间) <= 7 THEN ucp.销量
                        ELSE 0
                    END
                ) + SUM(
                    CASE
                        WHEN DATEDIFF(CURRENT_DATE, qcp.创建时间) <= 7 THEN qcp.销量
                        ELSE 0
                    END
                ) AS 总销量
            FROM
                用户收藏菜品 uc
                JOIN 菜品 p ON uc.菜品_id = p.id
                LEFT JOIN (
                    SELECT
                        uco.在线订单_id,
                        ucp.菜品_id,
                        COUNT(*) AS 销量,
                        oo.创建时间
                    FROM
                        用户创建在线订单 uco
                        JOIN 在线订单包含菜品 ucp ON uco.在线订单_id = ucp.在线订单_id
                        JOIN 在线订单 oo ON uco.在线订单_id = oo.id
                    WHERE
                        uco.用户_id = 用户ID
                    GROUP BY
                        uco.在线订单_id, ucp.菜品_id, oo.创建时间
                ) AS ucp ON uc.菜品_id = ucp.菜品_id
                LEFT JOIN (
                    SELECT
                        uco.排队订单_id,
                        qcp.菜品_id,
                        COUNT(*) AS 销量,
                        pq.创建时间
                    FROM
                        用户创建排队订单 uco
                        JOIN 排队订单包含菜品 qcp ON uco.排队订单_id = qcp.排队订单_id
                        JOIN 排队订单 pq ON uco.排队订单_id = pq.id
                    WHERE
                        uco.用户_id = 用户ID
                    GROUP BY
                        uco.排队订单_id, qcp.菜品_id, pq.创建时间
                ) AS qcp ON uc.菜品_id = qcp.菜品_id
            WHERE
                uc.用户_id = 用户ID
            GROUP BY
                p.名称
            HAVING
                总销量 >= 最低销量
            ORDER BY 总销量 DESC ;

        WHEN 1 THEN -- 近一月销量分析
            SELECT
                p.名称 AS 菜品名称,
                SUM(
                    CASE
                        WHEN DATE_FORMAT(ucp.创建时间, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m') THEN ucp.销量
                        ELSE 0
                    END
                ) + SUM(
                    CASE
                        WHEN DATE_FORMAT(qcp.创建时间, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m') THEN qcp.销量
                        ELSE 0
                    END
                ) AS 总销量
            FROM
                用户收藏菜品 uc
                JOIN 菜品 p ON uc.菜品_id = p.id
                LEFT JOIN (
                    SELECT
                        uco.在线订单_id,
                        ucp.菜品_id,
                        COUNT(*) AS 销量,
                        oo.创建时间
                    FROM
                        用户创建在线订单 uco
                        JOIN 在线订单包含菜品 ucp ON uco.在线订单_id = ucp.在线订单_id
                        JOIN 在线订单 oo ON uco.在线订单_id = oo.id
                    WHERE
                        uco.用户_id = 用户ID
                        AND DATE_FORMAT(oo.创建时间, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')
                    GROUP BY
                        uco.在线订单_id, ucp.菜品_id, oo.创建时间
                ) AS ucp ON uc.菜品_id = ucp.菜品_id
                LEFT JOIN (
                    SELECT
                        uco.排队订单_id,
                        qcp.菜品_id,
                        COUNT(*) AS 销量,
                        pq.创建时间
                    FROM
                        用户创建排队订单 uco
                        JOIN 排队订单包含菜品 qcp ON uco.排队订单_id = qcp.排队订单_id
                        JOIN 排队订单 pq ON uco.排队订单_id = pq.id
                    WHERE
                        uco.用户_id = 用户ID
                        AND DATE_FORMAT(pq.创建时间, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')
                    GROUP BY
                        uco.排队订单_id, qcp.菜品_id, pq.创建时间
                ) AS qcp ON uc.菜品_id = qcp.菜品_id
            WHERE
                uc.用户_id = 用户ID
            GROUP BY
                p.名称
            HAVING
            总销量 >= 最低销量
            ORDER BY 总销量 DESC ;

        WHEN 2 THEN -- 近一年销量分析
            SELECT
                p.名称 AS 菜品名称,
                SUM(
                    CASE
                        WHEN YEAR(ucp.创建时间) = YEAR(NOW()) THEN ucp.销量
                        ELSE 0
                    END
                ) + SUM(
                    CASE
                        WHEN YEAR(qcp.创建时间) = YEAR(NOW()) THEN qcp.销量
                        ELSE 0
                    END
                ) AS 总销量
            FROM
                用户收藏菜品 uc
                JOIN 菜品 p ON uc.菜品_id = p.id
                LEFT JOIN (
                    SELECT
                        uco.在线订单_id,
                        ucp.菜品_id,
                        COUNT(*) AS 销量,
                        oo.创建时间
                    FROM
                        用户创建在线订单 uco
                        JOIN 在线订单包含菜品 ucp ON uco.在线订单_id = ucp.在线订单_id
                        JOIN 在线订单 oo ON uco.在线订单_id = oo.id
                    WHERE
                        uco.用户_id = 用户ID
                        AND YEAR(oo.创建时间) = YEAR(NOW())
                    GROUP BY
                        uco.在线订单_id, ucp.菜品_id, oo.创建时间
                ) AS ucp ON uc.菜品_id = ucp.菜品_id
                LEFT JOIN (
                    SELECT
                        uco.排队订单_id,
                        qcp.菜品_id,
                        COUNT(*) AS 销量,
                        pq.创建时间
                    FROM
                        用户创建排队订单 uco
                        JOIN 排队订单包含菜品 qcp ON uco.排队订单_id = qcp.排队订单_id
                        JOIN 排队订单 pq ON uco.排队订单_id = pq.id
                    WHERE
                        uco.用户_id = 用户ID
                        AND YEAR(pq.创建时间) = YEAR(NOW())
                    GROUP BY
                        uco.排队订单_id, qcp.菜品_id, pq.创建时间
                ) AS qcp ON uc.菜品_id = qcp.菜品_id
            WHERE
                uc.用户_id = 用户ID
            GROUP BY
                p.名称
            HAVING
                总销量 >= 最低销量
            ORDER BY 总销量 DESC ;
        ELSE
            SELECT '时间段参数错误' AS 错误信息;
    END CASE;

END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE 商户忠实粉丝消费分布(IN 商户ID INT)
BEGIN
    DECLARE 阈值 INT DEFAULT 5;

    -- 获取符合条件的忠实粉丝用户
    CREATE TEMPORARY TABLE IF NOT EXISTS 忠实粉丝用户 (用户_id INT PRIMARY KEY);

    INSERT INTO 忠实粉丝用户 (用户_id)
    SELECT 用户_id
    FROM (
        SELECT
            uco.用户_id,
            COUNT(*) AS 消费次数
        FROM
            用户创建在线订单 uco
            JOIN 在线订单 oo ON uco.在线订单_id = oo.id
            JOIN 在线订单包含菜品 ucp ON uco.在线订单_id = ucp.在线订单_id
            JOIN 菜品 p ON ucp.菜品_id = p.id
            JOIN 菜品属于商户 cps ON p.id = cps.菜品_id
        WHERE
            cps.商户_id = 商户ID
            AND oo.创建时间 >= DATE_SUB(NOW(), INTERVAL 2 WEEK)
        GROUP BY
            uco.用户_id
        HAVING
            消费次数 > 阈值
    ) AS t;

    -- 查询忠实粉丝对各个菜品的购买次数
    SELECT
        p.名称 AS 菜品名称,
        COUNT(*) AS 购买次数
    FROM
        忠实粉丝用户 f
        JOIN 用户创建在线订单 uco ON f.用户_id = uco.用户_id
        JOIN 在线订单 oo ON uco.在线订单_id = oo.id
        JOIN 在线订单包含菜品 ucp ON uco.在线订单_id = ucp.在线订单_id
        JOIN 菜品 p ON ucp.菜品_id = p.id
        JOIN 菜品属于商户 cps ON p.id = cps.菜品_id
    WHERE
        cps.商户_id = 商户ID
        AND oo.创建时间 >= DATE_SUB(NOW(), INTERVAL 2 WEEK)
    GROUP BY
        p.名称;

    DROP TEMPORARY TABLE IF EXISTS 忠实粉丝用户;

END //

DELIMITER ;

SHOW triggers ;

DELIMITER //

CREATE PROCEDURE 用户群体特征分析(
    IN role CHAR(1),
    IN ageRange VARCHAR(20),
    IN gender CHAR(1)
)
BEGIN
    DECLARE startAge INT;
    DECLARE endAge INT;
    DECLARE startDate DATE;
    DECLARE endDate DATE;

    -- 解析年龄范围
    SET startAge = SUBSTRING_INDEX(ageRange, '-', 1);
    SET endAge = SUBSTRING_INDEX(ageRange, '-', -1);
    SET startDate = DATE_SUB(CURDATE(), INTERVAL endAge YEAR);
    SET endDate = DATE_SUB(CURDATE(), INTERVAL startAge YEAR);

    -- 查找符合条件的用户最爱吃的三道菜
    SELECT
        p.名称 AS 菜品名称,
        COUNT(*) AS 点餐次数
    FROM
        用户 u
        JOIN 用户创建在线订单 uco ON u.id = uco.用户_id
        JOIN 在线订单包含菜品 ucp ON uco.在线订单_id = ucp.在线订单_id
        JOIN 菜品 p ON ucp.菜品_id = p.id
    WHERE
        u.性别 = gender
        AND u.出生日期 BETWEEN startDate AND endDate
        AND SUBSTRING(u.学工号, 1, 1) = role
    GROUP BY
        p.名称
    ORDER BY
        点餐次数 DESC
    LIMIT 3;
END //

DELIMITER ;

DELIMITER //

CREATE PROCEDURE 用户活跃度分析(IN 时间段 VARCHAR(23))
BEGIN
    DECLARE start_date DATE;
    DECLARE end_date DATE;

    -- 提取开始日期和结束日期
    SET start_date = STR_TO_DATE(SUBSTRING_INDEX(时间段, '~', 1), '%Y-%m-%d');
    SET end_date = STR_TO_DATE(SUBSTRING_INDEX(时间段, '~', -1), '%Y-%m-%d');

    -- 输出该时间段内用户的总点餐次数（包括在线订单和排队订单）
    SELECT
        u.id AS 用户ID,
        u.名称 AS 用户名称,
        COUNT(DISTINCT oo.id) + COUNT(DISTINCT qo.id) AS 总点餐次数
    FROM
        用户 u
        LEFT JOIN 用户创建在线订单 uco ON u.id = uco.用户_id
        LEFT JOIN 在线订单 oo ON uco.在线订单_id = oo.id AND oo.创建时间 BETWEEN start_date AND end_date
        LEFT JOIN 用户创建排队订单 uq ON u.id = uq.用户_id
        LEFT JOIN 排队订单 qo ON uq.排队订单_id = qo.id AND qo.创建时间 BETWEEN start_date AND end_date
    GROUP BY
        u.id, u.名称;
END //

DELIMITER ;
