package com.nizar.invoice.payload.request.invoice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceUpdateRequest {
    private String invoiceId;
    private String dueDate;
    private Long userId;
    Set<InvoiceItemRequest> invoiceItems;
}
