package com.nizar.invoice.payload.response.invoice;

import com.nizar.invoice.models.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Builder
@Setter
@Getter
public class InvoiceResponse {

    private String invoice_id;
    private Date due_date;
    private double total_price;
    private Set<Item> items;
}
