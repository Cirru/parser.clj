
(ns cirru-parser.comp.container
  (:require [hsl.core :refer [hsl]]
            [respo-ui.style :as ui]
            [respo.macros :refer [defcomp cursor-> <> div button span textarea]]
            [verbosely.core :refer [verbosely!]]
            [respo.comp.space :refer [=<]]
            [reel.comp.reel :refer [comp-reel]]
            [cirru-parser.core :refer [lex parse]]))

(def style-code {:width 400, :height 400})

(defcomp
 comp-container
 (reel)
 (let [store (:store reel), states (:states store)]
   (div
    {:style (merge ui/global ui/row)}
    (textarea
     {:style (merge ui/textarea style-code),
      :value (:content store),
      :on {:input (fn [e d! m!]
             (let [content (:value e), tokens (lex content), tree (parse tokens)]
               (d! :parse {:content content, :tokens tokens, :tree tree})))}})
    (=< "8px" nil)
    (textarea {:style (merge ui/textarea style-code), :value (:tokens store)})
    (=< "8px" nil)
    (textarea {:style (merge ui/textarea style-code), :value (:tree store)})
    (cursor-> :reel comp-reel states reel {}))))
