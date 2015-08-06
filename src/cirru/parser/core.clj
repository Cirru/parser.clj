
(ns cirru.parser.core
  (:require [cirru.parser.tree :as tree])
  (:use [clojure.pprint :only [pprint]]))

(declare parsing)

(defn parse [code filename]
  (let
    [ buffer ""
      state {
        :name :indent
        :x 1
        :y 1
        :level 1
        :indent 0
        :indented 0
        :nest 0
        :path filename}]
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
  (throw (Exception. "EOF in escape state")))

(defn string-eof [_ _ _ _]
  (throw (Exception. "EOF in string state")))

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
  (throw (Exception. "new line while escape")))

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
  (throw (Exception. "newline in a string")))

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
    (throw (Exception. "incorrect nesting"))
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
    (throw (Exception. "close at space"))
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
  (throw (Exception. "open parenthesis in token")))

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
  (throw (Exception. "close parenthese at indent")))


(defn indent-else [xs buffer state code]
  (let
    [ indented (if
        (= (mod (state :indented) 2) 1)
        (throw (Exception. "odd indentation"))
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

(defn parsing [& args]
  ; (println "running parsing")
  (let
    [ [xs buffer state code] args
      eof (= (count code) 0)
      char (if eof nil (first code))]
    ; (println "\n")
    ; (prn "state is:" state)
    ; (prn "buffer is:" state)
    ; (prn "code is:" code)
    #(case (state :name)
      :escape (if eof   (apply escape-eof       args)
        (case char
          \newline      (apply escape-newline   args)
          \n            (apply escape-n         args)
          \t            (apply escape-t         args)
                        (apply escape-else      args)))
      :string (if eof   (apply string-eof       args)
        (case char
          \\            (apply string-backslash args)
          \newline      (apply string-newline   args)
          \"            (apply string-quote     args)
                        (apply string-else      args)))
      :space (if eof    (apply space-eof        args)
        (case char
          \space        (apply space-space      args)
          \newline      (apply space-newline    args)
          \(            (apply space-open       args)
          \)            (apply space-close      args)
          \"            (apply space-quote      args)
                        (apply space-else       args)))
      :token (if eof    (apply token-eof        args)
        (case char
          \space        (apply token-space      args)
          \newline      (apply token-newline    args)
          \(            (apply token-open       args)
          \)            (apply token-close      args)
          \"            (apply token-quote      args)
                        (apply token-else       args)))
      :indent (if eof   (apply indent-eof       args)
        (case char
          \space        (apply indent-space     args)
          \newline      (apply indent-newline   args)
          \)            (apply indent-close     args)
                        (apply indent-else      args)))
      (throw (Exception. "unknown state")))))
