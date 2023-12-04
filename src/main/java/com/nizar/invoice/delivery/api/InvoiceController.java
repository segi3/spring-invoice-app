package com.nizar.invoice.delivery.api;

import com.nizar.invoice.configuration.security.jwt.AuthEntryPointJwt;
import com.nizar.invoice.exception.ResourceNotFoundException;
import com.nizar.invoice.models.Invoice;
import com.nizar.invoice.models.InvoiceItem;
import com.nizar.invoice.models.Item;
import com.nizar.invoice.models.User;
import com.nizar.invoice.payload.request.invoice.InvoiceItemRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceRequest;
import com.nizar.invoice.payload.response.invoice.InvoiceResponse;
import com.nizar.invoice.repository.InvoiceItemRepository;
import com.nizar.invoice.repository.InvoiceRepository;
import com.nizar.invoice.repository.ItemRepository;
import com.nizar.invoice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Invoice>> getAllInvoice() {

        try {
            /*
                If user can only get their own invoices
             *//*
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("user not found"));

            List<Invoice> invoices = invoiceRepository.findByUserId(user.getId());
            */

            List<Invoice> invoices = invoiceRepository.findAll();

            if (invoices.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<InvoiceResponse> getAlltest(@PathVariable("id") String id) {

        try {
            Invoice invoice = invoiceRepository.findById(UUID.fromString(id))
                    .orElseThrow(() -> new ResourceAccessException("Invoice not found"));

            Set<InvoiceItem> items = invoice.getInvoiceItems();

            Set<Item> x = new HashSet<>();

            for (InvoiceItem iv : items) {
                x.add(iv.getItem());
            }

            InvoiceResponse response = InvoiceResponse.builder()
                    .invoice_id(String.valueOf(invoice.getId()))
                    .due_date(invoice.getDueDate())
                    .total_price(invoice.getTotalPrice())
                    .items(x)
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/invoice/create")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceRequest request) {
        try {

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

            InvoiceResponse response = InvoiceResponse.builder()
                    .invoice_id(String.valueOf(invoice.getId()))
                    .due_date(invoice.getDueDate())
                    .total_price(invoice.getTotalPrice())
                    .items(new HashSet<Item>(items))
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
