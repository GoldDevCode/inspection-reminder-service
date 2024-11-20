package com.tribia.application.dto.svv.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
public class KjoretoyData {

    @JsonProperty("periodiskKjoretoyKontroll")
    private PeriodiskKjoretoyKontroll periodiskKjoretoyKontroll;
}
