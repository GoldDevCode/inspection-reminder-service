package com.tribia.application.dto.svv.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PeriodiskKjoretoyKontroll {

    @JsonProperty("kontrollfrist")
    private String kontrollfrist;

    @JsonProperty("sistGodkjent")
    private String sistGodkjent;
}
