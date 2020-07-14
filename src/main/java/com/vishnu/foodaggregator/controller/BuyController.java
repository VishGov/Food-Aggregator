package com.vishnu.foodaggregator.controller;


import com.vishnu.foodaggregator.exception.InvalidItemRequestException;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;
import com.vishnu.foodaggregator.service.AggregatorService;
import com.vishnu.foodaggregator.validation.PriceValidation;
import com.vishnu.foodaggregator.validation.PropertySetValidation;
import com.vishnu.foodaggregator.validation.QuantityValidation;
import com.vishnu.foodaggregator.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

        Validator.createValidator()
                .addValidation(new PropertySetValidation("quantity", quantity))
                .addValidation(new QuantityValidation(quantity))
                .validate();

        return new ResponseEntity<>(aggregatorService.getByNameQuantity(itemName, quantity), HttpStatus.OK);
    }

    @GetMapping(value = BUY_ITEM_QTY_PRICE)
    public ResponseEntity<ItemResponse> buyItemWithQuantityAndPrice(@PathVariable String itemName,
                                                                    @RequestParam(required = true) Integer quantity,
                                                                    @RequestParam(required = true) String price) throws ItemNotFoundException, InvalidItemRequestException {
        Validator.createValidator()
                .addValidation(new PropertySetValidation("quantity", quantity))
                .addValidation(new QuantityValidation(quantity))
                .addValidation(new PropertySetValidation("price", price))
                .addValidation(new PriceValidation(price))
                .validate();

        return new ResponseEntity<>(aggregatorService.getByNameQuantityPrice(itemName, quantity, price), HttpStatus.OK);
    }

    @GetMapping(value = SHOW_SUMMARY)
    public ResponseEntity<Map<String, List<ItemResponse>>> showSummary() {
        return new ResponseEntity<>(aggregatorService.getSummary(), HttpStatus.OK);
    }
}
