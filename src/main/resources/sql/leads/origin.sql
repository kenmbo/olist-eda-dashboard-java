SELECT
    origin,
    COUNT(mql_id) AS total_leads
FROM leads_qualified
WHERE origin IS NOT NULL AND origin != ''
  AND origin != 'other'
GROUP BY origin
ORDER BY total_leads ASC -- Sorted ascending so Plotly puts the largest bar at the top
