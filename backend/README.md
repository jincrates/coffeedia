# coffeedia-backend
coffee + encyclopedia

### Module Graph

```mermaid
%%{
  init: {
    'theme': 'neutral'
  }
}%%

graph LR
  :infrastructure --> :domain
  :domain --> :common
  :bootstrap --> :common
  :bootstrap --> :domain
  :bootstrap --> :application
  :bootstrap --> :infrastructure
  :application --> :common
  :application --> :domain
```
