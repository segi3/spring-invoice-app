package com.nizar.invoice.delivery.api;

import com.nizar.invoice.models.Item;
import com.nizar.invoice.repository.InvoiceItemRepository;
import com.nizar.invoice.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/item")
public class ItemController {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Item>> getAllItem() {

        List<Item> items = itemRepository.findAll();

        return new ResponseEntity<>(items, HttpStatus.OK);
    }
}
