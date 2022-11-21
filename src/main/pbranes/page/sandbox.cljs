(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [monet.canvas :as canvas]
            [pbranes.component.canvas :refer [canvas-component PIXEL-RATIO]]))

(defn background-entity [canvas color]
  (canvas/entity
   {:x 0 :y 0 :w (.-width canvas) :h (.-height canvas)} ; val
   nil                       ; update function
   (fn [ctx val]             ; draw function
     (-> ctx
         (canvas/fill-style color)
         (canvas/fill-rect val)))))

(defn circle-entity []
  (canvas/entity 
   {:x 120 :y 100 :r 90 :start-angle 0 :end-angle (* 2 Math/PI) :counter-clockwise? false}
   nil 
   (fn [ctx val]
     (-> ctx
         (canvas/begin-path)
         (canvas/arc val)
         (canvas/stroke)))))

(defn draw-background [{:keys [background-color]}]
  (fn [monet-canvas]
    (let [ctx (:ctx monet-canvas)
          canvas (.-canvas ctx)]      
      (canvas/add-entity monet-canvas :background (background-entity canvas background-color))
      (canvas/add-entity monet-canvas :circle (circle-entity)))))

(defnc sandbox []
  (<>
   ($ canvas-component {:draw (draw-background {:background-color "grey"}) :style {:width 800 :height 400}})
   (d/div "hello")))