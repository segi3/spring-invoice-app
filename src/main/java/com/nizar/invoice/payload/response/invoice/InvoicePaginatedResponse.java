package com.nizar.invoice.payload.response.invoice;

import com.nizar.invoice.models.Invoice;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class InvoicePaginatedResponse {
    private int currentPage;
    private long totalItems;
    private int totalPages;
    private List<Invoice> invoices;
}
