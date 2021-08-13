package com.maersk.tpdoc.listener;

import com.maersk.tpdoc.entity.GDSMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class ReaderListener implements ItemReadListener<GDSMessage> {

    @Override
    public void beforeRead() {
        log.info("Before reading ..");
    }

    @Override
    public void afterRead(GDSMessage item) {
        log.info("After read {}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.error("Failed to read message.", ex);
    }
}
