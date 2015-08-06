
(ns cirru.parser.tree)

(defn append-item [xs level buffer]
  (if (= level 0)
    (conj xs buffer)
    (conj (pop xs)
      (append-item (last xs) (- level 1) buffer))))

(defn create-helper [xs n]
  (if (<= n 1) xs
    [(create-helper xs (- n 1))]))

(defn create-nesting [n]
  (create-helper [] n))

(declare resolve-dollar)
(declare resolve-comma)

; helper since rest returns list
(defn restv [xs]
  (into [] (rest xs)))
(defn concatv [xs ys]
  (into [] (concat xs ys)))

(defn dollar-helper [before after]
  (if (= (count after) 0) before
    (let
      [ cursor (first after)
        cursor-rest (restv after)]
      (cond
        (vector? cursor) (dollar-helper
          (conj before (resolve-dollar cursor))
          cursor-rest)
        (= ((first after) :text) "$") (conj before
          (resolve-dollar cursor-rest))
        :else (dollar-helper
          (conj before cursor)
          cursor-rest)))))

(defn resolve-dollar [xs]
  (if (= (count xs) 0) xs
    (dollar-helper [] xs)))

(defn comma-helper [before after]
  (if (= (count after) 0) before
    (let
      [ cursor (first after)
        cursor-rest (restv after)]
      (if
        (and (vector? cursor) (> (count cursor) 0))
        (let
          [ head (first cursor)]
          (cond
            (vector? head) (comma-helper
              (conj before (resolve-comma cursor))
              cursor-rest)
            (= (head :text) ",") (comma-helper before
              (concatv
                (resolve-comma (restv cursor))
                cursor-rest))
            :else (comma-helper
              (conj before (resolve-comma cursor))
              cursor-rest)))
        (comma-helper (conj before cursor) cursor-rest)))))

(defn resolve-comma [xs]
  (if (= (count xs) 0) xs
    (comma-helper [] xs)))
