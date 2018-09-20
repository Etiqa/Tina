(ns server.store)


(def server (atom nil))

(defn start [app port]
  (reset! server (.listen app port #(println (str "Server started on " port)))))


(defn stop []
  (println @server)
  (and (not (nil? @server))
       (.close @server))
  (reset! server nil))
