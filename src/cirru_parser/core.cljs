
(ns cirru-parser.core )

(defn build-exprs [pull-token! peek-token! put-back!]
  (let [chunk (pull-token!)]
    (if (some? chunk)
      (do (println "chunk" chunk) (recur pull-token! peek-token! put-back!))
      (println "Finished"))))

(defn parse-indentation [buffer]
  (let [size (count buffer)]
    (if (odd? size) (throw (js/Error. (str "Invalid indentaion size: " size))) (/ size 2))))

(defn lex [acc state buffer code]
  (comment println "got" acc state buffer)
  (if (empty? code)
    (case state
      :space acc
      :token (conj acc buffer)
      :escape (throw (js/Error. "Should not be escape"))
      :indent acc
      :string (throw (js/Error. "Should not be string"))
      (throw (js/Error. (str "Unknown state:" (pr-str state)))))
    (let [c (first code), body (subs code 1)]
      (case state
        :space
          (case c
            " " (recur acc :space "" body)
            "\n" (recur acc :indent "" body)
            "(" (recur (conj acc :open) :space "" body)
            ")" (recur (conj acc :close) :space "" body)
            "\"" (recur acc :string "" body)
            (recur acc :token c body))
        :token
          (case c
            " " (recur (conj acc buffer) :space "" body)
            "\"" (recur (conj acc buffer) :string "" body)
            "\n" (recur (conj acc buffer) :indent "" body)
            "(" (recur (conj acc buffer :open) :space "" body)
            ")" (recur (conj acc buffer :close) :space "" body)
            (recur acc :token (str buffer c) body))
        :string
          (case c
            "\"" (recur (conj acc buffer) :space "" body)
            "\\" (recur acc :escape buffer body)
            "\n" (throw (js/Error. "Unexpected newline in string"))
            (recur acc :string (str buffer c) body))
        :escape
          (case c
            "" (recur acc :string (str buffer "\"") body)
            "t" (recur acc :string (str buffer "\t") body)
            "n" (recur acc :string (str buffer "\n") body)
            "\\" (recur acc :string (str buffer "\\") body)
            (throw (js/Error. (str "Unknown " (pr-str c) " in escape."))))
        :indent
          (case c
            " " (recur acc :indent (str buffer c) body)
            "\n" (recur acc :indent "" body)
            "\"" (recur (conj acc (parse-indentation buffer)) :string "" body)
            (recur (conj acc (parse-indentation buffer)) :token c body))
        (do (println "Unknown:" (pr-str c)) acc)))))

(defn resolve-indentations [acc level tokens]
  (if (empty? tokens)
    (vec (concat [:open] acc))
    (let [cursor (first tokens)]
      (cond
        (string? cursor) (recur (conj acc cursor) level (rest tokens))
        (number? cursor)
          (cond
            (> cursor level)
              (let [delta (- cursor level)]
                (recur (vec (concat acc (repeat delta :open))) cursor (rest tokens)))
            (< cursor level)
              (let [delta (- level cursor)]
                (recur
                 (vec (concat acc (repeat delta :close) [:close :open]))
                 cursor
                 (rest tokens)))
            :else (recur (conj acc :close :open) level (rest tokens)))
        (keyword? cursor) (recur (conj acc cursor) level (rest tokens))
        :else (throw (js/Error. (str "Unknown token: " cursor)))))))

(defn parse [code]
  (let [tokens (resolve-indentations [] 0 (lex [] :space "" code))
        *tokens (atom tokens)
        pull-token! (fn []
                      (if (empty? @*tokens)
                        nil
                        (let [cursor (first @*tokens)] (swap! *tokens rest) cursor)))
        peek-token! (fn [] (first @*tokens))
        put-back! (fn [x] (swap! *tokens (fn [xs] (cons x xs))))]
    (build-exprs pull-token! peek-token! put-back!)))
