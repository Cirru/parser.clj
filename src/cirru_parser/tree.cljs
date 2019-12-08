
(ns cirru-parser.tree )

(declare resolve-comma)

(declare comma-helper)

(declare resolve-dollar)

(declare dollar-helper)

(defn add-to-vec [acc xs] (if (empty? xs) acc (recur (conj acc (first xs)) (rest xs))))

(defn resolve-comma [xs] (if (empty? xs) [] (comma-helper [] xs)))

(defn comma-helper [before after]
  (if (empty? after)
    before
    (let [cursor (first after), cursor-rest (rest after)]
      (if (and (vector? cursor) (not (empty? cursor)))
        (let [head (first cursor)]
          (cond
            (vector? head) (comma-helper (conj before (resolve-comma cursor)) cursor-rest)
            (= head ",")
              (comma-helper before (add-to-vec (resolve-comma (rest cursor)) cursor-rest))
            :else (comma-helper (conj before (resolve-comma cursor)) cursor-rest)))
        (comma-helper (conj before cursor) cursor-rest)))))

(defn resolve-dollar [xs] (if (empty? xs) [] (dollar-helper [] xs)))

(defn dollar-helper [before after]
  (if (empty? after)
    before
    (let [cursor (first after), cursor-rest (rest after)]
      (cond
        (vector? cursor) (dollar-helper (conj before (resolve-dollar cursor)) cursor-rest)
        (= cursor "$") (conj before (resolve-dollar cursor-rest))
        :else (dollar-helper (conj before cursor) cursor-rest)))))
