(ns pbranes.component.canvas
  (:require [helix.core :refer [defnc]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            [monet.canvas :as monet]))

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
  (let [ctx (.getContext canvas "2d")
        ratio (PIXEL-RATIO ctx)]
    (js/console.log "PIXEL RATIO" ratio)
    (set! (.-width canvas) (* width ratio))
    (set! (.-height canvas) (* height ratio))))

(defnc canvas-component [{:keys [style]}]
  (let [canvas-ref (hooks/use-ref nil)]

    (hooks/use-effect
     "Get the canvas context after it is rendered."
     :once
     (let [canvas-dom (.-current canvas-ref)]
       (adjust-canvas-ratio (:width style) (:height style) canvas-dom)))

    (d/div
     (d/canvas {:id "canvas"
                :ref canvas-ref
                :style style}))))
