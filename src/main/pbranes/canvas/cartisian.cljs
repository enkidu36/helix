(ns pbranes.canvas.cartisian
  (:require
   [monet.canvas :as canvas]
   [pbranes.canvas.graph-tools :as gt]
   [pbranes.canvas.entities :as pce :refer [line rectangle text xy-grid]]))


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

(defn add-grid-marks [mc {:keys [h partitions n x-spacing y-spacing]}]
  (loop [i 1]
    (when (< i n)
      (canvas/add-entity mc (str :x_grid_marks (gensym)) (text {:text (+ (* -1 (+ 1 partitions)) i) :x (+ (* i x-spacing)) :y h}))
      (canvas/add-entity mc (str :y_grid_marks (gensym)) (text {:text (- (+ 1 partitions) i) :x 0 :y (* i y-spacing) :vert? true}))
      (recur (inc i)))))

(defn add-grid-marks-new [mc {:keys [h domain range spacing]}]
  (js/console.log domain)
  (loop [i 0 position 1]
    (when (< i (count domain))
      (canvas/add-entity mc (pce/make-monet-key "x_grid-marks") (text {:text (nth domain i)  :x  (* position spacing) :y h}))
      (recur (inc i) (inc position))))
  (loop [i 0 position 1]
    (when (< i (count range))
      (canvas/add-entity mc (pce/make-monet-key "y_grid_marks") (text {:text (nth range i) :x 0 :y (* position spacing) :vert? true}))
      (recur (inc i) (inc position)))))

(defn add-center-axis [mc {:keys [x y w h]}]
  (let [ctr-x (Math/floor (/ w 2))
        ctr-y (Math/floor (/ h 2))]
    (canvas/add-entity mc :y-axis (line [ctr-x y] [ctr-x h] 2 "yellow"))
    (canvas/add-entity mc :x-axis (line [x ctr-y] [w ctr-y] 2 "yellow"))))

(defn add-axis [mc {:keys [x y w h spacing domain range]} ]
  (let [x-pos (* spacing (+ 1 (.indexOf domain 0)))
        y-pos (* spacing (+ 1 (.indexOf range 0)))]
    (canvas/add-entity mc :y-axis (line [x-pos y] [x-pos h] 2 "yellow"))
    (canvas/add-entity mc :x-axis (line [x y-pos] [w y-pos] 2 "yellow"))))


(defn cartisian-center-wrapper [mc]
  (fn [graph-ctx draw-fn]
    (let [margin (:margin graph-ctx)
          ctr-x (/ (- (.-width (:canvas mc)) (* 2 margin)) 2)
          ctr-y (/ (- (.-height (:canvas mc)) (* 2 margin)) 2)
          scale {:x 1 :y -1}]
      (canvas/add-entity mc :xy-grid-bg (rectangle graph-ctx "grey"))
      (canvas/add-entity mc :xy-grid (xy-grid graph-ctx))
      (add-grid-marks mc graph-ctx)
      (add-center-axis mc graph-ctx)
      (canvas/add-entity mc :start-translate-draw (pce/start-translate-entity {:x ctr-x :y ctr-y}))
      (canvas/add-entity mc :start-scale-draw (pce/start-scale-entity scale))
      (draw-fn mc graph-ctx)
      (canvas/add-entity mc :stop-scale (pce/stop-scale-entity))
      (canvas/add-entity mc :stop-draw (pce/stop-translate-entity)))))

(defn ->canvas
  ([mc] (->canvas mc 50 "white"))
  ([mc margin color]
   (fn [draw]
     (add-background mc color :background)
     (canvas/add-entity mc :start-canvas (pce/start-translate-entity margin))
     (when draw (draw mc))
     (canvas/add-entity mc :stop-canvas (pce/stop-translate-entity)))))