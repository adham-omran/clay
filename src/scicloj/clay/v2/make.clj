(ns scicloj.clay.v2.make
  (:require [scicloj.clay.v2.config :as config]
            [scicloj.clay.v2.files :as files]
            [scicloj.clay.v2.util.path :as path]
            [scicloj.clay.v2.read :as read]
            [scicloj.clay.v2.item :as item]
            [scicloj.clay.v2.prepare :as prepare]
            [scicloj.clay.v2.notebook :as notebook]
            [scicloj.clay.v2.page :as page]
            [scicloj.clay.v2.server :as server]
            [scicloj.clay.v2.util.time :as time]
            [clojure.string :as string]
            [clojure.java.shell :as shell]))

(defn form->context [form]
  {:form form
   :value (eval form)})

(defn make [{:keys [source-path
                    single-form]
             :as options}]
  (let [ns-form (-> source-path
                    slurp
                    read/read-ns-form)
        ns-name (-> ns-form
                    second
                    name)
        ;; merge configurations
        {:as pre-config
         :keys [base-target-path
                format
                html
                quarto]}
        (merge (config/config)
               (-> ns-form
                   meta
                   :clay)
               options)
        ;; target path
        target-path (path/ns->target-path
                     base-target-path
                     ns-name
                     (str (when (-> pre-config
                                    :format
                                    second
                                    (= :revealjs))
                            "-revealjs")
                          ".html"))
        config (assoc pre-config
                      :target-path target-path)]
    (when (-> config :show)
      (server/update-page! {:page (page/html
                                   {:items [item/loader]
                                    :config config})}))
    (files/init-target! target-path)
    (let [items      (-> source-path
                         (notebook/notebook-items config))]
      (case (first format)
        :html (let [page (page/html {:items items
                                     :config config})]
                (server/update-page!
                 (merge {:page page
                         :html-path target-path}
                        (select-keys config [:show]))))
        :quarto (let [md-path (-> target-path
                                  (string/replace #"\.html$" ".md"))
                      output-file (-> target-path
                                      (string/split #"/")
                                      last)]
                  (->> {:items items
                        :config (-> config
                                    (update-in [:quarto :format]
                                               select-keys [(second format)])
                                    (update-in [:quarto :format (second format)]
                                               assoc :output-file output-file))}
                       page/md
                       (spit md-path))
                  (println [:wrote md-path (time/now)])
                  #_(Thread/sleep 500)
                  (->> (shell/sh "quarto" "render" md-path)
                       ((juxt :err :out))
                       (mapv println))
                  (println [:created target-path (time/now)])
                  (server/update-page! {:html-path target-path}))))))



(comment
  (make {:format [:html]
         :source-path "notebooks/index.clj"})

  (make {:format [:html]
         :source-path "notebooks/index.clj"
         :show false})

  (make {:format [:html]
         :source-path "notebooks/index.clj"
         :single-form '(kind/cytoscape
                        [{:style {:width "100px"
                                  :height "100px"}}
                         cytoscape-example])})

  (make {:format [:quarto :html]
         :source-path "notebooks/index.clj"})
  )