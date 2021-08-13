package com.maersk.tpdoc.listener;

import com.maersk.tpdoc.entity.GDSUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;

import java.util.List;

@Slf4j
public class WriterListener implements ItemWriteListener<GDSUpdate> {

    @Override
    public void beforeWrite(List<? extends GDSUpdate> items) {
        log.debug("Received total {} records to update.", items.size());
    }

    @Override
    public void afterWrite(List<? extends GDSUpdate> items) {
        log.debug("Updated total {} records to update.", items.size());
    }

    @Override
    public void onWriteError(Exception exception, List<? extends GDSUpdate> items) {
        log.error("Failed to update record. Records {}", items, exception);
    }
}
