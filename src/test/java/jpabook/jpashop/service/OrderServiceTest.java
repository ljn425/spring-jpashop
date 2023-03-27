package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class OrderServiceTest {
    @PersistenceContext EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;
    
    @Test
    public void 상품주문() throws Exception {
        //given
        Member member = createMember();

        Item book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = createOrder(member, book, orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //상품 주문시 상태는 ORDER
        assertThat(OrderStatus.ORDER).isEqualTo(getOrder.getStatus());

        //주문한 상품 종류 수가 정확해야 한다.
        assertThat(1).isEqualTo(getOrder.getOrderItems().size());

        //주문 가격은 가격 * 수량이다.
        assertThat(10000 * orderCount).isEqualTo(getOrder.getTotalPrice());

        //주문 수량만큼 재고가 줄어야 한다.
        assertThat(8).isEqualTo(book.getStockQuantity());

    }

    @Test
    public void 상품주문_재고수량초과() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        //when & then
        assertThatThrownBy(() -> createOrder(member, item, orderCount))
                .isInstanceOf(NotEnoughStockException.class);

    }

    @Test
    public void 주문취소() throws Exception {
        //given
        Member member = createMember();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = createOrder(member, item, orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        //주문 취소시 상태는 CANCEL 이다.
        assertThat(OrderStatus.CANCEL).isEqualTo(getOrder.getStatus());

        //주문이 취소된 상품은 그만큼 재고가 증가해야 한다.
        assertThat(10).isEqualTo(item.getStockQuantity());
    }
    private Member createMember() {
        Member member = Member.builder()
                .name("회원1")
                .address(new Address("서울", "강가", "123-123"))
                .build();
        em.persist(member);
        return member;
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = Book.builder()
                .name(name)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
        em.persist(book);
        return book;
    }

    private Long createOrder(Member member, Item item, int orderCount) {
        return orderService.order(member.getId(), item.getId(), orderCount);
    }

}