
(ns cirru-parser.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.core :as ui]
            [respo.core
             :refer
             [defcomp defeffect cursor-> <> div button textarea span input]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [respo-md.comp.md :refer [comp-md]]
            [cirru-parser.config :refer [dev?]]
            [cirru-parser.core :refer [lex parse resolve-indentations]]
            [cirru-parser.nim :as nim]))

(def style-code {:font-family ui/font-code, :white-space :pre, :font-size 12})

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       state (or (:data states) {:draft "", :result "", :tree ""})]
   (div
    {:style (merge ui/global ui/fullscreen ui/column)}
    (div
     {:style (merge ui/row-parted {:padding 8})}
     (span nil)
     (button
      {:inner-text "Parse",
       :style ui/button,
       :on-click (fn [e d! m!]
         (m!
          (merge
           state
           {:result (comment
                     pr-str
                     (resolve-indentations [] 0 (lex [] :space "" (:draft state)))),
            :tree (pr-str
                   (let [started (.now js/Date), result (parse (:draft state))]
                     (println "Cost" (- (.now js/Date) started))
                     result))})))}))
    (div
     {:style (merge ui/expand ui/row)}
     (textarea
      {:style (merge ui/expand ui/textarea style-code),
       :placeholder "Text...",
       :value (:draft state),
       :on-input (fn [e d! m!] (m! (assoc state :draft (:value e))))})
     (div
      {:style (merge ui/expand ui/column)}
      (textarea
       {:style (merge ui/expand ui/textarea style-code),
        :placeholder "Result...",
        :value (or (:result state) "")})
      (textarea
       {:style (merge ui/expand ui/textarea style-code),
        :placeholder "Tree result...",
        :value (or (:tree state) "")})))
    (when dev? (cursor-> :reel comp-reel states reel {})))))
