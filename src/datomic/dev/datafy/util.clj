;; Copyright (c) Cognitect, Inc.
;; All rights reserved.

(ns datomic.dev.datafy.util
  (:import [java.lang.reflect Field Modifier]))

(defn- reflective-get
  "Emits source code for reflective get"
  [cls sym name]
  `(let [~'fld (doto (.getDeclaredField ~cls ~name)
               (.setAccessible true))]
     (.get ~'fld ~sym)))

(defn -datafy-declared-fields-fn
  [clsym]
  (let [cls (resolve clsym)
        _ (when-not (class? cls) (throw (IllegalArgumentException. (str "Not a class: [" clsym "]"))))
        field-exprs (->> (.getDeclaredFields cls)
                         (filter (fn [^Field f]
                                   (zero? (bit-and Modifier/STATIC (.getModifiers f)))))
                         (map (fn [^Field f]
                                (let [name (.getName f)]
                                  (if (Modifier/isPublic (.getModifiers f))
                                    [(keyword name) (list '. 'this (symbol name))]
                                    [(keyword name) (reflective-get clsym 'this name)])))))]
    (list 'extend-protocol
          'clojure.core.protocols/Datafiable
          (symbol (.getName cls))
          (list 'datafy ['this] (into {} field-exprs)))))

(defmacro datafy-declared-fields
  "Given classes, datafy those classes to return a map of their declared
fields. Useful for e.g. deftypes."
  [& classes]
  `(do
     ~@(map -datafy-declared-fields-fn classes)))

(defn val-navs
  "Create a nav function that handles each key in k->f by appling f to
  v, leaving other values unchanged"
  [k->f]
  (fn [_ k v]
    (if-let [f (k->f k)]
      (f v)
      v)))

(defn with-nav
  "Add nav-fn as nav to x."
  [x nav-fn]
  (when x
    (vary-meta x assoc 'clojure.core.protocols/nav nav-fn)))


