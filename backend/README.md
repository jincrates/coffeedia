# coffeedia-backend

coffee + encyclopedia


<br/>

## 기술스택

![Generic badge](https://img.shields.io/badge/21-OpenJDK-537E99.svg)
![Generic badge](https://img.shields.io/badge/3.5.4-SpringBoot-6DB33F.svg)
![Generic badge](https://img.shields.io/badge/14-PosgreSQL-01578B.svg)
![Generic badge](https://img.shields.io/badge/5.0-JUnit-DD524A.svg)

<br/>

## Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  subgraph :application
    :application:port["port"]
    :application:usecase["usecase"]
  end
  :domain -->|api| :common
  :infrastructure -->|api| :application:port
  :bootstrap -->|implementation| :application:usecase
  :bootstrap -->|implementation| :infrastructure
  :application:usecase -->|api| :domain
  :application:usecase -->|api| :application:port
  :application:port -->|api| :domain
```

<br/>

## 아키텍처 설계

```mermaid
graph TD
    subgraph "Bootstrap Layer"
        HTTP[HTTP Controllers]
        LISTENER[Event Listeners]
    end
    
    subgraph "Application Layer"
        UC[Use Cases]
        PORT[Ports]
    end
    
    subgraph "Domain Layer"
        MODEL[Domain Models]
        EVENT[Domain Events]
        VO[Value Objects]
    end
    
    subgraph "Infrastructure Layer"
        JPA[JPA]
        KAFKA[Kafka]
        REDIS[Redis]
    end
    
    %% 의존성 방향 (Bootstrap → Application → Domain)
    HTTP --> UC
    LISTENER --> UC
    UC --> MODEL
    UC --> EVENT
  
    UC --> PORT
    PORT --> MODEL
    
    %% 구현 방향 (Infrastructure → Application)
    JPA -.-> PORT
    KAFKA -.-> PORT

    
    %% 스타일링
    classDef bootstrap fill:#e1f5fe
    classDef application fill:#f3e5f5
    classDef domain fill:#e8f5e8
    classDef infrastructure fill:#fff3e0
    
    class HTTP,LISTENER bootstrap
    class UC,PORT application
    class MODEL,VO,EVENT domain
    class JPA,KAFKA,REDIS infrastructure
```

| **계층**               | **설명**                                                                  |
|----------------------|-------------------------------------------------------------------------|
| Bootstrap Layer      | HTTP, 이벤트 리스너, 스케줄러 등 외부 트리거를 받아 애플리케이션을 연결하는 진입점(http, event listener) |
| Application Layer    | 비즈니스 유즈케이스를 통해 도메인 객체들을 협력시키는 계층(usecase, port)                         |
| Domain Layer         | 핵심 비즈니스 로직과 규칙이 담긴 계층                                                   |
| Infrastructure Layer | 기술적 구현 세부사항을 담당하는 계층(port의 실제 구현체)                                      |

<br/>

