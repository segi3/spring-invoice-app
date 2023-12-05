package com.nizar.invoice.payload.request.invoice;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InvoiceItemRequest {
    private Long itemId;
    private int quantity;

}
