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
(def cookie-parser (node/require "cookie-parser"))
(def axios (node/require "axios"))
(def https (node/require "https"))
(def process (js* "process"))

(def in-memory-data (atom {}))

(set! (.-ca (.-options (.-globalAgent (node/require "https"))))
      (.create (node/require "ssl-root-cas/latest")))

(defn auth-route [api-fn]
  (fn [req res]
    (let [auth-token (.get req "auth-token")]
      (if (= "undefined" auth-token)
        (-> res
            (.status 403)
            (.send {:error "NOT_LOGGED"}))
        (-> auth-token
         mysql/get-user-by-token
         (utils/->then (fn [user]
                         (if-not user
                           (-> res
                               (.status 401)
                               (.send {:error "WRONG_TOKEN"}))
                           (do
                             (set! (.-user-id req) (:user_id user))
                             (api-fn req res)))))))
      ;; (mysql/get-user-by-token auth-token)
      )))

(defn directories [req res]
  (-> "SELECT id, j FROM directories WHERE user_id = ?"
      (mysql/run-query (-> req .-user-id))
      (utils/->then (fn [result]
                      (.send res (utils/query->resp result :j [:id]))))))

(defn directory [req res]
  (utils/json-response res)
  (let [id (-> req .-params .-id)
        user-id (-> req .-user-id)]
    (-> "SELECT j FROM directories WHERE id = ? AND user_id = ?"
        (mysql/run-query id user-id)
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
  (let [dir-name (-> req .-body .-name)
        user-id (-> req .-user-id)]
    (mysql/add-directory dir-name user-id)
    (.send res (utils/map->json { :valid true}))))

(defn add-service [req res]
 (let [dir-id (-> req .-params .-id)
       user-id (-> req .-user-id)
       data (-> req .-body)]
   (-> dir-id
       (mysql/add-service data user-id)
       (utils/->then (fn [result]
                       (.send res (clj->js {:done true}))))
       (utils/->catch (fn [error]
                        (.send res error))))))

(defn update-service [req res]
  (let [dir-id (-> req .-params .-id)
        serv-id (-> req .-params .-sid)
        user-id (-> req .-user-id)
        data (-> req .-body)]

    (-> dir-id
        (mysql/update-service serv-id data user-id)
        (utils/->then (fn [result]
                        (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error]
                         (.send res error))))))

(defn delete-service [req res]
  (let [dir-id (-> req .-params .-id)
        serv-id (-> req .-params .-sid)
        user-id (-> req .-user-id)]

    (-> dir-id
        (mysql/delete-service serv-id user-id)
        (utils/->then (fn [result]
                        (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error]
                         (.send res error))))))

(defn delete-directory [req res]
  (let [dir-id (-> req .-params .-id)
        user-id (-> req .-user-id)]
    (-> dir-id
        (mysql/delete-directory user-id)
        (utils/->then (fn [result]
                        (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error]
                         (.send res error))))))

(defn signup [req res]
  (let [email (-> req .-body .-email)
        password (-> req .-body .-password)
        ip (or (.-x-forwarded-for (.-headers req))
               (.-remoteAddress (.-connection req)))]

    (-> email
        (mysql/create-user password ip)
        (utils/->then (fn [] (.send res (clj->js {:done true}))))
        (utils/->catch (fn [error] (-> res
                                       (.status 400)
                                       (.send error)))))))

(def auth-cookie-name )
(defn signin [req res]
  (let [email (-> req .-body .-email)
        password (-> req .-body .-password)]

    (-> email
     (mysql/get-user password)
     (utils/->then (fn [token]
                     (.send res (clj->js {:token token}))))
     (utils/->catch (fn [error]
                      (-> res
                          (.status 400)
                          (.send error)))))))

(defn attach-api [app]
  (.get app "/" (auth-route directories))
  (.get app "/directory/:id" (auth-route directory))
  (.post app "/fetch-url" (auth-route fetch-url))
  (.post app "/directory" (auth-route add-directory))
  (.post app "/directory/:id" (auth-route add-service))
  (.put app "/directory/:id/:sid" (auth-route update-service))
  (.delete app "/directory/:id/:sid" (auth-route delete-service))
  (.delete app "/directory/:id/" (auth-route delete-directory))
  (.post app "/signup" signup)
  (.post app "/signin" signin))

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
