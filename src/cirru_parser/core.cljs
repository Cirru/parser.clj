
(ns cirru-parser.core
  (:require [cirru-parser.tree :as tree]))

(declare parsing)

(def state0 {
  :name :indent
  :x 1
  :y 1
  :level 1
  :indent 0
  :indented 0
  :nest 0
  :path nil})

(defn parse [code filename]
  (let
    [ buffer ""
      state (assoc state0 :path filename)]
    (tree/resolve-comma
      (tree/resolve-dollar
        (trampoline parsing [] buffer state code)))))

(defn shorten [xs]
  (if (vector? xs)
    (mapv shorten xs)
    (xs :text)))

(defn pare [code filename]
  (shorten (parse code filename)))

; eof

(defn escape-eof [_ _ _ _]
  (throw (js/Error. "EOF in escape state")))

(defn string-eof [_ _ _ _]
  (throw (js/Error. "EOF in string state")))

(defn space-eof [xs _ _ _]
  xs)

(defn token-eof [xs buffer state code]
  (tree/append-item xs (state :level)
    (assoc buffer
      :ex (state :x)
      :ey (state :y))))

(defn indent-eof [xs _ _ _]
  xs)

; escape

(defn escape-newline [_ _ _ _]
  (throw (js/Error. "new line while escape")))

(defn escape-n [xs buffer state code]
  (parsing xs
    (assoc buffer :text (str (buffer :text) "\n"))
    (assoc state
      :x (+ (state :x) 1)
      :name :string)
    (subs code 1)))

(defn escape-t [xs buffer state code]
  (parsing xs
    (assoc buffer :text (str (buffer :text) "\t"))
    (assoc state
      :x (+ (state :x) 1)
      :name :string)
    (subs code 1)))

(defn escape-else [xs buffer state code]
  (parsing xs
    (assoc buffer :text
      (str (buffer :text) (first code)))
    (assoc state
      :x (+ (state :x) 1)
      :name :string)
    (subs code 1)))

; string

(defn string-backslash [xs buffer state code]
  (parsing xs buffer
    (assoc state
      :name :escape
      :x (+ (state :x) 1))
    (subs code 1)))

(defn string-newline [_ _ _ _]
  (throw (js/Error. "newline in a string")))

(defn string-quote [xs buffer state code]
  (parsing xs buffer
    (assoc state
      :name :token
      :x (+ (state :x) 1))
    (subs code 1)))

(defn string-else [xs buffer state code]
  (parsing xs
    (assoc buffer :text
      (str (buffer :text) (subs code 0 1)))
    (assoc state :x
      (+ (state :x) 1))
    (subs code 1)))

; space

(defn space-space [xs buffer state code]
  (parsing xs buffer
    (assoc state :x
      (+ (state :x) 1))
    (subs code 1)))

(defn space-newline [xs buffer state code]
  (if (not= (state :nest) 0)
    (throw (js/Error. "incorrect nesting"))
    (parsing xs buffer
      (assoc state
        :name :indent
        :x 1
        :y (+ (state :indented) 1)
        :indented 0)
      (subs code 1))))

(defn space-open [xs buffer state code]
  (parsing
    (tree/append-item xs (state :level) (tree/create-nesting 1))
    buffer
    (assoc state
      :nest (+ (state :nest) 1)
      :level (+ (state :level) 1)
      :x (+ (state :x) 1))
    (subs code 1)))

(defn space-close [xs buffer state code]
  (if (<= (state :nest) 0)
    (throw (js/Error. "close at space"))
    (parsing xs buffer
      (assoc state
        :nest (- (state :nest) 1)
        :level (- (state :level) 1)
        :x (+ (state :x) 1))
      (subs code 1))))

(defn space-quote [xs buffer state code]
  (parsing xs
    { :text ""
      :x (state :x)
      :y (state :y)
      :path (state :path)}
    (assoc state
      :name :string
      :x (+ (state :x) 1))
    (subs code 1)))

(defn space-else [xs buffer state code]
  (parsing xs
    { :text (subs code 0 1)
      :x (state :x)
      :y (state :y)
      :path (state :path)}
    (assoc state
      :name :token
      :x (+ (state :x) 1))
    (subs code 1)))

; token

