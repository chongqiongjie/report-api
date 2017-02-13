package report.api

import grails.transaction.Transactional
import groovy.sql.Sql
import org.postgresql.ds.PGSimpleDataSource
import groovy.json.JsonSlurper

// @Transactional
class MysqlService {
    def  _sqlClient = openSqlClient("192.168.1.3", "5432", "ms", "spiderdt")
    //def  _sqlClient = openSqlClient("10.212.36.41", "5432", "ms", "spiderdt")
    def openSqlClient(hostname, port, username, password) {
        def client = new Sql(new PGSimpleDataSource().each {
            it.url = "jdbc:postgresql://${hostname}:${port}/ms?useSSL=falsaracterEncoding=utf-8&stringtype=unspecified&sslmode=require&sslkey=/.clojurians-org/opt/tomcat/ssl/client/client.key.pk8&sslcert=/.clojurians-org/opt/tomcat/ssl/client/client.cert.pem&sslrootcert=/.clojurians-org/opt/tomcat/ssl/client/root.cert.pem&sslfactory=org.postgresql.ssl.jdbc4.LibPQFactory".toString()
            it.user = username
            it.password = password
        })
        [client: client, args: [hostname:hostname, port:port, username:username, password:password]]
    }

    def closeSqlClient() {
      sqlClient.client.close()
    }

    def refreshSqlClient() {
        _sqlClient
    }
    
    def getProjects(){
        def sqlClient = refreshSqlClient() 
        sqlClient.client.rows("select project from report")*.project.unique()
    }
   
    def getCategorys(projectId){
        def sqlClient = refreshSqlClient()
        sqlClient.client.rows("select category from report where project = ${projectId}")*.category.unique()
    }
    
    def getReports(projectId,categoryId){
        log.info("sql_report:" + "select report from report where project = ${projectId} and category = ${categoryId}")
        def sqlClient = refreshSqlClient()
        sqlClient.client.rows("select report from report where project = ${projectId} and category = ${categoryId}")*.report.unique()
    }

    def getMutilReportsSelector(projectId,categoryId,reportIds){
            log.info("getReport: $projectId, $categoryId, $reportIds")
            def sqlClient = refreshSqlClient()
            def reportPlaceholder = reportIds.collect{"?"}.join(",")
            //log.info(new Date())
            println("before select : "+new Date())
            def selectors =  sqlClient.client.rows(
                "select selector from report where project = ?  and category = ?  and report in (${reportPlaceholder})",
                [projectId, categoryId] + reportIds
            ) *.selector.unique()
           // log.info(new Date())
            println("before tree : "+new Date())
            compositeTrees(selectors.collect{nestMap(new JsonSlurper().parseText(it.toString()))},{it*.get(0)},true)
            //log.info(new Date())
            println("END :" +new Date())
    }

    def compositeTrees(trees,fn, isFirst=false){
        //log.info("trees_size:" + trees.size())
        //log.info("trees:" + trees)
        //log.info("trees-name:" + trees.class.name)
        if(!isFirst && trees.size >= 10000){
            "OVERFLOW"
         }else{
            trees*.keySet().sum().unique().collectEntries{key ->
                [key, trees.collect{it.find{it.key == key}?.value}.grep{it}.with{
                    if((!it.empty) && it.every{ it instanceof Map}) compositeTrees(it,fn)
                    else if(it.contains("OVERFLOW")) "OVERFLOW"
                    else fn(it)
                } ]
            }
        }
    }
   
    def nestMap(coll){
       // log.info("coll:" + coll)
        //log.info("coll_size:" + coll.size())
        // log.info("coll-name:" + new JsonSlurper().parseText(coll).class.name )
        coll.size() > 1 ? [(coll[0]): nestMap(coll.drop(1))] : coll }
    
   // def nestMap(coll){
   //    log.info("coll:" + coll)
   //    log.info("coll_size:" + coll.size())
   //    if(coll.size() == 1){
   //        [(coll): []]
   //    }else
   //        [(coll[0]): nestMap(coll.drop(1))] }

   
    def getSelectors(projectId,categoryId,reportId){
       log.info("getSelectors: $projectId, $categoryId, $reportId")
       getMutilReportsSelector(projectId,categoryId,[reportId])
       //log.info("selector:" + selectors)
      // log.info("selector_result:" + selectors.collect{nestMap(new JsonSlurper().parseText(it.toString()))})
       //compositeTrees(selectors.collect{nestMap(new JsonSlurper().parseText(it.toString()))},{it*.get(0)},true)
    }

    
    def getData(projectId,categoryId,reportId,selectorIds){
        log.info("getData: $projectId,$categoryId,$reportId,$selectorIds" )
        def sqlClient = refreshSqlClient()
        def selectorPlaceholder = selectorIds.collect{"?"}.join(",")

        sqlClient.client.rows(
            "select dimension_metrics  from report where project = ? and category = ? and report = ? and selector in ($selectorPlaceholder)", 
            [projectId, categoryId, reportId] + selectorIds
        )*.dimension_metrics
    } 

}

