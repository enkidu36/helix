(ns pbranes.canvas.entities
  (:require [monet.canvas :as canvas]))

(defn make-monet-key [prefix]
  (keyword (str prefix (gensym))))

(defn calc-graph-pt [pt graph-ctx]
  (vector (* (:x-spacing graph-ctx) (first pt)) (* (:y-spacing graph-ctx) (second pt))))

(defn calc-graph-points [points graph-ctx]
  (map #(calc-graph-pt % graph-ctx) points))

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

(defn rectangle [{:keys [x y w h]} color]
  (canvas/entity
   {:x x :y y :w w :h h}
   (fn [value] value)
   (fn [ctx val]
     (-> ctx
         (canvas/save)
         (canvas/fill-style color)
         (canvas/fill-rect val)
         (canvas/restore)))))

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

(defn draw-line [ctx {:keys [x1 y1 x2 y2] }]
  (-> ctx
      (canvas/save)
      (canvas/begin-path)
      (canvas/stroke-cap "rounded")
      (canvas/stroke-width 1)
      (canvas/move-to (- x1 0.5) (- y1 0.5))
      (canvas/line-to (- x2 0.5) (- y2 0.5))
      (canvas/stroke)
      (canvas/restore)))

(defn xy-grid [{:keys [w h n]}]
  (canvas/entity
   {:w w :h h :n n}
   (fn [value] value)
   (fn [ctx {:keys [w h n]}]
     (let [x-spacing (/ w n)
           y-spacing (/ h n)]
       (dotimes [i (+ n 1)]
         (let [x (* i x-spacing)
               y1 0
               y2 h]
           (draw-line ctx {:x1 x :y1 y1 :x2 x :y2 y2}))
         (let [y (* i y-spacing)
               x1 0
               x2 w]
           (draw-line ctx {:x1 x1 :y1 y :x2 x2 :y2 y})))))))

(defn xy-grid-new [{:keys [w h vert-lines horiz-lines spacing]}]
  (canvas/entity
   {:w w :h h :vert-lines vert-lines :horiz-lines horiz-lines :spacing spacing}
   (fn [value] value)
   (fn [ctx {:keys [w h vert-lines horiz-lines]}]
     (dotimes [i (+ vert-lines 1)]
       (let [x (* i spacing)
             y1 0
             y2 h]
         (draw-line ctx {:x1 x :y1 y1 :x2 x :y2 y2})))
     (dotimes [i (+ horiz-lines 1)]
       (let [y (* i spacing)
             x1 0
             x2 w]
         (draw-line ctx {:x1 x1 :y1 y :x2 x2 :y2 y}))))))

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

(defn plot-points! 
  ([pts r] (plot-points! pts r "black"))
  ([pts r color]
   (canvas/entity
    {:pts pts :r r :color color}
    nil
    (fn [ctx {:keys [pts r color]}]
      (dotimes [i (count pts)]
        (-> ctx
            (canvas/save)
            (canvas/begin-path)
            (canvas/fill-style color)
            (canvas/arc {:x (first (nth pts i)) :y (second (nth pts i)) :r r :start-angle 0 :end-angle (* 2 Math/PI) :conterclockwise true})
            (canvas/fill)
            (canvas/restore)))))))

(defn calc-polygon-segments 
  "Transforms a list of points to a list of line segments connecting the points in the list.
    [[0 0] [4 0] [4 3]] --> [[[0 0] [4 0]] [[4 0] [4 3]] [[4 3] [0 0]]]"
  [points]
  (when (and points (not-empty points))
    (loop [i 0 acc []]
      (if  (= i (dec (count points)))
        (conj acc [(last points) (first points)])
        (let [ndx-1 i
              ndx-2 (inc i)
              line-seg [(nth points ndx-1) (nth points ndx-2)]]
          (recur (inc i) (conj acc line-seg)))))))

(comment
  (def points [[0 0] [4 0] [4 3]])
  (def one-point [[0 0]])
  (calc-polygon-segments points)
  (= [[[0 0] [4 0]]
      [[4 0] [4 3]]
      [[4 3] [0 0]]] (calc-polygon-segments points))
  (= [[[0 0] [0 0]]] (calc-polygon-segments one-pt))
  (= (calc-polygon-segments nil) nil)
  (= (calc-polygon-segments []) nil)
  
  ,)

(defn plot-polygon! [pts]
  (canvas/entity
   {:pts pts}
   (fn [value] (assoc value :segments (calc-polygon-segments (:pts value))))
   (fn [ctx {:keys [segments]}]
     (dotimes [i (count segments)]
       (let [seg (nth segments i)
             p0 (first seg)
             p1 (second seg)]
         (-> ctx
             (canvas/save)
             (canvas/begin-path)
             (canvas/move-to (first p0) (second p0))
             (canvas/line-to (first p1) (second p1))
             (canvas/stroke)
             (canvas/restore)))))))
