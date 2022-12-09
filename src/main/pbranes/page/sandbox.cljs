(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [monet.canvas :as canvas]
            [pbranes.canvas.cartisian :refer [->canvas cartisian-center-wrapper]]
            [pbranes.canvas.entities :as pce :refer [plot-points! plot-polygon!]]
            [pbranes.component.canvas :refer [canvas-component]]))

(def dino-pts [[6 4] [3 1] [1 2] [-1 5] [-2 5] [-3 4]
               [-4 4] [-5 3] [-5 2] [-2 2] [-5 1] [-4 0] [-2 1]
               [-1 0] [0 -3] [-1 -4] [1 -4] [2 -3] [1 -2] [3 -1] [5 1]])

(defn make-graph-ctx [{:keys [canvas]}]
  (let  [margin 50
         partitions 6
         width (- (.-width canvas) (* 2 margin))
         height (- (.-height canvas) (* 2 margin))
         n (-> partitions (* 2) (+ 2))
         x-spacing (/ width n)
         y-spacing (/ height n)
         coord-radius 4.7]
    {:x 0 :y 0 :w width :h height :margin margin :coord-radius coord-radius :partitions partitions :n n :x-spacing x-spacing :y-spacing y-spacing}))

(defn get-background-color [background?]
  (if background? "rgba(80, 80, 80, 1.0)" "rgba(80, 80, 80, 0.0)"))

(defn draw [controls]
  (fn [mc graph-ctx]
    (let [points (pce/calc-graph-points dino-pts graph-ctx)]
      (when (:lines? controls) (canvas/add-entity mc :dino-poly (plot-polygon! points)))
      (when (:points? controls) (canvas/add-entity mc :dino-pts (plot-points! points 2.5 "yellow"))))))

(defn draw-cartisian-center-graph [mc margin bg-color draw-fn]
  (let [render-fn (->canvas mc margin bg-color)
        graph-ctx (make-graph-ctx mc)
        make-cartisian (cartisian-center-wrapper mc)]
    (render-fn (fn [] (make-cartisian graph-ctx draw-fn)))))

(defn draw-cartisian [mc margin bg-color]
  (let [render-fn (->canvas mc margin bg-color)]
    (render-fn nil)))

(defn make-toggle-button [graph set-fn key]
  (d/button {:on-click #(set-fn (assoc graph key (not (key graph))))} (name key)))

(defnc sandbox []
  (let [monet-canvas (hooks/use-ref nil)
        [graph set-graph] (hooks/use-state {:points? true
                                            :lines? true
                                            :background? true
                                            :dino? true})
        make-toggle (partial make-toggle-button graph set-graph)]

    (hooks/use-effect [graph]
                      :always
                      (set! (.-current monet-canvas) (canvas/init (.getElementById js/document "canvas")))
                      (let [mc (.-current monet-canvas)
                            margin {:x 50 :y 50}
                            bg-color (get-background-color (:background? graph))]
                        (if (:dino? graph)
                          (draw-cartisian-center-graph mc margin bg-color (draw graph))
                          (draw-cartisian mc margin bg-color))))
    (<>
     ($ canvas-component {:id "canvas" :class "canvas" :style {:width 400 :height 400}})
     (make-toggle :background?)
     (make-toggle :lines?)
     (make-toggle :points?)
     (make-toggle :dino?))))



