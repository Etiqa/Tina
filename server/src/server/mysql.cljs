(ns server.mysql
  (:require [cljs.nodejs :as node]
            [server.utils :as utils]))

(def mysql (node/require "mysql"))

(def promise (node/require "promise"))

(def connection (atom nil))

(def options (clj->js {:connectionLimit 10
                       :host "localhost"
                       :user "root"
                       :password "root"
                       :database"tina_db"}))

(defn raw->clj [data]
  (js->clj (->> data
                (.stringify js/JSON)
                (.parse js/JSON)) :keywordize-keys true))

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

(defn test-query []
  (-> "SELECT id, j->'$.name' from directories"
      run-query
      (utils/->then #(println %1))
      (utils/->catch #(do (println "ERROR")
                    (println %1)))))
