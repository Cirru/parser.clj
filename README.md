
Cirru Parser in ClojureScript
----

### Usage

```clojure
[cirru/parser "0.1.0"]
```

```clojure
; get full AST
(cirru-parser.core/parse "code" "filename")
; get simplified AST
(cirru-parser.core/pare "code" "filename")
```

### Develop

```bash
yarn
yarn watch

# another terminnal
node target/test.js
```

### License

MIT
