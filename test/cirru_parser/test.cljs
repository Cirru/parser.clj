(ns cirru-parser.test
  (:require [cljs.test :refer [deftest run-tests is testing]]
            [cirru-parser.core :refer [parse]]
            [cljs.reader :refer [read-string]]
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

(defn check [x]
  ; (println (parse-file x))
  ; (println (parse-edn x))
  (=
    (parse-file x)
    (parse-edn x)))

(deftest parse-comma
  (testing "parse comma"
    (is (check "comma"))))

(deftest parse-demo
  (testing "parse demo"
    (is (check "demo"))))

(deftest parse-folding
  (testing "parse folding"
    (is (check "folding"))))

(deftest parse-html
  (testing "parse html"
    (is (check "html"))))

(deftest parse-indent
  (testing "parse indent"
    (is (check "indent"))))

(deftest parse-line
  (testing "parse line"
    (is (check "line"))))

(deftest parse-parentheses
  (testing "parse parentheses"
    (is (check "parentheses"))))

(deftest parse-quote
  (testing "parse quote"
    (is (check "quote"))))

(deftest parse-spaces
  (testing "parse spaces"
    (is (check "spaces"))))

(deftest parse-unfolding
  (testing "parse unfolding"
    (is (check "unfolding"))))
