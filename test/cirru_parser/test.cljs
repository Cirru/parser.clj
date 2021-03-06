(ns cirru-parser.test
  (:require [cljs.test :refer [deftest run-tests is testing]]
            [cirru-parser.core :refer [parse]]
            [cljs.reader :refer [read-string]]
            [cirru-parser.nim :as nim]
            ["fs" :as fs]))

(defn slurp [x] (fs/readFileSync x "utf8"))

; (deftest parse-test
;   (testing "parse lines"
;     (is (= (pare "as aa () \n  a" "") []))))

(defn parse-file [x]
  (let
    [file (str "data/cirru/" x ".cirru")]
    (parse (slurp file))))

(defn parse-edn [x]
  (let
    [file (str "data/ast/" x ".edn")]
    (read-string (slurp file))))

(deftest parse-comma
  (testing "parse comma"
    (is (= (parse-file "comma") (parse-edn "comma")))))

(deftest parse-demo
  (testing "parse demo"
    (is (= (parse-file "demo") (parse-edn "demo")))))

(deftest parse-folding
  (testing "parse folding"
    (is (= (parse-file "folding") (parse-edn "folding")))))

(deftest parse-html
  (testing "parse html"
    (is (= (parse-file "html") (parse-edn "html")))))

(deftest parse-indent
  (testing "parse indent"
    (is (= (parse-file "indent") (parse-edn "indent")))))

(deftest parse-line
  (testing "parse line"
    (is (= (parse-file "line") (parse-edn "line")))))

(deftest parse-parentheses
  (testing "parse parentheses"
    (is (= (parse-file "parentheses") (parse-edn "parentheses")))))

(deftest parse-quote
  (testing "parse quote"
    (is (= (parse-file "quote") (parse-edn "quote")))))

(deftest parse-spaces
  (testing "parse spaces"
    (is (= (parse-file "spaces") (parse-edn "spaces")))))

(deftest parse-unfolding
  (testing "parse unfolding"
    (is (= (parse-file "unfolding") (parse-edn "unfolding")))))

(defn parse-file-with-nim [x]
  (let
    [file (str "data/cirru/" x ".cirru")]
    (nim/parse (slurp file))))

(deftest parse-demo-with-nim
  (testing "parse demo with nim"
    (is (= (parse-file-with-nim "demo") (parse-edn "demo")))))

(deftest parse-empty
  (testing "parse empty"
    (is (= (parse-file "empty") (parse-edn "empty")))))

(deftest parse-folded-beginning
  (testing "parse folded-beginning"
    (is (= (parse-file "folded-beginning") (parse-edn "folded-beginning")))))
