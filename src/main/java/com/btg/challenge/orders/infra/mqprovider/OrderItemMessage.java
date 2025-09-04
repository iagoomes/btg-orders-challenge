package com.btg.challenge.orders.infra.mqprovider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemMessage {

    @JsonProperty("produto")
    private String produto;

    @JsonProperty("quantidade")
    private Integer quantidade;

    @JsonProperty("preco")
    private BigDecimal preco;
}