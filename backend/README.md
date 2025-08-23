# coffeedia-backend

coffee + encyclopedia


<br/>

## 기술스택

![Generic badge](https://img.shields.io/badge/21-OpenJDK-537E99.svg)
![Generic badge](https://img.shields.io/badge/3.5.4-SpringBoot-6DB33F.svg)
![Generic badge](https://img.shields.io/badge/14-PosgreSQL-01578B.svg)
![Generic badge](https://img.shields.io/badge/5.0-JUnit-DD524A.svg)

<br/>

### Module Graph

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
  :bootstrap -->|runtimeOnly| :infrastructure
  :application:usecase -->|api| :domain
  :application:usecase -->|api| :application:port
  :application:port -->|api| :domain
```
