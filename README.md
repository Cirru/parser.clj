
cirru/parser
----

Cirru Parser in Clojure.

### Usage

```clojure
[cirru/parser "0.0.3"]
```

```clojure
(ns your-project.core
  [:require [cirru.parser.core :as cirru]])

; get full AST
(cirru/parse "code" "filename")
; get simplified AST
(cirru/pare "code" "filename")
```

### License

Copyright © 2015 jiyinyiyong

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
