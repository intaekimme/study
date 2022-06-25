intellij 단축키
command + option + v : 변수로 추출?

command + shift + 위/아래 화살표 : 문법 오류 발생하지 않는 범위 내 이동
option + shift + 위/아래 화살표 : 문법 오류 고려 안하고 이동

assertThat과 assertThrow의 기능 이해하기


control + r : 실행

command + shift + Enter : format에 맞게 완성되는거 같음

control + tab + 숫자 : 패널? 간 이동

option + command + N : 인라인 변수화

control + o : override method

------


@Entity : JPA가 관리하는 엔티티가 되는 것

@Id : pk가 됨, 
DB에 값을 넣으면 id가 자동으로 생성되는 전략 : identity 전략

@GeneratedValue(stratege = GenerationType.IDENTITY)


JPA는 EntityManager에 의해 동작
JPA를 사용하려면 EntityManager를 주입받아야 한다.
주입에 사용하는 어노테이션 : @PersistenceContext


command + shift + T : 테스트 클래스 작성

MemberRepository를 테스트 해보기 위해 MemberRepositoryTest 작성
테스트 클래스에서 MemberRepository의 Method인 save와 ,find를 테스트 한다.
이 save와 find는 JPA EntityManager의 메소드들에 의해 동작한다.
save에 해당하는 것이 persist
find에 해당하는 것이 find, find 시에는 Entity와 해당 Entity의 id를 매개변수로 받는다.

!!!!같은 영속성 컨텍스트에서 안에서는 같은 ID면 같은 엔티티이다!!!
1차 캐시에서 가지고 온다.



상속 관계 매핑에도 전략이 있다. 3가지 -> 조인전략, 단일 테이블 전략, 구현 클래스마다 테이블 전략
싱글 테이블 매핑 전략

객체는 다대다 관계가 가능하지만
관계형 데이터베이스는 그렇지 않다. 다대다 관계는 일대다, 다대일로 풀어야 한다

일대다, 다대일 관계에서는 다에 외래키가 존재하게 된다. 주인의 반대 경우는 거울이라 생각하면 됨

------


엔티티 클래스 개발1 - 어노테이션

@Entity
@Getter @Setter
@Column(name = "") : 보통 엔티티_컬럼명으로 지정
@Embeddable : 내장타입으로 만들어줌
@Embedded : 내장타입 주입
@Table(name = "") : 테이블을 만듬

@ManyToOne : 다대일 관계, 먼저 읽은 것이 현재 엔티티, 나중이 타겟 엔티티
@JoinColumn(name = "") : 연관관계에서 주인 지정, 외래 키가 있는 엔티티가 주인이 된다(=JoinColumn을 가진다).
외래키는 다 쪽에 있다(= name에는 일 쪽의 Id가 해당된다.).

@OneToMany(mappedBy = "") : 일대다 관계, 연관 관계의 주인이 변하면 그걸 반영한다. 거울이라 생각

@Inheritance(strategy = InheritanceType.SINGLE_TABLE) : 상속관계 지정, 전략을 설정할 수 있으며 총 3가지이다.
@DiscriminatorColumn(name = "dtype") : 구분자 컬럼에 해당
@DiscriminatorValue("B") : 구분자 값에 해당


@Enumerated(EnumType.STRING) : EnumType 지정시 사용, 기본이 ORDINARY 하지만 이건 사용하면 안됨. STRING 사용해라

@OneToOne : 일대일 관계 지정, 연관관계 주인 지정 시 고민 필요, 자주 조회하는 것을 연관관계 주인으로 설정

------

## 22.06.24

### 회원 도메인 개발

jpql 형식 : 쿼리, 반환타입으로 구성

JPA의 모든 데이터 변경이나 로직들은 트랜잭션 안에서 이루어져야 한다. -> @Transactional 적용

- class에 적용된 것은 1차적으로 클래스 내 메소드에 적용
- 메소드에 따로 적용하면 그것이 최종적으로 적용
- 예로 class 에서는 @Transactional(readonly = true)를 적용하고 method 에서는 @Transactional만 적용



Service에서는 Repository를 주입하기 마련인데 주입 방식에 3가지가 있음

1. 필드 주입 : 멤버 선언 후 @Autowired로 빈 객체 연결

2. Setter 주입

   - ```java
     		@Autowired
         public void setMemberRepository(MemberRepository memberRepository) {
             this.memberRepository = memberRepository;
         }
     ```

   - 장점 : 테스트 코드 작성 시 필드 주입에 비해 편함

   - 단점 : 런타임에 누군가 변경할 여지가 있음. 보통 애플리케이션 로딩 시점에 조립이 다 끝나고 추후 변경할 경우가 없음

