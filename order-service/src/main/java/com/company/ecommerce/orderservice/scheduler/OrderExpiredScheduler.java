package com.company.ecommerce.orderservice.scheduler;

import com.company.ecommerce.orderservice.client.ProductClient;
import com.company.ecommerce.orderservice.client.dto.ListReplenishProductQuantityRequest;
import com.company.ecommerce.orderservice.client.dto.ReplenishProductQuantityRequest;
import com.company.ecommerce.orderservice.dto.projection.ProductQuantitySummary;
import com.company.ecommerce.orderservice.dto.response.OrderExpiredCheckResponse;
import com.company.ecommerce.orderservice.servicer.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExpiredScheduler {

    private final OrderService orderService;
    private final ProductClient productClient;

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void orderPaymentCompleted() {
        OrderExpiredCheckResponse orderExpiredCheckResponse = orderService.orderExpiredCheck();
        log.info("expired order ids={}", orderExpiredCheckResponse.getOrderIds());

        if (orderExpiredCheckResponse.getOrderIds().isEmpty()) {
            return;
        }

        List<ProductQuantitySummary> productQuantitySummaries = orderService.replenishProductQuantity(orderExpiredCheckResponse.getOrderIds());
        log.info("productQuantitySummaries={}", productQuantitySummaries);

        if (productQuantitySummaries.isEmpty()) {
            return;
        }

        List<ReplenishProductQuantityRequest> items = productQuantitySummaries.stream()
                .map(summary -> {
                    ReplenishProductQuantityRequest request = new ReplenishProductQuantityRequest();
                    request.setProductId(summary.getProductId());
                    request.setQuantity(summary.getQuantity());
                    return request;
                })
                .toList();

        ListReplenishProductQuantityRequest request = new ListReplenishProductQuantityRequest();
        request.setReplenishProductQuantityRequestList(items);

        productClient.setProductStockInProductIdIsQuantity(request);
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void orderShipment() {
        int i = orderService.orderShipment();
        log.info("shipment order count={}", i);
    }
}
