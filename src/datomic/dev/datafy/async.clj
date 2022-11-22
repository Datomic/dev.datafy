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

(ns datomic.dev.datafy.async
  (:require
   [clojure.core.async :as a]
   [clojure.core.async.impl.protocols :as async-p]
   [clojure.core.async.impl.channels :as async-c]
   [datomic.dev.datafy.util :as util]))

(defn datafy!
  "Datafy async channels and buffers."
  []
  (util/datafy-declared-fields
   clojure.core.async.impl.channels.ManyToManyChannel
   clojure.core.async.impl.buffers.FixedBuffer
   clojure.core.async.impl.buffers.DroppingBuffer
   clojure.core.async.impl.buffers.SlidingBuffer
   clojure.core.async.impl.buffers.PromiseBuffer))

