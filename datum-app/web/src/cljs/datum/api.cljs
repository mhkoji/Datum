(ns datum.api
  (:require [cljs.core.async :refer [put! chan]]))

(defn req
  ([method path]
   (req method path {}))
  ([method path opts]
   (let [ch (chan)]
     (method (str "/api" path)
             (merge
              {:format :json
               :handler (fn [obj] (put! ch (obj "result")))}
              opts))
     ch)))
