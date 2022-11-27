(ns pbranes.page.sandbox
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [monet.canvas :as canvas]
            [pbranes.canvas.tools :refer [get-canvas-dimensions get-center]]
            [pbranes.component.canvas :refer [canvas-component]]))

(defn background-entity [canvas color]
  (canvas/entity
   {:x 0 :y 0 :w (.-width canvas) :h (.-height canvas)} ; val
   nil                       ; update function
   (fn [ctx val]             ; draw function
     (-> ctx
         (canvas/fill-style color)
         (canvas/fill-rect val)))))

(defn circle-entity []
  (canvas/entity
   {:x 120 :y 100 :r 90 :start-angle 0 :end-angle (* 2 Math/PI) :counter-clockwise? false}
   nil
   (fn [ctx val]
     (-> ctx
         (canvas/begin-path)
         (canvas/arc val)
         (canvas/stroke)))))


(defn start []
  (canvas/entity
   {:x 1}
   nil
   (fn [ctx ]
     (-> ctx
         (canvas/translate 100 100)))))

(defn line
  ([pt1 pt2] (line pt1 pt2 [0 0]))
  ([pt1 pt2 origin]
   (canvas/entity
    {:x (first pt1) :y (last pt1) :x2 (first pt2) :y2 (last pt2) :tx (first origin) :ty (last origin)}
    nil
    (fn [ctx val]
      (-> ctx
          (canvas/save)
        ;;  (cartision-scale-translate 1 -1 (:tx val) (:ty val))
          (canvas/begin-path)
          (canvas/move-to (:x val) (:y val))
          (canvas/line-to (:x2 val) (:y2 val))
          (canvas/stroke)
          (canvas/restore))))))

(defn add-axis [mc]
  (let [center (get-center mc)
        padding 10
        x-ctr (first center)
        y-ctr (- (last center) padding)
        dim (get-canvas-dimensions mc)
        width (:x dim)
        height (:y dim)
        y-start [x-ctr padding]
        y-end [x-ctr (- height padding)]
        x-start [padding y-ctr]
        x-end [(- width padding) y-ctr]]
    (canvas/add-entity mc :y-axis (line y-start y-end center))
    (canvas/add-entity mc :x-axis (line x-start x-end center))))

(defn add-background [mc color]
  (canvas/add-entity mc :background (background-entity (-> mc :ctx .-canvas) color)))

(def door-color (atom "yellow"))

(defn add-house [mc id translate color]
  (canvas/add-entity
   mc id
   (canvas/entity
    {:color color}
    nil
    (fn [ctx val]
      (-> ctx
          (canvas/save)
          (canvas/translate (first translate) (second translate))
          (canvas/stroke-width 10)
          (canvas/fill-style @(:color val))
          (canvas/stroke-rect {:x 75 :y 140 :w 150 :h 110})
          (canvas/fill-rect {:x 130 :y 190 :w 40 :h 60})
          (canvas/begin-path)
          (canvas/move-to 50 140)
          (canvas/line-to 150 60)
          (canvas/line-to 250 140)
          (canvas/close-path)
          (canvas/stroke)
          (canvas/restore))))))

(defn add-circle [mc id {:keys [x y r color]}]
  (canvas/add-entity
   mc id
   (canvas/entity
    {:x x :y y :r r :color @color}
    ;; nil
    (fn [val]
     (-> val (assoc :color @color)))
    (fn [ctx val]
      (-> ctx
          (canvas/save)
          (canvas/stroke-style (:color val))
          (canvas/circle val)
          (canvas/stroke)
          (canvas/restore))))))

(defn add-translate [mc {:keys [x y]}]
  (canvas/add-entity
   mc :global-translate 
   (canvas/entity 
    {:x x :y y}
    nil 
    (fn [ctx {:keys [x y]}]
      (-> ctx
          (canvas/save)
          (canvas/translate x y))))))

(defn add-restore [mc]
  (canvas/add-entity
   mc :global-restore 
   (canvas/entity
    nil
    nil 
    (fn [ctx]
      (canvas/restore ctx)))))


(defn draw-background [{:keys [background-color]}]
  (fn [monet-canvas]
    (add-background monet-canvas background-color)
    (add-translate monet-canvas {:x -20 :y -20})
    (add-restore monet-canvas)
    (add-house monet-canvas :h1 [0 0] door-color)
    (add-house monet-canvas :h2 [220 0] door-color)
    (add-house monet-canvas :h3 [440 0] door-color)
    (add-circle monet-canvas :circle {:x 200 :y 200 :r 75 :color door-color})
    (add-circle monet-canvas :circle2 {:x 300 :y 300 :r 75 :color door-color})
    (add-axis monet-canvas)
    ))



(defnc sandbox []

  (<>
   ($ canvas-component {:class "canvas" :draw (draw-background {:background-color "grey"}) :style {:width 800 :height 400 :color "lightgreen"}})
   (d/button {:on-click #(do (reset! door-color (if (= "yellow" @door-color) "green" "yellow")))} "test")))