3. 생성자 주입 : 클래스 인스턴스 생성 시 주입이 됨.

   - ```java
     		private final MemberRepository memberRepository;
       
       		@Autowired
         public MemberService(MemberRepository memberRepository) {
             this.memberRepository = memberRepository;
         }
     ```

   - 필드 변경의 여지가 없기에 final로 제한

   - @AllArgsConstructor로 클래스 내 모든 필드를 이용한 생성자롤 만들 수 있음(lombok 라이브러리)

   - @RequiresArgsConstructor로 final 선언된 필드를 이용한 생성자를 만들 수 있음(lombok 라이브러리)



```java
@RunWith(SpringRunner.class)
```

JUnit 테스트 시 스프링과 엮어서 테스트를 할 것이다.



```java
@SpringBootTest
```

스프링 컨테이너 안에서 테스트를 진행할 것이다.



```java
@Transactional
```

JUnit 테스트에서 사용시 롤백을 할 것이다.



JUnit 테스트만 하려고 하는데 DB를 연결하고 이런 작업은 좀 과하다 생각할 수 있음. 이 때 **메모리DB**를 이용하는 방법이 있다.

1. 메모리 DB를 이용하기 위해 test 폴더 내에도 resource 폴더를 생성한다.
2. resource 폴더 내 application.yml 파일을 복제한다. -> main 폴더내 로직들이 실제 서비스 동작 시 실행되며 main 내의 resource 폴더 내 application.yml의 설정을 따른다. 마찬가지로 test 시에는 test 폴더내 로직들이 실행되며 test 폴더 내 yml 파일 존재 시 이것을 우선한 설정을 따른다.
3. 기존에 설정된 datasource url을 (h2 db 사용시)  **jdbc:h2:mem:test**로 바꾸면 메모리 DB 사용이 가능하다.
4. 하지만 스프링부트를 사용중이라면...yml에 아무 설정이 없으면 메모리DB로 동작한다. (gradle에 h2 있는 가정하에)



### 상품 도메인 개발

상품 도메인에는 수량(stockQuantity) 필드가 존재함. 이 **수량에 관한 비지니스 로직은** 다른 서비스 클래스에 만드는 것보다 **상품 클래스 내에 만드는 것이** 좋다. 즉, **도메인 주소 설계시** 엔티티 **자체** 내에서 **해결할 수 있는 것**들은 **엔티티 내에 비지니스 로직을 생성하는 것**이 객체지향적으로 **옳은 설계**이다. 그렇게 해야 응집력이 높아진다.



```java
public void save(Item item){
    if(item.getId() == null){
        em.persist(item);
    }else{
        em.merge(item);
    }
}
```

Item은 JPA에 저장하기 전까지  id가 존재하지 않는다. (=완전히 새로 생성한 객체)

- 존재하지 않으면 DB에 처음 넣는 것
- 존재하면 update를 해야함. EntityManager가 제공하는 merge가 이와 비슷한 역할



### 주문 도메인 개발

주문의 경우 내부가 매우 복잡함 -> 복잡한 생성의 경우 별도의 생성관련 함수가 있으면 개발에 편리함.

```java
//==생성 메서드==/
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) 		{
        Order order = new Order();
        order.setMember(member);    //  주문한 사람
        order.setDelivery(delivery);    //  주문 내역   
        for (OrderItem orderItem : orderItems) {    // 주문 상품    
            order.addOrderItem(orderItem);
        }
        order.setStaus(OrderStaus.ORDER);   //  주문 상태 
        order.setOrderDate(LocalDateTime.now());    //  주문 날짜
        return order;
    }
```



- 주문 서비스
  1. 어디까지 cascade?  종속된 것이 하나의 주인만을 private 하게 가질 때 cascade를 사용할 수 있다. 참조를 여러개가 한다면 cascade로 변화시 파급효과가 크고 오류 발생 가능성이 높아진다.
  2. 지정한 생성 방법 외 다른 생성을 막는 방법
     - JPA는 protected까지 생성자를 만들 수 있게 해줌. 그래서 protected를 사용하면 됨
     - @NoArgsConstructor(access = AccessLevel.PROTECTED)로 어노테이션을 이용해 설정 가능
  3. 디자인 패턴
     - 도메인 모델 패턴 : 서비스 계층은 엔티티에 필요한 요청을 위임하는 역할, 엔티티가 비즈니스 로직을 가지고 있음
     - 트랜잭션 스크립트 패턴 : 서비스 계층에 대부분의 비즈니스 로직을 처리하는 것



------

22.06.25

