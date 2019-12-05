
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
            [cirru-parser.core :refer [lex parse resolve-indentations]]))

(defcomp
 comp-container
 (reel)
 (let [store (:store reel)
       states (:states store)
       state (or (:data states) {:draft "", :result ""})]
   (div
    {:style (merge ui/global ui/fullscreen ui/column)}
    (div
     {:style (merge ui/row-parted {:padding 8})}
     (span nil)
     (button
      {:inner-text "Parse",
       :style ui/button,
       :on-click (fn [e d! m!]
         (println (parse (:draft state)))
         (m!
          (assoc
           state
           :result
           (pr-str (resolve-indentations [] 0 (lex [] :space "" (:draft state)))))))}))
    (div
     {:style (merge ui/expand ui/row)}
     (textarea
      {:style (merge ui/expand ui/textarea {:font-family ui/font-code}),
       :placeholder "Text...",
       :value (:draft state),
       :on-input (fn [e d! m!] (m! (assoc state :draft (:value e))))})
     (textarea
      {:style (merge ui/expand ui/textarea {:font-family ui/font-code}),
       :placeholder "Result...",
       :value (:result state)}))
    (when dev? (cursor-> :reel comp-reel states reel {})))))
