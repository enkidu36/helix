(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [monet.canvas :as canvas]
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

(defn line
  ([pt1 pt2] (line pt1 pt2 1 "black"))
  ([pt1 pt2 width color]
   (canvas/entity
    {:x (first pt1) :y (last pt1) :x2 (first pt2) :y2 (last pt2) :color color}
    (fn [value] (assoc-in value [:value :x2] 10))
    (fn [ctx val]
      (-> ctx
          (canvas/save)
          (canvas/begin-path)
          (canvas/stroke-style color)
          (canvas/stroke-join "rounded")
          (canvas/stroke-width width)
          (canvas/move-to (- (:x val) 0.5) (- (:y val) 0.5))
          (canvas/line-to (- (:x2 val) 0.5) (- (:y2 val) 0.5))
          (canvas/stroke)
          (canvas/restore))))))

(defn point
  ([pt r] (point pt r "black"))
  ([[x y] r color]
   (canvas/entity
    {:x x :y y :r r :color color}
    nil
    (fn [ctx {:keys [x y r color]}]
      (-> ctx
          (canvas/save)
          (canvas/begin-path)
          (canvas/fill-style color)
          (canvas/arc {:x x :y y :r r :start-angle 0 :end-angle (* 2 Math/PI) :conterclockwise true})
          (canvas/fill)
          (canvas/restore))))))

(defn text [{:keys [text x y vert?] :or {vert? false}}]
  (canvas/entity
   {:text text :x x :y y}
   nil
   (fn [ctx val]
     (-> ctx
         (canvas/save)
         (canvas/fill-style "yellow")
         (canvas/font-style "16px serif")
         (canvas/text-baseline "middle")
         (canvas/text-align "center")
         (canvas/text (if vert?
                        (-> val (assoc :x (- (:x val) 24)))
                        (-> val (assoc :y (+ (:y val) 24)))))
         (canvas/restore)))))

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
      (add-graph-bg mc {:x x :y y :w w :h h :color "grey"})

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

(defn start-translate-entity [translate]
  (canvas/entity
   translate
   (fn [_] translate)
   (fn [ctx {:keys [x y]}]
     (-> ctx
         (canvas/save)
         (canvas/translate x y)))))

(defn start-scale-entity [{:keys [x y]}]
  (canvas/entity
   {:x x :y y}
   nil
   (fn [ctx {:keys [x y]}]
     (-> ctx
         (canvas/save)
         (canvas/scale x y)))))

(defn stop-scale-entity []
  (canvas/entity
   nil
   nil
   (fn [ctx _]
     (-> ctx
         (canvas/restore)))))

(defn stop-translate-entity []
  (canvas/entity
   nil
   nil
   (fn [ctx _]
     (-> ctx
         (canvas/restore)))))

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
      (canvas/add-entity mc :start-translate-draw (start-translate-entity {:x ctr-x :y ctr-y}))
      (canvas/add-entity mc :start-scale-draw (start-scale-entity scale))
      (draw-fn mc graph-ctx)
      (canvas/add-entity mc :stop-scale (stop-scale-entity))
      (canvas/add-entity mc :stop-draw (stop-translate-entity)))))

(defn draw-points [mc graph-ctx]
  (dotimes [i (count data)]
    (let [d-pt  (nth data i)
          pt (vector (* (:x-spacing graph-ctx) (first d-pt)) (* (:y-spacing graph-ctx) (second d-pt)))]
      (canvas/add-entity mc (keyword (str "pt_" i)) (point pt (:coord-radius graph-ctx))))))

(defn draw-cartisian-graph [mc]
  (let [graph-ctx (make-graph-ctx mc)
        make-cartisian (cartisian-wrapper mc)]
    (make-cartisian graph-ctx draw-points)))


(defn ->canvas [mc]
  (fn [draw]
    (add-background mc "blue" :background)
    (canvas/add-entity mc :start-canvas (start-translate-entity {:x 50 :y 50}))
    (draw mc)
    (canvas/add-entity mc :stop-canvas (stop-translate-entity))))

(defnc sandbox []
  (let [monet-canvas (hooks/use-ref nil)
        [background set-background] (hooks/use-state true)]

    (hooks/use-effect []
                      :always
                      (set! (.-current monet-canvas) (canvas/init (.getElementById js/document "canvas")))
                      (let [mc (.-current monet-canvas)
                            render-fn (->canvas mc)]
                        (render-fn (fn [mc]
                                     (draw-cartisian-graph mc)))))

    (hooks/use-effect [background]
                      :once
                      (let [mc (.-current monet-canvas)]
                        (canvas/update-entity mc :background
                                              (fn [val] (assoc-in val [:value :color] (if background
                                                                                        "rgba(80, 80, 80, 1.0)"
                                                                                        "rgba(80, 80, 80, 0.0)"))))))

    (<>
     ($ canvas-component {:class "canvas" :style {:width 800 :height 800 :color "lightgreen"}})
     (d/button {:on-click #(set-background (not background))} "background"))))


