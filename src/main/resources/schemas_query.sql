SELECT distinct a.APP_SCHEMA_NAME SCHEMA_NAME,
                a.APP_NO,
                a.DB_NAME,
                a.DB_URL_NET db_url,
                FNC_GET_FUND_APP_PASS(a.APP_SCHEMA_NAME) SCHEMA_PASS,
                a.FUND_TYPE_ID,
                a.FUND_TYPE_NAME
FROM
    vw_all_fund a
WHERE a.TYPE_ID = 2
  AND a.APP_NO IS NOT NULL
  AND a.STATUS_ID in (1,3)



