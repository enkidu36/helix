(ns pbranes.helix.app
  (:require [goog.dom :as gdom]
            [helix.core :refer [defnc $ <>]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]))

(defnc library-component []
  (let [[display set-display] (hooks/use-state "none")]
    (<>
     (d/div {:class "calendar-side"}
            (d/button {:on-click #(set-display (if (= "" display) "none" ""))} "..."))
     (d/div {:class "calendar-training"
             :style (if (= display "none") {:display display} {})} "training"))))

(defnc app []
  (d/div {:class "page-main"}
         (d/header {:class "page-header"} "Header")
         (d/div {:class "calendar-container"}
                ($ library-component)
                (d/div {:class "calendar-main"}
                       (d/div {:class "calendar-date"} "Date Controls")
                       (d/div {:class "calendar-date"}
                              (d/div {:class "week-controls"}
                                     (for [day ["Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday" "Sunday"]]
                                       (d/div {:class "day"} day)))
                              (d/div {:class "summary-controls"} "...")
                              (d/div {:class "calendar-summary"} "summary"))
                       (d/div {:class "calendar-body"}
                              (d/div {:class "calendar-body-list"}
                                     (d/div {:class "calendar-day"}
                                            (for [day ["1" "2" "3" "4" "5" "6" "7"]]
                                              (d/div {:class "day"} day)))
                                     (d/div {:class "calendar-dates"}
                                            (for [day ["1" "2" "3" "4" "5" "6" "7"]]
                                              (d/div {:class "day"} ""))))
                              (d/div {:class "calendar-week-controls"} "")
                              (d/div {:class "calendar-summary"} "summary"))))))

(defonce root (rdom/createRoot (gdom/getElement "root")))

(defn ^:dev/after-load init! []
  (.render root ($ app)))

(init!)