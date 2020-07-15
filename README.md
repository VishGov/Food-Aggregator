# Food-Aggregator
Food aggregator that will call external suppliers and aggregate their items data. We will expose endpoints to get items from this data based on certain criterias.

# Highlights

- Spring Boot and Java 11
- Lombok used to generate code for getter/setter/constructors and for builders
- Logging AOP to log incoming method calls and log the total method call time (this will be useful to check the time difference between Challenge #1 and Challenge #5)
  - Also uses MDC so that we can log a REQUEST_ID per request to track logs better
- Exception Handling using @ControllerAdvice and @ExceptionHandler
- REST calls made with simple RestTemplate
- Async behavior achieved with CompletableFuture and RestTemplate
- Validator class that validates the input parameters (uses Strategy Pattern)


# Assumptions

- Challenge #1
  - We have to call each endpoint and THEN aggregate all the responses into a list.
  - Using this list, we will filter based on name. This means the items that failed the check will be rejected as we are not told to save the supplier API responses.
  - As the dummy response had TWO items of the same NAME (but different product id's), the assumption is that we must return a list to accomodate multiple items that fulfill the conditions (in this case, name)
     - i.e ```List<ItemResponse>```
- Challenge #2
  - In addition to Challenge #1 assumptions, it is assumed that filter is based on name and quantity (request quantity <= supplier response quantity)
  - If supplier response has TWO items for the same name, and only one of the items matches the quantity criteria, then only one item will be present in ```List<ItemResponse>```
    - Ex : 
    - If for "apple" there are two supplier response items with quantities 4 and 1 respectively, and the request quantity was 2, only the item with quantity 4 has passed the critera
    - Assumption is that the final response will have ItemResponse with quantity 2 instead of 4 (to show that the requested quantity is available for the item)
    ```
    [
      {
          "id": "74-033-7213",
          "name": "okra",
          "quantity": 2,
          "price": "$61.42"
      }
    ]

    ```
    
- Challenge #3
  - In addition to Challenge #1 and #2 assumptions, it is assumed that the caching mechanism will work as below:
    - When the endpoint is hit for the first time, the cache is empty, so all the items from all the suppliers will be added to the cache. These items will be filtered for name, quantity and price and ``` List<ItemResponse> ``` will be returned. 
      - Cache key will be itemName ("apple") and value will be ```List<ItemResponse>``` (as there can be different apple items with different product ids)
    - After this, if a call requests for an item name, and it is present in the cache, its value (``` List<ItemResponse> ```) is taken.
      - In this case, the List<ItemResponse> will be filtered and only those ItemResponse's that match the quantity and price criteria (cacheItem price <= request price), will be returned.
      - Before returning, we will update the cache for those ItemResponse's, reducing its quantity by the requested quantity (to signify that we have taken the items)
        - If after the reduction, the ItemResponse in the cache has 0 quantity, it is removed from the ``` List<ItemResponse> ``` that it is in.
        - If an ItemResponse is removed and the List it was in becomes empty, we evict the key corresponding to this List.
    - If a call requests for an item name, and it is NOT PRESENT in the cache, we will call the suppliers to get all the items.
      - In this case, we will add all the items from the suppliers call, into our cache. If it is already present, the supplier item quantity will be added to the cache's item quantity. Its price will be updated to the supplier item price (as price changes can happen according to problem statement)
      

- Challenge #4
  - Will just return a ``` Map<String, List<ItemResponse>> ```
  
- Challenge #5
  - Same assumptions as #1 but calls to suppliers will be made in async manner.
