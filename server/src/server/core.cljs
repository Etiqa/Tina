(ns server.core
  (:require [cljs.nodejs :as node]
            [server.store :as server]
            [server.mysql :as mysql]
            [server.utils :as utils]))

(node/enable-util-print!)

(def express (node/require "express"))
(def fs (node/require "fs"))
(def cors (node/require "cors"))
(def body-parser (node/require "body-parser"))
(def axios (node/require "axios"))
(def https (node/require "https"))
(def process (js* "process"))

(def in-memory-data (atom {}))

(set! (.-ca (.-options (.-globalAgent (node/require "https"))))
      (.create (node/require "ssl-root-cas/latest")))

(defn json->map [json]
  (js->clj (.parse js/JSON json) :keywordize-keys true))

(defn map->json [mp]
  (.stringify js/JSON (clj->js mp)))

(defn json-response [res]
  (.setHeader res "Content-Type" "application/json")
  res)

(defn load-data [callback]
  (.readFile fs "data/directory.json" "utf8" callback))

(defn load-in-memory []
  (load-data #(reset! in-memory-data (json->map %2))))

(defn directories [req res]
  (json-response res)
  (.send res (map->json @in-memory-data)))

(defn directory-by-id [id]
  (->> @in-memory-data
       (filter #(= (:id %) id))
       first))

(defn directory [req res]
  (json-response res)
  (.send res (-> req
                 .-params
                 .-id
                 directory-by-id
                 map->json)))

(defn fetch-url [req res]
  (set! (.-NODE_TLS_REJECT_UNAUTHORIZED (.-env process))  "0")

  (let [url (.-url (.-body req))]
    (-> axios
        (.get url)
        (utils/->then #(.-data %))
        (utils/->then (fn [content] (.send res (map->json { :content content}))))
        (utils/->catch (fn [error] (println error)
                   (.send res (map->json { :error error})))))))

(defn attach-api [app]
  (.get app "/" directories)
  (.get app "/directory/:id" directory)
  (.post app "/fetch-url" fetch-url))

(defn start-server []
  (load-in-memory)

  (let [app (express)
        port 3002]
    (.use app (cors))
    (.use app (.json body-parser))
    (attach-api app)
    (server/start app port)))

(defn stop-server []
  (server/stop))

(defn restart-server []
  (stop-server)
  (start-server))

(defn -main [& args]
  (stop-server)
  (start-server))

(set! *main-cli-fn* -main)
