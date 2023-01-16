(ns pbranes.page.cron
  (:refer-clojure :exclude [parse-double])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require  [helix.core :refer [$ <> defnc]]
             [cljs.core.async :refer [<!]]
             [cljs-http.client :as http]
             [helix.hooks :as hooks]
             [helix.dom :as d]))

(defn make-remote-call [endpoint fn-state]
  (go (let [response (<! (http/get endpoint {:with-credentials? false}))
            data (:body response)]
        (fn-state data))))

(defnc cron-page []
  (let [[state set-state] (hooks/use-state {:value "init state"})]
    
    (hooks/use-effect
     []
     :once
     (make-remote-call "http://localhost:3000/testing" (fn [data] (set-state assoc :value data))))

    (d/div (:value state))))

