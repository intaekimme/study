# JPA

## 자바 ORM 표준 JPA 프로그래밍 - 기본편

------

## Hello JPA - 애플리케이션 개발

### [Q. EntityManagerFactory에 대해서 한가지 여쭤보고싶습니다!](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21685?tab=community&volume=1.00&speed=1.75&quality=1080&q=381719)

​	자바 ORM 표준 JPA 프로그래밍 - 기본편의

​	JPA 시작하기목차의 Hello JPA - 애플리케이션 개발의 30:20 에 관해 궁금한게 있습니다.

​	 EntityManagerFactory가 데이터베이스 당 하나씩 묶여서 돌아간다고 설명해주셨는데

 	그렇다면, persistence.xml에 persistence-unit 으로 다른 데이터 베이스를 설정하여 EntityManagerFactory를 사용한다면, 2개 이상의 데이터베이스도 사용할 수 있게 되는건가요??

### A. 생각하신 내용이 맞습니다.

------

## 영속성 컨텍스트1

### [Q. 준영속 상태와 비영속 상태의 차이점이 있을까요?](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21686?tab=community&volume=1.00&speed=1.75&quality=1080&q=45195)

​	준영속상태의 경우 entityManager에서 관리하는 영속상태에서 detach를 통해서 다시 비영속 상태가 된 객체라고 이해하였습니다. 아직까지 진도로는 이 경우 비영속 대신에 굳이 준영속 이라는 걸로 따로 구분하는 이유가 있을지 궁굼합니다. 그냥 논리적인 구분일 뿐일까요? 실제 txCommit에 반영되지 않는 다는 점은 동일할 것 같아서요.

### A. 비영속과 준영속은 사실 비슷한 느낌이 있지요^^

둘의 가장 큰 차이는 한번 영속상태가 된 적이 있는가 없는가의 차이입니다.

영속상태가 되려면 식별자가 반드시 존재해야 합니다. 그래서 **영속 상태가 되었다가 다시 준영속 상태가 되면 식별자가 항상 존재하게 됩니다.**

사실 실무에서는 이렇게 영속상태가 되었다가 바로 준영속 상태가 되는 일은 드뭅니다.

대신에 엔티티를 조회했는데, 트랜잭션이 끝나버리고 영속성 컨택스트도 사라지면 그때 조회한 엔티티가 준영속 상태가 됩니다. 이런 경우는 사실 실무에서 많이 발생합니다.

이 부분에 대한 실무 상황의 예시들은 활용2편에서 자세히 설명드립니다^^

감사합니다.



### [Q. 비영속 준영속 상태에 대해 궁금한 것이 있습니다.](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21686?tab=community&volume=1.00&speed=1.75&quality=1080&q=574353)

개인적으로 궁금한 내용이 있습니다. 공부하면서 궁금한 점이 있어서 아래와 같은 서비스를 만들고 controller 에서 이 서비스의 함수를 호출하게 했습니다. 제 생각으로는 em.detach를 호출해 영속성 컨텍스트에서 관리되지 않고 @transactional 때문에 commit 전이라 db에 데이터가 들어가면 안된다고 생각했습니다. 그러나 확인해보니 db에 데이터가 들어갔습니다. 혹시나 해서 em.clear()도 해봤는데 동일하게 데이터가 들어갔습니다. detach, clear 했음에도 데이터가 insert되는 이유가 궁금해서 질문드립니다.



```java
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final EntityManager em;

    @Override
    @Transactional
    public void test() {
        Member member = new Member();
        em.persist(member);
        em.detach(member);
    }
}
```



### A. 디테일하게 들어가면 2가지 경우로 나눌 수 있습니다.

1. auto increment 같은 전략을 사용하기 위해 IDENTITY 방법을 쓰는 경우

```java
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
```

이 경우에는 em.persist()로 엔티티를 저장하는 시점에 DB에 INSERT 쿼리를 실행하게 됩니다. 그렇게 해야 ID 값을 획득할 수 있기 때문이지요. JPA에는 영속화 시점에 항상 @Id의 값이 필수로 필요합니다.

이미 INSERT 쿼리가 실행되어 버렸기 때문에 이후에 커밋이 일어나게 되면 DB에 반영됩니다.

2. 나머지 경우

@Id만 사용해서 ID를 직접 지정하거나 또는 GenerationType.SEQUENCE, GenerationType.TABLE를 사용하는 경우

