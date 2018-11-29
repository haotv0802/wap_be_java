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
