package com.maersk.tpdoc.mapper;

import com.maersk.tpdoc.entity.GDSMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class GDSMessageMapper implements RowMapper<GDSMessage> {

    @Override
    public GDSMessage mapRow(ResultSet resultSet, int i) throws SQLException {
        log.debug("Mapping row number {}.", i);
        if (null != resultSet) {
            long instanceId = resultSet.getLong("INSTANCEID");
            Blob content = resultSet.getBlob("CONTENT");
            if (null != content) {
                return GDSMessage.builder().instanceId(instanceId).content(new String(content.getBytes(1, (int) content.length()))).build();
            } else {
                log.warn("Row number {} is having empty blob");
            }
        }
        return null;
    }
}
