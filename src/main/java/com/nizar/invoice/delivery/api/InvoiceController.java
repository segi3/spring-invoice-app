package com.nizar.invoice.delivery.api;

import com.nizar.invoice.exception.ResourceNotFoundException;
import com.nizar.invoice.models.Invoice;
import com.nizar.invoice.payload.request.invoice.InvoiceDeleteRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceRequest;
import com.nizar.invoice.payload.request.invoice.InvoiceUpdateRequest;
import com.nizar.invoice.payload.response.invoice.InvoicePaginatedResponse;
import com.nizar.invoice.payload.response.invoice.InvoiceResponse;
import com.nizar.invoice.usecase.InvoiceUsecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InvoicePaginatedResponse> getAllInvoice(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size
    ) {

        Pageable paging = PageRequest.of(page, size);

        try {

            Page<Invoice> invoices = invoiceUsecase.findAllPaginated(paging);

            // create paging response
//            Map<String, Object> response = new HashMap<>();
//            response.put("currentPage", invoices.getNumber());
//            response.put("totalItems", invoices.getTotalElements());
//            response.put("totalPages", invoices.getTotalPages());
//            response.put("invoices", invoices.getContent());

            InvoicePaginatedResponse response = InvoicePaginatedResponse.builder()
                    .currentPage(invoices.getNumber())
                    .totalItems(invoices.getTotalElements())
                    .totalPages(invoices.getTotalPages())
                    .invoices(invoices.getContent())
                    .build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/invoice/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> getInvoiceById(@PathVariable("id") String id) {

        try {
            InvoiceResponse response = invoiceUsecase.getInvoiceById(id);

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/invoice/create")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceRequest request) {
        try {

            InvoiceResponse response = invoiceUsecase.createInvoice(request);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }



    @PostMapping("/invoice/delete")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> deleteOneInvoice(@RequestBody InvoiceDeleteRequest request) {
        try {

            invoiceUsecase.deleteOneInvoice(request);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/invoice/update")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<InvoiceResponse> updateInvoice(@RequestBody InvoiceUpdateRequest request) {
        try {

            InvoiceResponse response = invoiceUsecase.updateInvoice(request);

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
