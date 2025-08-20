package com.sinse.websocket.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class MessageDto {
    @JsonProperty("cmd")
    private String command;
    @JsonProperty("payload")
    private Object payload;
}
