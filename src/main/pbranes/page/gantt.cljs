(ns pbranes.page.gantt
  (:require [helix.core :refer [$ <> defnc]]
            [helix.dom :as d]
            [helix.hooks :as hooks]))

(def timeline-4 ["08:00" "12:00" "16:00" "20:00" "24:00" "04:00"])
(def day-minutes 1440)
(def time-offset 6)

(def bar-data
  [{:name "First File" :start 6 :end 12 :color "blue"}
   {:name "Second File" :start 12 :end 18 :color  "green"}
   {:name "3rd File" :start 22 :end 5.9 :color  "red"}
   {:name "4th File" :start 20 :end 24 :color  "red"}
   {:name "5th File" :start 10 :end 14 :color  "blue"}])

(defnc time-comp [{:keys [values]}]
  (d/ul {:class "chart-values"}
        (for [value values]
          (d/li value))))

(defn chart-width
  [ref]
  (when ref
    (-> ref
        (.-current)
        (.-parentNode)
        (.getBoundingClientRect)
        (.-width))))

(defn scale->minutes [scale t-hour]
  (* t-hour 60 scale))

(defn adj-start [t-hour]
  (let [offset (if (and (>= t-hour 0) (< t-hour 6)) 24 0)]
    (+ t-hour offset)))


(defn bar-position [scale start]
  (let [offset (scale->minutes scale time-offset)
        position (scale->minutes scale (adj-start start))]
    (+ 40 (- position  offset))))

(defn bar-width [scale start end]
  (js/console.log "start/end" start end)
  (let [p1 (bar-position scale start)
        p2 (bar-position scale end)]
    (- p2 p1)))

(defnc task-row-comp [{:keys [tasks]}]
  (let [ref (hooks/use-ref nil)
        [width set-width] (hooks/use-state 0)
        [scale set-scale] (hooks/use-state 0)]

    (hooks/use-layout-effect
     :auto-deps
     (set-width (- (chart-width ref) 40))
     (set-scale (/ width day-minutes)))

    (<>
     (for [task tasks]
       (d/div {:ref ref
               :style {:left (bar-position scale (:start task))
                       :width (bar-width scale (:start task) (:end task))
                       :background-color (:color task)}} (:name task))))))

(defnc tasks-comp [{:keys [tasks]}]
  (d/div {:class "chart-bars"}
         ($ task-row-comp {:tasks tasks})))

(defnc gantt-chart [{:keys [x-values tasks]}]
  (d/div {:class "chart-wrapper"}
         (d/div {:style {:display "flex" :text-align "center" :font-size "18px" :font-weight "bold" :border-bottom "1px solid white" :padding-bottom "16px"}}
                "File times - Color coded by source"
                (d/div "PBraneCo")
                (d/div { :style {:background-color "red"}} "Violux")
                (d/div {:class "chart-legend" :style {:background-color "green"}} "InsuraCare"))
         ($ time-comp {:values x-values})
         ($ tasks-comp {:tasks tasks})))

(defnc gantt-page []
  ($ gantt-chart {:x-values timeline-4 :tasks bar-data}))

