package com.maersk.tpdoc.entity;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.StringJoiner;

@Builder
@Getter
public class GDSUpdate implements Serializable {

    private static final long serialVersionUID = 3372139150096813082L;

    private long instanceId;

    private String contentInstanceId;

    @Override
    public String toString() {
        return new StringJoiner(", ", GDSUpdate.class.getSimpleName() + "[", "]")
                .add("instanceId=" + instanceId)
                .add("contentInstanceId='" + contentInstanceId + "'")
                .toString();
    }
}
