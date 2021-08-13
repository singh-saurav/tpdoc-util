package com.maersk.tpdoc.listener;

import lombok.extern.slf4j.Slf4j;

import javax.batch.api.chunk.listener.ChunkListener;

@Slf4j
public class GDSChunkListener implements ChunkListener {

    @Override
    public void beforeChunk() throws Exception {

    }

    @Override
    public void onError(Exception e) throws Exception {
        log.error("Failed chunk processing.", e);
    }

    @Override
    public void afterChunk() throws Exception {

    }
}
