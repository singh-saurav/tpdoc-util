package com.maersk.tpdoc.listener;

import com.maersk.tpdoc.entity.GDSMessage;
import com.maersk.tpdoc.entity.GDSUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class UpdateListener implements ItemProcessListener<GDSMessage, GDSUpdate> {

    @Override
    public void beforeProcess(GDSMessage item) {
        log.trace("Received input {}", item);
    }

    @Override
    public void afterProcess(GDSMessage item, GDSUpdate result) {
        log.trace("Processed output {}", result);
    }

    @Override
    public void onProcessError(GDSMessage item, Exception e) {
        log.error("Failed to process item {}.", item, e);
    }
}
