## spring boot event practice

#### 목표

- **스프링부트의 Event를 사용법을 숙지하기.**

#### 기술스택

- Spring boot 
- Java
- Spring Data Jpa
- Maria DB

#### 스프링 이벤트 ??

- 스프링 이벤트는 스프링의 Bean 과 Bean 사이에 데이터를 전달하는 방법을 말합니다.

- 일반적으로 DI 를 통해 이루어진다 A Class 에서 B Class 에 대한 의존성을 주입받아 A Class 에서 B Class Method 를 호출하여 
  본인의 클래스에서 사용

- 이벤트는 A Class 에서 ApplicationContext 로 넘겨주고 이를 Listener 에서 받아 처리.
  
- 이벤트에는 발생시키는 Publisher 와 받는 Listener 이 있고 이벤트에서 데이터를 담는 이벤트 모델로 이루어져 있다.

- 직접적인 결합이 없기 때문에 로직의 흐름을 파악하기 쉽지 않다는 단점이 생길 수 도 있다.

- 스프링 이벤트는 언제 사용할까?
  - 서비스간의 결합도를 낮추고 싶고 메인 로직과 크게 상관이 없는 로직을 사용할 때(트랜잭션 분리)
  - 서브 로직이 에러가 나더라도 메인 로직은 정상적 완료 하고 싶을때이지만 이건 트랜잭션 설정에 따라서 다르게 할 수 있다.

- 스프링 이벤트에 사용되는 어노테이션

### @EventListener

이 어노테이션은 스프링 4.2이상부터 사용되는 어노테이션으로 특정 클래스를 상속하지 않고도, 즉 스프링에 종속되지 않고 순수 POJO 객체만을 활용해서도
이벤트 프로그래밍이 가능합니다.

@EventListener 어노테이션의 내부는 다음과 같습니다. 

````
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Reflective
public @interface EventListener {

	@AliasFor("classes")
	Class<?>[] value() default {};

	@AliasFor("value")
	Class<?>[] classes() default {};

	String condition() default "";

	String id() default "";

}

````
내용을 차례대로 설명을 하자면 다음과 같습니다.

value : 이벤트 클래스 타입 배열로, 이벤트 타입을 지정합니다.

classes : SpEL 표현식으로, 조건을 정의합니다.

condition : 이벤트 클래스 타입 배열로, 이벤트 타입을 지정합니다.(value와 비슷한 기능)

id : 이벤트 리스너의 고유 식별자를 지정하는 속성

---------------------------------------------------------------

### @TransactionalEventListener

@TransactionalEventListener는 동작하는 메서드를 트랜잭션으로 묶어서 처리하는 경우 Transaction의 
상태에 따라 발생하는 이벤트를 처리해 주는 이벤트 리스너입니다.

해당 어노테이션의 내부는 다음과 같습니다. 

```
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EventListener
public @interface TransactionalEventListener {

	TransactionPhase phase() default TransactionPhase.AFTER_COMMIT;

	boolean fallbackExecution() default false;

	@AliasFor(annotation = EventListener.class, attribute = "classes")
	Class<?>[] value() default {};

	@AliasFor(annotation = EventListener.class, attribute = "classes")
	Class<?>[] classes() default {};

	@AliasFor(annotation = EventListener.class, attribute = "condition")
	String condition() default "";

	@AliasFor(annotation = EventListener.class, attribute = "id")
	String id() default "";

}

```
기존의 @EventListener에 있는 기능은 생략을 하고 추가적으로 들어있는 기능을 보면 

phase() 와 fallbackExecution() 입니다.

우선 phase()는 트랜잭션의 상태에 따라 이벤트 처리 시점을 지정합니다. 이벤트가 실행되는 시점은
4가지의 경우로 지정이 가능합니다.

BEFORE_COMMIT : 커밋이 되기전에 이벤트를 실행한다.

AFTER_COMMIT : 커밋이 되고난 후에 이벤트를 실행한다.(Default)

AFTER_ROLLBACK : 롤백이 되고난 후에 이벤트를 실행한다.

AFTER_COMPLETION : 커밋 또는 롤백 이후에 이벤트를 실행한다.

fallbackExecution은 트랜잭션이 존재하지 않을 때도 이벤트를 처리할 수 있도록 합니다. 

---------------------------------------------------------------

실습에서 사용된 시나리오는 다음과 같습니다.
 
회원가입을 하면 회원가입이 되었다는 이메일과 알림을 보내주는 기능입니다.

1. 이벤트를 발행할 쪽에서 ApplicationEventPublisher를 선언합니다. 

````
@Log4j2
@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final ApplicationEventPublisher applicationEventPublisher;
    
    ......
    
}
````

2.이벤트 정보를 담아서 전달할 이벤트 객체를 만듭니다.

```
import com.example.event_driven_practice.domain.Member;
import lombok.Getter;

@Getter
public class MemberEvent {

    private final Member member;

    public MemberEvent(Member member){
        this.member = member;
    }
}

```

3.이벤트 핸들러가 적절한 이벤트 리스너를 연결, 실행합니다. 추가적으로 비동기로 이벤트를 사용하고 
싶다면 @Async 어노테이션을 사용하면 됩니다.

````
@Log4j2
@RequiredArgsConstructor
@Component
public class MemberEventPublisher {

    private final EmailService emailService;

    private final NoticeService noticeService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener(MemberEvent.class)
    public void publishEvent(MemberEvent memberEvent){
        log.info("event start");
        log.info("email - send??");
        try{
            log.info("email sending");
            emailService.sendJoinMail(memberEvent.getMember().getUserEmail());
        }catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.info(e.getMessage());
        }
        log.info("event end");
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @EventListener(MemberEvent.class)
    public void publishNotification(MemberEvent event){
        Member member = event.getMember();
        noticeService.notifySave(member, NotificationType.MEMBER_JOIN,"회원 가입을 축하합니다.", member.getUserId(), "/api/member/create");
    }

}

````

