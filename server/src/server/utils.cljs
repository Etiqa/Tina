(ns server.utils)

(defn ->then
  "Add a then fn to a promise"
  [fn then]
  (.then fn then))

(defn ->finally
  "Add a then fn to a promise"
  [fn then]
  (.finally fn then))

(defn ->catch
  "Add a catch fn to a promise"
  [fn catch]
  (.catch fn catch))

(defn json->map [json]
  (js->clj (.parse js/JSON json) :keywordize-keys true))

(defn map->json [mp]
  (.stringify js/JSON (clj->js mp)))

(defn json-response [res]
  (.setHeader res "Content-Type" "application/json")
  res)

(defn query->resp
  ([q-result j-key]
   (query->resp q-result j-key []))

  ([q-result j-key oth-keys]
   (->> q-result
        (map #(-> (.parse js/JSON (j-key %))
                  (js->clj  :keywordize-keys true)
                  (merge (select-keys % oth-keys))))
        clj->js)))
