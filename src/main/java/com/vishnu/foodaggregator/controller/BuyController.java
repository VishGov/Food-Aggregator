package com.vishnu.foodaggregator.controller;


import com.vishnu.foodaggregator.exception.InvalidItemRequestException;
import com.vishnu.foodaggregator.exception.ItemNotFoundException;
import com.vishnu.foodaggregator.response.ItemResponse;
import com.vishnu.foodaggregator.service.AggregatorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.vishnu.foodaggregator.constants.Constants.ITEM_REQUEST_INVALID_QUANTITY;
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
        return new ResponseEntity<>(aggregatorService.getByName(itemName), HttpStatus.OK);
    }

    @GetMapping(value = BUY_ITEM_QTY)
    public ResponseEntity buyItemWithQuantity(@PathVariable String itemName,
                                              @RequestParam(required = true) Integer quantity) throws ItemNotFoundException, InvalidItemRequestException {
        if (quantity < 1)
            throw new InvalidItemRequestException(ITEM_REQUEST_INVALID_QUANTITY);

        return new ResponseEntity<>(aggregatorService.getByNameQuantity(itemName, quantity), HttpStatus.OK);
    }
}
