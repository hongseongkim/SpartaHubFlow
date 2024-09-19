package com.sparta.order.service;

import com.sparta.order.client.CompanyClient;
import com.sparta.order.client.DeliveryClient;
import com.sparta.order.client.ProductClient;
import com.sparta.order.domain.dto.CompanyDto;
import com.sparta.order.domain.dto.DeliveryDto;
import com.sparta.order.domain.dto.OrderDto;
import com.sparta.order.domain.dto.ProductDto;
import com.sparta.order.domain.entity.Order;
import com.sparta.order.domain.entity.OrderStatusEnum;
import com.sparta.order.repository.OrderRepository;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final CompanyClient companyClient;
    private final DeliveryClient deliveryClient;

    @Transactional
    public OrderDto.Response createOrder(Long userId, OrderDto.Create orderDto) {

        ProductDto.Response productDto = productClient.getProductById(orderDto.getProductId());

        if (productDto == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        if (productDto.getStock() - orderDto.getQuantity() < 0) {
            throw new RuntimeException("재고가 충분하지 않습니다.");
        }

        CompanyDto.Response DepartureCompanyDto = companyClient.getCompany(orderDto.getDepartureCompanyId());
        CompanyDto.Response DestinationCompanyDto = companyClient.getCompany(orderDto.getDestinationCompanyId());

        if (DepartureCompanyDto == null || DestinationCompanyDto == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        UUID departureHubId = DepartureCompanyDto.getHubId();
        UUID destinationHubId = DestinationCompanyDto.getHubId();

        // 주문 생성
        Order order = orderRepository.save(new Order(
                orderDto.getProductId(),
                orderDto.getQuantity(),
                orderDto.getOrderStatus()
        ));

        order.setCreateBy(userId);
        order.setCreatedAt(LocalDateTime.now());

        // 배송 생성
        deliveryClient.createDelivery(DeliveryDto.Create.builder()
                .orderId(order.getOrderId())
                .departureHubId(departureHubId)
                .destinationHubId(destinationHubId)
                .deliveryAddress(orderDto.getDeliveryAddress())
                .receiverId(orderDto.getReceiverId())
                .build()
        );

        // 주문 생성 시 재고 감소
        if (order.getOrderStatus() == OrderStatusEnum.DELIVERING) {
            productClient.modifyProduct(productDto.getProductId(),
                    ProductDto.Modify.builder()
                            .productName(productDto.getProductName())
                            .description(productDto.getDescription())
                            .stock(productDto.getStock() - order.getQuantity())
                            .hubId(productDto.getHubId())
                            .companyId(productDto.getCompanyId())
                            .build()
            );
        }


        return OrderDto.Response.of(order);
    }

    @Transactional(readOnly = true)
    public OrderDto.Response getOrder(Long userId, String userRole, UUID orderId) {
        Order order = orderRepository.findByIdNotDeleted(orderId);

        if (order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다.");
        }

        // MASTER 권한을 갖지 않은 경우 타인의 주문 조회 불가
        if (!userRole.equals("MASTER") && !order.getCreateBy().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        return OrderDto.Response.of(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDto.GetAllOrdersResponse> getAllOrders(Long userId, String userRole, int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Order> orderList;

        if (userRole.equals("MASTER")) {
            orderList = orderRepository.findAllByIsDeletedFalse(pageable);
        }

        // MASTER 권한을 갖지 않은 경우 자신의 주문만 조회 가능
        else {
            orderList = orderRepository.findAllByCreateByAndIsDeletedFalse(userId, pageable);
        }

        return orderList.map(OrderDto.GetAllOrdersResponse::new).stream().toList();

    }

    @Transactional
    public OrderDto.Response modifyOrder(Long userId, String userRole, UUID orderId, OrderDto.Modify orderDto) {

        Order order = orderRepository.findByIdNotDeleted(orderId);

        if (order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다.");
        }

        ProductDto.Response productDto = productClient.getProductById(order.getProductId());

        if (productDto == null || productClient.getProductById(orderDto.getProductId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        // MASTER 권한을 갖지 않은 경우 타인의 주문 수정 불가
        if (!userRole.equals("MASTER") && !order.getCreateBy().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        // 해당 주문이 취소됨 상태가 아닐 때 취소된 경우
        if (order.getOrderStatus() != OrderStatusEnum.CANCELED && orderDto.getOrderStatus() == OrderStatusEnum.CANCELED) {

            // 재고 복구
            productClient.modifyProduct(order.getProductId(),
                    ProductDto.Modify.builder()
                            .productName(productDto.getProductName())
                            .description(productDto.getDescription())
                            .stock(productDto.getStock() + order.getQuantity())
                            .hubId(productDto.getHubId())
                            .companyId(productDto.getCompanyId())
                            .build());

        }

        order.modify(
                orderDto.getProductId(),
                orderDto.getQuantity(),
                orderDto.getOrderStatus()
        );

        return OrderDto.Response.of(orderRepository.save(order));
    }

    @Transactional
    public OrderDto.DeleteResponse deleteOrder(Long userId, String userRole, UUID orderId, OrderDto.Delete orderDto) {

        Order order = orderRepository.findByIdNotDeleted(orderId);

        if (order == null) {
            throw new IllegalArgumentException("존재하지 않는 주문입니다.");
        }

        // MASTER 권한을 갖지 않은 경우 타인의 주문 삭제 불가
        if (!userRole.equals("MASTER") && !order.getCreateBy().equals(userId)) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        orderRepository.deleteOrderById(orderId, LocalDateTime.now(), orderDto.getUserIdToDelete());

        return OrderDto.DeleteResponse.builder()
                .orderId(orderId)
                .build();
    }
}
