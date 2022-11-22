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

(ns datomic.dev.datafy.clojure
  (:require
   [clojure.core.protocols :as p]
   [clojure.string :as str])
  (:import [java.lang.reflect Field]))

(set! *warn-on-reflection* true)

(defn- reify-impl-detail-name
  "Fields we don't want to see when reflecting on reify"
  [s]
  (or (str/starts-with? s "__cached")
      (str/starts-with? s "const__")))

(defn- datafy-reify-fields
  "Returns a map of reify field names to vals."
  [v]
  (let [consts (->> (.getDeclaredFields (class v))
                    (filter #(not (reify-impl-detail-name (.getName ^Field %))))
                    (map (fn [^Field f] (doto f (.setAccessible true)))))]
    (when (seq consts)
      (reduce
       (fn [m ^Field fld] (assoc m (symbol (.getName fld)) (.get fld v)))
       {}
       consts))))

(defn- datafy-reify
  "If v is a clojure reify, datafy it, else nil."
  [v]
  ;; guessing
  (when (and (instance? clojure.lang.IObj v)
             (not (instance? clojure.lang.ILookup v))
             (not (instance? java.lang.Iterable v)))
    (datafy-reify-fields v)))

(defn datafy!
  "Datafy Clojure plumbing"
  []
  (extend-protocol p/Datafiable
    clojure.lang.IObj
    (datafy [v]
      (if-let [m  (datafy-reify v)]
        (assoc m :type 'clojure.core/reify)
        v))))




