package org.example.logisticapplication.service;

import lombok.RequiredArgsConstructor;
import org.example.logisticapplication.domain.Order.OrderInfo;
import org.example.logisticapplication.repository.OrderRepository;
import org.example.logisticapplication.utils.OrderMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderInfoService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderInfo> findLastOrders(
            Integer countOfLastOrders
    ) {

        Pageable pageable = PageRequest.of(0, countOfLastOrders, Sort.by(Sort.Direction.DESC, "id"));
        var lastOrders = orderRepository.findLast(pageable);

        return lastOrders.stream()
                .map(orderMapper::toDomainInfo)
                .toList();
    }
}
