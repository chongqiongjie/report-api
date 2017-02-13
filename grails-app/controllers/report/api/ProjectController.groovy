package report.api

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import static groovy.json.JsonOutput.toJson
import spiderdt_adapter.auth.Token
import spiderdt_common.rpc.Rest

@Transactional(readOnly = true)
class ProjectController {
    def mysqlService
    def restClient =  Rest.open()
    def index() {
        def header = request.getHeader("Authorization")
        if(!header){
           render toJson([error: "not send token"])
        }else {
           log.info("header:" + header)
           log.info("token:" + header.split().last())
           //def token_ret = Token.get(restClient,"10.212.36.41",header.split().last())
           def token_ret = Token.get(restClient,"192.168.1.2",header.split().last())
           token_ret.error ? render(toJson(token_ret.error)) : render(toJson(mysqlService.getProjects()))
        }
    }

}
