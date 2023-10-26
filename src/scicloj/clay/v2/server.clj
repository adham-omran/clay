(ns scicloj.clay.v2.server
  (:require
   [clojure.java.browse :as browse]
   [clojure.java.io :as io]
   [clojure.java.shell :as sh]
   [clojure.string :as string]
   [hiccup.core]
   [org.httpkit.server :as httpkit]
   [scicloj.clay.v2.server.state :as server.state]
   [scicloj.clay.v2.util.path :as path]
   [scicloj.clay.v2.util.time :as time]
   [scicloj.kindly.v4.api :as kindly]))

(def default-port 1971)

(defonce *clients (atom #{}))

(defn broadcast! [msg]
  (doseq [ch @*clients]
    (httpkit/send! ch msg)))

(defn get-free-port []
  (loop [port 1971]
    ;; Check if the port is free:
    ;; (https://codereview.stackexchange.com/a/31591)
    (or (try (do (.close (java.net.ServerSocket. port))
                 port)
             (catch Exception e nil))
        (recur (inc port)))))

(defn routes [{:keys [:body :request-method :uri]
               :as req}]
  (if (:websocket? req)
    (httpkit/as-channel req {:on-open (fn [ch] (swap! *clients conj ch))
                             :on-close (fn [ch _reason] (swap! *clients disj ch))
                             :on-receive (fn [_ch msg])})
    (case [request-method uri]
      [:get "/"] {:body (or (some-> (server.state/html-path)
                                    slurp)
                            (:page @server.state/*state))
                  :status 200}
      [:get "/counter"] {:body (-> (server.state/counter)
                                   str)
                         :status 200}
      ;; else
      {:body (let [base-path (or (some-> (server.state/html-path)
                                         path/path->parent)
                                 "docs")]
               (try (->> uri
                         (str base-path)
                         (java.io.FileInputStream.))
                    (catch java.io.FileNotFoundException e
                      ;; Ignoring missing source maps.
                      ;; TODO: Figure this problem out.
                      (if (.endsWith ^String uri ".map")
                        nil
                        (throw e)))))
       :status 200})))

(defonce *stop-server! (atom nil))

(defn core-http-server [port]
  (httpkit/run-server #'routes {:port port}))

(defn port->url [port]
  (str "http://localhost:" port "/"))

(defn url []
  (port->url (server.state/port)))

(defn browse! []
  (browse/browse-url (url)))

(defn welcome-hiccup []
  [:div
   [:p [:pre (str (java.util.Date.))]]
   [:p [:pre [:a {:href "https://scicloj.github.io/clay/"}
              "Clay"]
        " is ready, waiting for interaction."]]])

(defn open! []
  (when-not @*stop-server!
    (let [port (get-free-port)
          server (core-http-server port)]
      (server.state/set-port! port)
      (reset! *stop-server! port)
      (println "serving scittle at " (port->url port))
      (-> (welcome-hiccup)
          hiccup.core/html
          server.state/set-page!)
      (browse!))))

(defn close! []
  (when-let [s @*stop-server!]
    (s))
  (reset! *stop-server! nil))
