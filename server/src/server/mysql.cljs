(ns server.mysql
  (:require [cljs.nodejs :as node]
            [server.utils :as utils]))

(def mysql (node/require "mysql"))f
(def promise (node/require "promise"))
(def bcrypt (node/require "bcrypt"))
(def random-token (node/require "random-token"))

(def options (clj->js {:connectionLimit 10
                       :host "localhost"
                       :user "root"
                       :password "root"
                       :database"tina_db"}))
(def connection (atom nil))

(def saltRounds 10)

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
     (reject (clj->js {:error (.-code error)})))))

(defn add-directory [dir-name user-id]
  (-> "INSERT INTO directories (user_id, j) VALUES (?, JSON_OBJECT('name', ?, 'services', JSON_ARRAY()))"
      (run-query user-id dir-name)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn add-service [dir-id data user-id]
  (-> "SELECT JSON_LENGTH(j,'$.services') AS cnt FROM directories WHERE id = ? AND user_id = ?"
      (run-query dir-id user-id)
      (utils/->then #(run-query "UPDATE directories SET j = JSON_SET(j, '$.services[?]', JSON_OBJECT('name', ?, 'url', ?)) WHERE id = ?"
                                (-> %1 first :cnt) (.-name data) (.-url data) dir-id))
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn update-service [dir-id serv-id data user-id]
  (-> "UPDATE directories SET j = JSON_SET(j, '$.services[?]', JSON_OBJECT('name', ?, 'url', ?)) WHERE id = ? AND user_id = ?"
      (run-query (js/parseInt serv-id) (.-name data) (.-url data) dir-id user-id)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn delete-service [dir-id serv-id user-id]
  (-> "UPDATE directories SET j = JSON_REMOVE(j, '$.services[?]') WHERE id = ? AND user_id = ?"
      (run-query (js/parseInt serv-id) dir-id user-id)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn delete-directory [dir-id user-id]
  (-> "DELETE FROM directories WHERE id = ? AND user_id = ?"
      (run-query dir-id user-id)
      (utils/->catch #(do (println "ERROR")
                          (println %1)
                          (error-promise %1)))))

(defn hash-pwd [password]
  (promise. (fn [done reject]
              (.genSalt bcrypt
                        (fn [err salt]
                          (if err
                            (reject err)
                            (.hash bcrypt password salt
                                   (fn [err hash] (if err
                                                    (reject err)
                                                    (done hash))))))))))

(defn check-hash [password hash]
  (promise. (fn [done reject]
              (.compare bcrypt password hash (fn [err res] (if err (reject err) (done res)))))))

(defn create-user [email password ip]
  (-> password
      hash-pwd
      (utils/->then (fn [hash]
                      (run-query "INSERT INTO users (email, password, creation_date, ip) VALUES (?, ?, CURRENT_TIMESTAMP(), ?)" email hash ip)))
      (utils/->catch (fn [err]
                       (error-promise err)))))

(defn register-user-token [id]
  (let [token  (random-token 32)]
    (-> "INSERT INTO user_token (user_id, token, refreshed_on) VALUES (?, ?, CURRENT_TIMESTAMP()) ON DUPLICATE KEY UPDATE token = ?, refreshed_on = CURRENT_TIMESTAMP()" (run-query id token token)
        (utils/->then (fn [result]
                        (if (> (:affectedRows result) 0)
                          (do
                            token)
                          (utils/reject-with-error "CANNOT_CREATE_SESSION"))))
        (utils/->catch (fn [err]
                         (error-promise err))))))

(defn get-user [email password]
  (-> "SELECT id, password FROM users WHERE email = ?"
      (run-query email)
      (utils/->then (fn [users]
                      (let [user (first users)]
                        (if-not user
                          (utils/reject-with-error "EMAIL_NOT_FOUND")
                          (utils/->then (check-hash password (:password user))
                                        (fn [valid-pwd]
                                          (assoc user :valid-pwd valid-pwd)))))))
      (utils/->then (fn [user]
                      (if-not (true? (:valid-pwd user))
                        (.reject promise (clj->js {:code "WRONG_PASSWORD"}))
                        (:id user))))
      (utils/->then (fn [id]
                      (register-user-token id)))
      (utils/->catch (fn [err]
                       (error-promise err)))))

(defn get-user-by-token [token]
  (-> "SELECT user_id FROM user_token WHERE token = ?"
      (run-query token)
      (utils/->then (fn [users]
                      (first users)))
      (utils/->catch (fn [err]
                       (error-promise err)))))
