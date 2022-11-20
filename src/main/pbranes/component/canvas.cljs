(ns pbranes.component.canvas
  (:require [helix.core :refer [defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]))

(set! *warn-on-infer* false)

(defn PIXEL-RATIO [ctx]
  (let [dpr (or (.-devicePixelRatio js/window) 1)
        bsr (or (.-webkitBackingStorePixelRatio ctx)
                (.-mozBackingStorePixelRatio ctx)
                (.-msBacingStorePixelRatio ctx)
                (.-oBackingStorePixelRatio ctx)
                (.-backingStorPixelRatio ctx)
                1)]
    (/ dpr bsr)) )

(defn adjust-canvas-ratio
  "This will scale the canvas properly and fixes canvas 2d Blur issue. 
   https://medium.com/wdstack/fixing-html5-2d-canvas-blur-8ebe27db07da"
  [width height canvas]
  (let [ratio (PIXEL-RATIO (.getContext canvas "2d"))]
    (set! (.-width canvas) (* width ratio))
    (set! (.-height canvas) (* height ratio))))

(defnc canvas [{:keys [draw width height]}]
  (let [canvas-ref (hooks/use-ref nil)]

    (hooks/use-effect
     "Get the canvas context after it is rendered."
     :once
     (let [canvas (.-current canvas-ref)
           context (.. canvas-ref -current (getContext "2d"))]
       (adjust-canvas-ratio width height canvas)
       (draw context)))

    (d/div
     (d/canvas {:id "canvas"
                :ref canvas-ref
                :style {:width width :height height :background-color "blue"}}))))
