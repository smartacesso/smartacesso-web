package br.com.startjob.acesso.common.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuditLogger {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    public void loginSuccess(String channel, String unidade, String login, Long userId, String clientIp) {
        log.info("event=LOGIN_SUCCESS channel={} unidade={} login={} userId={} ip={}",
                channel, unidade, login, userId, clientIp);
    }

    public void loginFailure(String channel, String unidade, String login, String reason, String clientIp) {
        log.warn("event=LOGIN_FAILURE channel={} unidade={} login={} reason={} ip={}",
                channel, unidade, login, reason, clientIp);
    }
}
