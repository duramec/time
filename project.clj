(defproject time "0.1.3"
            :description "Duramec time management library"
            :java-source-paths ["src/java"]
            :javac-options["-target" "1.7"
                           "-source" "1.7"
                           "-deprecation"]
            :repositories [["internal" {:url "https://repo.duramec.com/internal"                                       :creds :gpg}]]
            :dependencies [[joda-time/joda-time "2.2"]])

