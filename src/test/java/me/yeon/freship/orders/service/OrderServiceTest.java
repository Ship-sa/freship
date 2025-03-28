package me.yeon.freship.orders.service;

import me.yeon.freship.common.domain.constant.ErrorCode;
import me.yeon.freship.common.exception.ClientException;
import me.yeon.freship.common.infrastructure.ClockHolder;
import me.yeon.freship.member.domain.Member;
import me.yeon.freship.member.domain.MemberRole;
import me.yeon.freship.member.infrastructure.MemberRepository;
import me.yeon.freship.orders.domain.Order;
import me.yeon.freship.orders.domain.contant.OrderStatus;
import me.yeon.freship.orders.infrastructure.OrderCodeGenerator;
import me.yeon.freship.orders.infrastructure.OrderRepository;
import me.yeon.freship.product.domain.Category;
import me.yeon.freship.product.domain.Product;
import me.yeon.freship.product.domain.Status;
import me.yeon.freship.product.infrastructure.ProductRepository;
import me.yeon.freship.store.domain.Store;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderCodeGenerator orderCodeGenerator;
    @Mock private ClockHolder clockHolder;

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks private OrderService orderService;

    @Nested
    class 주문_생성 {
        @Test
        void 주문_생성이_정상적으로_생성된다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
            when(orderRepository.save(any(Order.class))).thenReturn(order);
            when(orderCodeGenerator.create(any(Category.class), any(LocalDate.class))).thenReturn("newOrderCode");

            Long orderId = orderService.create(1L, 4, 1L);

            // then
            assertThat(orderId).isNotNull();
            assertThat(product.getQuantity()).isEqualTo(6);
        }

        @Test
        void 주문_생성_도중_Product_를_찾지_못하면_에러를_던진다() {
            // given & when
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // then
            Assertions.assertThatThrownBy(
                            () -> orderService.create(1L, 2, 1L))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.EXCEPTION);
        }

        @Test
        void 주문_생성_도중_Member_를_찾지_못하면_에러를_던진다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            // when
            when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
            when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

            // then
            Assertions.assertThatThrownBy(
                            () -> orderService.create(1L, 2, 1L))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.NOT_FOUND_MEMBER);
        }
    }

    @Nested
    class 주문_취소 {

        @Test
        void 주문_취소가_정상적으로_수행된다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Member owner = new Member("email2@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_OWNER);
            ReflectionTestUtils.setField(member, "id", 2L);

            Store store = new Store(owner, "example store", "bizRegNum", "city district detail");
            ReflectionTestUtils.setField(store, "id", 1L);
            ReflectionTestUtils.setField(product, "store", store);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(orderRepository.findByIdWithMember(anyLong())).thenReturn(Optional.of(order));
            when(productRepository.findByIdWithStoreAndOwner(anyLong())).thenReturn(Optional.of(product));
            orderService.cancel(anyLong(), 2L);

            // then
            assertThat(product.getQuantity()).isEqualTo(14);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCEL);
        }

        @Test
        void 주문_상태가_DELI_PROGRESS_라면_에러를_던진다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            order.changeStatus(OrderStatus.DELI_PROGRESS);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(orderRepository.findByIdWithMember(anyLong())).thenReturn(Optional.of(order));

            // then
            assertThatThrownBy(
                    () -> orderService.cancel(anyLong(), 1L))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
        }

        @Test
        void 주문_상태가_DELI_DONE_라면_에러를_던진다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            order.changeStatus(OrderStatus.DELI_DONE);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(orderRepository.findByIdWithMember(anyLong())).thenReturn(Optional.of(order));

            // then
            assertThatThrownBy(
                    () -> orderService.cancel(anyLong(), 1L))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
        }

        @Test
        void 자신이_생성하거나_자신의_가게에_들어온_주문이_아니면_에러를_던진다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Member owner = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_OWNER);
            ReflectionTestUtils.setField(member, "id", 2L);

            Store store = new Store(owner, "example store", "bizRegNum", "city district detail");
            ReflectionTestUtils.setField(store, "id", 1L);
            ReflectionTestUtils.setField(product, "store", store);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            ReflectionTestUtils.setField(order, "id", 1L);


            // when
            when(orderRepository.findByIdWithMember(anyLong())).thenReturn(Optional.of(order));
            when(productRepository.findByIdWithStoreAndOwner(anyLong())).thenReturn(Optional.of(product));

            // then
            assertThatThrownBy(() -> orderService.cancel(anyLong(), 3L))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.FORBIDDEN_ORDER_CANCELLATION);
        }

    }

    @Nested
    class 주문_상태_변경 {

        @Test
        void 배송_출발이_정상적으로_수행된다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Member owner = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 2L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            order.changeStatus(OrderStatus.DELI_PROVISION);
            ReflectionTestUtils.setField(order, "id", 1L);

            LocalDateTime currentTime = LocalDateTime.now();

            // when
            when(orderRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(order));
            when(clockHolder.currentLocalDateTime()).thenReturn(currentTime);
            orderService.startDelivery(anyLong(), anyLong());

            // then
            assertThat(order.getShippedAt()).isEqualTo(currentTime);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELI_PROGRESS);
        }

        @Test
        void 배송_출발_시도_중_Order_를_찾지_못하면_에러를_던진다() {

            // given & when
            when(orderRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.startDelivery(anyLong(), anyLong()))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.FORBIDDEN_DELI_START);
        }

        @Test
        void 배송_출발_시도_중_DELI_PROVISION_상태가_아니면_에러를_던진다() {

            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(orderRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(order));

            // then
            assertThatThrownBy(() -> orderService.startDelivery(anyLong(), anyLong()))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_REQ_DELIVERY);
        }

        @Test
        void 결제_완료_절차가_정상적으로_수행된다() {
            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            order.changeStatus(OrderStatus.PENDING);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(orderRepository.findByOrderCode(anyString())).thenReturn(Optional.of(order));
            orderService.paymentDone(anyString());

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELI_PROVISION);
        }

        @Test
        void 결제_완료_절차_중_Order_를_찾지_못하면_에러를_던진다() {

            // given & when
            when(orderRepository.findByOrderCode(anyString())).thenReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> orderService.paymentDone(anyString()))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.NOT_FOUND_ORDER);
        }

        @Test
        void 결제_완료_절차_중_PENDING_상태가_아니면_에러를_던진다() {

            // given
            Product product = new Product("product1", 10, Status.ON_SALE, Category.MEAT, 10000, "url", "description1");
            ReflectionTestUtils.setField(product, "id", 1L);

            Member member = new Member("email1@example.com", "password", "name", "01023456789", "city district detail", MemberRole.ROLE_MEMBER);
            ReflectionTestUtils.setField(member, "id", 1L);

            Order order = Order.newOrder("newOrderCode", member, product, 4);
            order.changeStatus(OrderStatus.DELI_DONE);
            ReflectionTestUtils.setField(order, "id", 1L);

            // when
            when(orderRepository.findByOrderCode(anyString())).thenReturn(Optional.of(order));

            // then
            assertThatThrownBy(() -> orderService.paymentDone(anyString()))
                    .isInstanceOf(ClientException.class)
                    .extracting("errorCode")
                    .isEqualTo(ErrorCode.INVALID_ORDER_STATUS);
        }
    }
}