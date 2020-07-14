package com.vishnu.foodaggregator.controller;


import com.vishnu.foodaggregator.exception.InvalidItemRequestException;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;
import com.vishnu.foodaggregator.service.AggregatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.vishnu.foodaggregator.constants.Constants.*;
import static com.vishnu.foodaggregator.constants.Mappings.*;

@RequestMapping(FOOD_AGGREGATOR_V1)
@RestController
public class BuyController {

    private AggregatorService aggregatorService;

    public BuyController(AggregatorService aggregatorService) {
        this.aggregatorService = aggregatorService;
    }

    @GetMapping(value = BUY_ITEM)
    public ResponseEntity<ItemResponse> buyItem(@PathVariable String itemName) throws ItemNotFoundException {
        return new ResponseEntity<>(aggregatorService.getByName(itemName, false), HttpStatus.OK);
    }

    @GetMapping(value = FAST_BUY_ITEM)
    public ResponseEntity<ItemResponse> fastBuyItem(@PathVariable String itemName) throws ItemNotFoundException {
        return new ResponseEntity<>(aggregatorService.getByName(itemName, true), HttpStatus.OK);
    }

    @GetMapping(value = BUY_ITEM_QTY)
    public ResponseEntity<ItemResponse> buyItemWithQuantity(@PathVariable String itemName,
                                                            @RequestParam(required = true) Integer quantity) throws ItemNotFoundException, InvalidItemRequestException {
        if (quantity < 1)
            throw new InvalidItemRequestException(ITEM_REQUEST_INVALID_QUANTITY);

        return new ResponseEntity<>(aggregatorService.getByNameQuantity(itemName, quantity), HttpStatus.OK);
    }

    @GetMapping(value = BUY_ITEM_QTY_PRICE)
    public ResponseEntity<ItemResponse> buyItemWithQuantityAndPrice(@PathVariable String itemName,
                                                                    @RequestParam(required = true) Integer quantity,
                                                                    @RequestParam(required = true) String price) throws ItemNotFoundException, InvalidItemRequestException {
        if (quantity < 1)
            throw new InvalidItemRequestException(ITEM_REQUEST_INVALID_QUANTITY);

        isValidPrice(price);

        return new ResponseEntity<>(aggregatorService.getByNameQuantityPrice(itemName, quantity, price), HttpStatus.OK);
    }

    @GetMapping(value = SHOW_SUMMARY)
    public ResponseEntity<Map<String, List<ItemResponse>>> showSummary() {
        return new ResponseEntity<>(aggregatorService.getSummary(), HttpStatus.OK);
    }


    private void isValidPrice(String price) throws InvalidItemRequestException {
        try {
            BigDecimal requestPrice = new BigDecimal(String.valueOf(NumberFormat.getCurrencyInstance(Locale.US).parse(price)));
            if (requestPrice.compareTo(BigDecimal.ZERO) <= 0)
                throw new InvalidItemRequestException(ITEM_REQUEST_INVALID_PRICE_LESS_THAN_ZERO);
        } catch (ParseException e) {
            //todo add logger?
            System.out.println(e.getMessage());
            throw new InvalidItemRequestException(ITEM_REQUEST_INVALID_PRICE_NOT_USD);
        }
    }
}
