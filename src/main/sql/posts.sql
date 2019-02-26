







SELECT 
    *
FROM
    crwlr_posts
WHERE
    LEFT(url,
        LENGTH(url) - LENGTH(SUBSTRING_INDEX(url, '-', - 1)) - 1) IN (SELECT 
            newURL
        FROM
            (SELECT 
                LEFT(url, LENGTH(url) - LENGTH(SUBSTRING_INDEX(url, '-', - 1)) - 1) AS newURL,
                    COUNT(*) AS cnt
            FROM
                crwlr_posts
            WHERE
                created_at > '2018-11-01'
            GROUP BY newURL
            HAVING cnt > 1) tbl)
ORDER BY url
;





SELECT 
    COUNT(*)
FROM
    crawler_db_180111.crwlr_posts
WHERE
    created_at > '2018-11-20';



SELECT 
    LEFT(url,
        LENGTH(url) - LENGTH(SUBSTRING_INDEX(url, '-', - 1)) - 1) AS newURL,
    COUNT(*) AS cnt
FROM
    crwlr_posts
WHERE
    created_at > '2018-11-01' 
GROUP BY newURL
HAVING cnt > 1
;


SELECT COUNT(*) FROM crwlr_posts WHERE MONTH(publish_date) = '01' AND YEAR(publish_date) = '2019';


SELECT COUNT(*) FROM crwlr_posts WHERE MONTH(publish_date) = '01' AND YEAR(publish_date) = '2019';