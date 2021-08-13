package com.maersk.tpdoc.processor;

import com.maersk.tpdoc.entity.GDSMessage;
import com.maersk.tpdoc.entity.GDSUpdate;
import com.maersk.tpdoc.processor.exception.EmptyGdsMessageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class GDSMessageProcessor implements ItemProcessor<GDSMessage, GDSUpdate> {

    private static final String DELIMITER = "\u0003";

    @Override
    public GDSUpdate process(GDSMessage gdsMessage) throws Exception {
        if (null != gdsMessage) {
            log.debug("Processing message {}.", gdsMessage);
            String[] split = gdsMessage.getContent().split(DELIMITER);
            if (null != split && split.length >= 7 && null != split[7]) {
                return GDSUpdate.builder().instanceId(gdsMessage.getInstanceId()).contentInstanceId(split[7]).build();
            } else {
                log.warn("No GDS InstanceId found for record {}", gdsMessage);
            }
        }
        throw new EmptyGdsMessageException("Empty GDS Message Found, Skipping..");
    }
}
