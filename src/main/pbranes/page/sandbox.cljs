(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]
            [monet.canvas :as canvas]
            [pbranes.component.canvas :refer [canvas-component]]))

(defn add-background [mc color]
  (canvas/add-entity
   mc :background
   (canvas/entity
    {:x 0 :y 0 :w (.-width (:canvas mc)) :h (.-height (:canvas mc)) :color color} ; val
    nil
    (fn [ctx val]             ; draw function
      (-> ctx
          (canvas/fill-style (:color val))
          (canvas/fill-rect val))))))

(defn line
  ([pt1 pt2] (line pt1 pt2 1 "black"))
  ([pt1 pt2 width color]
   (canvas/entity
    {:x (first pt1) :y (last pt1) :x2 (first pt2) :y2 (last pt2) :color color}
    nil
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
      (recur (inc i))))
  (js/console.log "legend done"))

(defn add-grid-lines [mc {:keys [x y w h n x-spacing y-spacing]}]
  (loop [i 0]
    (when (<= i n)
      (let [is-marked? (if (and (> i 0) (< i n)) true false)
            x-height (if is-marked? (+ h 8) h)
            y-start (if is-marked? -8 0)]
        (add-graph-bg mc {:x x :y y :w w :h h :color "grey"})

        (canvas/add-entity mc (str :x_grid-lines i) (line [(* i x-spacing)  0] [(* i x-spacing) x-height]))

        (canvas/add-entity mc (str :y_grid-lines i) (line [y-start (* i y-spacing)] [w (* i y-spacing)])))
      (recur (inc i))))
  (js/console.log "lines done"))

(defn start-translate-entity [x y]
  (canvas/entity
   {:x x :y y}
   nil
   (fn [ctx {:keys [x y]}]
     (-> ctx
         (canvas/save)
         (canvas/translate x y)))))

(defn stop-translate-entity []
  (canvas/entity
   nil
   nil
   (fn [ctx val]
     (-> ctx
         (canvas/restore)))))

(defn make-graph-ctx [{:keys [canvas]}]
  (let  [margin 50
         partitions 4
         width (- (.-width canvas) (* 2 margin))
         height (- (.-height canvas) (* 2 margin))
         n (-> partitions (* 2) (+ 2))
         x-spacing (/ width n)
         y-spacing (/ height n)]
    {:x 0 :y 0 :w width :h height :margin margin :partitions partitions :n n :x-spacing x-spacing :y-spacing y-spacing}))

(defn draw-cartisian-graph [mc]
  (let [ graph-ctx (make-graph-ctx mc)]

    (add-background mc "yellow")
    (canvas/add-entity mc :start (start-translate-entity (:margin graph-ctx) (:margin graph-ctx)))
    (add-grid-lines mc graph-ctx)
    (add-grid-marks mc graph-ctx)
    (add-axis mc graph-ctx)
    (canvas/add-entity mc :stop (stop-translate-entity))
    (canvas/stop-updating mc)))

(defnc sandbox []
  (let [monet-canvas (hooks/use-ref nil)
        [color set-color] (hooks/use-state "yellow")
        [background set-background] (hooks/use-state true)]

    (hooks/use-effect []
                      :once
                      (set! (.-current monet-canvas) (canvas/init (.getElementById js/document "canvas")))
                      (let [mc (.-current monet-canvas)]
                        (draw-cartisian-graph mc)))

    ;; (hooks/use-effect [color]
    ;;                   :once
    ;;                   (let [mc (.-current monet-canvas)
    ;;                         c-func (fn [val] (assoc-in val [:value :color] color))]
    ;;                     (canvas/update-entity mc :c1 c-func)
    ;;                     (canvas/update-entity mc :c2 c-func)
    ;;                     (canvas/update-entity mc :h1 c-func)
    ;;                     (canvas/update-entity mc :h2 c-func)))

    

    (hooks/use-effect [background]
                      :once
                      (let [mc (.-current monet-canvas)]
                        (canvas/update-entity mc :background
                                              (fn [val] (assoc-in val [:value :color] (if background
                                                                                        "rgba(80, 80, 80, 1.0)"
                                                                                        "rgba(80, 80, 80, 0.0)"))))))


    (<>
     ($ canvas-component {:class "canvas" :style {:width 800 :height 800 :color "lightgreen"}})
     (d/button {:on-click #(if (= "yellow" color) (set-color "green") (set-color "yellow"))} "color")
     (d/button {:on-click #(set-background (not background))} "background"))))