1. 주문 검색 기능 개발

   - JPA에서 동적쿼리를 어떻게 사용할 것인가?

   - 동적쿼리 사용 경우 

     1. 검색 내용으로 검색을 하는 경우
     2. 필터를 이용해 검색하는 경우

   - 그럼 JPA에서 동적쿼리는 어떻게?

     1. [JPQL](https://ict-nroo.tistory.com/116)
     2. JPA Criteria
     3. Querydsl

   - 1, 2는 JPA 공식 스펙 하지만...현업에서 사용하기에는 너무 복잡하다

   - 3인 Querydsl이 코드 생산 시간을 줄여주고 재사용성을 높여주기에 현업에서는 이것을 사용한다. 

   - Querydsl 코드로 작성한 findAll함수

     ```java
     public List<Order> findAll(OrderSearch orderSearch) {
         QOrder order = QOrder.order;
         QMember member = QMember.member;
     
         return query
                 .select(order)
                 .from(order)
                 .join(order.member, member)
                 .where(statusEq(orderSearch.getOrderStatus()),
                         nameLike(orderSearch.getMemberName()))
                 .limig(1000)
                 .fetch();
     }
     
     private BooleanExpression statusEq(OrderStatus statusCond) {
         if(statusCond == null) {
             return null;
         }
         return order.status.eq(statusCond);
     }
     
     private BooleanExpression nameLike(String nameCond) {
         if(!StringUtils.hasText(nameCond)){
             return null;
         }
         return member.name.like(nameCond);
     }
     ```



### 웹 계층 개발

1. 회원 등록

   1. Springboot 2.3 이후로 부터는 `validation`을 지원하지 않아 직접 injection을 해주어야 함.

   ```java
   implementation 'org.springframework.boot:spring-boot-starter-validation'
   ```

   build.gradle에 다음으로 주입이 가능하다.

   - `@NotEmpty` 을 통해 값이 비어있으면 오류가 발생하도록 할 수 있다.
   - `@Valid` 를 통해 validation을 확인해 줄 수 있다. 

  2. BindingResult

     - 컨트롤러 단에서 오류 발생 시 원래는 거기서 중단되는데 BindingResult를 사용하면 오류가 담겨서 실행이 됨.

  3. 엔티티를 폼으로 사용하지 마라 

     - 엔티티를 폼으로 사용 시 엔티티가 화면을 처리하기 위한 기능이 점점 추가가 되고 이는 화면 종속성을 높인다. -> 지저분해지고 유지보수가 어려워진다.

  4. 화면에 값을 뿌리는 경우에도 엔티티를 이용하기 보다 DTO를 사용해서 해라

  5. API 작성 시에는 절대 엔티티를 넘기면 안된다. 보안상 문제가 생기고 엔티티 변경 시 api 스펙이 변하게 된다.

     

상품 수정

1. 수정은 중요하다.
2. id 조심해라
3. 현업에서 merge는 잘 사용하지 않는다. 다음 시간 권장하는 방법인 변경 감지에 대해 알아볼 것이다.



변경 감지와 병합(merge) : 매우 중요

- 영속성 컨텍스트 : 
- **[준영속성 엔티티](https://ultrakain.gitbooks.io/jpa/content/chapter3/chapter3.6.html)** : 식별자가 생성되고 jpa에 한 번 들어갔다 나온 것, <u>임의로 새로 생성한 객체도 식별자가 존재한다면 준영속성 엔티티에 속한다</u>. 거의 비영속 상태에 가깝다. 
  - 영속성 컨텍스트에 의해 관리가 되지 않는다. 
  - 값이 변경되어도 dirty checking이 되지 않고 
  - 쿼리 저장소에 쿼리가 생기지 않아 
  - flush 시 반영이 되지 않는다. 
  - update가 되지 않는다.
- 그럼 준영속성 엔티티를 영속 상태로 어떻게 바꾸나?
  - **변경 감지 기능 사용** - DB에서 해당 **식별자로 조회**를 한다. 그럼 **조회 결과가 영속 상태에 해당**한다. 해당 **영속 상태 엔티티를 변경하면 flush 시 자동 반영**된다.
  - 병합(merge) 사용 : 변경하지 않은 값마저 바뀌기에 null로 바뀔 가능성이 존재하기에 잘 사용하지 않는다.

**컨트롤러에서 어설프게 엔티티를 생성하지 않는다.**

컨트롤러와 서비스 둘 중 어디서 엔티티를 찾아서 넘길 것인가?

- Best : 컨트롤러에서 찾기 보다는 **컨트롤러는 식별자만 넘기고**
- **트랜잭션**이 작동하는 서비스 단에서 찾는 것이 **더 할 수 있는 것이 많고**, **<u>영속 상태</u>**에서 진행되기에 **변경이 발생해도 바로 반영**된다.

------

참고 자료

- [[토크ON세미나] JPA 프로그래밍 기본기 다지기 1강 - JPA 소개 | T아카데미](https://www.youtube.com/watch?v=WfrSN9Z7MiA&list=PL9mhQYIlKEhfpMVndI23RwWTL9-VL-B7U&index=1)