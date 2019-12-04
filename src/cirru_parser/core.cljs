
(ns cirru-parser.core )

(defn build-exprs [*tokens] [])

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
            "\"" (recur (conj acc (count buffer)) :string "" body)
            (recur (conj acc (count buffer)) :token c body))
        (do (println "Unknown:" (pr-str c)) acc)))))

(defn parse [code]
  (let [tokens (lex [] :space "" code), *tokens (atom tokens)] (build-exprs *tokens)))
