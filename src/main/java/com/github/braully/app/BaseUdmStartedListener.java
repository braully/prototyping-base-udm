package com.github.braully.app;

import com.github.braully.persistence.GenericDAO;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 *
 * @author braully
 */
@Component
public class BaseUdmStartedListener implements
        ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = Logger.getLogger(BaseUdmStartedListener.class);
    /*
    
     */
    private static boolean started = false;

    /* */
    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            log.debug("CONTEXTO SPRING INICIALIZADO");
        } catch (Exception e) {
            log.warn("Falha ao verificar o contexto inicial", e);
        }
    }

}
