;; Copyright (c) Cognitect, Inc.
;; All rights reserved.
;;
;; Licensed under the Apache License, Version 2.0 (the "License");
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;      http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS-IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns datomic.dev.datafy)

(defn datafy!
  "Datafy everything this lib knows how to datafy."
  []
  (doseq [nss '[datomic.dev.datafy.java
                datomic.dev.datafy.async
                datomic.dev.datafy.clojure]]
    ((requiring-resolve
      (symbol (name nss) "datafy!")))))
