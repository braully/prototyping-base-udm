/*
 Copyright 2109 Braully Rocha

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
 */
package com.github.braully.business;

import com.github.braully.persistence.GenericDAO;
import com.github.braully.constant.StatusExecutionCycle;
import com.github.braully.domain.BinaryFile;
import com.github.braully.util.UtilIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author braully
 */
@Service("binaryFileProcessorBC")
public class BinaryFileProcessorBC {

    private static final Logger log = Logger.getLogger(BinaryFileProcessorBC.class);

    @Autowired(required = false)
    private List<BinaryFileProcessor> binaryFileProcessors;
    @Resource(name = "genericDAO")
    private GenericDAO genericDAO;

    @Scheduled(cron = "0 0 0 * * *")
    public void processReadiesFile() {
        List<BinaryFile> arquivosPendentes = this.genericDAO.loadCollectionWhere(BinaryFile.class, "statusExecution", StatusExecutionCycle.READY);
        if (arquivosPendentes != null) {
            for (BinaryFile arquivo : arquivosPendentes) {
                processFile(arquivo);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void processFile(BinaryFile arquivo) {
        if (arquivo == null || arquivo.isReady()) {
            return;
        }
        arquivo = genericDAO.loadEntity(arquivo);
        if (binaryFileProcessors != null) {
            for (BinaryFileProcessor processador : binaryFileProcessors) {
                try {
                    if (processador.getType().equals(arquivo.getType())) {
                        processador.processFile(arquivo);
                        arquivo.setStatusExecution(StatusExecutionCycle.DONE);
                    }

                } catch (Exception e) {
                    arquivo.setStatusExecution(StatusExecutionCycle.BLOCKED);
                    log.error("Falha ao processar arquivo: " + arquivo.getName(), e);
                }
            }
        }
        genericDAO.update(arquivo);
    }

    @Async
    public void processReadiesAsync() {
        processReadiesFile();
    }

    public void saveBinaryFile(String fileName, InputStream inputStream) throws IOException {
        BinaryFile binaryFile = new BinaryFile();
        binaryFile.setName(fileName);
        //TODO: Save on storage. Avoid database
        binaryFile.setFileBinary(UtilIO.loadStream(inputStream));
        this.genericDAO.saveEntity(binaryFile);
    }
}
