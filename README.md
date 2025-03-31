![image](https://github.com/user-attachments/assets/3177d258-d1dc-4b71-9aa2-4c31dd3fbb9c)

<br/><br/>

## 🖥️ 프로젝트 정보
| 구분     | 내용                                                   |
|--------|------------------------------------------------------|
| 기간     | 2025.03.24 ~ 2025.03.31 (7일)                                |
| 팀원     | 김나연, 김예나(팀장), 김종연, 민혜원, 박은지                            |
| 설명     | 신선식품 배달 서비스 Freship의 백앤드 애플리케이션       |
| 사용 기술  | Java, Spring MVC, JPA, Spring Data JPA, MySQL, Redis, Amazon s3                 |
| JDK    | Amazon Corretto 17.0.14                    |
| Spring | Boot 3.4.4, Core 6.2.5                               |
  
<br/><br/>

## 👩🏻‍💻 역할 분담

| 팀원     | 내용                                                   |
|--------|------------------------------------------------------|
| 김나연   | 캐싱, 검색수 기반 인기 상품 조회                     |
| 김예나   | 캐싱, 조회수 기반 인기 상품 조회                             |
| 김종연    | 주문 및 결제 도메인, 동시성 제어                             |
| 민혜원   | 가게 및 상품 도메인, S3 이미지 업로드                            |
| 박은지   | 회원 도메인, 인증/인가                             |

<br/><br/>

## 📌 핵심 기능
### 🧮 Redis를 활용한 조회수 구현, 인기 상품 조회
**의사결정**
<br/>

- 기획 상, `검색 횟수`나 `조회수`는 자주 호출되지만, 정각이 되면 초기화되는 데이터
- 또한, **실시간 반영이 치명적인 데이터가 아님**
- 데이터베이스를 거치지 않은 빠른 조회, 변경을 위해 `캐시를 도입하기`로 결정
- 저장할 데이터의 크기가 크지 않고, sorted set(ZSET)를 통해 랭킹을 매기기 쉬운 Redis를 사용하기로 함

<br/>

**구현**
<br/>

<details>
  <summary>조회수, 검색수 저장</summary>
  <br/>
  <img width="40%" src="https://github.com/user-attachments/assets/a87031d5-f92e-41c9-bfdb-1031406769b6" />

  <br/><br/>
  
  - Redis에 조회수 데이터를 Zset으로 저장하는 로직
  - 상품이 최초로 조회되었다면 ZSet에 키를 추가하고 값을 1로 세팅
  - 이미 조회된 적 있다면, 키에 해당하는 값 1 증가

  <br/>

```java
// 조회수가 없는 경우엔 1로 초기화, 존재하는 경우엔 조회수를 1만큼 증가
    public Long addReadCount(Long productId) {
        if (productRedisUtils.notExistsReadCount(productId)) {
            productRedisUtils.setReadCount(productId);
            return 1L;
        }
        return productRedisUtils.addReadCount(productId);
    }
```
</details>

<details>
  <summary>랭킹(상위 10개) 조회 Cache Around 패턴 적용</summary>
  <br/>
  <img width="50%" alt="스크린샷 2025-03-31 12 58 16" src="https://github.com/user-attachments/assets/bc75383a-14cf-4085-99df-e11cab62a6c0" />

  <br/><br/>
  
  - Zset에 누적된 조회수, 검색 수를 기반으로 상위 10개의 상품 조회
  - Redis에 캐싱된 조회 결과가 있는지 확인 후, 없는 경우에는 Redis(조회수), DB(상품 정보) 접근
  - DB에 접근한 경우, 결과 값을 Redis에 30분간 보관 (TTL 30 min)

  <br/>

```java
// 조회수 기준 상위 10개의 상품 리스트 조회하기
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "rank", key = "'products:rank'", cacheManager = "productCacheManager")
    public List<ProductRankResponse> findProductsByReadCount() {
        List<Long> idList = productRedisUtils.findProductIds();
        List<Product> products = productRepository.findProductsByRank(idList);

        // 순위별로 정렬
        products.sort(Comparator.comparingInt(p -> idList.indexOf(p.getId())));

        List<ProductRankResponse> readCountResponses = ProductRankResponse.toProductRankResponseList(products);
        return readCountResponses;
    }
```
</details>

<br/>

**성능테스트**
<br/>

<details>
  <summary>상품 검색 성능 테스트</summary>
  
<br/>

  - 10만건의 데이터와 99명의 VUser를 기준으로 조회 진행
<img width="50%" src="https://github.com/user-attachments/assets/af568a8a-3461-4c4e-94c1-80139f0f45fb">

<br/><br/>

  - 결과(응답시간 95.4% 개선)

| 테스트조건     | TPS(초당 트랜잭션 수) |  평균 웅답 시간(ms) |
|--------|-------------------------|-----------------------------|
| 캐싱 미적용   | 14.9|      6213         |
| 캐싱 적용  | 333.2 |    286.94 |

</details>


<details>
  <summary>랭킹 조회 성능 테스트</summary>
  
<br/>

  - 100만건의 데이터 중에서 1초동안 1000번의 요청을 30번 반복
<img width="50%" alt="스크린샷 2025-03-31 13 34 53" src="https://github.com/user-attachments/assets/cd38ceec-7384-4c00-80c4-5fcec91cee08" />

<br/><br/>

  - 결과(응답시간 48.5% 개선)

| 테스트조건     | Throughput |  평균 웅답 시간(ms) |
|--------|-------------------------|-----------------------------|
| 조회수 조회(캐싱x)   | 1671 |      569         |
| 조회수 조회(캐싱o)  | 293 |   3035 |
| 상품 랭킹 조회(캐싱x)   | 605 |      1541         |
| 상품 랭킹 조회(캐싱o)  | 387 |   2405 |

</details>
<br/><br/>

### 🐎 주문 동시성 제어
**의사결정**
<br/>
- 신선 식품은 재고가 한정적인 경우가 많음
- 여러 사용자가 동시에 같은 상품을 주문하면 재고 이상으로 주문이 발생할 가능성이 있음
  
<br/>

- Redis의 setnx를 활용하여 분산락 구현
- Lock을 획득한 클라이언트의 UUID를 value에 저장
- Lua script를 이용해 atomic한 release 작업 지원

<br/><br/>

**구현**
<details>
  <summary>결제 프로세스</summary>
  
</details>

<br/><br/>

**성능테스트**
<br/>

<br/><br/>


### 💰 PG 결제 연동
**의사결정**
<br/>

- 커머스에서 결제 흐름을 이해하는 것이 핵심적이라는 판단
- 사실상 대부분의 서비스에서 사용되는 기술
- 고객의 재산과 관련된 부분이 있어, 세밀한 처리를 하는 경험 필요

**구현**
<br/>

<details>
  <summary>결제 프로세스</summary>
  <br/>
  <img width="60%" src="https://github.com/user-attachments/assets/082c9c6e-2c7c-41c9-92e6-1c3d7bc24623" />

  <br/><br/>
  

  ```java
  @GetMapping("/success")
      public ResponseEntity<Response<ConfirmResponse>> confirm(@ModelAttribute CheckoutResponse checkoutResponse) {
          // 트랜잭션 1
          ConfirmResponse confirmResponse = paymentService.verifyAndSend(checkoutResponse);

          // 트랜잭션 2
          Order order = orderRepository.findByOrderCodeWithMember(checkoutResponse.getOrderId())
                  .orElseThrow(() -> new ClientException(ErrorCode.NOT_FOUND_ORDER));

          // 트랜잭션 3
          return ResponseEntity.ok(
                  Response.of(paymentService.confirmPayment(confirmResponse, order.getMember().getId()))
          );
      }
  ```
  
  - 결제검증-승인요청 / 주문 확정을 별도로 처리
  - 트랜잭션을 쪼개서 각 영역간의 영향을 줄이고자 함

  <br/><br/>

```java
 @Transactional
    public ConfirmResponse verifyAndSend(CheckoutResponse res) {

        Order order = orderRepository.findByOrderCodeWithMember(res.getOrderId())
                .orElseThrow(() -> new ClientException(ErrorCode.NO_SUCH_ORDER));

        // PG에 요청된 금액과 주문서 금액 비교
        if (order.getTotalPrice() != res.getAmount()) {
            orderService.cancel(order.getId(), order.getMember().getId());
            throw new ClientException(ErrorCode.INVALID_PRICE_CHECKED);
        }

        return sendConfirmRequest(res);
    }
```

- 결제 요청된 주문이 존재하는지, 금액은 일치하는지 검증처리
- 일치한다면 PG에 결제 요청 전송
- 결제 전송시 Idempotency-Key를 헤더로 담아 보내면, 중복 요청 처리를 방지할 수 있음

<br/><br/>

```java
@Transactional
    public ConfirmResponse confirmPayment(ConfirmResponse confirmResponse, Long memberId) {
        // 결제 정보 저장
        paymentHistoryRepository.save(confirmResponse.toSuccessHistory(memberId));
        // 주문 정보 저장
        orderService.paymentDone(confirmResponse.getOrderId());

        return confirmResponse;
    }
```
- 결제 승인에 성공하면, 결제 정보를 저장하고 주문 상태(결제대기 -> 상품준비중)를 변경
</details>

<br/><br/>

## 📐 와이어 프레임
<img alt="스크린샷 2025-03-31 12 22 38" src="https://github.com/user-attachments/assets/c8ace5fe-2647-4b4b-aabc-485faf6bea30" />

<br/><br/>

## 📃 ERD
<br/>
<img alt="스크린샷 2025-03-31 12 20 34" src="https://github.com/user-attachments/assets/ba3eb2b8-087e-4a43-ae49-f5075d9b3c87" />

<br/><br/>

## 📃 API 명세서
<br/><br/>
<img alt="image" src="https://github.com/user-attachments/assets/3fcfa069-201e-4a67-88eb-f1a9bcfed1c3" />



