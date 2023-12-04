package com.nizar.invoice.payload.response.invoice;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class InvoiceItemResponse {
    private String name;
    private double price;
    private int quantity;
}
