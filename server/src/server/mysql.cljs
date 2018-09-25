(ns server.mysql
  (:require [cljs.nodejs :as node]
            [server.utils :as utils]))

(def mysql (node/require "mysql"))

(def promise (node/require "promise"))

(def options (clj->js {:connectionLimit 10
                       :host "localhost"
                       :user "root"
                       :password "root"
                       :database"tina_db"}))

(def connection (atom nil))

(defn raw->clj [data]
  (if-not (nil? data)
    (js->clj (->> data
                     (.stringify js/JSON)
                     (.parse js/JSON)) :keywordize-keys true)
    nil))

(defn connect []
  (let [pool (.createPool mysql options)]
    (reset! connection pool)))

(defn run-query
  ([query]
   (run-query query []))
  ([query & bindings]
   (promise. (fn [done reject]
               (if (nil? @connection)
                 (reject "No DB connection")
                 (let [q (.query @connection query (clj->js bindings)
                                 (fn [error results fields]
                                   (if-not (nil? error)
                                     (reject error)
                                     (done (raw->clj results) (raw->clj fields)))))]
                   (println (.-sql q))))))))

(defn error-promise [error]
  (promise.
   (fn [_ reject]
     (println error)
     (reject (clj->js {:error (.-code error)})))))

(defn add-directory [dir-name]
  (-> "INSERT INTO directories (j) VALUES (JSON_OBJECT('name', ?, 'services', JSON_ARRAY()))"
      (run-query dir-name)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn add-service [dir-id data]
  (-> "SELECT JSON_LENGTH(j,'$.services') AS cnt FROM directories WHERE id=?"
      (run-query dir-id)
      (utils/->then #(run-query "UPDATE directories SET j = JSON_SET(j, '$.services[?]', JSON_OBJECT('name', ?, 'url', ?)) WHERE id = ?"
                                (-> %1 first :cnt) (.-name data) (.-url data) dir-id))
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn update-service [dir-id serv-id data]
  (-> "UPDATE directories SET j = JSON_SET(j, '$.services[?]', JSON_OBJECT('name', ?, 'url', ?)) WHERE id = ?"
      (run-query (js/parseInt serv-id) (.-name data) (.-url data) dir-id)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))


(defn delete-service [dir-id serv-id]
  (-> "UPDATE directories SET j = JSON_REMOVE(j, '$.services[?]') WHERE id = ?"
      (run-query (js/parseInt serv-id) dir-id)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn delete-directory [dir-id]
  (-> "DELETE FROM directories WHERE id = ?"
      (run-query dir-id)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))
