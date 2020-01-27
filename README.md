
Cirru Parser in ClojureScript
----

### Usage

[![Clojars Project](https://img.shields.io/clojars/v/cirru/parser.svg)](https://clojars.org/cirru/parser)

```edn
[cirru/parser "0.2.5"]
```

```clojure
(cirru-parser.core/parse "code")
```

### Use Nim parser

```bash
yarn add @cirru/parser.nim
```

Make use of [Cirru parser compiled from nim](https://github.com/Cirru/parser.nim):

```clojure
(cirru-parser.nim/parse "code")
```

### Develop

```bash
yarn
yarn watch
```

### License

MIT