이런 경우에는 em.persist()를 호출해도 식별자를 별도의 방법으로 조회하기 때문에 DB에 INSERT 쿼리를 바로 보내지 않습니다. 따라서 이 경우에는 detach를 사용하면 영속성 컨텍스트에서 해당 엔티티가 제거되기 때문에 INSERT 쿼리가 나가지 않습니다. 이후에 커밋이 일어나도 DB에 데이터가 저장되지 않습니다.

참고로 이런 내용들이 복잡해보여도 크게 문제가 되지 않는 이유는 em.detach()를 실무에서는 거의 사용할 일이 없기 때문입니다. 더욱이 em.persist() 직후에 em.detach()를 사용하는 일은 없다고 생각하시면 됩니다.

감사합니다.



### [Q. 영속성 컨텍스트에 관한 질문입니다](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21686?tab=community&volume=1.00&speed=1.75&quality=1080&q=246769)

영속성 컨텍스트는 눈에 보이지않는 논리적인 개념이라고 하셨는데 이 문장의 의미가 와닿지 않습니다.

구체적으로 영속성 컨텍스트라는 클래스가 없다라는 말씀이신거간요? 아님 내부적으로는 존재하는데 개발자가 선언해서 사용하지 않는다는 말씀이신건가요?

### A. 영속성 컨텍스트는 JPA가 애플리케이션과 DB 사이에서 엔티티를 관리해주는 개념입니다.

물론 이 개념을 구현한 EntityManager의 구현 코드들이 있습니다.

그런데 구체적인 코드는 하이버네이트 같은 구현체에서 매우 복잡하게 구현되어 있습니다.

개발자는 단순히 EntityManager를 통해서 영속성 컨텍스트에 접근한다고 이해하시면 됩니다.

감사합니다.



------

## 영속성 컨텍스트2

### [Q. 플러시 관련 질문드립니다.](https://www.inflearn.com/questions/32789)

flush()를 했을 때 1차 캐시에서 Entity와 스냅샷이 다를 경우 

쓰기 지연 SQL저장소 update 쿼리를 에 만들고 나면

Entity값과 스냅샷 값은 같아지는 것인가요?!

당연히 변경된 Entity값이 스냅샷에 저장되면서 같아질 것이라고 생각은 하는데 혹시나 하여 질문합니다.



### A. 질문하신 내용이 맞습니다. 플러시를 통해 변경감지가 진행되면, 이후 Entity값과 스냅샷 같이 같아집니다.



### Q. [[상황]](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21687?tab=community&volume=1.00&speed=1.75&quality=1080&q=541399)

1. 일대다 양방향 관계인 A,B를 저장하려고 합니다.

2. B(다) entity insert시 오류가 발생했습니다. ( 컬럼 크기 보다 큰 데이터 입력)

3. A(일)는 B 기록에 대한 로그성 테이블입니다.

4. @Transactional 처리 없음

5. A에 오류기록이 저장되지않습니다.

(exception catch 실패, finally에서 persist 작업시 무반응)

[문의]

1. 한 트랜잭션 안에서 2개의 entity 저장시

B entity 작업에 오류 발생해도 A entity 작업은 rollback이 안되었으면 합니다.

2. JPA는 @transactional 없이도 rollback이 적용되는걸까요?

어느부분을 참조하면 좋을지 조언부탁드립니다.

### A. 1. 트랜잭션 이라는 것은 작업을 하나의 원자적인 단위로 만드는 작업입니다.

따라서 한 트랜잭션 안에서 2개의 entity가 모두 저장되던가 모두 실패해야 합니다.

이 경우 <u>트랜잭션을 각각 따로 분리해서 사용</u>하셔야 합니다.

