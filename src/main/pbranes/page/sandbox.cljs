(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [pbranes.component.canvas :refer [canvas]]))

(defn draw [ctx]
  (.beginPath ctx)
  (.arc ctx 75 75 50 0 (* Math/PI 2) true)
  (.stroke ctx))

(defnc sandbox []
  (<>
   ($ canvas {:draw draw :style { :width 200 :height 200  :background-color "lightgreen"}}
      (d/div "hello"))))