(defn token-space [xs buffer state code]
  (parsing
    (tree/append-item xs (state :level)
      (assoc buffer
        :ex (state :x)
        :ey (state :y)))
    nil
    (assoc state
      :name :space
      :x (+ (state :x) 1))
    (subs code 1)))

(defn token-newline [xs buffer state code]
  (parsing
    (tree/append-item xs (state :level)
      (assoc buffer
        :ex (state :x)
        :ey (state :y)))
    nil
    (assoc state
      :name :indent
      :indented 0
      :x 1
      :y (+ (state :y) 1))
    (subs code 1)))

(defn token-open [_ _ _ _]
  (throw (js/Error. "open parenthesis in token")))

(defn token-close [xs buffer state code]
  (parsing
    (tree/append-item xs (state :level)
      (assoc buffer
        :ex (state :x)
        :ey (state :y)))
    nil
    (assoc state
      :name :space)
    code))

(defn token-quote [xs buffer state code]
  (parsing xs buffer
    (assoc state
      :name :string
      :x (+ (state :x) 1))
    (subs code 1)))

(defn token-else [xs buffer state code]
  (parsing xs
    (assoc buffer :text
      (str (buffer :text) (first code)))
    (assoc state :x (+ (state :x) 1))
    (subs code 1)))

; indent

(defn indent-space [xs buffer state code]
  (parsing xs buffer
    (assoc state
      :indented (+ (state :indented) 1)
      :x (+ (state :x) 1))
    (subs code 1)))

(defn indent-newline [xs buffer state code]
  (parsing xs buffer
    (assoc state
      :x 1
      :y (+ (state :y) 1)
      :indented 0)
    (subs code 1)))

(defn indent-close [_ _ _ _]
  (throw (js/Error. "close parenthese at indent")))


(defn indent-else [xs buffer state code]
  (let
    [ indented (if
        (= (mod (state :indented) 2) 1)
        (throw (js/Error. "odd indentation"))
        (/ (state :indented) 2))
      diff (- indented (state :indent))
      nextState (assoc state
        :name :space
        :level (+ (state :level) diff)
        :indent indented)]
    (cond
      (<= diff 0) (parsing
        (tree/append-item xs
          (- (+ (state :level) diff) 1)
          (tree/create-nesting 1))
        buffer
        nextState
        code)
      (> diff 0) (parsing
        (tree/append-item xs (state :level)
          (tree/create-nesting diff))
        buffer
        nextState
        code)
      :else (parsing xs buffer nextState code))))

; parse

(defn parsing [xs buffer state code]
  ; (println "running parsing")
  (let
    [ eof (= (count code) 0)
      char (if eof nil (first code))]
    ; (println "\n")
    ; (prn "state is:" state)
    ; (prn "buffer is:" state)
    ; (prn "code is:" code)
    #(case (state :name)
      :escape (if eof   (escape-eof       xs buffer state code)
        (case char
          \newline      (escape-newline   xs buffer state code)
          \n            (escape-n         xs buffer state code)
          \t            (escape-t         xs buffer state code)
                        (escape-else      xs buffer state code)))
      :string (if eof   (string-eof       xs buffer state code)
        (case char
          \\            (string-backslash xs buffer state code)
          \newline      (string-newline   xs buffer state code)
          \"            (string-quote     xs buffer state code)
                        (string-else      xs buffer state code)))
      :space (if eof    (space-eof        xs buffer state code)
        (case char
          \space        (space-space      xs buffer state code)
          \newline      (space-newline    xs buffer state code)
          \(            (space-open       xs buffer state code)
          \)            (space-close      xs buffer state code)
          \"            (space-quote      xs buffer state code)
                        (space-else       xs buffer state code)))
      :token (if eof    (token-eof        xs buffer state code)
        (case char
          \space        (token-space      xs buffer state code)
          \newline      (token-newline    xs buffer state code)
          \(            (token-open       xs buffer state code)
          \)            (token-close      xs buffer state code)
          \"            (token-quote      xs buffer state code)
                        (token-else       xs buffer state code)))
      :indent (if eof   (indent-eof       xs buffer state code)
        (case char
          \space        (indent-space     xs buffer state code)
          \newline      (indent-newline   xs buffer state code)
          \)            (indent-close     xs buffer state code)
                        (indent-else      xs buffer state code)))
      (throw (js/Error. "unknown state")))))
