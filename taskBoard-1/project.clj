(defproject taskdesk "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://localhost:3000"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.9.7"]
            [lein-localrepo "0.5.3"]
            [compojure "1.5.1"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [mysql/mysql-connector-java "5.1.38"]
                 [ring/ring "1.5.0"]
                 [ring/ring-json "0.4.0"]
                 [metosin/ring-http-response "0.8.0"]
                 [compojure "1.5.1"]
                 [hiccup "1.0.5"]
                 [selmer "1.0.9"]]
  :ring {:handler taskdesk.core/engine
         :auto-reload? true
         :auto-refresh? false}
  :profiles
  {:dev {:dependencies [
                        [javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]
         }
   })
