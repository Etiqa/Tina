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

  ([query binding]
   (promise. (fn [done reject]
               (if (nil? @connection)
                 (reject "No DB connection")
                 (.query @connection query (clj->js binding) (fn [error results fields]
                                                               (if-not (nil? error)
                                                                 (reject error)
                                                                 (done (raw->clj results) (raw->clj fields))))))))))

(defn add-directory [data]
  (-> "INSERT INTO directories SET j = ?"
      (run-query (utils/map->json data))
      (utils/->then (fn [& _] (println "DOOOOONW")))
      (utils/->catch #(do (println "ERROR")
                          (println %1)))))


(defn test-query []
  (-> "SELECT id, j->'$.name' from directories"
      run-query
      (utils/->then #(println %1))
      (utils/->catch #(do (println "ERROR")
                    (println %1)))))
