package report.api

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import static groovy.json.JsonOutput.toJson
import groovy.json.JsonSlurper
import java.io.InputStream
import spiderdt_adapter.auth.Token
import spiderdt_common.rpc.Rest

@Transactional(readOnly = true)
class SelectorController {
    def mysqlService
    def restClient =  Rest.open()
    def index() {
        def header = request.getHeader("Authorization")
        if(!header) {
            render toJson([error: "not send token"])
        }else {
           // def token_ret = Token.get(restClient,"10.212.36.41",header.split().last())
              def token_ret = Token.get(restClient,"192.168.1.2",header.split().last())
            if(token_ret.error){
                render toJson(token_ret.error)
            }else{
                log.info("all data: ${params.pppid},${params.ppid},${params.pid}")
                if(params.pid == "ARGS"){
                    def input_stream = request.inputStream
                    def stream_json = new JsonSlurper().parseText(input_stream.text)
                    log.info("stream_json:"+stream_json.toString())
                    render toJson(mysqlService.getMutilReportsSelector(params.pppid,params.ppid,stream_json.args.reportIds))
                } else{
                    render toJson(mysqlService.getSelectors(params.pppid,params.ppid,params.pid))
                }
            }
        }
    }  
}