트랜잭션에 대한 자세한 내용은 [스프링 DB 1편 강의](https://www.inflearn.com/course/스프링-db-1)를 참고해주세요.

2. Q: JPA는 **@transactional 없이도 rollback이 적용**되는걸까요?

-> JPA의 모든 데이터 변경은 트랜잭션 안에서 이루어져야 합니다. **트랜잭션이 없으면 데이터 변경 자체가 불가능**합니다.

감사합니다.



### [Q. JPA Repository에서 save메서드 호출 이후 바로 user id를 가지고 올 수 있는 이유](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21687?tab=community&volume=1.00&speed=1.75&quality=1080&q=528371)

### A. 이 부분은 스프링 데이터 JPA와는 무관합니다.

**영속성 컨텍스트에 엔티티를 관리하려면 그 순간 바로 PK인 ID가 필요**합니다.

PK인 ID를 기준으로 영속성 컨텍스트가 관리되기 때문이지요.

그래서 **영속성 컨텍스트**는 그래서 **em.persist()를 호출하는 순간에 바로 PK인 ID를 획득**하게 됩니다.

그리고 해당 ID를 엔티티의 PK ID에 넣어둡니다.



### [Q. no args constructor를 개발자에게 강제하는 이유](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21687?tab=community&volume=1.00&speed=1.75&quality=1080&q=482663)

하이버네이트팀이 no args constructor를 개발자에게 강제하는 이유가 궁금합니다. entity 어노테이션이 있는 클래스를 annotation processor로 감지하여 생성자가 없는경우 만들어줬으면 막힘없이 컴파일이 되면서 모두가 행복하지 않을까란 생각이 계속듭니다.

왜 하이버네이트팀은 개발자에게 위 코드를 계속 강제하고 있을까요? 

### A1. 옛날에 같은 궁금증을 가지고, 직접 찾아본 내용을 공유해드립니다😀

**1. 표준 스펙에 명시되어있습니다.**

JPA 2.0 스펙 (37.1.1 Requirements for Entity Classes)과

하이버네이트 스펙에는 기본 생성자를 구현해라고 명시되어 있습니다.

**2. 강제되긴 하지만, 사실 하이버네이트에서 기본생성자가 없더라도 JPA 구현체를 못만드는건 아닙니다.
**- 물론 사용자가 기본생성자를 사용하는게 제일 쉽고 깔끔한 방법이긴 합니다.

**3. 궁금하면 하이버네이트 구현체를 까보시면 되지만, 사실 이는 버전에 따라 구현방법이 조금씩 달라져서 큰 의미는 없을겁니다.**

**4. 그럼 '왜 강제했냐?'라고 물어본다면, 추측은 가능하겠지만 공식적인 명확한 이유는 없습니다.**

\- 사실 [hibernate.interceptor](https://docs.jboss.org/hibernate/orm/3.5/api/org/hibernate/Interceptor.html) 처럼 기본생성자 없이도 만들 수 있게 규칙을 정할 수 있었지만, 그들은 그러지 않았죠.

\- 초창기 하이버네이트에는 이 기능이 없어서 그랬을 수 도 있습니다. 또는 버전간 호환성 때문에 유지할수도 있죠

\- 아마 JPA 제작자가 무지한 Java 개발자들이 사용할 마법 같은 도구를 보여주기위해 그랬을 수도 있습니다. 이러는게 멋져보이니까요 (농담입니다.)

**5. 참고로 어노테이션 프로세서, 컴파일타임 위빙은 좋은 해결책이 아닙니다.**

\- 어노테이션 프로세서는 기존 코드를 수정할 수 없습니다. (@lombok은 수정하지만, 이는 컴파일러 해킹입니다)

\- 컴파일타임 위빙은 특별한 컴파일러가 설치 & 복잡한 설정이 필요로 합니다. 이는 유지보수와 버전호환성이 끔찍합니다.

\- 로드타임 위빙(전용컴파일러 없이 JVM classLoader로 조작)은 객체를 읽어들일 때 위빙이 발생해서, 앱 성능을 하락시킬 수 있습니다.

 

참고하면 좋은 링크

https://stackoverflow.com/questions/2935826/why-does-hibernate-require-no-argument-constructor?fbclid=IwAR0sOCDbtTzmjs3sfJOSToJPoylWO0VUm5BrIPfTCBgsUfBPvq8Fe58DsaU

### A2. 과거 JPA 스펙을 처음 만드는 시점에는 지금처럼 프록시를 처리하는 기술들이 발달하지 않았습니다.

그래서 여러 구현체들이 이런 부분을 편리하게 구현할 수 있게 기본 생성자를 들어둔 것입니다.



### [Q. EntityManager와 싱글톤 빈](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21687?tab=community&volume=1.00&speed=1.75&quality=1080&q=265688)

JPA 활용 1과 연관해서 궁금한게 생겨서 문의드립니다. 아래의 내용이 맞는지 궁금합니다. 여기저기서 뒤죽박죽 조사해서 난해한 감이 있네요 ㅠ


emf를 통해 직접적으로 em을 생성하는 경우 당연히 em의 원칙을 지키기 위해 트랜잭션 단위로 다른 em을 생성해서 반환하여, 공유하는 문제를 방비한다.

그러나 이렇게 호출할 때 마다 새로운 em을 생성하는 것은 너무 큰 자원, 시간 낭비이다.
실무에서는 JPA를 단독으로 사용하지 않고, 스프링 프레임워크와 함께 사용하는데 이러한 문제를 해결할 방법이 있다.

분명 em은 트랜잭션 단위로 생성이 되어야 하며, 트랜잭션 종료 시 em도 종료되어야 한다고 했다. 그러나 스프링에서는 em을 트랜잭션 단위로 생성하지 않고도 동시성 문제를 해결할 수 있다. 

**repository 클래스는 @Repository로 인해 <u>스프링 빈으로 등록되므로 싱글톤으로 관리</u>**되는데 그 내부 필드인 em도 private final 이기 때문에 빈이 생성될 때 초기화된 후 변경되지 않는다.

근데 어떻게 해결하는 것일까? 
**@PersistenceContext 에너테이션에 그 답이 있다**. @PersistenceContext 에너테이션을 사용하면, 이 repository 클래스가 빈으로 등록될 때 **em을 주입하는 시점에서 EntityManager를 생성해서 <u>주입할 때 프록시 객체로 감싸서 주입</u>해주는 것이다**. 이 프록시 객체가 **내부적으로 동시성 문제를 해결**해준다. **결국 한 repository에서 하나의 em만으로 문제를 해결**하는 것이다.

※ 최신 스프링에서는 @PersistenceContext 대신에 @Autowired로도 할 수 있다고 한다. 

```java
@Repository
@Transactional
public class MemberScheduleRepository {

    private final EntityManager em;

    @Autowired
    public MemberScheduleRepository(EntityManager em) {
        this.em = em;
    }

}
```

\+ 정리 강의에서 지금 강의에서하는 EntityManager와 스프링을 이용하는거와 다른데 나중에 설명해주겠다고 하셨는데 그거와 관련된 영상이 없는것 같습니다. 아래 그림의 차이도 포함해서요!



### A. 해당 내용은 본 강의는 아니고 활용1편에서 어떻게 사용되는지 보여드리는 것으로 이해해주시면 됩니다.

추가로 궁금한 내용은 다음 질문을 참고해주세요^^

https://www.inflearn.com/questions/159466



### [Q. EntityManager 차이 스프링위에서 차이](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21687?tab=community&volume=1.00&speed=1.75&quality=1080&q=261287)

### A. flush가 자동으로 실행되는 몇가지 상황이 있습니다.

1. 트랜잭션 커밋 전에

2. JPQL 실행 전에

3. 직접 flush를 호출할 때

따라서 JPQL이 실행될 때 flush가 자동으로 실행되는게 맞습니다.

JPA 활용1에서 보셨던 코드는 아마도 서비스쪽에서 트랜잭션이 생성된 다음 repository의 save가 호출되었을 겁니다. 트랜잭션이 종료되면서 자동으로 flush가 되었기 때문에 저장되는 것입니다. 이는 트랜잭션 커밋 전에 flush가 자동으로 실행되는 상황에 해당됩니다.





### [Q. DB 쿼리 시 꼭 기본생성자(no argument)가 필요한가요?](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21687?tab=community&volume=1.00&speed=1.75&quality=1080&q=152953)

안녕하세요 강사님^^!

강의를 따라 코드를 작성하면서 Main의 update코드를 날렸더니, @NoArgsConstructor가 없는 경우에는 update가 동작하지 않더라구요. 

'기본생성자 없음' 오류가 뜨는데, 일반적인 java코드에선 꼭 @NoArgsConstructor가 없어도 동작하였는데 JPA에서 동작하지 않는 이유가 무엇일까요?

혹 DB에서 쿼리 시에 @NoArgsConstructor를 필요로 해서일까요??

### A. JPA 스펙상 기본 생성자가 필수입니다.

JPA 구현체들은 기본 생성자가 있어야, 리플렉션 같은 기술을 사용해서, 기본 생성자를 기반으로 객체를 프록시 하거나 내부에서 생성해서 사용하는 등 다양하게 사용합니다.

그래서 기본 생성자는 꼭! 있어야 합니다.



------

## 플러시

### [Q. JPQL쿼리 실행 시 플러시가 자동으로 되는데 그렇다면 JPQL은 1차 캐시를 먼저 조회하지 않는 건가요?](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21688?tab=community&volume=1.00&speed=1.75&quality=1080&q=10969)

### A. 질문하신 것 처럼 JPQL은 1차 캐시를 먼저 조회하지 않습니다. JPQL을 실행하면 항상 1차 캐시를 무시하고, 데이터베이스에 직접 SQL을 실행합니다.

그리고 실행 결과를 1차 캐시에 보관하고, 최종적으로 1차 캐시에 보관된 결과를 반환합니다.

이런 방식으로 동작하는 이유는 em.find(식별자) 처럼 단순하게 식별자를 조회하는 경우는 1차 캐시에 있는지 없는지 판별하기가 쉬운데, JPQL은 광범위하게 데이터를 찾기 때문에 이런 방식의 구현이 어렵습니다. 그래서 우선 데이터베이스에서 조회부터 하는 것이지요.

추가로 JPQL을 실행해서, 데이터베이스에서 결과를 가져 왔는데, 이미 1차 캐시에 동일한 식별자를 가진 엔티티가 있으면, 데이터베이스에서 가져온 엔티티를 버리고 1차 캐시에 있는 엔티티를 유지합니다. 이런방식 덕분에 JPQL을 사용해도 엔티티 동일성을 유지합니다.



------

## 준영속성 체크

### [Q. 다른 서버에서 DB 업데이트시 캐시에 있는 엔티티는 어떻게 될까요?](https://www.inflearn.com/course/ORM-JPA-Basic/lecture/21689?tab=community&volume=1.00&speed=1.75&quality=1080&q=17498)

find시에 캐시를 먼저 뒤져서 있다면 재사용한다고 하셨는데요

다른 서버에서 해당 DB 값을 바꾼다면 이런것도 dirty checking해주나요?

이런 케이스에서는 캐시를 업데이트 해야 할 것 같은데요?

### A. find에서 조회하는 캐시는 정확히는 영속성 컨텍스트, 또는 1차 캐시라고 합니다. 이 캐시는 우리가 일반적으로 말하는 애플리케이션 전체에서 공유하는 캐시가 아니라, 해당 트랜잭션의 시작과 끝에서만 짧게 유지되는 캐시입니다. 쉽게 이야기하면 특정 한 유저의 API 호출의 시작과 끝 사이에서 그 유저에게만 유지되는 짧은 캐시입니다. (설명을 돕기위해 유저라고 설명했지만 정확히는 트랜잭션을 시작할 때 생성되고, 트랜잭션이 끝날때 지워지는 캐시 입니다.)

그래서 같은 트랜잭션 안에서 같은 엔티티를 조회할 때만 캐시가 히트됩니다. (그래서 실무에서 이 1차 캐시로 성능 이득을 얻는 일은 많지 않습니다.)

다음으로 변경에 대한 부분을 설명드릴께요 :)

앞서 설명드린대로 트랜잭션 안에서만 캐시가 유효범위를 가지기 때문에, DB에 맞추어 캐시를 업데이트 하거나 더티 채킹을 하지 않습니다. 그리고 한다고 해도 다음에 설명드릴 내용 때문에 정확하게 맞출 수 도 없습니다.

예를 들어서 JPA를 사용하지 않고 자바로 직접 SQL을 사용한다고 가정하겠습니다. SQL로 상품 정보를 조회했는데 조회 당시 가격이 5000원인 것을 확인하고, 내가 그 가격에 500원을 더해서 정확히 5500원을 만들고 싶었는데, 중간에 누군가 가격은 6000원으로 변경해버리면 어떻게 해야할까요? 중간에 체크하는 로직을 넣으면 가능할 것 도 같지만, 체크를 통과하는 시점의 미묘한 지점에 누군가 6000원으로 변경해버릴 수도 있습니다. **결론적으로 이런 문제는 SQL을 직접 사용해도 락을 걸지 않는 이상 단순하게 해결이 안됩니다.**

JPA는 데이터베이스가 제공하는 락 기능을 적극 제공하고, 추가로 더 나아가서 낙관적 락이라는 기능도 제공합니다.

이 부분을 설명드리면 좋겠지만, 책에서도 수십 Page로 풀어서 설명을 해야 하는 부분이어서, 자세한 내용은 JPA 책 16장 트랜잭션과 락, 2차 캐시 부분을 읽어보시길 권장드립니다^^



------

## 데이터베이스 스키마 자동 생성





------

## 필드와 컬럼 매핑

