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
