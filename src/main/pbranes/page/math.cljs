(ns pbranes.page.math
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [monet.canvas :as canvas]
[pbranes.canvas.cartisian :refer [->canvas cartisian-center-wrapper add-axis add-grid-marks-new]]
            [pbranes.canvas.entities :as pce :refer [plot-points! plot-polygon! rectangle]]
            [pbranes.component.canvas :refer [canvas-component]]
            [pbranes.canvas.graph-tools :as gt]))

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

(defn make-toggle-button [graph set-fn key]
  (d/button {:on-click #(set-fn (assoc graph key (not (key graph))))} (name key)))

(defn draw-cartisian-center-graph [mc margin bg-color draw-fn]
  (let [render-fn (->canvas mc margin bg-color)
        graph-ctx (make-graph-ctx mc)
        make-cartisian (cartisian-center-wrapper mc)]
    (render-fn (fn [] (make-cartisian graph-ctx draw-fn)))))

(defn graph-context [mc]
  (let [margin 50
        coord-radius 4.7
        domain (into [] (range 0 11 1)) ;; must add one more number to the end to get that value
        range (into [] (range 10 -1 -1))
        vert-lines (+ 1 (count domain ))
        horiz-lines (+ 1 (count range))
        spacing (gt/calc-spacing mc vert-lines horiz-lines margin)
        width  (* vert-lines spacing )
        height (* horiz-lines spacing)]
    {:x 0
     :y 0
     :w width
     :h height
     :margin margin
     :domain domain
     :range range
     :coord-radius coord-radius
     :vert-lines vert-lines
     :horiz-lines horiz-lines
     :spacing spacing}
    ))

(defn graph-wrapper [mc]
  (fn [graph-ctx draw-fn]
    (canvas/add-entity mc (pce/make-monet-key ":xy-grid-bg") (rectangle graph-ctx "cyan"))
    (canvas/add-entity mc (pce/make-monet-key ":xy-grid") (pce/xy-grid-new graph-ctx))
    (add-axis mc graph-ctx)
    (add-grid-marks-new mc graph-ctx)
    ))

(comment 
  (def domain (into [] (range 6 -5 -1)))
  (prn (.indexOf domain -3))
  (map #(js/console.log %) domain)
  )

(defn draw-cartisian [mc margin bg-color]
  (let [render-fn (->canvas mc margin bg-color)
        graph (graph-wrapper mc)]
    (render-fn (fn [] (graph (graph-context mc) nil)))))

(defnc math []
  (let [monet-canvas (hooks/use-ref nil)
        [graph set-graph] (hooks/use-state {:width 600
                                            :height 800
                                            :points? true
                                            :lines? true
                                            :background? true
                                            :dino? false})
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
     ($ canvas-component {:id "canvas" :class "canvas" :style {:width (:width graph) :height (:height graph)}})
     (make-toggle :background?)
     (make-toggle :lines?)
     (make-toggle :points?)
     (make-toggle :dino?))))



