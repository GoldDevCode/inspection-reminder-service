package com.tribia.application.dto.svv.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ToString
public class SVVApiResponse {

    @JsonProperty("kjoretoydataListe")
    private List<KjoretoyData> kjoretoydataListe;

    @JsonProperty("feilmelding")
    private String feilmelding;
}
