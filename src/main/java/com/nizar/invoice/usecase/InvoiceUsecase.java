package com.nizar.invoice.usecase;

import com.nizar.invoice.exception.ResourceNotFoundException;
import com.nizar.invoice.models.Invoice;
import com.nizar.invoice.models.InvoiceItem;
import com.nizar.invoice.models.Item;
import com.nizar.invoice.models.User;
import com.nizar.invoice.payload.request.invoice.InvoiceItemRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceRequest;
import com.nizar.invoice.payload.response.invoice.InvoiceItemResponse;
import com.nizar.invoice.payload.response.invoice.InvoiceResponse;
import com.nizar.invoice.repository.InvoiceItemRepository;
import com.nizar.invoice.repository.InvoiceRepository;
import com.nizar.invoice.repository.ItemRepository;
import com.nizar.invoice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class InvoiceUsecase {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public List<InvoiceResponse> getAllInvoices () {
        /*
            If user can only get their own invoices
         *//*

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        List<Invoice> invoices = invoiceRepository.findByUserId(user.getId());
        */

        List<Invoice> invoices = invoiceRepository.findAll();
        List<InvoiceResponse> invoiceResponses = new ArrayList<>();

        for (Invoice invoice:invoices) {
            invoiceResponses.add(generateInvoiceResponse(invoice));
        }

        return invoiceResponses;
    }

    public InvoiceResponse getInvoiceById(String id) {
        Invoice invoice = invoiceRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceAccessException("Invoice not found"));

        return generateInvoiceResponse(invoice);
    }

    public InvoiceResponse createInvoice(InvoiceRequest request) throws ParseException {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date parsedDate = dateFormat.parse(request.getDueDate());

        // create invoice
        Invoice invoice = new Invoice(user, parsedDate, 0);
        double totalPrice = 0;

        // create invoice items
        Set<InvoiceItem> invoiceItems = new HashSet<>();
        Set<Item> items = new HashSet<>();
        for (InvoiceItemRequest item:request.getInvoiceItems()) {
            Item temp = itemRepository.findById(item.getItemId()).orElseThrow(() -> new ResourceNotFoundException("user not found"));
            InvoiceItem invoiceItemTemp = new InvoiceItem(invoice, temp, item.getQuantity());
            invoiceItems.add(invoiceItemTemp);
            items.add(temp);

            totalPrice = totalPrice + (temp.getPrice() * item.getQuantity());
        }

        // append invoice item to invoice
        invoice.setInvoiceItems(invoiceItems);
        invoice.setTotalPrice(totalPrice);

        //save
        invoiceRepository.save(invoice);
        invoiceItemRepository.saveAll(invoiceItems);

        return generateInvoiceResponse(invoice);
    }

    private InvoiceResponse generateInvoiceResponse(Invoice invoice) {

        Set<InvoiceItem> invoiceItems = invoice.getInvoiceItems();
        Set<InvoiceItemResponse> items = new HashSet<>();

        for (InvoiceItem iit : invoiceItems) {
            InvoiceItemResponse item = InvoiceItemResponse.builder()
                    .name(iit.getItem().getName())
                    .price(iit.getItem().getPrice())
                    .quantity(iit.getQuantity())
                    .build();
            items.add(item);
        }

        return InvoiceResponse.builder()
                .invoice_id(String.valueOf(invoice.getId()))
                .due_date(invoice.getDueDate())
                .total_price(invoice.getTotalPrice())
                .items(items)
                .build();
    }
}
