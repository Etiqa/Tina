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

(defn directories [req res]
  (utils/json-response res)
  (-> "select id, j from directories"
      mysql/run-query
      (utils/->then (fn [result]
                      (.send res (utils/query->resp result :j [:id]))))))

(defn directory [req res]
  (utils/json-response res)
  (let [id (-> req
               .-params
               .-id)]
    (-> "select j from directories where id = ?"
        (mysql/run-query [id])
        (utils/->then (fn [result]
                        (.send res (first (utils/query->resp result :j))))))))

(defn fetch-url [req res]
  (set! (.-NODE_TLS_REJECT_UNAUTHORIZED (.-env process)) "0")
  (let [url (.-url (.-body req))]
    (-> axios
        (.get url)
        (utils/->then #(.-data %))
        (utils/->then (fn [content]
                        (.send res (utils/map->json {:content content}))))
        (utils/->catch (fn [error]
                         (.send res (utils/map->json {:content (.-message error)})))))))

(defn add-directory [req res]
  (let [dir-name (.-name (.-body req))]
    (mysql/add-directory dir-name)
    (.send res (utils/map->json { :valid true}))))

(defn add-service [req res]
 (let [dir-id (-> req .-params .-id)
       data (-> req .-body)]
   (-> dir-id
       (mysql/add-service data)
       (utils/->then (fn [result]
                       (.send res (clj->js {:done true}))))
       (utils/->catch (fn [error]
                        (.send res error))))))

(defn update-service [req res]
  (let [dir-id (-> req .-params .-id)
        serv-id (-> req .-params .-sid)
        data (-> req .-body)]

    (-> dir-id
        (mysql/update-service serv-id data)
        (utils/->then (fn [result]
                        (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error]
                         (.send res error))))))

(defn delete-service [req res]
  (let [dir-id (-> req .-params .-id)
        serv-id (-> req .-params .-sid)]

    (-> dir-id
        (mysql/delete-service serv-id)
        (utils/->then (fn [result]
                        (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error]
                         (.send res error))))))

(defn delete-directory [req res]
  (let [dir-id (-> req .-params .-id)]
    (-> dir-id
        mysql/delete-directory
        (utils/->then (fn [result]
                        (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error]
                         (.send res error))))))

(defn attach-api [app]
  (.get app "/" directories)
  (.get app "/directory/:id" directory)
  (.post app "/fetch-url" fetch-url)
  (.post app "/directory" add-directory)
  (.post app "/directory/:id" add-service)
  (.put app "/directory/:id/:sid" update-service)
  (.delete app "/directory/:id/:sid" delete-service)
  (.delete app "/directory/:id/" delete-directory))

(defn start-server []
  (mysql/connect)

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
