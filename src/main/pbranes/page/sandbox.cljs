(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [monet.canvas :as canvas]
            [pbranes.canvas.entities :as pce :refer [calc-graph-points line
                                                     plot-points! plot-polygon!
                                                     text]]
            [pbranes.component.canvas :refer [canvas-component]]))

(def data [[6 4] [3 1] [1 2] [-1 5] [-2 5] [-3 4]
           [-4 4] [-5 3] [-5 2] [-2 2] [-5 1] [-4 0] [-2 1]
           [-1 0] [0 -3] [-1 -4] [1 -4] [2 -3] [1 -2] [3 -1] [5 1]])

(defn add-background [mc color key]
  (canvas/add-entity
   mc key
   (canvas/entity
    {:x 0 :y 0 :w (.-width (:canvas mc)) :h (.-height (:canvas mc)) :color color} ; val
    nil
    (fn [ctx val]             ; draw function
      (-> ctx
          (canvas/fill-style (:color val))
          (canvas/fill-rect val)))))
  mc)

(defn add-graph-bg [mc {:keys [x y w h color]}]
  (canvas/add-entity mc :graph-bg (canvas/entity
                                   {:x x :y y :w w :h h}
                                   nil
                                   (fn [ctx val]
                                     (-> ctx
                                         (canvas/save)
                                         (canvas/fill-style color)
                                         (canvas/fill-rect val)
                                         (canvas/restore))))))

(defn add-axis [mc {:keys [x y w h]}]
  (let [ctr-x (Math/floor (/ w 2))
        ctr-y (Math/floor (/ h 2))]
    (canvas/add-entity mc :y-axis (line [ctr-x y] [ctr-x h] 2 "yellow"))
    (canvas/add-entity mc :x-axis (line [x ctr-y] [w ctr-y] 2 "yellow"))))


(defn add-grid-marks [mc {:keys [x y w h partitions n x-spacing y-spacing]}]
  (loop [i 1]
    (when (< i n)
      (canvas/add-entity mc (str :x_grid_marks (gensym)) (text {:text (+ (* -1 (+ 1 partitions)) i) :x (+ (* i x-spacing)) :y h}))
      (canvas/add-entity mc (str :y_grid_marks (gensym)) (text {:text (- (+ 1 partitions) i) :x 0 :y (* i y-spacing) :vert? true}))
      (recur (inc i)))))

(defn add-grid-lines [mc {:keys [x y w h n x-spacing y-spacing]}]
  (loop [i 0]
    (when (<= i n)
      (let [is-marked? (if (and (> i 0) (< i n)) true false)
            x-height (if is-marked? (+ h 8) h)
            y-start (if is-marked? -8 0)]
        (add-graph-bg mc {:x x :y y :w w :h h :color "grey"})

        (canvas/add-entity mc (keyword (str "x_grid-lines" i)) (line [(* i x-spacing)  0] [(* i x-spacing) x-height]))

        (canvas/add-entity mc (keyword (str "y_grid-lines" i)) (line [y-start (* i y-spacing)] [w (* i y-spacing)])))
      (recur (inc i)))))

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

(defn cartisian-wrapper [mc]
  (fn [graph-ctx draw-fn]
    (let [margin (:margin graph-ctx)
          ctr-x (/ (- (.-width (:canvas mc)) (* 2 margin)) 2)
          ctr-y (/ (- (.-height (:canvas mc)) (* 2 margin)) 2)
          scale {:x 1 :y -1}]
      (add-grid-lines mc graph-ctx)
      (add-grid-marks mc graph-ctx)
      (add-axis mc graph-ctx)
      (canvas/add-entity mc :start-translate-draw (pce/start-translate-entity {:x ctr-x :y ctr-y}))
      (canvas/add-entity mc :start-scale-draw (pce/start-scale-entity scale))
      (draw-fn mc graph-ctx)
      (canvas/add-entity mc :stop-scale (pce/stop-scale-entity))
      (canvas/add-entity mc :stop-draw (pce/stop-translate-entity)))))

(defn draw-cartisian-graph [mc draw-fn]
  (let [graph-ctx (make-graph-ctx mc)
        make-cartisian (cartisian-wrapper mc)]
    (make-cartisian graph-ctx draw-fn)))

(defn ->canvas
  ([mc] (->canvas mc nil nil))
  ([mc margin color]
   (fn [draw]
     (add-background mc color :background)
     (canvas/add-entity mc :start-canvas (pce/start-translate-entity margin))
     (draw mc)
     (canvas/add-entity mc :stop-canvas (pce/stop-translate-entity)))))

(defn get-background-color [background?]
  (if background? "rgba(80, 80, 80, 1.0)" "rgba(80, 80, 80, 0.0)"))

(defn draw [mc graph-ctx]
  (let [points (pce/calc-graph-points data graph-ctx)]

    (canvas/add-entity mc :dino-poly (plot-polygon! points))
    (canvas/add-entity mc :dino-pts (plot-points! points 2.5 "yellow"))))

(defnc sandbox []
  (let [monet-canvas (hooks/use-ref nil)
        [background set-background] (hooks/use-state true)]

    (hooks/use-effect [background]
                      :always
                      (set! (.-current monet-canvas) (canvas/init (.getElementById js/document "canvas")))
                      (let [mc (.-current monet-canvas)
                            render-fn (->canvas mc {:x 50 :y 50} (get-background-color background))]
                        (render-fn (fn [mc] (draw-cartisian-graph mc draw)))))
    (<>
     ($ canvas-component {:class "canvas" :style {:width 400 :height 400 :color "lightgreen"}})
     (d/button {:on-click #(set-background (not background))} "background"))))



