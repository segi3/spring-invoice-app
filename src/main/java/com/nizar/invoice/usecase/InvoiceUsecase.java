package com.nizar.invoice.usecase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nizar.invoice.configuration.security.jwt.AuthEntryPointJwt;
import com.nizar.invoice.exception.ResourceNotFoundException;
import com.nizar.invoice.models.Invoice;
import com.nizar.invoice.models.InvoiceItem;
import com.nizar.invoice.models.Item;
import com.nizar.invoice.models.User;
import com.nizar.invoice.payload.request.invoice.InvoiceDeleteRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceItemRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceUpdateRequest;
import com.nizar.invoice.payload.response.invoice.InvoiceItemResponse;
import com.nizar.invoice.payload.response.invoice.InvoiceResponse;
import com.nizar.invoice.repository.InvoiceItemRepository;
import com.nizar.invoice.repository.InvoiceRepository;
import com.nizar.invoice.repository.ItemRepository;
import com.nizar.invoice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class InvoiceUsecase {

    private static final Logger log = LoggerFactory.getLogger(InvoiceUsecase.class);
    public static final Gson gson = new GsonBuilder().create();

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

        for (InvoiceItemRequest item:request.getInvoiceItems()) {
            Item temp = itemRepository.findById(item.getItemId()).orElseThrow(() -> new ResourceNotFoundException("user not found"));
            InvoiceItem invoiceItemTemp = InvoiceItem.builder()
                    .item(temp)
                    .invoice(invoice)
                    .quantity(item.getQuantity())
                    .build();
            invoiceItems.add(invoiceItemTemp);

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

    @Transactional
    public void deleteOneInvoice(InvoiceDeleteRequest request) {
        try {
            Invoice invoice = invoiceRepository.findById(UUID.fromString(request.getInvoiceId()))
                    .orElseThrow(() -> new ResourceAccessException("Invoice not found"));

            Set<InvoiceItem> invoiceItems = invoice.getInvoiceItems();

            for (InvoiceItem iit : invoiceItems) {
                invoiceItemRepository.deleteById(iit.getId());
            }

            invoiceRepository.deleteById(invoice.getId());
        } catch (Exception e) {
            throw e;
        }
    }

    public InvoiceResponse updateInvoice(InvoiceUpdateRequest request) throws ParseException {
        try {
            // check if invoice exists
            Invoice invoice = invoiceRepository.findById(UUID.fromString(request.getInvoiceId()))
                    .orElseThrow(() -> new ResourceAccessException("Invoice not found"));

            // check if user exists
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("user not found"));

            // update invoice data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(request.getDueDate());
            double totalPrice = 0;

            invoice.setDueDate(parsedDate);
            invoice.setUser(user);

            // delete existing invoice items
            Set<InvoiceItem> invoiceItemsOld = invoice.getInvoiceItems();
            invoiceItemRepository.deleteAll(invoiceItemsOld);

            // create new invoice items
            Set<InvoiceItem> invoiceItemsNew = new HashSet<>();

            log.info("request item : {}", gson.toJson(request.getInvoiceItems()));

            for (InvoiceItemRequest item:request.getInvoiceItems()) {
                Item temp = itemRepository.findById(item.getItemId()).orElseThrow(() -> new ResourceNotFoundException("item not found"));
                InvoiceItem invoiceItemTemp = InvoiceItem.builder()
                        .item(temp)
                        .invoice(invoice)
                        .quantity(item.getQuantity())
                        .build();
                invoiceItemsNew.add(invoiceItemTemp);
                invoiceItemRepository.save(invoiceItemTemp);

                log.info("invoice id : {}", invoiceItemTemp.getInvoice().getId());
                log.info("item id : {}", invoiceItemTemp.getItem().getId());
                log.info("quantity : {}", invoiceItemTemp.getQuantity());

                totalPrice = totalPrice + (temp.getPrice() * item.getQuantity());
            }

            // append invoice item to invoice
            invoice.setInvoiceItems(invoiceItemsNew);
            invoice.setTotalPrice(totalPrice);

            //save
            invoiceRepository.save(invoice);

            return generateInvoiceResponse(invoice);

        } catch (Exception e) {
            throw e;
        }
    }

    private InvoiceResponse generateInvoiceResponse(Invoice invoice) {

        log.info("invoice id : {}", invoice.getId());
        log.info("invoice item size : {}", invoice.getInvoiceItems().size());

        Set<InvoiceItem> invoiceItems = invoice.getInvoiceItems();
        Set<InvoiceItemResponse> items = new HashSet<>();

        for (InvoiceItem iit : invoiceItems) {
            log.info("invoice id : {}", iit.getInvoice().getId());
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
