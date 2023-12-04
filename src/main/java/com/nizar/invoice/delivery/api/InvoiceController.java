package com.nizar.invoice.delivery.api;

import com.nizar.invoice.exception.ResourceNotFoundException;
import com.nizar.invoice.payload.request.invoice.InvoiceRequest;
import com.nizar.invoice.payload.response.invoice.InvoiceResponse;
import com.nizar.invoice.usecase.InvoiceUsecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/invoice")
public class InvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceUsecase invoiceUsecase;

    @GetMapping("/all")
    public ResponseEntity<List<InvoiceResponse>> getAllInvoice() {

        try {
            List<InvoiceResponse> invoices = invoiceUsecase.getAllInvoices();

            if (invoices.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            return new ResponseEntity<>(invoices, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable("id") String id) {

        try {
            InvoiceResponse response = invoiceUsecase.getInvoiceById(id);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/invoice/create")
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest request) {
        try {

            InvoiceResponse response = invoiceUsecase.createInvoice(request);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
}
