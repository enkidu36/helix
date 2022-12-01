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
  ([pt1 pt2] (line pt1 pt2 1))
  ([pt1 pt2 width]
   (canvas/entity
    {:x (first pt1) :y (last pt1) :x2 (first pt2) :y2 (last pt2)}
    nil
    (fn [ctx val]
      (-> ctx
          (canvas/save)
          (canvas/begin-path)
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
     (let [text-metrics (.measureText ctx (:text val))
           curr (:x val)]
       (-> ctx
           (canvas/save)
           (canvas/fill-style "yellow")
           (canvas/font-style "16px serif")
           (canvas/text-baseline "middle")
           (canvas/text-align "center")
           (canvas/text (if vert?
                          (-> val (assoc :x (- (:x val) 24)))
                          val))
           (canvas/restore))))))

(defn add-graph-bg [mc {:keys [x y w h color]}]
  (canvas/add-entity mc :graph-bg (canvas/entity
                                   {:x x :y y :w w :h h}
                                   nil 
                                   (fn [ctx val]
                                     (-> ctx 
                                         (canvas/save)
                                         (canvas/fill-style color)
                                         (canvas/fill-rect val)))) )
  )

(defn add-axis [{:keys [canvas] :as mc} {:keys [parti x y w h] :as graph-ctx}]
  (let [ctr-x (Math/floor (/ w 2))
        ctr-y (Math/floor (/ h 2))]
    (canvas/add-entity mc :y-axis (line [ctr-x y] [ctr-x h] 2))
    (canvas/add-entity mc :x-axis (line [x ctr-y] [w ctr-y] 2))))

(defn calc-spacing [val n]
   (/ val n))

(defn add-grid [{:keys [canvas] :as mc} {:keys [margin x y w h partitions] :as graph-ctx}]
  (let [n (-> partitions (* 2) (+ 2) )
        ndx-start partitions
        x-spacing (calc-spacing w n)
        y-spacing (calc-spacing h n)]
    (loop [i 0]
      (when (<= i n)
        (js/console.log "n: " n "x-location" (* i x-spacing))
        (add-graph-bg mc {:x x :y y :w w :h h :color "grey"})
        ;; (canvas/add-entity mc (str :text_y_ (gensym)) (text {:text (- ndx-start i) :x x :y (* i y-spacing) :vert? true}))
        (canvas/add-entity mc (str :text_x_ (gensym)) (text {:text (+ (* -1 ndx-start) i) :x (+ (* i x-spacing)) :y (+ h 20)}))
        (canvas/add-entity mc (str :x i) (line [(+ (* i x-spacing) margin)  y] [(+ margin (* i x-spacing)) (+ h margin)]))
        ;; (canvas/add-entity mc (str :y i) (line [x (+ (* i y-spacing) margin)] [(- w y-spacing) (+ (* i y-spacing) margin)]))
        (recur (inc i))))
    (js/console.log "done")))


(defn draw-cartisian-graph [{:keys [canvas] :as mc}]
  (let [margin 50
        partitions 4
        
        width (- (.-width canvas) (* 2 margin))
        height (- (.-height canvas) (* 2 margin))
        graph-ctx {:x margin :y margin :w width :h height :partitions partitions :margin margin}]
    (add-background mc "yellow")
    ;; (add-axis mc graph-ctx)
    (add-grid mc graph-ctx)
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


