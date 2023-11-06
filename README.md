# 개요
- 이커머스 도메인 전반에 대한 백엔드 설계
  - 구매자, 판매자, 상점, 상품, 주문, 결제, 장바구니, 즐겨찾기
  - [요구사항 상세](docs/commerce-scenario.md)

# 기술스택
- Application
  - Spring boot 3
  - Kotlin, JPA
  - Mysql 8, Redis
  - Resilience4J
- Testing
  - Junit5, Jacoco, Ktlint, Testcontainer, MockK
  - K6, InfluxDB, Grafana
- CI/CD
  - Github Action, AWS ECR, AWS ECS
- Log & Monitoring
  - Logback, AWS CloudWatch, AWS Lambda

# 아키텍처
### 프로젝트 모듈 구성
- commerce-api
  - 웹 인터페이스 + 도메인 모듈
- commerce-infra
  - 의존하는 외부 시스템 구현체 모음
  - :db-main - JPA Entity, JpaRepository 구현체 등
  - :external-api - 써드파티 클라이언트 
  - :redis-main - redis 접근 구현체
- commerce-support
  - :logging - logback
  - :monitoring
  
### API 모듈 패키지 구성
- Presentation - 사용자에게 정보를 표시하고 사용자가 내린 명령을 수행하는 등의 상호작용을 책임지는 인터페이스
- Application - 표현 영역과 도메인 영역을 연결하는 창구 역할. 주로 도메인 개체 간 흐름 제어만을 수행하는 단순한 형태
- Domain - 비즈니스 정책 및 규칙을 담당하는 책임 수행
- Infrastructure - DB, Redis, 외부 API 와 같은 애플리케이션이 의존하는 외부 시스템에 대한 인터페이스를 제공하는 책임 수행

# 추가 고민 정리
- Github action
  - CI Phase에서 단위 테스트, 통합 테스트를 분리해서 실행하도록 구성한다면 얻게 되는 장단점은?
    - 단위 테스트는 빠르게 실행되므로, 그 속도만으로 얻는 장점에 대해 고민해볼 수 있음. 빠른 피드백, 디버깅 용이성, 자원 절약, 테스트 실패 파악 용이
    - 둘 다 관리해야 하기 때문에 초기 테스트 작성에 더 많은 시간과 노력이 필요함. 테스트 환경과 실행을 관리하는 것이 더 복잡해 질 수 있음
  - PR시 ktlint가 라인에 코멘트되어 가독성을 해쳐 코드를 보는 것이 어렵다. 좀 더 깔끔한 방법은 없을까?
    - 삽질 필요
  - 태깅을 통해 흔히 운영배포와 버전관리를 하는데, 수동으로 입력할 때 태그명을 잘못입력하게 된다면? 자동으로 태그명을 부여할 수 있는 방법은?
- Testing
  - 단위, 통합, E2E 테스트는 각각 어느 상황에 작성해야 하는 지?
  - 테스트 간 동일한 프로덕션 코드에 대한 검증이 중복되는 것을 최소화하는 게 좋을까?
  - 외부 시스템에 대한 client(payment api) 는 어떤 방식으로 테스트하는 게 좋을까?
  - 아키텍처를 테스트하고 싶을 때 사용할 수 있는 도구도 있을까?
    - ArchUnit
  - 테스트 코드와 어느 정도 완성된 실제 코드가 함께 있는 경우가 많다. 커밋 순서나 메세지 작성 방식은 어떻게 하는 것이 좋을까?
  - 테스트 상황설정을 위해 더미 데이터 모델 객체를 정의할 때, 일일이 데이터를 세팅하는 것 말고 좀 더 편리한 방법은 없을까?
    - Instancio, Fixture Monkey, Easy Random
  - 단위테스트의 단위란?
    - 해당 객체가 수행해야하는 책임의 범위
- Logging & Monitoring
  - 로깅의 목적은 무엇일까?
  - INFO 레벨에는 어떤 내용들을 찍어야 할까?
  - 에러 로그를 효율적으로 받아볼 수 있는 방법과 도구들은 무엇이 있을까?
    - 에러 레벨에 대한 기준을 잘 정하고, 기준에 따라 방법이 다를 것 같다.
      - 예를 들면, info/debug level은 어딘가에 저장해두었다가 필요할 때 꺼내 쓰도록 기록
      - warn level은 자주는 안보지만 확인 가능한 이메일로 발송
      - error level은 실시간으로 바로 볼 수 있게 슬랙이나 온콜띠
- 설계
  - 도메인 주도 개발은 query에 비즈니스 로직이 있으면 안 된다.
  - 도에민 모델, JPA Entity 간에 의존관계 설정을 어떻게 해야할지
    - 객체참조 vs Repository를 통한 탐색(Id)
  - Service 계층과 Repository 계층 간의 강한 결합도는 어떻게 낮출수 있을까?
  - 레이어드 아키텍처에서 각각의 레이어는 무슨 역할을 수행하는 가?
  - DDD와 Layered Architecture 와의 관계는 무엇일까?
  - 각각의 레이어를 무조건 모듈로 분리하는 것이 좋은 방법일까?
    - 개발 초기 단계에서는 변동사항이 많기 때문에 적절한 타이밍에 모듈로 분리하되, 분리할 때 편하도록 패키지 단위를 잘 나눠둔다.
  - Application 레이어의 Facade 는 어떤 역할을 수행하는 가?
  - JPA 엔티티를 그대로 도메인 모델로 사용해도 될까?
    - JPA 엔티티를 그대로 도메인 모델로 사용하는 경우와 비교해볼 수 있다. JPA 엔티티를 도메인 레이어에서 활용하게 될 경우 DB에 대한 결합을 충분히 분리했다고 볼 수 있는지
  - 계층간 모델 변환을 위해 어떤 방식이 수월할까?
    - 코틀린을 사용한다면 확장함수를 활용할 수 있음. OrderMapper
  - DB 설계 시 외래키를 사용하지 않으면 어떤 장단점들이 있을까?
  - 외래키를 최소화하면서 테이블간의 연관관계 제어할 수 있는 방법은?
- 구현
  - 동시성 이슈를 해결하기 위한 방법들로는 어떤 것들이 있을까?
    - 낙관락, 비관락, 분산락, 
  - 현업에서 트랜잭션을 불필요하게 많이 사용하고 있지는 않나?
  - 트랜잭션의 범위를 최소화 했을 때의 장단점은?
    - 장애 전파를 최소화 할수 있음. DB 락의 범위와 세션 유지시간을 줄일 수 있음.
  - 트랜잭션이란 논리적으로 하나의 연산 단위로 생각해왔는데, 최소화한다면 어느정도 범위로 트랜잭션을 묶어야 하는 지?
- 장애허용 기술들
  - Circuit Breaker, RateLimiter, Retry 는 어떤 용도로 사용되는 지?
  - 장애허용 기술들을 서로 조합하여 사용할 수도 있는 지?
    - ex) Circuit Breaker + Retry
  - Retry 의 expotional backoff, jitter 란 무엇인지?
- 부하테스트
