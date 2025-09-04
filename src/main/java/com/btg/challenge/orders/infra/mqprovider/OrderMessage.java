package com.btg.challenge.orders.infra.mqprovider;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage {

    @JsonProperty("codigoPedido")
    private Long codigoPedido;

    @JsonProperty("codigoCliente")
    private Long codigoCliente;

    @JsonProperty("itens")
    private List<OrderItemMessage> itens;
}
