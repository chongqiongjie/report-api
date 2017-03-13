# report-api



curl -L -X GET -H "Authorization: Bearer 23be2dcf-3de0-4915-999d-8bc4a8ef935d" -d '{"args":{"reportIds": ["sovi_period_trend","cooler_period_trend","sovi_period_trend","availability_period_trend"]}}' http://192.168.1.2:2222/project/cocacola/category/score/report/ARGS/selector


curl -L -X GET -H "content-type: application/json" -H "Authorization: Bearer ae582b86-c2d9-430f-bd87-dc3dd94f6013" -d '{"args":{"selectorIds": ["[\"period=2016-12-31\",\"bg=BIG\",\"bottler=Heilongjiang / 黑龙江\"]
"]}}' http://192.168.1.2:2222/project/cocacola/category/score/report/kpi/selector/ARGS/data


curl -L -X GET -H "content-type: application/json" -H "Authorization: Bearer ae582b86-c2d9-430f-bd87-dc3dd94f6013" -d '{"args":{"selectorIds": ["[\"period=2016-10-31\",\"bg=BIG\",\"bottler=BIG Total / 全体\"]"]}}' https://192.168.1.2:8443/report-api-v1/project/cocacola/category/score/report/channel/selector/ARGS/data
